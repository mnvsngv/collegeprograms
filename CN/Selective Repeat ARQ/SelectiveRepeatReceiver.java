/** Java Program to implement Selective Repeat ARQ - Receiver side
  *
  * Input: - Number of frames to be received, which is same as the value on the Sender side
  *
  * Output: - Text describing received frames, sent ACKs/NAKs and time outs.
  *
  * Author: Manav Sanghavi
  */

import java.util.*;
import java.net.*;
import java.io.*;

// Communicator2 class is the client side receiver, which will also send ACKs/NAKs.
class Communicator2 {
	ObjectInputStream in;
	ObjectOutputStream out;
	// Creating Input and Output stream objects which can send objects over the network.
	int frame[];
	// 'frame' array will receive and store all the frames.
	int number_of_correct_frames, expected_frame;
	// 'number_of_correct_frames' will keep a track of the number of frames received and stored.
	// 'expected_frame' will be used to decide whether to send an ACK or a NAK according to the
	// frame last received.
	int window_start, window_end, window_size;
	// 'window_start' indicates the value of the start of the window
	// 'window_end' indicates the value of the end of the window
	// 'window_size' indicates the value of the size of the window
	
	Communicator2(ObjectInputStream ois, ObjectOutputStream oos) {
		in = ois;
		out = oos;
		// Initializing the I/O stream objects.
		number_of_correct_frames = 0;
		expected_frame = 0;
		// Setting variables to initial values.
		getFrameSize();
	}
	void getFrameSize() {
		// getFrameSize() method separates the frame creation code from the rest of the code.
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the number of the frames:");
		int n = scan.nextInt();
		// 'n' is the number of frames.
		frame = new int[n];
		getWindowSize();
		for(int i=0 ; i<frame.length ; i++) {
			frame[i] = -1;
			// Initially all the frame values are -1, indicating that they are empty.
		}
	}
	
	void getWindowSize() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the window size (max value=" + (frame.length/2) + "):");
		window_size = scan.nextInt();
		window_start = 0;
		// Initially, window_start will be at the first frame.
		window_end = window_size;
		// Initially, window_end will equal to the window size.
		// The window size will remain constant throughout, except at the end when the frames
		// have finished.
	}
	
	void start() {
		// Communicator code:
		int i = -1;
		// 'i' will be used to store the frame received.
		while(number_of_correct_frames < frame.length) {
			System.out.println("Number of correct frames = " + number_of_correct_frames);
			// This loop runs while the number of frames received are less than expected.
			Timer timer = new Timer();
			ResendTask resend = new ResendTask();
			timer.schedule(resend,200,200);
			// 'timer' is a Timer object which will schedule TimerTasks.
			// 'resend' is a TimerTask object which has the code to resend a NAK when the timer
			// runs out.
			try {
				i = (int) in.readObject();
				// Receive an object.
			} catch(IOException e) {
				e.printStackTrace();
			} catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println("Received: " + i);
			System.out.println("Expected: " + expected_frame);
			if(i == expected_frame) {
				// If this is the frame that we were expecting, then we will store it.
				frame[i] = i;
				// We call the method to correctly find and send the appropriate ACK.
				sendAck(i);
				expected_frame++;
				// Next frame expected is the immediate next frame.
				number_of_correct_frames++;
				// We have received and stored a frame, so increase the counter for number of
				// correctly received frames.
				timer.cancel();
				// Cancel the timer for sending a time-out NAK as we have received a frame.
			} else {
				// This is not the frame we were expecting. This frame is thus out of the expected
				// order.
				if((i < window_end) && (i >= window_start)) {
					// The frame lies within the window, so we can save it.
					frame[i] = i;
					// Store the out of order, unexpected frame.
					for(int j=expected_frame ; j<i ; j++) {
						// Send NAKs for every frame which we have not received. This includes all
						// the frames from the last correct frame received to the frame received 
						// now.
						// If the received frame is higher than the expected frame, then the NAKs
						// are not sent (as j<i).
						System.out.println("Not acknowledged: " + j);
						try {
							out.writeObject("NAK" + j);
						} catch(IOException e) {
							e.printStackTrace();
						}
					}
					if(i > expected_frame) {
						// If the received frame is more than the expected frame, then it is not a
						// resent frame. Therefore we can set the expected_frame value to the frame
						// which comes after the frame we just received.
						expected_frame = i+1;
						while((expected_frame < frame.length) && (frame[expected_frame] != -1)) {
							// But if the new value of expected_frame contains a received frame,
							// then we have to go to the next value and check if it is not
							// received. We continue till we find a non-received frame or if we
							// reach the last frame value.
							expected_frame++;
						}
					} else {
						// This is a resent frame, so we must send the appropriate ACK.
						// This is a repeat of the same ACK resolving code as done above.
						sendAck(i);
					}
					number_of_correct_frames++;
					// At this point, we have received and stored a frame, which may or may not 
					// be the expected frame but a correct frame nonetheless, hence we have 
					// stored it.
					timer.cancel();
					// We can also cancel the scheduled task of sending a NAK, as we have 
					// received a frame.
				} else {
					// This line should not execute, as the algorithm ensures that frames received
					// are not outside the window. However, it is there in case there is an error
					// in the timing and randomness of the sender. In case of an error, the time
					// outs, ACKs and NAKs will allow the server and client to eventually 
					// synchronize and correctly send the remaining frames.
					System.out.println(i + " lies outside the window.");
					try {
						Thread.sleep(2000);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if(i == window_start) {
				// If we received the frame which is at the lower edge of the window, then we will
				// move the window. This will guarantee that all frames below the lower edge of
				// the window (all frames lower than the window_start) have been received.
				window_start++;
				window_end++;
				// Initially, we move the window by one frame, as we have received the frame at
				// the lower edge of the window.
				if(window_start >= frame.length) {
					// In case the window_start value goes higher than the frame.length value, we
					// will set it to its maximum permissible value of the last frame expected.
					window_start = frame.length-1;
				} else {
					// Otherwise, we will advance the window edge so that the window_start value
					// is no a received frame. Thus, when we receive this frame, this code can
					// run again to set the new window values correctly.
					while((window_start < frame.length) && (frame[window_start] != -1)) {
						// We advance the window edges when the first frame of the window is 
						// already received.
						window_start++;
						window_end++;
					}
				}
				if(window_end > frame.length) {
					// If the window_end value (upper edge of the window) is higher than the
					// maximum frame size, then we set it to the last value (frame.length)
					window_end = frame.length;
				}
			}
			showWindow();
			// We have received a frame, so we will show the current window values which the
			// receiver will accept.
			timer.cancel();
			// At this point, we have received a frame, so we will cancel the timer that sends
			// a NAK on time-out.
		}
		// At this point, we have received all the frames. We send a final ACK for the sender to
		// shut down.
		try {
			out.writeObject("ACK" + frame.length);
		} catch(IOException e) {
			e.printStackTrace();
		}
		// We now shall simply print all the frames we have received.
		System.out.println("Received frames:");
		for(i=0 ; i< number_of_correct_frames ; i++) {
			System.out.print(frame[i] + " ");
		}
		System.out.println();
	}
	
	class ResendTask extends TimerTask {
		// This class contains the method to be called once the timer runs out.
		public void run() {
			// We have to send a NAK for the last frame which we haven't received.
			// According to our window positioning, the frame at the window_start location is
			// the first unreceived frame, hence we send a NAK for that frame.
			System.out.println("Time out! Resend " + window_start);
			try {
				out.writeObject("NAK" + window_start);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	void showWindow() {
		// This method prints the window.
		// On the receiver side, the window size is constant till the end of the program and
		// contains the frames that the receiver will accept.
		System.out.print("Window: ");
		for(int i=window_start ; i<window_end ; i++) {
			System.out.print(i+" ");
		}
		System.out.println();
	}
	
	void sendAck(int i) {
		// We will only send an ACK if all the previous frames have been received.
		// Because when the server gets the ACK, it assumes that all previous frames
		// have been received by the client.
		boolean isAcknowledgable = true;
		int j = window_start;
		// Assume that all previous frames have been received by the client.
		for( ; (j<window_end) && (j<=i) ; j++) {
			// All frames before the window's lower edge have been received. So we start
			// checking for unreceived frames from the window_start value onwards.
			// We go till either the end of the window or till the currently received
			// frame, because the currently received frame may be lower than the window's
			// upper edge and the frames after the current frame might not even have been
			// sent by the server.
			if(frame[j] == -1) {
				// If any of the frames which match the above checking criteria are empty
				// then we do not send acknowledgement till we receive the previous frames.
				isAcknowledgable = false;
			}
		}
		j--;
		if(isAcknowledgable) {
			// Otherwise, we have found that all the frames before the current frame have
			// been received so we can send the ACK for the current frame.
			System.out.println("Acknowledged: " + j);
			try {
				out.writeObject("ACK" + j);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class SelectiveRepeatReceiver {
	public static void main(String args[]) {
		// Create the socket objects.
		Socket client_socket = null;
		try {
			client_socket = new Socket("localhost", 7777);
		} catch(IOException e) {
			e.printStackTrace();
		}
		// Create the I/O Stream objects.
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(client_socket.getOutputStream());
			in = new ObjectInputStream(client_socket.getInputStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
		// Create the Communicator2 object.
		Communicator2 comm = new Communicator2(in, out);
		// Start the communicator.
		comm.start();
	}
}

/*

Output:
Enter the number of the frames:
15
Enter the window size (max value=7):
7
Number of correct frames = 0
Received: 2
Expected: 0
Not acknowledged: 0
Not acknowledged: 1
Window: 0 1 2 3 4 5 6
Number of correct frames = 1
Received: 4
Expected: 3
Not acknowledged: 3
Window: 0 1 2 3 4 5 6
Number of correct frames = 2
Received: 5
Expected: 5
Window: 0 1 2 3 4 5 6
Number of correct frames = 3
Received: 0
Expected: 6
Acknowledged: 0
Window: 1 2 3 4 5 6 7
Number of correct frames = 4
Received: 1
Expected: 6
Acknowledged: 1
Window: 3 4 5 6 7 8 9
Number of correct frames = 5
Received: 3
Expected: 6
Acknowledged: 3
Window: 6 7 8 9 10 11 12
Number of correct frames = 6
Received: 7
Expected: 6
Not acknowledged: 6
Window: 6 7 8 9 10 11 12
Number of correct frames = 7
Received: 6
Expected: 8
Acknowledged: 6
Window: 8 9 10 11 12 13 14
Number of correct frames = 8
Received: 11
Expected: 8
Not acknowledged: 8
Not acknowledged: 9
Not acknowledged: 10
Window: 8 9 10 11 12 13 14
Number of correct frames = 9
Received: 9
Expected: 12
Window: 8 9 10 11 12 13 14
Number of correct frames = 10
Received: 10
Expected: 12
Window: 8 9 10 11 12 13 14
Number of correct frames = 11
Time out! Resend 8
Time out! Resend 8
Received: 8
Expected: 12
Acknowledged: 8
Window: 12 13 14
Number of correct frames = 12
Received: 14
Expected: 12
Not acknowledged: 12
Not acknowledged: 13
Window: 12 13 14
Number of correct frames = 13
Received: 12
Expected: 15
Acknowledged: 12
Window: 13 14
Number of correct frames = 14
Received: 13
Expected: 15
Acknowledged: 13
Window:
Received frames:
0 1 2 3 4 5 6 7 8 9 10 11 12 13 14

*/