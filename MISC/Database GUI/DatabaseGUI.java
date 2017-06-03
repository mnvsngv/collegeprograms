/** Java database connectivity (using PostgreSQL and its JDBC)
  * 
  * In order to load the driver, store the <postgres_jdbc>.jar file in the
  * same folder as this .java file.
  *
  * Compiling:
  * Windows & Linux: javac -classpath postgresql.jar DatabaseGUI.java
  *
  * Running:
  * Windows: java -classpath .;postgresql.jar DatabaseGUI
  * Linux: java -classpath .:postgresql.jar DatabaseGUI
  *
  * "-cp" can also be used instead of "-classpath", as an abbreviation.
  */

import java.sql.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

class Database {
	boolean isConnected;
	Statement s;
	Database(String db, String user, String pass) {
		isConnected = false;
		try {
			Class.forName("org.postgresql.Driver");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		Connection con = null;
		s = null;
		try {
			con = DriverManager.getConnection("jdbc:postgresql:" + db, user,
												pass);
			s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_UPDATABLE);
			isConnected = true;
		} catch(SQLException e) {
			JOptionPane.showMessageDialog(null, "Wrong Credentials. Try"
											+ "again.", "ERROR",
											JOptionPane.ERROR_MESSAGE);
		}
	}
	
	void executeQuery(String query) {
		try {
			ResultSet rs = s.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int number_of_rows = 0;
			while(rs.next()) {
				number_of_rows++;
			}
			rs.first();
			int number_of_columns = rsmd.getColumnCount();
			JLabel l[][] = new JLabel[number_of_rows][number_of_columns];
			JFrame frame = new JFrame("View");
			frame.setLayout(new GridLayout(number_of_rows+1,
							number_of_columns));
			int i;
			for(i=0 ; i<number_of_columns ; i++) {
				frame.add(new JLabel(rsmd.getColumnName(i+1)));
			}
			do {
				for(i=0 ; i<number_of_columns ; i++) {
					l[rs.getRow() - 1][i] = new JLabel(rs.getString(i+1));
					frame.add(l[rs.getRow() - 1][i]);
				}
			} while(rs.next());
			frame.setLocationRelativeTo(null);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
		} catch(SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Requested table does not"
											+ "exist.", "ERROR",
											JOptionPane.ERROR_MESSAGE);
		}
	}
	
	void executeUpdate(String table_name) {
		JFrame frame = new JFrame("Column Values");
		JLabel l[];
		JPanel p[];
		int i;
		try {
			final String TABLE_NAME = table_name;
			ResultSet rs = s.executeQuery("select * from " + TABLE_NAME);
			ResultSetMetaData rsmd = rs.getMetaData();
			final int NUMBER_OF_COLUMNS = rsmd.getColumnCount();
			final JTextField tf[] = new JTextField[NUMBER_OF_COLUMNS];
			p = new JPanel[NUMBER_OF_COLUMNS];
			frame.setLayout(new GridLayout(NUMBER_OF_COLUMNS+1, 2));
			final JButton go = new JButton("Go");
			go.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					int j;
					String query = "insert into " + TABLE_NAME + " values(";
					for(j=0 ; j<NUMBER_OF_COLUMNS-1 ; j++) {
						query = query + "'" + tf[j].getText() + "', ";
					}
					query = query + "'" + tf[j].getText() + "')";
					try {
						s.executeUpdate(query);
					} catch(SQLException e) {
						e.printStackTrace();
					}
				}
			});
			for(i=0 ; i<NUMBER_OF_COLUMNS ; i++) {
				tf[i] = new JTextField(10);
				tf[i].getInputMap().put(KeyStroke.getKeyStroke("Enter"),
										"enter");
				tf[i].getActionMap().put("enter", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						go.doClick();
					}
				});
				p[i] = new JPanel(new FlowLayout());
				p[i].add(new JLabel("Column " + (i+1) + ":"));
				p[i].add(tf[i]);
				frame.add(p[i]);
			}
			frame.add(go);
		} catch(SQLException e) {
			JOptionPane.showMessageDialog(null, "Unknown error!!", "ERROR",
											JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
}

class LoginFrame {
	Database d;
	JFrame frame;
	JPanel db_panel, user_panel, pass_panel;
	JTextField db, user, pass;
	JLabel db_label, user_label, pass_label;
	JButton login;
	EnterAction enter_action;
	
	LoginFrame() {
		frame = new JFrame("Login");
		frame.setLayout(new GridLayout(4,1));
		
		db_panel = new JPanel(new FlowLayout());
		user_panel = new JPanel(new FlowLayout());
		pass_panel = new JPanel(new FlowLayout());
		
		db = new JTextField(10);
		user = new JTextField(10);
		pass = new JPasswordField(10);
		
		db_label = new JLabel("Database:");
		user_label = new JLabel("Username:");
		pass_label = new JLabel("Password:");
		
		db_label.setLabelFor(db);
		user_label.setLabelFor(user);
		pass_label.setLabelFor(pass);
		
		db_panel.add(db_label);
		db_panel.add(db);
		
		user_panel.add(user_label);
		user_panel.add(user);
		
		pass_panel.add(pass_label);
		pass_panel.add(pass);
		
		login = new JButton("Login");
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == login) {
					d = new Database(db.getText(), user.getText(),
										pass.getText());
					if(d.isConnected) {
						frame.dispose();
						new MainFrame(d);
					}
				}
			}
		});
		
		enter_action = new EnterAction();
		db.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enter");
		db.getActionMap().put("enter", enter_action);
		user.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enter");
		user.getActionMap().put("enter", enter_action);
		pass.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enter");
		pass.getActionMap().put("enter", enter_action);
		
		frame.add(db_panel);
		frame.add(user_panel);
		frame.add(pass_panel);
		frame.add(login);
		
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	class EnterAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			login.doClick();
		}
	}
	
	Database getDatabase() {
		return d;
	}
}

class ResultFrame {
	JFrame frame;
	JTextField textfield;
	JLabel label;
	JPanel panel;
	JButton input;
	String table;
	
	ResultFrame(Database db, int query_type) {
		final Database DATABASE = db;
		final int QUERY_TYPE = query_type;
		frame = new JFrame("Input");
		frame.setLayout(new GridLayout(2,1));
		
		textfield = new JTextField(10);
		textfield.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enter");
		textfield.getActionMap().put("enter", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				input.doClick();
			}
		});
		
		label = new JLabel("Table:");
		label.setLabelFor(textfield);
		
		panel = new JPanel(new FlowLayout());
		panel.add(label);
		panel.add(textfield);
		
		input = new JButton("OK");
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table = textfield.getText();
				if(QUERY_TYPE == 0) {
					DATABASE.executeQuery("select * from " + table);
				} else {
					DATABASE.executeUpdate(table);
				}
			}
		});
		
		frame.add(panel);
		frame.add(input);
		
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
}

class MainFrame {
	JFrame frame;
	JButton view, insert;
	
	MainFrame(Database db) {
		final Database DATABASE = db;
		frame = new JFrame("Database GUI");
		frame.setLayout(new GridLayout(2,1));
		
		view = new JButton("View a table");
		view.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ResultFrame(DATABASE, 0);
			}
		});
		insert = new JButton("Insert into a table");
		insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ResultFrame(DATABASE, 1);
			}
		});
		
		frame.add(view);
		frame.add(insert);
		
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

class DatabaseGUI {
	public static void main(String args[]) {
		new LoginFrame();
	}
}