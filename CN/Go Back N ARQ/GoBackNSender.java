/** Java program to implement Go Back N ARQ - Sender side
  *
  * Input: -Number of frames to be sent
  *
  * Output: -Text describing the frames sent/not sent, ACKs/NAKs received and sender window.
  *
  * Author: Manav Sanghavi
  */

import java.net.*;
import java.io.*;
import java.util.*;

class Communicator {
	PrintWriter out;
	BufferedReader in;
	// Create objects to read and write strings to the I/O stream.
	Sender sender;
	Receiver receiver;
	// Create the sender and receive Thread objects, which will run concurrently.
	int frame[];
	// Create the frame array to store the frames to be sent.
	int window_start, window_end, window_size;
	// 'window_start' indicates the value of the start of the window.
	// 'window_end' indicates the value of the end of the window.
	// 'window_size' indicates the value of the size of the window.
	int current_frame;
	// 'current_frame' will be used by the sender to keep track of which frame is to be sent.
	boolean isActive;
	// 'isActive' is a boolean value which will be used to make sure that the window is displayed
	// correctly.
	boolean isFinished;
	// 'isFinished' is a boolean value which will tell the sender to stop sending and shut down.
	
	Communicator(BufferedReader br, PrintWriter pw) {
		in = br;
		out = pw;
		// Call the getFrames() method to initialize the frames.
		getFrames();
		current_frame = 0;
		isActive = false;
		isFinished = false;
		// Set initial values for the variables.
		sender = new Sender();
		receiver = new Receiver();
		// Create the sender and receiver thread objects.
		sender.start();
		receiver.start();
		// Start the sender and receiver threads, running concurrently.
	}
	
	class Sender extends Thread {
		// The Sender class sends the frames to the client.
		public void run() {
			Random r = new Random();
			// 'r' will be used to randomly decide if frames will be sent or not.
			while(!isFinished) {
				// This loop runs while the window has frames left to be sent.
				if(current_frame >= window_end) {
					// The sender has sent frames till its maximum limit, so it must now wait.
				} else {
					if(r.nextInt(10) < r.nextInt(10)) {
						while(isActive) {
							// wait.
						}
						System.out.println("Sent " + current_frame);
						out.println(frame[current_frame]);
					} else {
						while(isActive) {
							// wait.
						}
						System.out.println("Not sent " + current_frame);
					}
					current_frame++;
					// In the next iteration of this loop, the next frame will be sent.
					while(isActive) {
						// wait.
					}
					showWindow();
				}
			}
		}
	}
	
	class Receiver extends Thread {
		// Receiver class is responsible for receiving the ACKs and NAKs.
		public void run() {
			String s = new String();
			// 's' will store the ACK/NAK.
			int i;
			// 'i' stores the frame value sent in the ACK/NAK.
			while(true) {
				// Receive ACKs/NAKs.
				Timer timer = new Timer(); 
				// Create a timer object every iteration of this loop, so we can delete and
				// reschedule it in the next iteration.
				timer.schedule(new TimerTask() {
					public void run() {
						// If the timer runs out, then all frames sent from the sender or all
						// ACKs/NAKs sent by the receiver have been lost, so we resend everything.
						System.out.println("Time out, restarting from the start of the window.");
						current_frame = window_start;
					}
				}, 200, 200);
				// timer.schedule(TimerTask task, int delay, int repeat);
				// 'task' is the action to be performed after the timer runs out.
				// 'delay' is the amount of time in milliseconds after which 'task' is executed.
				// 'repeat' (if it is not left empty) specifies the amount of time after which 
				// 'task' repeats itself till it is cancelled.
				try {
					s = in.readLine();
					// Read the ACK/NAK. It is sent in the format "ACK0" or "NAK2" etc., meaning
					// ("ACK/NAK" + frame_number).
				} catch(IOException e) {
					e.printStackTrace();
				}
				// Since we have received an ACK/NAK we can stop the timer we have scheduled.
				timer.cancel();
				// 'i' will store the frame number sent in the ACK.
				i = Integer.parseInt(s.substring(3));
				s = s.substring(0,3);
				while(isActive) {
					// wait.
				}
				if(s.equalsIgnoreCase("ACK")) {
					// In case of an ACK:
					while(isActive) {
						// wait.
					}
					System.out.println("Got an ACK for " + i);
					// Since we only receive ACK for the first frame of the window, we increment
					// 'window_start' by 1.
					window_start++;
					// We will also increment 'window_end', as long as it does not exceed the
					// number of frames to be sent.
					if(window_end < frame.length) {
						window_end++;
					}
					// If we have received ACK containing frame number equal to number of frames
					// we have sent, then it means that the receiver has received all frames.
					if(i == frame.length) {
						// We set 'isFinished' to true so that the sender thread knows that it can
						// stop sending.
						isFinished = true;
						// We will also break out of the while(true) loop of the receiver.
						break;
					}
				} else {
					// In case of a NAK:
					while(isActive) {
						// wait.
					}
					System.out.println("Got a NAK for " + i);
					// Since the sender simply sends the frame stored in 'current_frame', we will
					// set the 'current_frame' value to the NAK frame, so that the sender can
					// start resending from that frame onwards.
					current_frame = i;
					while(isActive) {
						// wait.
					}
					System.out.println("Restarting from " + i);
				}
			}
		}
	}
	
	void getFrames() {
		// getFrames() method is used to separate the frame initiation code from the other code.
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the number of frames to be sent:");
		frame = new int[scan.nextInt()];
		for(int i=0 ; i<frame.length ; i++) {
			frame[i] = i;
		}
		getWindowSize();
	}
	
	void getWindowSize() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the window size:");
		window_size = scan.nextInt();
		window_start = 0;
		// Initially, window_start will be at the first frame.
		window_end = window_size;
		// Initially, window_end will equal to the window size.
		// The window size will remain constant throughout, except at the end when the frames
		// have finished.
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

class GoBackNSender {
	public static void main(String args[]) {
		// Create the socket objects.
		ServerSocket server_socket = null;
		Socket client_socket = null;
		try {
			// Initialize the sockets.
			server_socket = new ServerSocket(7777);
			client_socket = server_socket.accept();
		} catch(IOException e) {
			e.printStackTrace();
		}
		// Create the I/O stream objects.
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			// Initialize the I/O stream objects.
			out = new PrintWriter(client_socket.getOutputStream(), true);
			// PrintWriter's constructor used is (InputStream, boolean auto_flush), with auto_flush
			// set to true so that the string written to the stream is sent immediately.
			in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
		} catch(IOException e) {
			e.printStackTrace();
		}
		// Create and initialize the Communicator object.
		Communicator comm = new Communicator(in, out);
	}
}

/*

Output:
Enter the number of frames to be sent:
9
Enter the window size:
5
Sent 0
Window: 0 1 2 3 4
Sent 1
Got an ACK for 0
Window: 1 2 3 4 5
Got an ACK for 1
Not sent 2
Window: 2 3 4 5 6
Not sent 3
Window: 2 3 4 5 6
Not sent 4
Window: 2 3 4 5 6
Sent 5
Window: 2 3 4 5 6
Sent 6
Got a NAK for 2
Window: 2 3 4 5 6
Sent 2
Restarting from 2
Window: 2 3 4 5 6
Got a NAK for 2
Restarting from 2
Not sent 3
Window: 2 3 4 5 6
Not sent 3
Window: 2 3 4 5 6
Sent 4
Got an ACK for 2
Window: 3 4 5 6 7
Got a NAK for 3
Restarting from 3
Sent 5
Window: 3 4 5 6 7
Got an ACK for 3
Not sent 4
Window: 4 5 6 7 8
Sent 5
Window: 4 5 6 7 8
Not sent 6
Window: 4 5 6 7 8
Not sent 7
Window: 4 5 6 7 8
Got a NAK for 4
Restarting from 4
Sent 4
Window: 4 5 6 7 8
Got an ACK for 4
Sent 5
Window: 5 6 7 8
Got an ACK for 5
Sent 6
Window: 6 7 8
Not sent 7
Window: 6 7 8
Got an ACK for 6
Not sent 8
Window: 7 8
Time out, restarting from the start of the window.
Sent 7
Window: 7 8
Got an ACK for 7
Not sent 8
Window: 8
Time out, restarting from the start of the window.
Sent 8
Window: 8
Got an ACK for 8
Got an ACK for 9

*/