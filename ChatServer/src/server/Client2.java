package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {
	static String receivedMessage;
	static String sentMessage;
	
	
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in); //tells the scanner that it will be taking input from the keyboard
		Socket socket = new Socket("localhost", 5100); //Currently trying to connect to our socket, will succeed
		String greeting = "Jay has entered the room";
			
		//My understanding of this output stream (these 4 lines) isn't fully correct I believe.	
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		out.println(greeting); 
		out.flush();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String welcome = br.readLine();
		
		System.out.println(welcome);
		
		
		//Prints out info from other clients
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					try {
						receivedMessage = br.readLine();
						if(!receivedMessage.isEmpty() || receivedMessage != sentMessage){
							System.out.println("Message:" + receivedMessage );
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					sentMessage = scanner.nextLine();
					out.println(sentMessage);
					out.flush();
				}
				
				
				
			}
			
		}).start();		  	 
	   
	}
	
}
