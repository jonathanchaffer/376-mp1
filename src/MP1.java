import java.util.Scanner;

public class MP1 {
    String domain;
    String password;
    String ipAddress;

    public void promptUser() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter domain:");
        domain = scan.nextLine();
        System.out.println("Enter password:");
        password = scan.nextLine();
        System.out.println("Enter IP address:");
        ipAddress = scan.nextLine();
    }

    public static void main(String[] args) {
        MP1 mp1 = new MP1();
        mp1.promptUser();
    }
}
