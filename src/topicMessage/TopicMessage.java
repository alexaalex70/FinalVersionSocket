package topicMessage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import message.Message;


public class TopicMessage<Message> extends Thread{
 
    private ArrayList<Message> topicQueue = new ArrayList<Message>();
 
    public synchronized void enqueue(Message msg) {
        System.out.println("Enqueued the message in topicQueue");
        this.topicQueue.add(msg);
        notify();
    }
    
    public int getSize() {
    	return topicQueue.size();
    }
    
 
    public synchronized Message dequeue() {
        while (this.topicQueue.isEmpty()) {
            try {
                System.out.println("Inside Dequeue -- Waiting");
                wait();
            } catch (Exception ex) {
                System.out.println("Exception occured in Dequeue");
            }
        }
        System.out.println("Dequeue -- Completed");
        return this.topicQueue.remove(0);
    }
    
    public ArrayList<Message> getTq() {
    	return this.topicQueue;
    }
    
    
    
//    @Override
//    public void run () {
//    	while(true) {
//
//    	if(this.topicQueue.isEmpty() == false) {
//    		for (int i=0; i<= this.topicQueue.size(); i++) {
//    			Message msg = this.topicQueue.get(i);
//
//    			if (msg.getTimeToDelete() > Server.timeout) {
//    				if(System.currentTimeMillis() - msg.getCreatedTime() >= Server.timeout) {
//    					this.topicQueue.remove(i);
//    				}
//    			} else {
//    				if(System.currentTimeMillis() - msg.getCreatedTime() >= msg.getTimeToDelete()) {
//    					this.topicQueue.remove(i);
//    				}
//    			}
//    		}
//    	}
//    	}
//    }
}