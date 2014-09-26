package server;

/*
 *  The purpose of this assignment is to deepen your understanding of the Half-Sync/Half-Async pattern in the context of Java. 
 *  In particular, you will write a platform-independent server program that builds upon the solution from PA#3 
 *  for Java by using the Half-Sync/Half-Async pattern to accept a connection from one or more clients and echoes back what the client sent.
 *  There will be a pool of java threads (the java.util.concurrent package can be used for this purpose) attending IO requests. 
 *   The hints below are simply for your convenience - you can design/implement the server as you see fit,
 *   as long as you apply the designated patterns correctly/clearly.

	The server program should do the following activities:

	1) Create a java class HalfSyncPool that includes a method to perform the "half-sync" portion of the server by:

		- Dequeueing messages containing the client input that was put into its synchronized request queue via a enqueue() method, 
		  which will be protected by a monitor object (classes implementing the interface java.util.concurrent.BlockingQueue could help)
		- First sends back to the client the thread id (Thread.currentThread().getId()) that is attending the request
		- Then sends back the client's input that was in the input buffer.

	2) Create an EchoServiceHandler that inherits from ServiceHandler (implementing the handleInput() method) that:

		- Reads the client data until the end of a line is reached (i.e., the symbols "\n", "\r", or "\r\n" are read).
		- Puts the client data into a synchronous queue calling the put method to enqueue the message for subsequent processing 
		  by a thread in the pool of threads that are waiting in the synchronous queue.
		 
	3) Create an Acceptor that inherits from ServiceHandler (implementing the handleInput() method), that uses an Internet domain 
	`passive-mode'' stream socket to listen a designated port number (ServerSocketChannel)

	4) Implement a main() function that

		- Creates an object EchoTasks that spawns a pool of N threads (where N > 1) with an activate() method.
		- Creates an EchoAcceptor instance and associate it with the EchoTasks.
		- Creates an EchoReactor
		- Registers the EchoAcceptor instance with the reactor registerHandler()
		- Run the reactor's event loop reactorLoop() to wait for connections/data to arrive from a client.

	5) When a client connection request arrives, the EchoReactor will automatically call the handleInput() method of the EchoAcceptor. 
		This template method automatically accepts the connection and registers an EchoServiceHandler in the EchoReactor 
		(after the EchoAcceptor has established the connection and created the EchoServiceHandler instance).

	6) When data arrives from the client the EchoReactor will automatically call back on the EchoServiceHandler handleInput() method described above to perform the "half-async" portion of the server. Note that implementing the half-async portion properly may require multiple trips through the reactor to read each chunk of client data via a single non-blocking read() each time.

For this assignment, you can simply exit the server process when you're done, which will close down all the open sockets.
*****/

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class NIOReactorServer   {
	private Selector selector;
	private static NIOReactorServer instance = null;
	public NIOReactorServer() {
		// TODO Auto-generated constructor stub
		try {
			selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static NIOReactorServer getInstance(){
		if(instance == null){
			instance = new NIOReactorServer();
		}
		return instance;
	}
	
	
	/*This function multiplexes Acceptable & Readable event
	 * */
	protected void handleEvents() {
		while (true) {
	        try {
	            int num = selector.selectNow();
	            if (num > 0) {
	            	Set keys = selector.selectedKeys();
	        	    for (Iterator iter = keys.iterator(); iter.hasNext();) {
	        	        SelectionKey key = (SelectionKey) iter.next();
	        	        if (key.isAcceptable()) {
	        	        	callHandler(key);
	        	        } else if (key.isReadable()) {
	        	        	callHandler(key);
	        			}
	        	        iter.remove();
	        	    }
	            } 
	        } catch (IOException ioe) {
	            System.err.println("Unable to select: " + ioe.toString());
	        }
	    }
	}
	/*This functions closes the selector instance & handlers associated with Keys.
	 * */
	
	protected void callHandler(SelectionKey key ) {
		EventHandler eventHandler = (EventHandler)key.attachment();
		eventStatus es =  eventHandler.handleEvent();
		if(es.compareTo(eventStatus.Event_Chaneel_close) > 1 || es.compareTo(eventStatus.Event_Chaneel_Force_close)>1 ){
			key.cancel();
		}
		
	}
	protected void close(){
		if (selector.isOpen()){
			try {
				selector.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/* This functions call the open event of eventHandler to register Events with Reactor
	 * */
	public boolean registerHandler(EventHandler eventHandler, int eventType){
		
		try {
			eventHandler.getHandle().register(selector, eventType, eventHandler );
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public static void main(String[] args) {
		
		NIOReactorServer nrs = NIOReactorServer.getInstance();
		nrs.registerHandler(new EchoServerAcceptor(80),SelectionKey.OP_ACCEPT);
		nrs.handleEvents();
		System.out.println("Press Q to exit");
		BufferedInputStream reader = new BufferedInputStream(System.in);
		int inp;
		try {
			inp = reader.read();
			if((char)inp== 'q' ||(char)inp == 'Q'){
				nrs.close();
				System.exit(0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}