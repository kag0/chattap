package blackdoor.chattap.shared;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MessageHistory {
	
	private ConcurrentMap<byte[], Message> history;
	private Message origin = null;
	private Message head = null;
	
	public MessageHistory() {
		history = new ConcurrentHashMap<byte[], Message>();
	}
	
	public Message getMessage(byte[] hash){
		return history.get(hash);
	}
	
	public Message getLatestMessage(){
		return head;
	}
	
	public Message getFirstMessage(){
		return head;
	}
	
	public void addMessage(Message m){
		if(origin == null){
			origin = m;
			head = m;
			m.setLastMessage(Message.ZEROTH_MESSAGE);
		}else{
			head.setNextMessage(m);
			m.setLastMessage(head);
			head = m;
		}
		history.put(m.getChainHash(), m);
	}

}
