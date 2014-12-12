package blackdoor.chattap.shared;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import blackdoor.crypto.Hash;
import blackdoor.util.DBP;

public class Message {
	private String body;
	private Message lastMessage;
	private Message nextMessage;
	private String sender;
	private byte[] chainHash = null;
	public static final Message ZEROTH_MESSAGE = new Message("", "");
	
	public Message(String body, String sender) {
		this.body = body;
		this.sender = sender;
	}
	
	
	
	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	@SuppressWarnings("deprecation")
	public byte[] getMessageHash(){
		try {
			return Hash.getSHA1((body + sender).getBytes(Settings.PASSWORD_ENCODING));
		} catch (UnsupportedEncodingException e) {
			DBP.printException(e);
		}
		return null;
	}
	
	public byte[] getChainHash(){
		if(chainHash == null){
			if(lastMessage.equals(ZEROTH_MESSAGE)){
				chainHash = getMessageHash();
			}else{
				byte[] appended = Arrays.copyOf(lastMessage.getChainHash(), 128*2);
				System.arraycopy(getMessageHash(), 0, appended, 128, 128);
				chainHash = Hash.getSHA1(appended);
			}
		}
		return chainHash;
	}
	
	/**
	 * @return the lastMessage
	 */
	public Message getLastMessage() {
		return lastMessage;
	}

	/**
	 * @param lastMessage the lastMessage to set
	 */
	public void setLastMessage(Message lastMessage) {
		this.lastMessage = lastMessage;
	}

	/**
	 * @return the nextMessage
	 */
	public Message getNextMessage() {
		return nextMessage;
	}

	/**
	 * @param nextMessage the nextMessage to set
	 */
	public void setNextMessage(Message nextMessage) {
		this.nextMessage = nextMessage;
	}
}
