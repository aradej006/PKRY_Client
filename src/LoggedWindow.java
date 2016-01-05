import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

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
    private JButton getHistoryButton;
    private DefaultTableModel defaultTableModel;

    private String login;
    private String sessionID;
    private Client client;

    private JFrame jFrame;

    public LoggedWindow(String login1, Client client1, String sessionid){
        super("BankClientApplication");
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jFrame = this;
        this.login = login1;
        this.client = client1;
        this.sessionID = sessionid;
        this.client.changeHandle(this);
        label1.setForeground(Color.red);
        label1.setText("Logged in as: " + login);

        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("Account Informations");
        defaultTableModel.addColumn("Values");

        table1.setModel(defaultTableModel);
        table1.setAutoCreateRowSorter(true);

        table1.setAutoCreateRowSorter(true);

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
                TransferWindow transferWindow = new TransferWindow(client,login,sessionID,jFrame);
            }
        });
        getHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("gethistory" + " " + login + " " + sessionID);
            }
        });

        refreshAccountInfoButton.doClick();
    }

    public void makeAccountTable(String data){

        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("Account Informations");
        defaultTableModel.addColumn("Values");
        table1.setModel(defaultTableModel);

        String[] array = data.split(" ");
        String[] infoArray = {"Balance","Currency","Number","FirstName","LastName"};
        for(int i=1;i<array.length;i++)
            defaultTableModel.addRow(new Object[]{infoArray[i-1],array[i]});
    }
    public void makeHistoryTable(String data){
        defaultTableModel = new DefaultTableModel();
        String[] infoArray = {"Amount","Currency","FromAccount","ToAccount","TransferDate"};
        for (int i = 0;i<infoArray.length;i++)
            defaultTableModel.addColumn(infoArray[i]);
        table1.setModel(defaultTableModel);

        String[] array = data.split(" ");

        for(int i=1;i<array.length;i+=5)
            defaultTableModel.addRow(new Object[]{array[i],array[i+1],array[i+2],array[i+3],new Date(Long.parseLong(array[i+4])) });
    }
    public String handle(String data) {
        if(data.contains("LOGOUT")) {
            try {
                client.closeSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(jFrame, "Logged out correctly", "Information", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            LoggingWindow loggingWindow = new LoggingWindow();
        }
        else if (data.contentEquals("TRANSFER DONE")){
            JOptionPane.showMessageDialog(jFrame, "Done transfer correctly", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        else if (data.contains("account")){
            makeAccountTable(data);
        }else if (data.contains("history")) {
            makeHistoryTable(data);
        }else if (data.contains("ERROR")){
            if(data.equals("ERROR SESSION EXPIRED")){
                try {
                    client.closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JOptionPane.showMessageDialog(jFrame, data, "ERROR", JOptionPane.ERROR_MESSAGE);
                dispose();
                LoggingWindow loggingWindow = new LoggingWindow();
            }
            else
                JOptionPane.showMessageDialog(jFrame, data, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}