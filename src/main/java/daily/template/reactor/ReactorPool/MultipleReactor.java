package daily.template.reactor.ReactorPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
//Basic Reactor Design
//Single threaded version
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//reactor 1: setup
public class MultipleReactor implements Runnable {
	
	public static void main(String args[] ) throws IOException {
		MultipleReactor server = new MultipleReactor(8000);
		new Thread(server).start();
	}
	
	final Selector selector;
	final ServerSocketChannel serverSocketChannel;
	
	final int DEFAULT_WORKER_SIZE = 10;
	Selector[] selectors;
	int next = 0;
	ExecutorService workerpool = Executors.newFixedThreadPool(DEFAULT_WORKER_SIZE);
	
	MultipleReactor(int port) throws IOException {
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
		SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		selectionKey.attach(new Acceptor());
		selectors = new Selector[DEFAULT_WORKER_SIZE];
		for(int i=0;i<DEFAULT_WORKER_SIZE;i++) {
			final Selector selector = Selector.open();
			selectors[i] = selector;
			workerpool.execute(new Runnable () {
				@Override
				public void run() {
					try {
						while(!Thread.interrupted()) {
							selector.select(500);
							Set set = selector.selectedKeys();
							Iterator it = set.iterator();
							while(it.hasNext()) 
								dispatch((SelectionKey)(it.next()));
							set.clear();
						}
					} catch(IOException ex) {
					}
				}
			});
		}
		
	}
	
//reactor 2: dispatch loop
	public void run() { //normally in a new Thread
		try {
			while(!Thread.interrupted()) {
				selector.select(500);
				Set set = selector.selectedKeys();
				Iterator it = set.iterator();
				while(it.hasNext()) 
					dispatch((SelectionKey)(it.next()));
				set.clear();
			}
		} catch(IOException ex) {
			
		}
	}
	
	void dispatch(SelectionKey selectionKey) {
		Runnable r = (Runnable) (selectionKey.attachment());
		if(r!=null) 
			r.run();
	}
	
	
//reactor 3: acceptor
	class Acceptor implements Runnable {//inner
		public synchronized void run() {
			SocketChannel socketChannel=null;
			try {
				socketChannel = serverSocketChannel.accept();
			} catch (IOException e1) {
			}
			if(socketChannel !=null)
				try {
					new Handler(selectors[next], socketChannel);
				} catch (IOException e) {
				}
			if(++next==selectors.length) next= 0;
		}
	}
}
class Handler implements Runnable {
	
	int MAXIN = 1024;
	int MAXOUT = 1024;
	final SocketChannel socketChannel;
	final SelectionKey selectionKey;
	ByteBuffer input = ByteBuffer.allocate(MAXIN);
	ByteBuffer output = ByteBuffer.allocate(MAXOUT);
	static final int READING = 0, SENDING = 1;
	int state = READING;
	
	Handler(Selector selector, SocketChannel socketChannel) throws IOException {
		this.socketChannel = socketChannel;
		socketChannel.configureBlocking(false);
		//Optionally try first read now
		selectionKey = socketChannel.register(selector, 0);
		selectionKey.attach(this);
		selectionKey.interestOps(SelectionKey.OP_READ);
		selector.wakeup();
	}
	
	public void run() {
		try {
			if(state == READING) 
				read();
			else if(state == SENDING) 
				send();
		} catch(IOException ex) {
			
		}
	}
	
	void send() throws IOException {
		output = output.put("send()".getBytes());
		socketChannel.write(output);
		if(outputIsComplete()) 
			selectionKey.cancel();
	}
	
	//uses util.concurrent thread pool
	static ExecutorService pool = Executors.newFixedThreadPool(10);
	static final int PROCESSING = 3;
	
	synchronized void read() throws IOException {
		socketChannel.read(input);
		byte[] requestBytes = new byte[1024];
		input.flip();
		input.get(requestBytes, 0, input.limit());
		System.out.println(new String(requestBytes));
		if(inputIsComplete()) {
			state = PROCESSING;
			pool.execute(new Processer());
		}
	}
	
	class Processer implements Runnable {
		public void run() {
			processAndHandOff();
		}
	}
	
	synchronized void processAndHandOff() {
		process();
		state = SENDING;//or rebind attachment
		selectionKey.interestOps(SelectionKey.OP_WRITE);
	}
	
	boolean inputIsComplete() {
		System.out.println("inputIsComplete()");
		return true;
	}
	
	boolean outputIsComplete() {
		System.out.println("outputIsComplete()");
		return true;
	}
	
	void process() {
		System.out.println("process()");
	}
	
}
