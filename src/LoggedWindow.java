import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by ene on 04.01.16.
 */
public class LoggedWindow extends JFrame implements Handle{
    private JTable table1;
    private JPanel mainPanel;
    private JButton logoutButton;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JButton refreshAccountInfoButton;
    private DefaultTableModel defaultTableModel;

    private String login;
    private String sessionID;
    private Client client;

    public LoggedWindow(String login1, Client client1, String sessionid){
        super("LoggedWindow");
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.login = login1;
        this.client = client1;
        this.sessionID = sessionid;
        this.client.changeHandle(this);
        label1.setForeground(Color.red);
        label1.setText("Logged in as: " + login);

        defaultTableModel = new DefaultTableModel();
        table1.setModel(defaultTableModel);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("Logout" + " " + login);
            }
        });
        refreshAccountInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("getaccount" + " " + login + " " + sessionID);
            }
        });
    }

    public void makeTable(DefaultTableModel defaultTableModel, String data){
        String[] array = data.split(" ");
        table1.setModel(defaultTableModel);
        defaultTableModel.addColumn("Dane konta");
        for(int i=1;i<array.length;i++){
            defaultTableModel.addRow(new Object[]{array[i]});
        }
    }

    public String handle(String data) {
        if(data.contains("LOGOUT")) {
            try {
                client.closeSocket();
                JOptionPane.showConfirmDialog(null, "Wylogowano Poprawnie", "Powiadomienie", JOptionPane.DEFAULT_OPTION);
                dispose();
                LoggingWindow loggingWindow = new LoggingWindow();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (data.contentEquals("TransferSuccess")){
        }
        else if (data.contains("account")){
            makeTable(defaultTableModel,data);
        }
        return null;
    }
}