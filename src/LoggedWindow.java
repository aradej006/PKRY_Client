import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private JButton doTransferButton;
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
        defaultTableModel.addColumn("Dane konta");
        defaultTableModel.addColumn("Wartości");
        table1.setModel(defaultTableModel);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("Logout" + " " + login + " " + sessionID);
            }
        });
        refreshAccountInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("getaccount" + " " + login + " " + sessionID);
            }
        });
        doTransferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransferWindow transferWindow = new TransferWindow(client,login,sessionID);
            }
        });
    }

    public void makeTable(DefaultTableModel defaultTableModel, String data){

        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("Dane konta");
        defaultTableModel.addColumn("Wartości");
        table1.setModel(defaultTableModel);

        String[] array = data.split(" ");
        String[] infoArray = {"Balance","Currency","Number","FirstName","LastName"};

        for(int i=1;i<array.length;i++){
            defaultTableModel.addRow(new Object[]{infoArray[i-1],array[i]});
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
        else if (data.contentEquals("transferok")){
            JOptionPane.showConfirmDialog(null, "Poprawnie wykonano przelew", "Powiadomienie", JOptionPane.DEFAULT_OPTION);
        }
        else if (data.contains("account")){
            makeTable(defaultTableModel,data);
        }
        return null;
    }
}