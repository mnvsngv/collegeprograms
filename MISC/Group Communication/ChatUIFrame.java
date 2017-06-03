import java.io.*;
import java.net.*;
import java.sql.*;
import javax.swing.*;
import java.text.*;
import java.awt.event.*;

public class ChatUIFrame extends JFrame {
	protected JButton sendButton;
	private JButton setUsernameButton;
	private JLabel setUsernameLabel;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	protected JTextArea textReceiveArea;
	protected JTextPane sendMessagePane;
	public boolean isReadyToSend;
	private String message;
	private String username;
	private MulticastSocket client;
	private String address;
	
	public ChatUIFrame(String groupName, String address, MulticastSocket client) {
		initComponents();
		this.client = client;
		this.address = address;
		isReadyToSend = false;
		message = new String();
		this.setTitle(groupName);
		sendMessagePane.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "send");
		sendMessagePane.getActionMap().put("send", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendButton.doClick();
			}
			
		});
		sendMessagePane.requestFocus();
	}
	
	public ChatUIFrame() {
		// do nothing.
	}

	private void initComponents() {
		sendButton = new JButton("Send");
		jScrollPane1 = new JScrollPane();
		sendMessagePane = new JTextPane();
		jScrollPane2 = new JScrollPane();
		textReceiveArea = new JTextArea();
		setUsernameLabel = new JLabel("Set Username...");
		setUsernameButton = new JButton("Set Username");
		username = "anon";

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		sendButton.setText("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton1ActionPerformed(e);
			}
		});

		jScrollPane1.setViewportView(sendMessagePane);

		textReceiveArea.setColumns(20);
		textReceiveArea.setLineWrap(true);
		textReceiveArea.setRows(5);
		textReceiveArea.setWrapStyleWord(true);
		jScrollPane2.setViewportView(textReceiveArea);

		setUsernameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton2ActionPerformed(e);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(jScrollPane2)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(sendButton))
					.addGroup(layout.createSequentialGroup()
						.addComponent(setUsernameButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(setUsernameLabel)
						.addGap(0, 0, Short.MAX_VALUE)))
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
					.addComponent(setUsernameLabel)
					.addComponent(setUsernameButton))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addComponent(sendButton))
				.addContainerGap())
		);
		pack();
	}

	private void jButton1ActionPerformed(ActionEvent evt) {
		String data = sendMessagePane.getText();
		if(data.length() > 0) {
			isReadyToSend = true;
			sendMessagePane.setText("");
			DatagramPacket packet = null;
			try {
				DateFormat format = new SimpleDateFormat( "h:mm:ss a" );
				String timestamp = format.format(new Timestamp(new java.util.Date().getTime()));
				String message = getUser() + " - " + timestamp + ":\n  " + data;
				packet = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(address), 8888);
				client.send(packet);
			} catch(Exception e) { e.printStackTrace(); }
			sendMessagePane.requestFocus();
		}
	}

	private void jButton2ActionPerformed(ActionEvent evt) {
		final JFrame frame = new JFrame("Set Username");
		JLabel l = new JLabel("Set Username (1 to 15 characters)");
		final JTextField tf = new JTextField(15);
		final JButton b = new JButton("Invisible");
		tf.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "click");
		tf.getActionMap().put("click", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				b.doClick();
			}
			
		});
		
		b.addActionListener(new ActionListener() {
			String username;

			@Override
			public void actionPerformed(ActionEvent e) {
				username = tf.getText();
				if(username.matches("[a-zA-Z0-9@_]{1,15}")) {
					this.username = username;
					setUser(username);
					frame.dispose();
				} else {
					JOptionPane.showMessageDialog(null, "Please enter a valid username:\n1 to 15 alphanumeric characters. '@' and '_' are allowed.", "Rejected", JOptionPane.ERROR_MESSAGE);
					tf.setText("");
				}
			}
		});
		
		frame.add(l);
		frame.add(tf);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void receiveMessage(String text) {
		textReceiveArea.append(text + "\n");
	}
	
	public String sendMessage() {
		isReadyToSend = false;
		return message;
	}
	
	public void setUser(String s) {
		username = s;
		setUsernameLabel.setText(username);
	}
	
	public String getUser() {
		return username;
	}
	
	public void createFrame(final String groupName, String address, MulticastSocket client) {
		new ChatUIFrame(groupName, address, client);
	}
}
