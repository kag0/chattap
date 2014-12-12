package blackdoor.chatapp.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import blackdoor.chattap.shared.Message;
import blackdoor.chattap.shared.SocketIOWrapper;
import blackdoor.util.DBP;

public class LongPoller implements Runnable {
	
	private Socket connection;
	private InetAddress server;
	private int port;
	public volatile boolean polling = true;
	private String head = null;
	private Connector c;
	private Random r;
	
	public LongPoller(Connector c){
		this.server = c.server;
		this.port = c.port;
		this.c = c;
		r = new Random();
	}
	
	@Override
	public void run() {
		try {
			connection = Connector.connectToServer(server, port, 10);
			while(polling){
				requestMessages();
			}
			connection.close();
		} catch (IOException e) {
			DBP.printException(e);
			throw new FatalException("Can not continue with server: " + e.getMessage());
		}
	}
	
	private JSONObject getMessageRequest(){
		JSONObject request = new JSONObject();
		JSONObject params;
		request.put("jsonrpc", "2.0");
		request.put("method", "update");
		params = new JSONObject();
		params.put("room", c.room);
		params.put("latest", head == null ? JSONObject.NULL : head);
		request.put("params", params);
		request.put("id", r.nextInt());
		return request;
	}
	
	private void requestMessages() throws IOException{
		Queue<Message> messageQ;
		SocketIOWrapper io;
		JSONObject request = getMessageRequest();
		JSONObject reply = null;
		try {
			io = new SocketIOWrapper(connection);
			io.write(request.toString());
			reply = new JSONObject(io.read());//TODO catch exception on malformed JSON
		} catch (IOException e) {
			connection = Connector.connectToServer(server, port, 10);
			requestMessages();
			return;
		}
		if(reply.has("result") && reply.getInt("id") == request.getInt("id")){
			JSONArray result = reply.getJSONArray("result");
			messageQ = new ArrayBlockingQueue<Message>(35);
			Message m;
			JSONObject serialM;
			for(int i = 0; i < result.length(); i++){
				serialM = result.getJSONObject(i);
				m = new Message(serialM.getString("body"), serialM.getString("sender"));
				if(!messageQ.offer(m))
					break;
				head = serialM.getString("hash");
			}
			writeMessages(messageQ);
		}else{
			//TODO handle server not responding positively
		}
	}
	
	private void writeMessages(Queue<Message> messageQ) throws IOException{
		for(Message m = messageQ.poll(); m != null; m = messageQ.poll()){
			String out = String.format("[%-15s] ", m.getSender());
			out += " " + m.getBody();
			c.chatStream.enq(out);
		}
	}
	
}
