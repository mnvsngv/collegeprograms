/*
	Implementing Basic One Time Password Authentication Mechanisms using Client-Server model in Java
	Server Program
	Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi
	www.pracspedia.com
*/

import java.util.*;
import java.io.*;
import java.net.*;

class TimeOutTask extends TimerTask {
	boolean isTimedOut = false;
	
	public void run() {
		isTimedOut = true;
	}
}

class OTPServer {
	public static void main(String args[]) throws IOException {
		ServerSocket serverSocket = new ServerSocket(7777);
		System.out.println("Server running and waiting for client...");
		Socket clientSocket = serverSocket.accept();
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		// Server waits for a client to send its user ID
		String id = in.readLine();
		
		// Server generates an OTP and waits for client to send this
		Random r = new Random();
		String otp = new String();
		for(int i=0 ; i<8 ; i++) {
			otp += r.nextInt(10);
		}
		System.out.println(otp);
		
		// Server starts a timer of 10 seconds during which the OTP is valid.
		TimeOutTask task = new TimeOutTask();
		Timer t = new Timer();
		t.schedule(task, 100000L);
		
		// Server listens for client to send its ID and OTP to check if it is
		// valid
		String newId = in.readLine();
		String newOtp = in.readLine();
		if(newId.equals(id)) {
			// User ID is verified
			if(task.isTimedOut) {
				// User took more than 100 seconds and hence the OTP is invalid
				out.println("Time out!");
			} else if(!newOtp.equals(otp)) {
				out.println("Incorrect OTP!");
			} else {
				out.println("Logged In!");
			}
		}
		System.exit(0);
	}
}

/*

Output:
Server running and waiting for client...
28748125

*/