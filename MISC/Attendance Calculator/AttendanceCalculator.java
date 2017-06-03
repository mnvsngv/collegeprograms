import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


class MainFrame implements ActionListener {
    JFrame frame;
    JTextField tfAttd, tfLecs;
    JLabel lAttd, lLecs;
    JPanel pAttd, pLecs, pButtons;
    JButton bGo, bHelp;
    JCheckBox checkbox;
    
    MainFrame() {
        // Create main frame
        frame = new JFrame("Home");
        frame.setLayout(new GridLayout(3,1));
        
        // Create 'attended' section
        tfAttd = new JTextField(7);
        lAttd = new JLabel("Enter number of lectures attended:");
        lAttd.setLabelFor(tfAttd);
        pAttd = new JPanel();
        pAttd.setLayout(new FlowLayout());
        pAttd.add(lAttd);
        pAttd.add(tfAttd);
        
        // Creat 'conducted' section
        tfLecs = new JTextField(7);
        lLecs = new JLabel("Enter number of lectures conducted:");
        lLecs.setLabelFor(tfLecs);
        pLecs = new JPanel();
        pLecs.setLayout(new FlowLayout());
        pLecs.add(lLecs);
        pLecs.add(tfLecs);
        
        // Create buttons section
        pButtons = new JPanel();
        pButtons.setLayout(new FlowLayout());
        bGo = new JButton("Go");
        bGo.addActionListener(this);
        bHelp = new JButton("Help");
        bHelp.addActionListener(this);
        checkbox = new JCheckBox("'Smart Mode'");
        pButtons.add(checkbox);
        pButtons.add(bGo);
        pButtons.add(bHelp);
        
        // Add all elements to the frame
        frame.add(pAttd);
        frame.add(pLecs);
        frame.add(pButtons);
        
        // Set and show frame
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == bGo) {
            // Go was clicked.
            String strAttd = tfAttd.getText();
            String strLecs = tfLecs.getText();
            if((strAttd.equals("")) || (strLecs.equals(""))) {
                // Empty text fields error
                JOptionPane.showMessageDialog(frame, "Please enter correct information.", "Error", JFrame.DISPOSE_ON_CLOSE);
            }
            else {
                // Calculate amount of lectures required
                int intAttd = Integer.parseInt(strAttd);
                int intLecs = Integer.parseInt(strLecs);
                double doubleReqd;
                String result;
                
                if(!checkbox.isSelected()) {
                    // Calculate lectures required to reach 75%
                    doubleReqd = (3*intLecs) - (4*intAttd);
                }
                else {
                    // Calculate lectures required to reach 60%
                    doubleReqd = ((3*intLecs) - (5*intAttd))/(2.0);
                }
                // Round up
                int intReqd = (int) Math.ceil(doubleReqd);
                
                if(intReqd >= 0) {
                    // Attendance is less than required
                    result = "You need to attend " + intReqd + " more lectures.";
                }
                else {
                    // Attendance is above the selected threshold
                    if(!checkbox.isSelected()) {
                        // Calculate number of lectures that can be safely bunked while keeping attendance above 75%
                        intReqd /= 3;
                    }
                    else {
                        // Calculate number of lectures that can be safely bunked while keeping attendance above 60%
                        intReqd = (int) (2*doubleReqd)/3;
                    }
                    result = "You can bunk " + (-(intReqd)) + " lectures.";
                }
                JOptionPane.showMessageDialog(frame, result, "Result", JFrame.DISPOSE_ON_CLOSE);
            }
        }
        else if(e.getSource() == bHelp) {
            String result = "Enter the number of lectures attended and conducted. 'Smart' mode considers 60% attendance.";
            JOptionPane.showMessageDialog(frame, result, "Help", JFrame.DISPOSE_ON_CLOSE);
        }
    }
}

public class AttendanceCalculator {
    public static void main(String[] args) {
        MainFrame m = new MainFrame();
    }
}