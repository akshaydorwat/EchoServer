package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

class EchoServerHandler implements EventHandler {
	SocketChannel socketChannel;
	TaskExecuter exe;
	ByteBuffer  inputBuffer;
	//CharBuffer cBuff;
	public EchoServerHandler(SocketChannel socketChannel, TaskExecuter exe) {
		// TODO Auto-generated constructor stub
		this.socketChannel = socketChannel;
		this.exe = exe;
	}
	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public eventStatus handleEvent() {
		// TODO Auto-generated method stub
		inputBuffer = ByteBuffer.allocate(2048);
		try {
			int bytesRead = socketChannel.read(inputBuffer);
			if(bytesRead != -1){
				String msg = receive();
				exe.enqueue(new EchoTask(msg));
				if( containsEndOfLine(msg)){
					close();
					return eventStatus.Event_Chaneel_close;
				}else{
					return eventStatus.Event_Successful;
				}
				
			}else
			{
				return eventStatus.Event_Chaneel_Force_close;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			close();
			return eventStatus.Event_Chaneel_Force_close;
		}
	}
	
	private String receive(){
		inputBuffer.flip();
		byte[] buffer = new byte[inputBuffer.limit()];
		inputBuffer.get(buffer);
		inputBuffer.clear();
		return(new String(buffer));
	}
	
	private boolean containsEndOfLine(String msg){
		if(msg.contains("\n")){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public eventStatus close() {
		// TODO Auto-generated method stub
		try {
			if(socketChannel.isOpen() && socketChannel != null)
				socketChannel.close();
			return eventStatus.Event_Chaneel_close;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return eventStatus.Event_Chaneel_Force_close;
		}
	}
	
	@Override
	public SelectableChannel getHandle() {
		// TODO Auto-generated method stub
		return socketChannel;
	}

}