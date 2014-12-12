/**
 * 
 */
package blackdoor.chatapp.client;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

import blackdoor.chattap.shared.Settings;
import blackdoor.crypto.Hash;
import blackdoor.util.DBP;

/**
 * @author nfischer3
 *
 */
public class Connector {
	ChatStream chatStream;
	Writer submissionStream;
	String room;
	byte[] password;
	InetAddress server;
	int port;
	String name;
	Socket connection;
	
	private Connector() throws IOException{
		connection = connectToServer(server, port, 5);
		chatStream = new ChatStream();
	}
		
	static Socket connectToServer(InetAddress server, int port, int tryout) throws IOException{
		final int sleep = 10;
		Socket connection;
		try {
			connection = new Socket(server, port);
		} catch (IOException e) {
			if(tryout > 0){
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e1) {
					DBP.printException(e1);
				}
				return connectToServer(server, port, tryout-1);
			}else{
				throw e;
			}
		}
		return connection;
	}
	
	public Reader getChatStream(){
		return chatStream;
	}
	
	public void post(String message){
		
	}
	
	public static class ConnectorBuilder{
		
		String room;
		String password;
		InetAddress server;
		String name;
		int port;
		
		
		public ConnectorBuilder(){}
		
		public Connector buildConnector() throws IOException{
			Connector ret = new Connector();
			ret.room = room;
			try {
				ret.password = Hash.getSHA256(password.getBytes(Settings.PASSWORD_ENCODING));
			} catch (UnsupportedEncodingException e) {
				DBP.printException(e);
			}
			ret.server = server;
			ret.name = name;
			return ret;
		}

		
		/**
		 * @return the port
		 */
		public int getPort() {
			return port;
		}

		/**
		 * @param port the port to set
		 */
		public void setPort(int port) {
			this.port = port;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the room
		 */
		public String getRoom() {
			return room;
		}

		/**
		 * @param room the room to set
		 */
		public void setRoom(String room) {
			this.room = room;
		}

		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}

		/**
		 * @return the server
		 */
		public InetAddress getServer() {
			return server;
		}

		/**
		 * @param server the server to set
		 */
		public void setServer(InetAddress server) {
			this.server = server;
		}
		
		
		
	}
}
