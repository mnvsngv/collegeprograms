/*
	Implementing Basic One Time Password Authentication Mechanisms in Java
	Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi
	www.pracspedia.com
*/

import java.util.*;

class TimeOutTask extends TimerTask {
	boolean isTimedOut = false;
	
	public void run() {
		isTimedOut = true;
	}
}

class BasicOTP {
	public static void main(String args[]) {
		Random r = new Random();
		String otp = new String();
		for(int i=0 ; i<8 ; i++) {
			otp += r.nextInt(10);
		}
		System.out.println(otp);
		
		TimeOutTask task = new TimeOutTask();
		Timer t = new Timer();
		t.schedule(task, 10000L);
		ClientThread c = new ClientThread(otp, t, task);
		c.start();
	}
}

class ClientThread extends Thread {
	String otp;
	Timer t;
	TimeOutTask task;
	ClientThread(String otp, Timer t, TimeOutTask task) {
		this.otp = otp;
		this.t = t;
		this.task = task;
	}
	
	public void run() {
		System.out.println("Enter the OTP:");
		String input = new Scanner(System.in).nextLine();
		if(task.isTimedOut) {
			System.out.println("Time Out!");
		} else if(!input.equals(otp)) {
			System.out.println("Incorrect OTP!");
		} else {
			System.out.println("Logged In!");
		}
		System.exit(0);
	}
}

/*

Output:
04195447
Enter the OTP:
04195447
Logged In!

*/