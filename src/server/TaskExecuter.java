package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TaskExecuter {
	private ExecutorService service;
	
	public TaskExecuter(int noOfThread ) {
		// TODO Auto-generated constructor stub
		service = Executors.newFixedThreadPool(noOfThread);
	}
	
	public void enqueue(Thread t){
		service.execute(t);
	}
	
	public void close(){
		service.shutdown();
	}
}
