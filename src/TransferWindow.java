import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by ene on 05.01.16.
 */

/**
 * Window where user can make transfers
 * @author Piotr Januszewski
 * @author Adrian Radej
 * @author Monika Stępkowska
 */
public class TransferWindow extends JFrame{

    private JTextField textField1;
    private JTextField textField2;
    private JButton doTransferButton;
    private JPanel mainPanel;
    private JButton cancelButton;
    private JLabel errorLabel;
    private JTextArea textArea1;
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

        doTransferButton.addActionListener(new ActionListener() {
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
                    errorLabel.setText("Money not entered correctly \nTry again");
                }

                if(isDouble)
                    if(textField1.getSelectedText().length() == 26)
                        if(isNumber){
                            client.sendData("DoTransfer" + " " + login + " " + sessionID + " " + textField2.getText() + " " + textField1.getText());
                            JOptionPane.showMessageDialog(jFrame, "Sent correctly", "Information", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        }
                        else
                            errorLabel.setText("Error. You not entered number.\nTry again");
                    else
                        errorLabel.setText("Error. Account number has 26 digits.\nTry again");
                else
                    errorLabel.setText("Error. You not entered number.\nTry again");
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
