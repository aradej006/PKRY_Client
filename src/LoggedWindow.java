import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

/**
 * Created by arade on 07-Jan-16.
 */
/**
 * Window where user is logged in correctly and now can do transfers and gets history of his transfers
 * @author Piotr Januszewski
 * @author Adrian Radej
 * @author Monika Stępkowska
 */
public class LoggedWindow extends JFrame implements Handle{
    private JPanel main;
    private JPanel north;
    private JPanel south;
    private JPanel center;
    private JButton refreshButton;
    private JButton transferButton;
    private JButton historyButton;
    private JTable table1;
    private JButton logoutButton;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private DefaultTableModel defaultTableModel;

    private String login;
    private String sessionID;
    private Client client;

    private JFrame jFrame;

    /**
     * Suppresses default constructor
     */
    private LoggedWindow(){}

    /**
     * Class constructor. Makes window
     * @param login1 User login
     * @param client1 Client which can communicate with server
     * @param sessionId User session ID
     */
    public LoggedWindow(String login1, Client client1, String sessionId){
        super("BankClientApplication");
        setContentPane(main);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jFrame = this;

        this.login = login1;
        this.client = client1;
        this.sessionID = sessionId;
        this.client.changeHandle(this);

        label1.setText("  Logged in as: " + login);

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
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("getaccount" + " " + login + " " + sessionID);
            }
        });
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransferWindow transferWindow = new TransferWindow(client,login,sessionID,jFrame);
            }
        });
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("gethistory" + " " + login + " " + sessionID);
            }
        });
        refreshButton.doClick();
    }

    /**
     * Function which makes table with account information
     * @param data Data from server
     */
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

    /**
     * Function which makes table with transfers history
     * @param data Data from server
     */
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
    /**
     * Handle function, which handle server messages
     * @param data Data from server
     * @return null
     */
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
