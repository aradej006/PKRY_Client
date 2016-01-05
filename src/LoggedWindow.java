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
    private Client client;
    private String startData;
    private String login;

    public LoggedWindow(String login1, Client client1, String startData){
        super("LoggedWindow");
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.login = login1;
        this.client = client1;
        this.client.changeHandle(this);
        this.startData = startData;
        label1.setForeground(Color.red);
        label1.setText("Logged in as: " + login);

        DefaultTableModel defaultTableModel = new DefaultTableModel();
        makeTable(defaultTableModel,startData);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("Logout" + " " + login);
            }
        });
    }
    public void makeTable(DefaultTableModel defaultTableModel, String data){
        table1.setModel(defaultTableModel);
        defaultTableModel.addColumn("Column1");
        defaultTableModel.addColumn("Column2");
        defaultTableModel.addRow(new Object[]{"Coś tam"});
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
        else if (data.contentEquals("TransferSucceded")){

        }
        return null;
    }
}