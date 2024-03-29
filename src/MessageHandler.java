import java.io.IOException;
import message.Message;

public class MessageHandler extends Thread {
	@Override
	public void run() {
		System.out.println("Message Dispatcher Started");
		
		while(true) {
			Message currentMessage = Server.mQ.dequeue();
			
			if(currentMessage != null) {
				for(ClientHandler client : Server.connectedUsers) {
					System.out.println("Current sender:" + " " + currentMessage.getName());
					System.out.println("Current receiver:" + " " + client.getUserName());
					System.out.println("Message body:" + " " + currentMessage.getBody());
					System.out.println(currentMessage.getRecipients());
					if(currentMessage.getRecipients().contains(client.getUserName())) {
						try {
							client.dos.writeUTF(currentMessage.getName() + ":" + currentMessage.getBody()
												+"(on all with MessageQueue)");
						} catch (IOException e) {
						  e.printStackTrace();
						}
					}
					
				}
			}
		}
	}
}