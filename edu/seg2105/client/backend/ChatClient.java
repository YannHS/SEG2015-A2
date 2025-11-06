// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
 * @throws IOException 
   */
  public void handleMessageFromClientUI(String message) throws IOException
  {
    
	// Check if the message is a command
	if (message.startsWith("#")) {
		// this is a command
		switch(message.split(" ")[0]) {
		case "#quit":
			quit();
			break;
		case "#logoff":
			if (this.isConnected()) {
			      closeConnection();					
			} else {
				clientUI.display("Already logged off.");
			}
			break;
			
		case "#login":
			if (! this.isConnected()) {
			      openConnection();					
			} else {
				clientUI.display("Already logged in.");
			}
			break;
			
		case "#gethost":
			clientUI.display(this.getHost());
			break;
			
		case "#getport":
			clientUI.display(String.valueOf(this.getPort()));
			break;
			
		case "#setport":
			if (! this.isConnected()) {
				try {
					this.setPort(Integer.parseInt(message.split(" ")[1]));
					clientUI.display("Port set to " + this.getPort());
				}
				catch (IndexOutOfBoundsException e) {
					clientUI.display("Port number needed.");
				}				
			} else {
				clientUI.display("Cannot change port when logged in.");
			}
			break;
			
		case "#sethost":
						
			if (! this.isConnected()) {
				try {
					this.setHost(message.split(" ")[1]);
					clientUI.display("Hostname set to " + this.getHost());
				}
				catch (IndexOutOfBoundsException e) {
					clientUI.display("Hostname needed.");
				}				
			} else {
				clientUI.display("Cannot change hostname when logged in.");
			}
			break;
			
		default:
			clientUI.display("Invalid command.");
		}
	} else {  
		// message is not a command, send it to the server
		try
	    {
	      sendToServer(message);
	    }
	    catch(IOException e)
	    {
	      clientUI.display
	        ("Could not send message to server.");
    }
	}
  }
  
  public void connectionException(Exception exception) {
	  clientUI.display 
      ("Server disconnected.");
	}
  
  public void connectionClosed() {
	  clientUI.display 
      ("Server disconnected.");
	}
  
  
  public void connectionEstablished() {
	  clientUI.display 
      ("Server connected.");
  }
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
