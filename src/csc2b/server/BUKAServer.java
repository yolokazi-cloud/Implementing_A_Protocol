package csc2b.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Class sets up the server for clients
 * @author Nogantsho Y 201602466
 * @version P06
 */
public class BUKAServer
{
	    private ServerSocket servSocket =  null;
    	private Boolean isConnected = false;
    	Socket socket = null;
  
    /**
     * Parametized constructor to get server running
     * @param Port to specify the port number
     */
    public BUKAServer(int Port) {
    	try {
			servSocket = new ServerSocket(Port);
			System.out.println("server running");
			isConnected = true;
			while(isConnected) {
				socket = servSocket.accept();
				System.out.println("Server has accepted a connection");
				//connecting multiple threads via BUKAhandler
				Thread thread = new Thread(new BUKAHandler(socket));  
						thread.start();
				 System.out.println("The handler has been successfully threaded");
						
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	
    }
    /**
     * helper function to get the number on which this socket is listening
     * @return port number
     */
    public int getPort() {
    	return servSocket.getLocalPort();
    }
    public static void main(String[] argv)
    {
	//Setup server socket and pass on handling of request
    	System.out.println("Waiting");
    	BUKAServer server = new BUKAServer(2001);
    	System.out.println("Waiting for connection on port " + server.getPort());
    }
}
