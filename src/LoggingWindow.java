import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
/**
 * Created by ene on 03.01.16.
 */

/**
 * BankClientApplication Window
 * @author Piotr Januszewski
 * @author Adrian Radej
 * @author Monika Stępkowska
 */
public class LoggingWindow extends JFrame implements Handle {

    private JButton loginButton;
    private JPanel mainPanel;
    private JList list1;
    private JLabel loggedLabel;
    private JTextArea textArea1;
    private JButton exitButton;
    private JPasswordField passwordField1 = new JPasswordField();

    private String login = null;
    private String password = null;
    private String passwordIndexes = null;
    private String peselIndexes = null;

    private Client client;

    private JFrame jFrame;

    /**
     * Class constructor. Makes safe ssl connection with server and BankClientApplication Window
     */
    public LoggingWindow() {
        super("BankClientApplication");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        loggedLabel.setForeground(Color.red);
        setContentPane(mainPanel);
        SwingUtilities.updateComponentTreeUI (mainPanel);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        list1.addSelectionInterval(1, 1);
        textArea1.setForeground(Color.blue);
        jFrame = this;

        try {
            client = new Client("127.0.1.1", 7000, this);
        } catch (Exception err) {
            err.printStackTrace();
        }

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.closeSocket();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                super.windowClosing(e);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea1.setText(null);

                Font font = new Font("Arial", Font.BOLD, 12);
                UIManager.put("OptionPane.messageFont", font);
                UIManager.put("OptionPane.buttonFont", font);

                login = JOptionPane.showInputDialog(jFrame, "Enter Login: ");

                if (login != null && !login.equals(""))
                    if (login.length() <= 2)
                        textArea1.setText("Login has a minimum 3 characters \nTry again");
                    else {
                        list1.clearSelection();
                        list1.addSelectionInterval(2, 2);
                        client.sendData("Login" + " " + login);
                        textArea1.setText("Sent to server");
                    }
                else
                    textArea1.setText("Error.\nYou did not enter a Login!");
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.closeSocket();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    /**
     * Handle function, which handle server messages
     * @param data Data from server
     * @return null
     */
    public String handle(String data) {
        if(data.contains("ERROR")){
            textArea1.setText("Message from server: \n");
            textArea1.append(data.substring(data.indexOf(" ")));
            list1.clearSelection();
            list1.addSelectionInterval(1, 1);
        }
        else
            if (data.contains("PasswordIndexes")) {
                passwordIndexes = data.split(" ")[1];
                list1.clearSelection();
                list1.addSelectionInterval(3, 3);

                password = getPassword(passwordIndexes,jFrame);

                if(password != null) {
                    client.sendData("Password" + " " + login + " " + password + " " + passwordIndexes);
                    list1.clearSelection();
                    list1.addSelectionInterval(4, 4);
                }else {
                    list1.clearSelection();
                    list1.addSelectionInterval(1, 1);
                    textArea1.setText("You canceled log in operation");
                }
            } else if (data.contains("PESELIndexes")) {
                peselIndexes = data.split(" ")[1];
                list1.clearSelection();
                list1.addSelectionInterval(5, 5);

                String peselNumbers = getPeselNumbers(peselIndexes,jFrame);

                if(peselNumbers != null){
                    client.sendData("PESELNumbers" + " " + login + " " + password + " " + passwordIndexes + " " + peselNumbers + " " + peselIndexes);
                    list1.clearSelection();
                    list1.addSelectionInterval(6, 6);
                    textArea1.setText("Sent to server");
                }else {
                    list1.clearSelection();
                    list1.addSelectionInterval(1, 1);
                    textArea1.setText("You canceled log in operation");
                }
            } else if (data.contains("LoggedIn")) {
                dispose();
                LoggedWindow loggedWindow = new LoggedWindow(login, client, data.split(" ")[1]);
            }
        return null;
    }

    /**
     * Function which is used to get letters from user password. It displays little window to enter it.
     * @param indexes Indexes of password letters to enter
     * @param jFrame JFrame which helps program to focus popping up window
     * @return Returns password letters or "" if user canceled operation
     */
    public String getPassword(String indexes, JFrame jFrame) {
        String response = "";
        textArea1.setText(null);
        String password = "";
        String message = "Enter Password letters: ";
        while(response.length() != indexes.split(",").length){

            int ok = JOptionPane.showConfirmDialog(jFrame, passwordField1, message + indexes, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (ok == JOptionPane.OK_CANCEL_OPTION){
                response = null;
                break;
            }

            if (ok == JOptionPane.OK_OPTION) {
                password = new String(passwordField1.getPassword());
            } else {
                list1.addSelectionInterval(1, 1);
                textArea1.setText("Error - you clicked cancel");
            }

            if (!password.equals("")) {
                response = password;
                textArea1.setText("Sent to server");
            } else {
                list1.clearSelection();
                list1.addSelectionInterval(1, 1);
                textArea1.setText("Error - you clicked cancel");
            }
            passwordField1.setText(null);
            message = "Enter password again:";
        }
        return response;
    }

    /**
     * Function which is used to get numbers from user PESEL. It displays little window to enter it.
     * @param indexes Indexes of PESEL numbers to enter
     * @param jFrame JFrame which helps program to focus popping up window
     * @return Returns PESEL numbers or "" if user canceled operation
     */
    public String getPeselNumbers(String indexes, JFrame jFrame) {
        String response = "";
        textArea1.setText(null);
        String numbers = "";
        String message = "Enter PESEL numbers: ";
        while(response.length() != indexes.split(",").length) {
            int ok = JOptionPane.showConfirmDialog(jFrame, passwordField1, message + indexes, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (ok == JOptionPane.OK_CANCEL_OPTION){
                response = null;
                break;
            }

            if (ok == JOptionPane.OK_OPTION) {
                numbers = new String(passwordField1.getPassword());
            } else {
                list1.clearSelection();
                list1.addSelectionInterval(1, 1);
                textArea1.setText("Error - you clicked cancel");
            }

            if (!numbers.equals("")) {
                response = numbers;
            } else {
                list1.clearSelection();
                list1.addSelectionInterval(1, 1);
                textArea1.setText("Error - you did not enter numbers");
            }
            passwordField1.setText(null);
            message = "Enter numbers again:";
        }
        return response;
    }
}