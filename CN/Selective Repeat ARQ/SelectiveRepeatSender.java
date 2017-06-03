/** Java Program to implement Selective Repeat ARQ - Sender side
  *
  * Input: - Number of frames to be received, which is same as the value on the Receiver side
  *
  * Output: - Text describing sent/resent frames, frames failed to be sent/resent and received
  *           ACKs/NAKs.
  *
  * Author: Manav Sanghavi
  */

import java.io.*;
import java.net.*;
import java.util.*;

// Communicator class is a combination of the Sender and Receiver classes.
// The Sender and Receiver classes are defined inside the Communicator class so that all methods
// of the Sender and Receiver classes can access specific instances of the objects used by a
// Communicator object.
class Communicator {
	ObjectOutputStream out;
	ObjectInputStream in;
	// Define the I/O streams. Object Input and Output streams are used so that we can send any
	// objects over the network.
	Sender sender;
	Receiver receiver;
	// Creating the sender and receiver objects of the respective classes.
	int frame[];
	// 'frame' is an array which stores the frames to be sent.
	int current_frame;
	// 'current_frame' stores the value of the frame which is being sent.
	int window_start, window_end, window_size;
	// 'window_start' indicates the value of the start of the window
	// 'window_end' indicates the value of the end of the window
	// 'window_size' indicates the value of the size of the window
	Random r;
	// 'r' is used to randomly determine which frames are sent and which frames are lost.
	boolean isActive;
	// 'isActive' is used to make sure that the showWindow() method is not active twice.
	
	Communicator(ObjectInputStream ois, ObjectOutputStream oos) {
		// Constructor to instantiate the variables used.
		in = ois;
		out = oos;
		sender = new Sender();
		receiver = new Receiver();
		current_frame = 0;
		isActive = false;
		r = new Random();
		getFrames();
	}
	
	void getFrames() {
		// getFrames() method is used to separate the frame initiation code from the other code.
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the number of frames to be sent:");
		int n = scan.nextInt();
		// 'n' is the number of frames to be sent.
		frame = new int[n];
		getWindowSize();
		for(int i=0 ; i<n ; i++) {
			frame[i] = i;
			// Initializing the values of the frames. For simplicity, we simply store the frame
			// number inside the frames. However, we can replace the 'frame' array with any object
			// and send it via the Object I/O streams.
		}
		sender.start();
		receiver.start();
		// Starting the sender and receiver threads.
	}
	
	void getWindowSize() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the window size (max value=" + (frame.length/2) + "):");
		window_size = scan.nextInt();
		window_start = 0;
		// Initially, window_start will be at the first frame.
		window_end = 0;
		// Initially, window_end will same as the start frame.
		// That is, the initial window will be empty.
	}
	
	class Sender extends Thread {
		// Sender thread:
		public void run() {
			while(current_frame < frame.length) {
				// This thread runs once, sending all the frames once, after which it quits.
				if((window_end - window_start) < window_size) {
					// The sender will only send frames while the current window size is less
					// than the maximum window size.
					if(r.nextInt(10) < r.nextInt(10)) {
						// Randomly decides whether the frame is sent or not.
						System.out.println("Sent: " + frame[current_frame]);
						// We send the 'current_frame'.
						try {
							out.writeObject(frame[current_frame]);
						} catch(IOException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("Not sent: " + frame[current_frame]);
					}
					current_frame++;
					// Next iteration of the loop sends the next frame.
					window_end++;
					// Increment the current window size.
					showWindow();
				} else {
					// We have to wait for an ACK or a NAK to shift the window and proceed.
					System.out.println("Window is at max, waiting for ACK/NAK...");
					try {
						sleep(100);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	class Receiver extends Thread {
		// Receiver thread:
		public void run() {
			String s = new String();
			// 's' is used to store the ACK/NAK sent by the client.
			int i = -1;
			// 'i' is used to store the frame number sent in the ACK/NAK.
			boolean isAck = false;
			// 'isAck' is a boolean value storing whether the received data is an ACK or a NAK.
			while(true) {
				isAck = false;
				// Initially assume it is a NAK.
				try {
					s = (String) in.readObject();
					// Read what was sent.
				} catch(IOException e) {
					e.printStackTrace();
				} catch(ClassNotFoundException e) {
					e.printStackTrace();
				}
				i = Integer.parseInt(s.substring(3));
				// Extract the frame number from the data received.
				if(i == frame.length) {
					// If we have received 'i' as number of frames sent, then we have an ACK
					// telling the server that the client has received all frames correctly.
					// Therefore this loop can stop via 'break'.
					System.out.println("Got an ACK: " + i);
					window_start = i;
					showWindow();
					System.out.println("Final frame ACK received. Sender quits.");
					break;
				}
				while(isActive) {
					// wait for the window to finish printing.
					try {
						sleep(20);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(s.substring(0,3).equals("ACK")) {
					// If the first three characters of the data is "ACK" then this is an ACK.
					System.out.println("Got an ACK: " + frame[i]);
					// If we have received an ACK, then we change the window parameters.
					window_start = i+1;
				} else {
					// Otherwise it is a NAK.
					System.out.println("Got a NAK: " + frame[i]);
					// We resend the frame as we received a NAK.
					if(r.nextInt(10) < r.nextInt(10)) {
						// There is a random chance of sending the frame successfully.
						try {
							out.writeObject(frame[i]);
						} catch(IOException e) {
							e.printStackTrace();
						}
						System.out.println("Resent " + frame[i]);
					} else {
						System.out.println("Couldn't resend " + frame[i]);
					}
					// If we have received a NAK, then we don't change the window parameters.
				}
				if(!isActive) {
					// If showWindow() method is not executing, then execute it now.
					showWindow();
				}
			}
		}
	}
	
	void showWindow() {
		// This method prints the window.
		// On the sender side, the window is a 'buffer' of sorts, storing the frames which have
		// been sent but their ACKs haven't been received yet.
		isActive = true;
		// isActive is set to true so that it doesn't run multiple times parallelly.
		System.out.print("Window: ");
		for(int i=window_start ; i<window_end ; i++) {
			System.out.print(frame[i]+" ");
		}
		System.out.println();
		isActive = false;
		// Now this method can be called again.
	}
}

class SelectiveRepeatSender {
	public static void main(String args[]) {
		// Create the socket objects.
		ServerSocket server_socket = null;
		Socket client_socket = null;
		// Initialize the sockets.
		try {
			server_socket = new ServerSocket(7777);
			client_socket = server_socket.accept();
		} catch(IOException e) {
			e.printStackTrace();
		}
		// Create input and output objects.
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		// Initialize the input and output objects.
		try {
			out = new ObjectOutputStream(client_socket.getOutputStream());
			in = new ObjectInputStream(client_socket.getInputStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		// Create and start the communicator.
		Communicator comm = new Communicator(in, out);
	}
}

/*

Output:
Enter the number of frames to be sent:
15
Enter the window size (max value=7):
7
Not sent: 0
Window: 0
Not sent: 1
Window: 0 1
Sent: 2
Window: 0 1 2
Not sent: 3
Window: 0 1 2 3
Sent: 4
Window: 0 1 2 3 4
Sent: 5
Window: 0 1 2 3 4 5
Not sent: 6
Window: 0 1 2 3 4 5 6
Window is at max, waiting for ACK/NAK...
Window is at max, waiting for ACK/NAK...
Got a NAK: 0
Resent 0
Window: 0 1 2 3 4 5 6
Got a NAK: 1
Resent 1
Window: 0 1 2 3 4 5 6
Got a NAK: 3
Resent 3
Window: 0 1 2 3 4 5 6
Got an ACK: 0
Window: 1 2 3 4 5 6
Got an ACK: 1
Window: 2 3 4 5 6
Got an ACK: 3
Window: 4 5 6
Sent: 7
Window: 4 5 6 7
Not sent: 8
Window: 4 5 6 7 8
Not sent: 9
Window: 4 5 6 7 8 9
Not sent: 10
Window: 4 5 6 7 8 9 10
Window is at max, waiting for ACK/NAK...
Got a NAK: 6
Resent 6
Window: 4 5 6 7 8 9 10
Got an ACK: 6
Window: 7 8 9 10
Sent: 11
Window: 7 8 9 10 11
Not sent: 12
Got a NAK: 8
Window: Couldn't resend 8
7 8 9 10 11 12
Not sent: 13
Window: 7 8 9 10 11 12 13
Window is at max, waiting for ACK/NAK...
Got a NAK: 9
Resent 9
Window: 7 8 9 10 11 12 13
Got a NAK: 10
Resent 10
Window: 7 8 9 10 11 12 13
Window is at max, waiting for ACK/NAK...
Window is at max, waiting for ACK/NAK...
Got a NAK: 8
Couldn't resend 8
Window: 7 8 9 10 11 12 13
Window is at max, waiting for ACK/NAK...
Window is at max, waiting for ACK/NAK...
Got a NAK: 8
Resent 8
Window: 7 8 9 10 11 12 13
Got an ACK: 8
Window: 9 10 11 12 13
Sent: 14
Window: 9 10 11 12 13 14
Got a NAK: 12
Resent 12
Window: 9 10 11 12 13 14
Got a NAK: 13
Resent 13
Window: 9 10 11 12 13 14
Got an ACK: 12
Window: 13 14
Got an ACK: 13
Window: 14
Got an ACK: 15
Window:
Final frame ACK received. Sender quits.

*/