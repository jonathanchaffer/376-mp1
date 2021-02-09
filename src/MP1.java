import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MP1 {
    static final int DEFAULT_PORT = 443;
    static final int TIMEOUT = 10 * 1000;
    static final String HOST = "dynamicdns.park-your-domain.com";
    static final int SLEEP_TIME = 180 * 1000;

    private String domain;
    private String password;
    private String ipAddress;

    private PrintStream send;
    private DataInputStream reply;
    private SSLSocket sock;

    public void clearCache() {
        System.setProperty("networkaddress.cache.ttl", "0");
    }

    public void printWelcome() {
        System.out.println("Welcome! This app lets you update the Dynamic DNS\nrecord associated with a Namecheap domain.");
        System.out.println();
    }

    public void promptUser() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter domain name:");
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
        System.out.println("Request has been sent.");
    }

    public boolean checkResponse() {
        System.out.println("Now checking response...");
        StringBuilder buffer = new StringBuilder();
        boolean eof = false;
        while (!eof) {
            try {
                byte b = reply.readByte();
                if (b != 0) buffer.append(Character.toChars(b));
                else eof = true;
            } catch (Exception e) {
                eof = true;
            }
        }
        closeSocket();
        return buffer.toString().contains("<ErrCount>0</ErrCount>");
    }

    public void closeSocket() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sleep() throws InterruptedException {
        System.out.println("Now sleeping for " + SLEEP_TIME / 1000 + " seconds. Please wait...");
        Thread.sleep(SLEEP_TIME);
        System.out.println("Done sleeping.");
    }

    public void verifyRequest() throws UnknownHostException {
        InetAddress resolution = InetAddress.getByName(domain);
        String resolvedAddress = resolution.getHostAddress();
        System.out.println("Resolved address: " + resolvedAddress);
        if (resolvedAddress.equals(ipAddress))
            System.out.println("Success! This matches the IP address you inputted.");
        else
            System.err.println("Something went wrong. This does not match the IP address you inputted.");
    }

    public static void main(String[] args) {
        try {
            MP1 mp1 = new MP1();
            mp1.clearCache();
            mp1.printWelcome();
            mp1.promptUser();
            mp1.initializeSocket();
            mp1.sendRequest();
            if (!mp1.checkResponse()) {
                System.err.println("Something went wrong. The server responded with an error.");
                return;
            }
            mp1.sleep();
            mp1.verifyRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
