import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by ene on 03.01.16.
 */
public class LoggingWindow extends JFrame implements Handle {
    private JButton loginButton;
    private JPanel mainPanel;
    private JList list1;
    private JLabel loggedLabel;
    private JTextArea textArea1;
    private JPasswordField passwordField1 = new JPasswordField();

    private String login = null;
    private String password = null;
    private String passwordIndexes = null;
    private String peselIndexes = null;

    private Client client;

    private JFrame jFrame;

    public LoggingWindow() {
        super("LoggingWindow");
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        list1.addSelectionInterval(1, 1);
        loggedLabel.setForeground(Color.red);
        textArea1.setForeground(Color.blue);
        jFrame = this;

        try {
            client = new Client("192.168.1.5", 7000, this);
        } catch (Exception err) {
            err.printStackTrace();
        }

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea1.setText(null);
                login = JOptionPane.showInputDialog(jFrame, "Enter Login: ");

                if (login != null && !login.equals(""))
                    if (login.length() <= 2)
                        textArea1.setText("Login ma minumum 3 znaki \nspróbuj jeszcze raz");
                    else {
                        list1.clearSelection();
                        list1.addSelectionInterval(2, 2);
                        client.sendData("Login" + " " + login);
                        textArea1.setText("Sent to server");
                    }
                else
                    textArea1.setText("Błąd. Nie podałeś loginu");
            }
        });
    }

    public String handle(String data) {
        if(data.contains("ERROR")){
            textArea1.setText("Message from server: \n");
            textArea1.append(data);
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
                }
            } else if (data.contains("LoggedIn")) {
                dispose();
                LoggedWindow loggedWindow = new LoggedWindow(login, client, data.split(" ")[1]);
            }
        return null;
    }

    public String getPassword(String indexes, JFrame jFrame) {
        String response = "";
        textArea1.setText(null);
        String password = "";

        while(response.length() != indexes.split(",").length){
            int ok = JOptionPane.showConfirmDialog(jFrame, passwordField1, "Enter Password letters: " + indexes, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (ok == JOptionPane.OK_OPTION) {
                password = new String(passwordField1.getPassword());
            } else {
                list1.addSelectionInterval(1, 1);
                textArea1.setText("Błąd - kliknąłeś cancel");
            }

            if (!password.equals("")) {
                response = password;
                textArea1.setText("Sent to server");
            } else {
                list1.clearSelection();
                list1.addSelectionInterval(1, 1);
                textArea1.setText("Błąd - nie wpisałeś hasła");
            }
            passwordField1.setText(null);
        }
        return response;
    }

    public String getPeselNumbers(String indexes, JFrame jFrame) {
        String response = "";
        textArea1.setText(null);
        String numbers = "";
        while(response.length() != indexes.split(",").length) {
            int ok = JOptionPane.showConfirmDialog(jFrame, passwordField1, "Enter PESEL Nummbers: " + indexes, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (ok == JOptionPane.OK_OPTION) {
                numbers = new String(passwordField1.getPassword());
            } else {
                list1.clearSelection();
                list1.addSelectionInterval(1, 1);
                textArea1.setText("Błąd - kliknąłeś cancel");
            }

            if (!numbers.equals("")) {
                response = numbers;
            } else {
                list1.clearSelection();
                list1.addSelectionInterval(1, 1);
                textArea1.setText("Błąd - nie wpisałeś liczb");
            }
            passwordField1.setText(null);
        }
        return response;
    }
}