/*
	Implementing Basic One Time Password Authentication Mechanisms using Client-Server model in Java
	Client Program
	Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi
	www.pracspedia.com
*/

import java.util.*;
import java.io.*;
import java.net.*;

class OTPClient {
	public static void main(String args[]) throws IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Connecting to the server...");
		Socket clientSocket = new Socket("localhost", 7777);
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		// Client enters ID. This will be used by the program for verifying who
		// is communicating as well as check the OTP against the ID, on the
		// server side
		System.out.println("Enter your ID:");
		String id = scan.nextLine();
		System.out.println("Contacting server...");
		out.println(id);
		System.out.println("Server has sent the OTP. Please enter it here:");
		String otp = scan.nextLine();
		System.out.println("Verifying...");
		out.println(id);
		out.println(otp);
		System.out.println(in.readLine());
		
		in.close();
		out.close();
		clientSocket.close();
	}
}

/*

Output:
Connecting to the server...
Enter your ID:
manav
Contacting server...
Server has sent the OTP. Please enter it here:
02489572
Verifying...
Logged In!

*/