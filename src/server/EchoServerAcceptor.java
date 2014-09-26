package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import server.EchoServerHandler;

class EchoServerAcceptor implements EventHandler{
	
	ServerSocketChannel serverSocketChannel;
	TaskExecuter exe = new TaskExecuter(3);
	int port;
	
	public EchoServerAcceptor(int port) {
		// TODO Auto-generated constructor stub
		this.port = port;
		System.out.println("Starting Server");
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			System.out.println("Registering EchoSeverAcceptor . . . ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	/* This function opens the ServerSocketChannel, Configure it non-Blocking mode and 
	 * register the EchoServerAcceptor with Reactor for any ACCEPT events. It also attaches 
	 * the EchoServerAcceptor class object to handle ACCEPT Events 
	 * */
	public boolean open() {
		return false;
		// TODO Auto-generated method stub
	}

	@Override
	/*This function accepts the connection from client and register EchoServerHandler 
	 * with reactor for READ events. 
	 * */
	public eventStatus handleEvent() {
		// TODO Auto-generated method stub
		
			try {
				SocketChannel socketChannel = serverSocketChannel.accept();
				socketChannel.configureBlocking(false);
				System.out.println("Connection accepted successfully");
				System.out.println("Registering EchoServerHandler");
				makeSrvHandler(socketChannel);
				return eventStatus.Event_Successful;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return eventStatus.Event_Chaneel_Force_close;
			}
	}
	
	private void makeSrvHandler(SocketChannel socketChannel){
		EchoServerHandler handler = new EchoServerHandler(socketChannel, exe);
		NIOReactorServer.getInstance().registerHandler(handler, SelectionKey.OP_READ);
	}
	/* This function close the ServerSocketChannel */
	public eventStatus close() {
		// TODO Auto-generated method stub
		try {
			serverSocketChannel.close();
			exe.close();
			return eventStatus.Event_Successful;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("unable to deregister event");
			return eventStatus.Event_Exception;
		}
	}

	@Override
	public SelectableChannel getHandle() {
		// TODO Auto-generated method stub
		return serverSocketChannel;
	}
}