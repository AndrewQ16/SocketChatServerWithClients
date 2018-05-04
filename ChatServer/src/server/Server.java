package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;




public class Server {
	protected static int iteration; //Never used 'protected', not sure if fully necessary, but to get used it at least once
	protected static ArrayList<Socket> clients; //Note for extensive purposes, this isn't thread safe
	static {
		int iteration = 0;
		clients = new ArrayList<>();
	}

	
	
	public static void main(String[] args) throws Exception {
		ServerSocket ss = new ServerSocket(8080);
		
		
		while(true) {
			Socket s = ss.accept();
			clients.add(s);
			new SocketLogic(s, iteration).start();
			System.out.println("New thread starting");
			iteration++;
			System.out.println("iteration:" + iteration);
		}
	}
}

class SocketLogic extends Thread {
	Socket socket;
	
	
	int socketIteration;
	
	
	public SocketLogic(Socket socket, int socketIteration) {
		this.socket = socket;
		this.socketIteration = socketIteration;
		
	}

	@Override
	public void run() {
		super.run();
		try {
			new Thread(new SendToAllClients(socket, socketIteration)).start();
			new Thread(new WriteToAllClients(socket, socketIteration)).start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}


//This object will take our input stream from the socket and output it to all sockets in the clients array, but not its own socket
class SendToAllClients implements Runnable {
	
	Socket socket;
	int clientNumber;
	PrintWriter out;
	BufferedReader br;
	
	
	public SendToAllClients(Socket socket, int clientNumber) throws IOException {
		this.socket = socket;
		this.clientNumber = clientNumber;
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		
	}

	@Override
	public void run() {
		while(true) {
			try {
				System.out.println("Waitint to read input from client" + (clientNumber+1) + "...");
				String messageToClients = br.readLine();
				if(!messageToClients.isEmpty()) {
					for(int i = 0; i < Server.clients.size(); i++) {
						if(i != clientNumber) {
							out = new PrintWriter(Server.clients.get(i).getOutputStream());
							out.println("From client" + (clientNumber+1) + " : " + messageToClients);
							out.flush();
						}
						
						else if(i == clientNumber) {
							out = new PrintWriter(Server.clients.get(i).getOutputStream());
							out.println("Sent: " + messageToClients);
							out.flush();
							System.out.println("Flushing from client " +  (clientNumber+1) + " : " + messageToClients);
							
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
}

//Read from all clients in array, output to one socket
class WriteToAllClients implements Runnable{
	Socket socket;
	int clientNumber;
	BufferedReader readingFromClients;
	String message;
	PrintWriter out;
	
	public WriteToAllClients(Socket socket, int clientNumber) throws IOException {
		this.socket = socket;
		this.clientNumber = clientNumber;
		out = new PrintWriter(socket.getOutputStream());
		message = new String();
		

	}
	
	@Override
	public void run() {
		while(true) {
			
			for(int i = 0; i < Server.clients.size(); i++) {
				try {
					if(i != clientNumber && !message.isEmpty()) {
						readingFromClients = new BufferedReader(new InputStreamReader(Server.clients.get(i).getInputStream()));
						message = readingFromClients.readLine();
						out.println(message);
						out.flush();
					}
					
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
}
