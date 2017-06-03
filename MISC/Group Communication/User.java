import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class User {
	public static Socket client;
	public static ObjectInputStream in;
	public static ObjectOutputStream out;
	public static Map<String, MulticastSocket> map;
	public static Map<String, ChatFrame> frameMap;
	
	public static void main(String args[]) {
		map = new HashMap<>();
		frameMap = new HashMap<>();
		try {
			client = new Socket("localhost", 8888);
			out = new ObjectOutputStream(client.getOutputStream());
			in = new ObjectInputStream(client.getInputStream());
		} catch(IOException e) { e.printStackTrace(); }
		new UserFrame();
	}
}

class UserFrame {
	JFrame frame;
	JPanel contentPane;
	JButton getGroups;
	
	UserFrame() {
		frame = new JFrame("User");
		contentPane = new JPanel(new GridLayout(1,1));
		frame.setContentPane(contentPane);
		
		getGroups = new JButton("Get list of available groups");
		getGroups.addActionListener(new GetGroupsListener());
		contentPane.add(getGroups);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private class GetGroupsListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				User.out.writeUnshared(new String("listRequest"));
				User.out.flush();
				@SuppressWarnings("unchecked")
				java.util.List<Group> groupList = (java.util.List<Group>) User.in.readObject();
				createFrame(groupList);
			} catch(Exception ex) { ex.printStackTrace(); }
		}
		
		private void createFrame(final java.util.List<Group> groupList) {
			final JFrame frame = new JFrame("Available Groups");
			int numGroups = groupList.size();
			JPanel contentPane = new JPanel(new GridLayout(numGroups+1, 2));
			frame.setContentPane(contentPane);
			
			contentPane.add(new JLabel("Name"));
			contentPane.add(new JLabel("Group Join Request"));
			
			JLabel nameLabel[] = new JLabel[numGroups];
			JButton groupButton[] = new JButton[numGroups];
			
			for(int i=0 ; i<numGroups ; i++) {
				String groupName = groupList.get(i).getName();
				nameLabel[i] = new JLabel(groupName);
				if(User.map.containsKey(groupName)) {
					groupButton[i] = new JButton("Leave Group");
					groupButton[i].addActionListener(new LeaveGroupListener(groupName, frame));
				} else {
					groupButton[i] = new JButton("Request to Join");
					groupButton[i].addActionListener(new JoinGroupListener(i, groupList, frame));
				}
				
				contentPane.add(nameLabel[i]);
				contentPane.add(groupButton[i]);
			}
			
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
		}
		
		private class LeaveGroupListener implements ActionListener {
			String groupName;
			JFrame groupFrame;
			
			LeaveGroupListener(String groupName, JFrame groupFrame) {
				this.groupName = groupName;
				this.groupFrame = groupFrame;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					User.out.writeUnshared(new String("leaveGroup"));
					User.out.writeUnshared(groupName);
					MulticastSocket s = User.map.get(groupName);
					User.out.writeUnshared(s);
					String address = (String) User.in.readObject();
					s.leaveGroup(InetAddress.getByName(address));
					User.map.remove(groupName);
					JFrame chatFrame = User.frameMap.get(groupName);
					chatFrame.dispose();
					User.frameMap.remove(groupName);
					groupFrame.dispose();
					getGroups.doClick();
				} catch(Exception ex) { ex.printStackTrace(); }
			}
		}
		
		private class JoinGroupListener implements ActionListener {
			int groupNumber;
			java.util.List<Group> groupList;
			JFrame frame;
			
			JoinGroupListener(int groupNumber, java.util.List<Group> groupList, JFrame frame) {
				this.groupNumber = groupNumber;
				this.groupList = groupList;
				this.frame = frame;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					User.out.writeUnshared(new String("joinGroup"));
					User.out.flush();
					String groupName = groupList.get(groupNumber).getName();
					User.out.writeUnshared(groupName);
					User.out.flush();
					String reply = (String) User.in.readObject();
					if(reply.equalsIgnoreCase("Y")) {
						CustomSocket s = null;
						try {
							s = new CustomSocket(8888);
						} catch(IOException ex1) { ex1.printStackTrace(); }
						User.map.put(groupName, s);
						User.out.writeUnshared(s);
						User.out.flush();
						String address = (String) User.in.readObject();
						s.joinGroup(InetAddress.getByName(address));
						frame.dispose();
						ChatFrame cFrame = new ChatFrame(groupName, address, s);
						User.frameMap.put(groupName, cFrame);
					} else {
						System.out.println("Join request rejected by admin");
					}
				} catch(Exception ex) { ex.printStackTrace(); }
			}
		}
	}
}

class ChatFrame extends ChatUIFrame {
	ChatFrame(String groupName, String address, final MulticastSocket client) {
		super(groupName, address, client);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					byte buffer[] = new byte[1000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						client.receive(packet);
					} catch(IOException e) { e.printStackTrace(); }
					receiveMessage(new String(packet.getData()).trim());
				}
			}
		}).start();
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
}

class CustomSocket extends MulticastSocket implements Serializable {
	CustomSocket(int port) throws IOException {
		super(port);
	}
}