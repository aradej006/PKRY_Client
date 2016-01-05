import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by ene on 05.01.16.
 */
public class TransferWindow extends JFrame{

    private JTextField textField1;
    private JTextField textField2;
    private JButton doTransferButton;
    private JPanel mainPanel;
    private Client client;

    private String login;
    private String sessionID;

    private JFrame jFrame;

    public TransferWindow(Client client1, String login1, String sessionID1){
        super("Okno przelewów");
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.client = client1;
        this.login = login1;
        this.sessionID = sessionID1;
        jFrame = this;

        doTransferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textField1.getText() != null)
                    if(textField2.getText() != null){
                        client.sendData("DoTransfer" + " " + login + " " + sessionID + " " + textField2.getText() + " " + textField1.getText());
                        JOptionPane.showConfirmDialog(jFrame, "Poprawnie wysłano", "Powiadomienie", JOptionPane.DEFAULT_OPTION);
                        dispose();
                    }
            }
        });

    }
}
