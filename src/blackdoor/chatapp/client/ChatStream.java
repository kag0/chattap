package blackdoor.chatapp.client;

import java.io.FilterReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.LinkedBlockingQueue;

import blackdoor.util.DBP;

public class ChatStream extends FilterReader {

	private LinkedBlockingQueue<String> queue;
	private StringReader reader = null;
	
	public ChatStream() {
		super(null);
		queue = new LinkedBlockingQueue<String>();
	}
	
	void enq(String s){
		queue.add(s);
	}
	
	public void close(){
		queue = null;
		reader.close();
		reader = null;
	}
	
	public boolean markSupported(){
		return false;
	}
	
	public int read() throws IOException{
		if(reader == null){
			try {
				reader = new StringReader(queue.take());
			} catch (InterruptedException e) {
				DBP.printException(e);
			}
		}
		int read = reader.read();
		if(read == -1){
			reader = null;
			return read();
		}
		return read;
	}
	
	public int read(char[] cbuf, int off, int len) throws IOException{
		int count = 0;
		for(int i = 0; i < len; i++){
			int read = read();
			if(read == -1){
				return -1;
			}
			count ++;
			cbuf[off + i] = (char) read;
		}
		return count;
	}
	
	public boolean ready() throws IOException{
		return reader.ready();
	}
	
	public void reset(){
		this.close();
		queue = new LinkedBlockingQueue<String>();
	}
	
	public long skip(long n) throws IOException{
		return reader.skip(n);
	}
	
	

}
