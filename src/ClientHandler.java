import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.util.concurrent.Semaphore;
import message.Message;

class ClientHandler extends Thread implements Runnable
{ 
	Scanner scn = new Scanner(System.in); 
	final DataInputStream dis; 
	final DataOutputStream dos; 
	private String topicName;
	private String name;
	private String type;
	private Socket socket; 
	private int numberOfTokens;
	private int timeToDelete;
	private boolean userAvailable;
	private boolean socketAvailable;
	private List<String> recipients = new ArrayList<String>();
	private Semaphore sem;
	
	// constructor 
	public ClientHandler(Socket s, String name, 
							DataInputStream dis, DataOutputStream dos, Semaphore sem) { 
		this.dis = dis; 
		this.dos = dos; 
		this.socket = s; 
		this.socketAvailable=true;
		this.sem = sem;
		this.name = name;
	} 

	private void close() {
		System.out.println("Socket disconnected");
		
		try {
			this.socket.close();
		} catch (IOException e1) {
			System.out.println("Error closing socket connection");
		}
		this.socketAvailable = false;
	} 
	
	public int getTimeToDeleete() {
		return this.timeToDelete;
	}
	
	public String getUserName() {
		return this.name;
	}
	
	public boolean getUserAvailable() {
		return this.userAvailable;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getTopicName() {
		return this.topicName;
	}
	
	@Override
	public void run() { 
		String received; 
		while (this.socketAvailable) 
		{ 
			try
			{ 
				received = dis.readUTF(); 
				
				if(received == null) {
					close();
					return;
				}
				System.out.println(name + " is waiting for a permit.");
				sem.acquire();
				System.out.println(name + " permited.");
				System.out.println("Received:" + received); 
			
				StringTokenizer st = new StringTokenizer(received, "#"); 
				String Header = st.nextToken(); 
				String Body = st.nextToken();
				
				this.recipients.clear();

				if(st.countTokens() == 1) {
					String recipient = st.nextToken();
					StringTokenizer receivers = new StringTokenizer(recipient,"-");
					int i;
					for (i = 0; i <= receivers.countTokens(); i++) {
						this.recipients.add(receivers.nextToken());
					}
				}
				System.out.println(this.recipients);
				StringTokenizer header = new StringTokenizer(Header, "-");
				this.numberOfTokens = header.countTokens();
				if(this.numberOfTokens == 2) {
					this.name = header.nextToken();
					this.type = header.nextToken();
					System.out.println("Communication choosed by user:" + this.type);
					if(type.contentEquals("message")) {
						this.userAvailable = true;
						Server.mQ.enqueue(new Message(this.name, this.type, Body , this.recipients));
					} else {
						System.out.println("Please use message as a type: client x-message#whatyouwanttowrite");
						continue;
					}
				} else if(this.numberOfTokens == 4) {
					this.name = header.nextToken();
					this.type = header.nextToken();
					this.topicName = header.nextToken();
					this.timeToDelete = Integer.parseInt(header.nextToken());
					System.out.println("Communication choosed by user:" + this.type);
					if(type.equals("topic")) {
						this.userAvailable = false;
						Server.tQ.enqueue(new Message(this.name, this.topicName, this.type, Body, this.timeToDelete));
							for(ClientHandler client : Server.connectedUsers) {
								System.out.println("Current sender:" + " " + this.name);
								System.out.println("Time to delete:" + " " + this.timeToDelete);
								System.out.println("Current receiver:" + " " + client.getUserName());
								System.out.println("Current sender topic:" + " " + this.topicName);
								System.out.println("Current receiver topic:" + " " + client.getTopicName());
								System.out.println("Message body:" + " " + Body);
								if (this.topicName.equals(client.getTopicName())) {
									try {
										client.dos.writeUTF(this.name + ":" + Body
															+ "(on " + this.topicName+")");
									} catch (IOException e) {
									  e.printStackTrace();
									}
								}
							}
					} else {
						System.out.println("Please use topic as a type: client x-topic-nametopic-timetodelete#whatyouwanttowrite");
						continue;
					}
				} else {
					System.out.println("Invalid header format.Please choose communication way: message or topic.");
					continue;
				}				
				
				Thread.sleep(8000);
				System.out.println(name + " release the permission.");
				sem.release();
				
			} catch (Exception e) { 
				if(e instanceof java.io.EOFException) {
					this.close();
				}
				
				if(e instanceof java.util.NoSuchElementException) {

					System.out.println(e.getMessage());
					System.out.println("Invalid socket format");
				}
				sem.release();
			} 
			
		} 
		try
		{ 
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 
