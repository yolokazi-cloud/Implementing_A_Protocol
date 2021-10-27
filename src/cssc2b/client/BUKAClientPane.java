package cssc2b.client;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
/**
 * Class for giving value for all GUI variables
 * @author Nogantsho, Y 201602466
 * @version P06
 */
public class BUKAClientPane extends GridPane
{
	PrintWriter pw;
	BufferedReader br;
	
	DataInputStream dis;
	DataOutputStream dos;
	Socket clientSocket;
	//GUI 
	
	Button btnAuth;
	Button btnList;
	TextArea textList;
	Label lblID;
	TextField txtID;
	Button btnPDFRet;
	Button btnLogout;
	TextField txtAuth;
	Label lblAuth;
	TextArea textResponse;
	String[] Files;
	 String fileTogetName ="";
	 /**
	  * parametized constructor allows connections to a specific host and port as well implements GUI elements
	  * @param primaryStage 
	  */
    public BUKAClientPane(Stage primaryStage)
    {
	//Create client connection
    	try {
    		clientSocket = new Socket("localhost", 2001);
			pw = new PrintWriter(clientSocket.getOutputStream());
		    br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    	} catch (IOException e) {
			
			e.printStackTrace();
		}
    	createGUI();
    	//Use buttons to send commands
    	//allowing authorization
   	 btnAuth.setOnAction(e->{
   		 
   		 sendcommand(pw,"AUTH "+ txtAuth.getText());
   	    try {
   	    	txtAuth.appendText(br.readLine()+ "\r\n");
   	    	
   	    }catch(IOException e2) {
   	    	e2.printStackTrace();
   	    }
   			 
   		
   	 });
   	 //returning a list
   	 btnList.setOnAction(e->{
   		 sendcommand(pw,"LIST");
   		 String response="";
   		 response = readResponse(br);
   		 System.out.println(response);
   		 Files = response.split("#");
   		 for(int i=0; i<Files.length; i++) {
   			 textList.appendText(Files[i] + "\n");
   		 }
   		 
   	 }); 
   	 //returning a pdf file
   	 btnPDFRet.setOnAction(e->{
   		//retrieve text int in the textfield inserted by user
   		 int intToRetrieve = Integer.parseInt(txtID.getText());
   		 pw.println("PDFRET" +intToRetrieve);
   		 pw.flush();
   		 
   		 //server responds with file and the file size
   		 String response ="";
   		 
   		 try {
   			 response = br.readLine();
   			 int fileSize = Integer.parseInt(response);
   			 textList.appendText("File received size " +response);
   			 
   			 //getting file from the list
   			 for(String s: Files) {
   				 StringTokenizer tok = new StringTokenizer(s);
   				 String id = tok.nextToken();
   				 String name = tok.nextToken();
   				 if(id.equals(txtID)) {
   					
   					 fileTogetName = name;
   				 }
   			 }
   			 File fileToReturn = new File("data/client/"+ fileTogetName);
   			 FileOutputStream fos =null;
   			 fos = new FileOutputStream(fileToReturn);
   			 byte[] buffer = new byte[1024];
   			 int n = 0;
   			 int totalbytes =0;
   			 while(fileSize !=totalbytes) {
   				 n= dis.read(buffer,0,buffer.length);
   				 fos.write(buffer,0,n);
   				 fos.flush();
   				 totalbytes +=n;
   				 
   			 }
   			 fos.close();
   			 System.out.println("File saved on client side!");
   		 }catch(IOException e3) {
   			 e3.printStackTrace();
   		 }
   		 
   	 });
    }
    public void createGUI() {
    	setVgap(10);
    	setHgap(10);
    	setAlignment(Pos.CENTER);
       
    	HBox hbox = new HBox();
    	hbox.setPadding(new Insets(0,5,5,5));
     	VBox vbox = new VBox();
    //Create buttons for each command
    	
     	//setting up the login elements
    	 lblAuth = new Label("Please Insert Username " + "\n" +" Username and Password");
         lblAuth.setFont(Font.font("Verdana", 15));
    	 txtAuth = new TextField(); 
    	 hbox.getChildren().add(lblAuth);
       	 hbox.getChildren().add(txtAuth);
     	
    	btnAuth = new Button("AUTH");
    	 btnAuth.setFont(Font.font("Verdana", 15));
    	 btnAuth.setPrefSize(155, 50);
    	 btnAuth.setEffect(getEffect());
    	 vbox.getChildren().add(btnAuth);
    
 
    	 btnList = new Button("LIST");
    	 btnList.setFont(Font.font("Verdana", 15));
    	 btnList.setPrefSize(155, 50);
    	 btnList.setEffect(getEffect());
    	 vbox.getChildren().add(btnList);
    	 vbox.getChildren().add(new Label("     "));
    	
    	 //setting up elements for retrieving list via ID
    	 lblID = new Label("Please Insert PDF ID");
    	 lblID.setFont(Font.font("Roman New Times",18));
    	 txtID = new TextField();
    	 vbox.getChildren().add(lblID);
    	 vbox.getChildren().add(txtID);
    	
    	 btnPDFRet = new Button("PDFRET");
    	 btnPDFRet.setFont(Font.font("Verdana", 15));
    	 btnPDFRet.setPrefSize(155, 50);
    	 btnPDFRet.setEffect(getEffect());
    	 vbox.getChildren().add(btnPDFRet);
    	 
    	 btnLogout = new Button("LOGOUT");
    	 btnLogout.setFont(Font.font("Verdana", 15));
    	 btnLogout.setPrefSize(155, 50);
    	 btnLogout.setEffect(getEffect());
    	 vbox.getChildren().add(btnLogout);
    
    	//add the vbox to hbox
    	hbox.getChildren().add(vbox);
    	//add hbox
    	add(hbox,0,4,1,1);
    	
    	//adding list  area
    	textList = new TextArea();
    	textList.setMaxSize(300, 400);
    	add(textList,0,3,1,1);
    	
    	//adding response area
    	textResponse = new TextArea();
    	textResponse.setMaxSize(300, 400);
    	add(textResponse,0,2,1,1);
    }
    /**
     * helper function helps with sending commands from the user
     * @param pw helps with input from output stream
     * @param command specific command inserted
     */
    private void sendcommand(PrintWriter pw, String command) {
    	pw.println(command);
    	pw.flush();
    	
    }
    /**
     * helper method helps read response from the input stream
     * @param br reads exact what we get from the stream line
     * @return a string
     */
    private String readResponse(BufferedReader br) {
    	String res =" ";
    	try {
    		res= br.readLine();
    		
    	}catch(IOException ex) {
    		ex.printStackTrace();
    	}
    	return res;
    }
}
