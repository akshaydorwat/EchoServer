package server;

class EchoTask extends Thread{
	String msg;
	EchoTask(String msg){
		this.msg = msg;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Client :"+msg);
	}
}

