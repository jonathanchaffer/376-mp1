import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class MP1 {
    static final int DEFAULT_PORT = 443;
    static final int TIMEOUT = 10 * 1000;
    static final String HOST = "dynamicdns.park-your-domain.com";

    private String domain;
    private String password;
    private String ipAddress;

    private PrintStream send;
    private DataInputStream reply;
    private SSLSocket sock;

    public void promptUser() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter domain:");
        domain = scan.nextLine();
        System.out.println("Enter password:");
        password = scan.nextLine();
        System.out.println("Enter IP address:");
        ipAddress = scan.nextLine();
    }

    public void initializeSocket() throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        sock = (SSLSocket) factory.createSocket(HOST, DEFAULT_PORT);
        if (sock != null) {
            sock.startHandshake();
            reply = new DataInputStream(sock.getInputStream());
            send = new PrintStream(sock.getOutputStream());
            sock.setSoTimeout(TIMEOUT);
        }
    }

    public void sendRequest() {
        send.println("GET /update?host=@&domain=" + domain + "&password=" + password + "&ip=" + ipAddress + " HTTP/1.1");
        send.println("Host: " + HOST);
        send.println("");
    }

    public static void main(String[] args) {
        try {
            MP1 mp1 = new MP1();
            mp1.promptUser();
            mp1.initializeSocket();
            mp1.sendRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
