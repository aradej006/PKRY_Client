import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * Created by arade on 04-Jan-16.
 */

/**
 * Class which makes SSL Client and connects to the server
 * @author Piotr Januszewski
 * @author Adrian Radej
 * @author Monika Stępkowska
 */
public class Client implements Runnable {

    private SSLSocket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Handle handle;

    @SuppressWarnings("unused")
    private int id;

    /**
     * Suppresses default constructor
     */
    private Client(){}

    /**
     * Class constructor
     * @param host Server IP address
     * @param port Server port number
     * @param handle Class which implements Handle interface
     * @throws Exception
     */
    public Client(String host, int port, Handle handle) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        this.handle = handle;
        socket = null;
        try {
            SSLSocketFactory socketfactory =(SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) socketfactory.createSocket(host,port);
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        } catch (NumberFormatException e) {
        }
        try {
            input = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
        }
        new Thread(this).start();
        send(TProtocol.LOGIN);
    }

    /**
     * Tells if Socket between server and client is disconnected
     * @return Bool
     */
    public synchronized boolean isDisconnected() {
        return socket == null;
    }

    /**
     * Function in which client is running
     */
    public void run() {
        while (true)
            try {
                String command = input.readLine();
                if (!handleCommand(command) || isDisconnected()) {
                    output.close();
                    input.close();
                    socket.close();
                    break;
                }
            } catch (IOException e) {
            }
        output = null;
        input = null;
        synchronized (this) {
            socket = null;
        }
    }

    /**
     * Function which handle data with commands from server
     * @param command Data with commands from server
     * @return Bool
     */
    private synchronized boolean handleCommand(String command) {
        StringTokenizer st = new StringTokenizer(command);
        String cd = st.nextToken();

        if (cd.equals(TProtocol.LOGGEDIN)) {
            id = Integer.parseInt(st.nextToken());
        }else if (cd.equals(TProtocol.DATA)) {
            String msg = handle.handle(command.substring(command.indexOf(' ') + 1));
            if (msg != null)
                sendData(msg);
        } else if (cd.equals(TProtocol.LOGGEDOUT)) {
            return false;
        } else if (cd.equals(TProtocol.STOP)) {
            send(TProtocol.STOPPED);
            return false;
        }
        return true;
    }

    /**
     * Function used to send data with command to server
     * @param command Data with command which is send to server
     */
    void send(String command) {
        if (output != null)
            output.println(command);
    }

    /**
     * Function used to send data to server
     * @param data Data which is send to server
     */
    public void sendData(String data){
        send(TProtocol.DATA + " " + data);
    }

    /**
     * Function used to do force logout from server
     */
    public synchronized void forceLogout() {
        if (socket != null)
            send(TProtocol.LOGOUT);
    }

    /**
     * Function which closes Socket
     * @throws IOException
     */
    public void closeSocket() throws IOException {
        try {
            send(TProtocol.LOGOUT);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function which helps to change class which implements Handle interface
     * @param handle Class which implements Handle interface
     */
    public void changeHandle(Handle handle){
        this.handle = handle;
    }
}