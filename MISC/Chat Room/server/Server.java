package server;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Manav
 */
public class Server {
    public static void main(String args[]) {
        MainServer ms = new MainServer();
        new ServerFrame();
        
        while(true) {
            try {
                ms.accept();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class ServerFrame extends JFrame {
    ServerFrame() {
        this.setLayout(new GridLayout(2,1));
        JLabel label1 = new JLabel("Server running - LAN IP:");
        JLabel label2 = null;
        try {
            label2 = new JLabel(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.add(label1);
        this.add(label2);
        
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}

class MainServer {
    List<Socket> serverlist;
    ServerSocket ss;
    
    MainServer() {
        serverlist = new LinkedList<>();
        try {
            ss = new ServerSocket(7777);
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void accept() throws IOException {
        final Socket s = ss.accept();
        new PrintWriter(s.getOutputStream(), true).println("user"+serverlist.size());
        serverlist.add(s);
        new Thread(new Runnable() {
            String msg = new String();

            @Override
            public void run() {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    while(true) {
                        msg = br.readLine();
                        broadcastMessage(msg);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        br.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }
    
    public void broadcastMessage(String msg) {
        for(Socket s : serverlist) {
            try {
                new PrintWriter(s.getOutputStream(), true).println(msg);
            } catch (IOException ex) {
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}