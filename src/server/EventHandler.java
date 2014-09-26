package server;

import java.nio.channels.SelectableChannel;
 
interface EventHandler {
	
	public boolean open();
	public eventStatus handleEvent();
	public SelectableChannel getHandle();
	public eventStatus close();
}
