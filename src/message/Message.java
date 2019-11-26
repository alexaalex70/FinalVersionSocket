package message;
import java.net.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List; 

public class Message {

	private String name;
	private String topicName;
	private boolean isTopicReg;
	private boolean isMessageQueue;
	private Socket toSocket;
	private String body;
	private List<String> recipients = new ArrayList<String>();
	private int timeToDelete;
	private long createDateInMs;
	
	public Message(String name, String topic, String type, String body, int time) {
		this.name = name;
		this.isTopicReg = type.equals("topic") ? true : false;
		this.topicName = topic;
		this.body = body;
		this.timeToDelete = time;
		this.createDateInMs = System.currentTimeMillis();
	}
	
	public Message(String name, String type, String body, List<String> recipients) {
		this.name = name;
		this.isMessageQueue = type.equals("message") ? true : false;
		this.body = body;
		this.recipients = recipients;
	}
	
	public int getTimeToDelete() {
		return this.timeToDelete;
	}
	
	public long getCreatedTime() {
		return this.createDateInMs;
	}
	
	public boolean isTopicReg() {
		return this.isTopicReg;
	}
	
	public String getTopicName() {
		return this.topicName;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<String> getRecipients() {
		return this.recipients;
	}
	
	public boolean isMessage() {
		return this.isMessageQueue;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public Socket getSocket() {
		return this.toSocket;
	}
}