package client;

import chatui.ChatUIFrame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 *
 * @author Manav
 */
class ClientFrame extends ChatUIFrame {
    public String ip;
    private final JMenuBar mb;
    private final JMenu m1_file;
    private final JMenuItem mi_ip;
    public boolean isReadyToConnect;
    
    public ClientFrame(String type) {
        super(type);
        isReadyToConnect = false;
        mb = new JMenuBar();
        m1_file = new JMenu("File");
        mi_ip = new JMenuItem("Set Server IP");
        mi_ip.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JFrame f = new JFrame("IP");
                f.setLayout(new GridLayout(2,1));
                
                JLabel label = new JLabel("  Set Server IP:");
                
                JPanel panel = new JPanel();
                final JTextField tf1 = new JTextField(3);
                final JTextField tf2 = new JTextField(3);
                final JTextField tf3 = new JTextField(3);
                final JTextField tf4 = new JTextField(3);
                JLabel l1 = new JLabel(".");
                JLabel l2 = new JLabel(".");
                JLabel l3 = new JLabel(".");

                final JButton invisible_button = new JButton("Invisible");
                invisible_button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        isReadyToConnect = false;
                        ip = "";
                        String quadrant = new String();
                        boolean ready = true;
                        
                        List<JTextField> l = new LinkedList<>();
                        l.add(tf1);
                        l.add(tf2);
                        l.add(tf3);
                        l.add(tf4);
                        
                        for(JTextField t : l) {
                            quadrant = t.getText();
                            if(quadrant.matches("[0-9]{1,3}")) {
                                ip += quadrant + ".";
                                ready = true;
                            } else {
                                JOptionPane.showMessageDialog(null, "Please enter a valid IP!", "Error", JOptionPane.ERROR_MESSAGE);
                                ready = false;
                                break;
                            }
                        }
                        if(ready) {
                            ip = ip.substring(0, ip.length()-1);
                            isReadyToConnect = true;
                            f.dispose();
                        }
                    }
                });
                
                class EnterAction extends AbstractAction {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        invisible_button.doClick();
                    }
                    
                }
                
                class PeriodAction extends AbstractAction {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() == tf1) {
                            tf2.requestFocus();
                        }
                        if(e.getSource() == tf2) {
                            tf3.requestFocus();
                        }
                        if(e.getSource() == tf3) {
                            tf4.requestFocus();
                        }
                        if(e.getSource() == tf4) {
                            tf1.requestFocus();
                        }
                    }
                    
                }
                
                EnterAction enteraction = new EnterAction();
                PeriodAction periodaction = new PeriodAction();
                
                tf1.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "click");
                tf1.getActionMap().put("click", enteraction);
                tf2.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "click");
                tf2.getActionMap().put("click", enteraction);
                tf3.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "click");
                tf3.getActionMap().put("click", enteraction);
                tf4.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "click");
                tf4.getActionMap().put("click", enteraction);
                
                tf1.getInputMap().put(KeyStroke.getKeyStroke('.'), "tab");
                tf1.getActionMap().put("tab", periodaction);
                tf2.getInputMap().put(KeyStroke.getKeyStroke('.'), "tab");
                tf2.getActionMap().put("tab", periodaction);
                tf3.getInputMap().put(KeyStroke.getKeyStroke('.'), "tab");
                tf3.getActionMap().put("tab", periodaction);
                tf4.getInputMap().put(KeyStroke.getKeyStroke('.'), "tab");
                tf4.getActionMap().put("tab", periodaction);
                
                panel.add(tf1);
                panel.add(l1);
                panel.add(tf2);
                panel.add(l2);
                panel.add(tf3);
                panel.add(l3);
                panel.add(tf4);
                f.add(label);
                f.add(panel);
                
                f.pack();
                f.setLocationRelativeTo(null);
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setVisible(true);
            }
        });
        
        m1_file.add(mi_ip);
        mb.add(m1_file);
        this.add(mb);
        this.setJMenuBar(mb);
        this.setVisible(true);
        mi_ip.doClick();
    }
    
    public String getIp() {
        return ip;
    }
}

public class Client {
    static BufferedReader in = null;
    static PrintWriter out = null;
    static ClientFrame frame;
        
    public static void main(String args[]) {
        frame = new ClientFrame("Client");
        
        Socket s = null;
        boolean isConnected = false;
        while(!isConnected) {
            while(!frame.isReadyToConnect) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            frame.receiveMessage("Trying to connect to \"" + frame.getIp() + "\"...");
            try {
                s = new Socket(frame.getIp(), 7777);
//                JOptionPane.showMessageDialog(null, "Connected!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.receiveMessage("Connected! You are ready to chat!");
                isConnected = true;
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Oops! Couldn't connect to " + frame.getIp(), "Error", JOptionPane.ERROR_MESSAGE);
                frame.isReadyToConnect = false;
            }
        }
        
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            frame.setUser(in.readLine());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        new Thread(new Runnable() {
            String msg;

            @Override
            public void run() {
                while(true) {
                    try {
                        msg = in.readLine();
                        frame.receiveMessage(msg);
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
        }).start();
        
        new Thread(new Runnable() {
            String msg;

            @Override
            public void run() {
                while(true) {
                    while(!frame.isReadyToSend) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    msg = frame.sendMessage();
                    DateFormat format = new SimpleDateFormat( "h:mm:ss a" );
                    String timestamp = format.format(new Timestamp(new java.util.Date().getTime()));
                    out.println(frame.getUser() + " - " + timestamp + ":\n  " + msg);
                }
            }
        }).start();
    }
}