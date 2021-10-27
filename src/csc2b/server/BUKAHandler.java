package csc2b.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
/**
 * Class implements the runnable interface for multiple threads
 * @author Nogantsho Y 201602466
 * @version P06
 */
public class BUKAHandler implements Runnable
{
	PrintWriter pw;
	BufferedReader br;
	
	DataInputStream dis;
	DataOutputStream dos;
	
	Socket newConnectionToClient;
    Boolean running;
    Boolean authStatus ;
    /**
     * constructor to set streams
     * @param newConnectionToClient to specify socket we connecting to
     */
	public BUKAHandler(Socket newConnectionToClient)
    {	this.newConnectionToClient = newConnectionToClient;
	//Bind streams
		try {
		       pw = new PrintWriter(newConnectionToClient.getOutputStream());
		       br = new BufferedReader(new InputStreamReader(newConnectionToClient.getInputStream()));
		       
		       dis = new DataInputStream(newConnectionToClient.getInputStream());
		       dos = new DataOutputStream(newConnectionToClient.getOutputStream());
		
		}catch(IOException e) {
			e.printStackTrace();
		}	
    }
    /**
     * Method runs multiple threads
     */
    public void run()
    {
    	running = true;
    	try {
    		
    		System.out.println("handling client requests");
    		while(running) {
    			String message = br.readLine();
    			System.out.println("Request " + message);
    			StringTokenizer st = new StringTokenizer(message);
    			String command = st.nextToken().toUpperCase();
    			
    				switch(command) {
    				case "AUTH":                                  //allow login if name and password match one of the users in users.txt list    //<Name><Password>
    				{
    				String Name = br.readLine();
    				String Password = br.readLine();
    				pw.println(matchUser(Name, Password));
    				if(authStatus==true) {
    				pw.println("200 successful login");
    	            pw.flush();
    				break;
    				}
    			}
    				//return a list of available PDF files
    			case "LIST":
    			{
    				pw.println(getFileList());
    				pw.flush();
    				break;
    			}
    				//return requested PDF file given an <ID>
    			case "PDFRET":
    			{
    				String textin = br.readLine();
    				idToFile(textin);
    				break;
    			}
    			case "LOGOUT" :
    			{
    				running = false;
    				authStatus =false;
    				dos.close();
    				dis.close();
    				pw.close();
    			    br.close();
    				newConnectionToClient.close();
    				break;
    			}
    			}
    			
    		}
    	}catch(IOException e) {
    		e.printStackTrace();
    	}	
    }
    
    /**
     * Helper function to get matching login credentials
     * @param username
     * @param password
     * @return
     */
    
    private boolean matchUser(String username,String password)
    {
	boolean found = false;
	File userFile = new File("data/server/users.txt");

	while(authStatus) {
			try
	{
	    Scanner scan = new Scanner(userFile);
	    if(!scan.hasNext()) {
			System.out.println("the pdf is currently empty");
		}
	    while(scan.hasNextLine()&&!found)
	    {
		String line = scan.nextLine();
		String lineSec[] = line.split("\\s");  //delimiter used to split the two string
		//Compare user 
		if((lineSec[0].equals(username) && lineSec[1].equals(password))) {
			found = true;	
			authStatus =true;
		   }
		else {
			System.out.println("500 unsuccessful login");
		}
	    }
	    scan.close();
	}
	catch(IOException ex)
	{
	    ex.printStackTrace();
	}
  }
	return found;
	}

   /**
    * function helps to get file list
    * @return an array of files
    */
    private ArrayList<String> getFileList()
    {
		ArrayList<String> result = new ArrayList<String>();
		File lstFile = new File("data/server/PdfList.txt");
		
		try
		{
			Scanner scan = new Scanner(lstFile);
            String line = " ";
            //Read in each line of file
            if(!scan.hasNext() ) {
            	System.out.println("the pdf is currently empty");
            }
            while(scan.hasNext()) {
            	line = scan.next();
            	String ret = line + "#";
            	result.add(ret);
            }
			scan.close();
		}	    
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
		return result;
    }
    /**
     * function helps to get id of one file
     * @param ID 
     * @return string of one specific id
     */
    private String idToFile(String ID)
    {
    	String result = "";
    	File lstFile = new File("data/server/PdfList.txt");
    	try
    	{
    		Scanner scan = new Scanner(lstFile);

    		//Read filename from file
    		while(scan.hasNext()) {
    			String inputID = br.readLine();
    			StringTokenizer st = new StringTokenizer(inputID);
    			ID = st.nextToken();
    			System.out.println("ID requested: "+ID);
    			//reading through to find match
    			String line = scan.nextLine();
    			StringTokenizer token = new StringTokenizer(line);
    			String id = token.nextToken();
    			String fName = token.nextToken();
    			if(id.equals(ID)){
    				result = fName;
    			}
    		}
    		scan.close();
    		System.out.println("Name of file requested file: "+ result);
    		File fileToReturn = new File("data/server"+result);
    		if(fileToReturn.exists()) {
    			pw.println(fileToReturn.length());   //sending file size to the client
    			pw.flush();
    			FileInputStream fis = new FileInputStream(fileToReturn);
    			byte[] buffer = new byte[1024];
    			int n= 0;
    			while((n=fis.read(buffer))>0) {
    				dos.write(buffer,0,n);
    				dos.flush();
    			
    			}
    			fis.close();
    			System.out.println("File sent to client!");
    		}
    		//calculate and display the number of bytes
    	}
    	catch(IOException ex)
    	{
    		ex.printStackTrace();
    	}
    	return result;
    }
}
