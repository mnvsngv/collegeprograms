/** Java program to implement Go Back N ARQ - Sender side
  *
  * Input: -Number of frames to be received
  *
  * Output: -Text describing the frame expected and received and the ACKs/NAKs sent.
  *
  * Author: Manav Sanghavi
  */

import java.net.*;
import java.io.*;
import java.util.*;

class Communicator2 {
	PrintWriter out;
	BufferedReader in;
	// Create objects to read from and write to the I/O stream.
	int frame[];
	// 'frame' is storing the frames received.
	int expected_frame;
	// 'expected_frame' stores the value of the frame that the receiver is expecting.
	int number_of_correct_frames;
	// 'number_of_correct_frames' stores the number of correct frames and will be used to determine
	// when the receiver will exit.
	
	Communicator2(BufferedReader br, PrintWriter pw) {
		in = br;
		out = pw;
		// Set initial values for variables:
		expected_frame = 0;
		number_of_correct_frames = 0;
		// Call the getFrames() method to initialize the 'frame' array.
		getFrames();
	}
	
	void getFrames() {
		// getFrames() method is used to separate the frame initiation code from the other code.
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the number of frames to be received:");
		frame = new int[scan.nextInt()];
		for(int i=0 ; i<frame.length ; i++) {
			// Setting each frame to -1 to indicate that it is empty.
			frame[i] = -1;
		}
	}
	
	void start() {
		int i = 0;
		// 'i' stores the frame received from the sender.
		while(number_of_correct_frames < frame.length) {
			// This loop runs while we haven't yet received all the frames correctly.
			try {
				i = Integer.parseInt(in.readLine());
			} catch(IOException e) {
				e.printStackTrace();
			}
			System.out.println("Received " + i);
			if(i == expected_frame) {
				// If the frame we received is the expected frame:
				// We will store that frame:
				frame[expected_frame] = i;
				// We will send an ACK for that frame:
				out.println("ACK" + expected_frame);
				System.out.println("Acknowledged " + i);
				// And we will increment the values for 'expected_frame' and 
				// 'number_of_correct_frames':
				expected_frame++;
				number_of_correct_frames++;
			} else {
				// Otherwise if the frame is not what we were expecting, then send a NAK containing
				// the frame number of the frame which we want:
				out.println("NAK" + expected_frame);
				System.out.println("Not acknowledged " + expected_frame);
			}
		}
		// If we have reached this point, then we have received all the frames correctly.
		// Therefore we send an ACK containing the number of frames we have received so that the
		// sender can shut down.
		out.println("ACK" + frame.length);
		System.out.println("Received frames:");
		// Now we simply print the frames we have received.
		for(i=0 ; i<frame.length ; i++) {
			System.out.print(frame[i] + " ");
		}
		System.out.println();
	}
}

class GoBackNReceiver {
	public static void main(String args[]) {
		// Create the socket objects.
		Socket client_socket = null;
		try {
			client_socket = new Socket("localhost", 7777);
		} catch(IOException e) {
			e.printStackTrace();
		}
		// Create the I/O stream objects.
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			out = new PrintWriter(client_socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		// Create the Communicator2 object to receive frames and send ACKs/NAKs.
		Communicator2 comm = new Communicator2(in, out);
		// Start the communicator.
		comm.start();
	}
}

/*

Output:
9
Received 0
Acknowledged 0
Received 1
Acknowledged 1
Received 5
Not acknowledged 2
Received 6
Not acknowledged 2
Received 2
Acknowledged 2
Received 4
Not acknowledged 3
Received 3
Acknowledged 3
Received 5
Not acknowledged 4
Received 4
Acknowledged 4
Received 5
Acknowledged 5
Received 6
Acknowledged 6
Received 7
Acknowledged 7
Received 8
Acknowledged 8
Received frames:
0 1 2 3 4 5 6 7 8

*/