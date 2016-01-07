import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by arade on 07-Jan-16.
 */
public class TransferWindow extends JFrame{
    private JPanel mainPanel;
    private JPanel south;
    private JPanel north;
    private JButton cancelButton;
    private JButton transferButton;
    private JTextField textField1;
    private JLabel errorLabel;
    private JTextField textField2;

    private Client client;
    private String login;
    private String sessionID;
    private JFrame jFrame;

    /**
     * Suppresses default constructor
     */
    private TransferWindow(){}

    /**
     * Class constructor. Makes window
     * @param client1 Client which can communicate with server
     * @param login1 User login
     * @param sessionID1 User session ID
     * @param jFrame1 JFrame which helps program to focus popping up window
     */
    public TransferWindow(Client client1, String login1, String sessionID1, JFrame jFrame1){
        super("Transfer Window");
        setContentPane(mainPanel);

        this.setLocation(jFrame1.getLocation());

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        errorLabel.setForeground(Color.red);

        this.client = client1;
        this.login = login1;
        this.sessionID = sessionID1;
        jFrame = jFrame1;

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField2.selectAll();
                textField1.selectAll();
                boolean isDouble = false;
                boolean isNumber = false;

                try{
                    Double.parseDouble(textField1.getSelectedText());
                    isDouble = true;
                    Integer.parseInt(textField2.getSelectedText());
                    isNumber = true;
                }catch (Exception er){
                    errorLabel.setText("Money was not entered correctly.");
                }

                if(isDouble)
                    if(textField1.getSelectedText().length() == 26)
                        if(isNumber){
                            client.sendData("DoTransfer" + " " + login + " " + sessionID + " " + textField2.getText() + " " + textField1.getText());
                            JOptionPane.showMessageDialog(jFrame, "Sent correctly", "Information", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        }
                        else
                            errorLabel.setText("Error. Bad value in amount field.");
                    else
                        errorLabel.setText("Error. Account number has 26 digits.");
                else
                    errorLabel.setText("Error. Account Number is not correct.");
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

}
