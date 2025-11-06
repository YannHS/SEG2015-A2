package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF serverUI; 
  
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) throws IOException
  {
    super(port);
    this.serverUI = serverUI;
    try 
    {
      this.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      serverUI.display("ERROR - Could not listen for clients!");
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  String msgString = (String) msg;
	  // check if the client is giving a login id
    if (msgString.startsWith("#login") && client.getInfo("loginID") == null) {
    	// the client is connected for the first time
    	try {
			client.setInfo("loginID", msgString.split(" ")[1]);
			System.out.println(client.getInfo("loginID") + " has logged on.");
		}
		catch (IndexOutOfBoundsException e) {
			try {client.sendToClient("Login id not found"); } catch (IOException e1) {}
			
			try{client.close();} catch (Exception f){}
		}	
    } else if (msgString.startsWith("#login") && client.getInfo("loginID") != null) {
    	// the client has connected before, but is trying to change the login id
    	try {client.sendToClient("Already logged in under a different id!"); } catch (IOException e1) {}
		try{client.close();} catch (Exception f){}
    } else {
	  
	  System.out.println("Message received: " + msg + " from " + client.getInfo("loginID"));
    this.sendToAllClients(client.getInfo("loginID") + " " + msg.toString());
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
    
  }
     
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  System.out.println
      (client.getInfo("loginID") + " has disconnected.");
  }
  
  
  public void handleMessageFromServerUI(String message) throws IOException {
	  
	// Check if the message is a command
		if (message.startsWith("#")) {
			// this is a command
			switch(message.split(" ")[0]) {
			case "#quit":
				quit();
				break;
			case "#stop":
				stopListening();
				break;
			
			case "#close":
				close();
				break;
				
			case "#start":
				if (! this.isListening()) {
					listen();
				} else {
					serverUI.display("Server already started.");
				}
				break;
								
			case "#getport":
				serverUI.display(String.valueOf(this.getPort()));
				break;
				
			case "#setport":
				if (! this.isListening()) {
					try {
						this.setPort(Integer.parseInt(message.split(" ")[1]));
						serverUI.display("Port set to " + this.getPort());
					}
					catch (IndexOutOfBoundsException e) {
						serverUI.display("Port number needed.");
					}				
				} else {
					serverUI.display("Cannot change port when logged in.");
				}
				break;
							
			default:
				serverUI.display("Invalid command.");
			}
		} else {  
			// message is not a command, broadcast it to the clients
			try
		    {
		      sendToAllClients("SERVER MSG> " + message);
		      serverUI.display
		        (message);
		    }
		    catch(Exception e)
		    {
		    	serverUI.display
		        ("Could not send message to clients.");
	    }
		}
  }
  
  
  public void quit()
  {
    try
    {
      stopListening();
    }
    catch(Exception e) {}
    System.exit(0);
  }
  
  
  //Class methods ***************************************************
  
  
}
//End of EchoServer class
