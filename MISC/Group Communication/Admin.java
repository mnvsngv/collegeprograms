import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Admin {
	private static int counter;
	public static java.util.List<Group> groupList;
	
	public static void main(String args[]) {
		groupList = new java.util.LinkedList<>();
		counter = 1;
		new AdminFrame();
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8888);
		} catch(IOException e) { e.printStackTrace(); }
		Socket clientSocket = null;
		try {
			while(true) {
				clientSocket = serverSocket.accept();
				System.out.println("Connected to a client");
				new ClientThread(clientSocket).start();
			}
		} catch(IOException e) { e.printStackTrace(); }
	}
	
	public static InetAddress newAddress() {
		String stringAddress = "224.0.0." + (++counter);
		InetAddress address = null;
		try {
			address = InetAddress.getByName(stringAddress);
		} catch(UnknownHostException e) { e.printStackTrace(); }
		return address;
	}
	
	public static void removeGroup(int index) {
		Group g = groupList.get(index);
		g.deleteGroup();
		groupList.remove(g);
	}
	
	public static Group findGroupByName(String groupName) {
		for(Group g : groupList) {
			if(g.getName().equals(groupName)) {
				return g;
			}
		}
		return null;
	}
	
	public static void addToGroup(String groupName, MulticastSocket s) {
		findGroupByName(groupName).addToGroup(s);
	}
	
	public static void removeFromGroup(String groupName, MulticastSocket s) {
		findGroupByName(groupName).removeFromGroup(s);
	}
}

class ClientThread extends Thread {
	Socket client;
	ObjectInputStream in;
	ObjectOutputStream out;
	
	ClientThread(Socket client) {
		this.client = client;
		try {
			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());
		} catch(IOException e) { e.printStackTrace(); }
	}
	
	@Override
	public void run() {
		String message = new String();
		while(true) {
			try {
				message = (String) in.readObject();
			} catch(Exception e) { 
				e.printStackTrace();
				return;
			}
			if(message.equals("listRequest")) {
				try {
					out.writeUnshared(Admin.groupList);
					out.flush();
				} catch(IOException e) { e.printStackTrace(); }
			}
			if(message.equals("joinGroup")) {
				try {
					String groupName = (String) in.readObject();
					System.out.println("Allow user to join the group '" + groupName + "'?");
					String decision = new Scanner(System.in).nextLine();
					out.writeUnshared(decision);
					if(decision.equalsIgnoreCase("Y")) {
						MulticastSocket s = (MulticastSocket) in.readObject();
						Group g = Admin.findGroupByName(groupName);
						out.writeUnshared(g.getAddress());
						Admin.addToGroup(groupName, s);
					}
				} catch(Exception e) { e.printStackTrace(); }
			}
			if(message.equals("leaveGroup")) {
				try {
					String groupName = (String) in.readObject();
					MulticastSocket s = (MulticastSocket) in.readObject();
					out.writeUnshared(Admin.findGroupByName(groupName).getAddress());
					Admin.removeFromGroup(groupName, s);
				} catch(Exception e) { e.printStackTrace(); }
			}
		}
	}
}

class AdminFrame {
	JFrame frame;
	JPanel contentPane;
	JButton createGroup;
	JButton listGroups;
	
	AdminFrame() {
		frame = new JFrame("Admin");
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(2,1));
		frame.setContentPane(contentPane);
		
		createGroup = new JButton("Create a new group");
		createGroup.addActionListener(new CreateGroupListener());
		listGroups = new JButton("Retrieve a list of existing groups");
		listGroups.addActionListener(new ListGroupsListener());
		
		contentPane.add(createGroup);
		contentPane.add(listGroups);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private class CreateGroupListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			createFrame();
		}
		
		private void createFrame() {
			final JFrame frame = new JFrame("Group Creation");
			JPanel contentPane = new JPanel(new GridLayout(2,1));
			JPanel addressPanel = new JPanel(new FlowLayout());
			JPanel buttonPanel = new JPanel(new FlowLayout());
			frame.setContentPane(contentPane);
			
			JLabel createLabel = new JLabel("Enter group name:");
			final JTextField nameField = new JTextField(15);
			addressPanel.add(createLabel);
			addressPanel.add(nameField);
			
			JButton create = new JButton("Create Group");
			create.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String name = nameField.getText();
					Admin.groupList.add(new Group(Admin.newAddress(), name));
					frame.dispose();
				}
			});
			buttonPanel.add(create);
			
			contentPane.add(addressPanel);
			contentPane.add(buttonPanel);
			
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
		}
	}
	
	private class ListGroupsListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			createFrame();
		}
		
		private void createFrame() {
			int numGroups = Admin.groupList.size();
			final JFrame frame = new JFrame("Groups list");
			JPanel contentPane = new JPanel(new GridLayout(numGroups+1, 3));
			frame.setContentPane(contentPane);
			
			contentPane.add(new JLabel("Address"));
			contentPane.add(new JLabel("Name"));
			contentPane.add(new JLabel("Group Deletion"));
			
			JLabel addressLabel[] = new JLabel[numGroups];
			JLabel nameLabel[] = new JLabel[numGroups];
			JButton deleteButton[] = new JButton[numGroups];
			
			for(int i=0 ; i<numGroups ; i++) {
				addressLabel[i] = new JLabel(Admin.groupList.get(i).getAddress());
				nameLabel[i] = new JLabel(Admin.groupList.get(i).getName());
				deleteButton[i] = new JButton("Delete Group");
				final int groupNumber = i;
				deleteButton[i].addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						Admin.removeGroup(groupNumber);
						frame.dispose();
						createFrame();
					}
				});
				
				contentPane.add(addressLabel[i]);
				contentPane.add(nameLabel[i]);
				contentPane.add(deleteButton[i]);
			}
			
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
		}
	}
}