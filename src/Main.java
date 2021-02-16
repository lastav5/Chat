/*
* Stav Lagziel - 313236457
* */
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        String name1 = JOptionPane.showInputDialog("Enter your name to join the chat");
        String name2 = JOptionPane.showInputDialog("Enter your name to join the chat");
        try {
            Server server = new Server();
            server.start();

            Client client1 = new Client(name1);
            client1.start();

            Thread.sleep(1000);

            Client client2 = new Client(name2);
            client2.start();

            Thread.sleep(10000);

            String name3 = JOptionPane.showInputDialog("Enter your name to join the chat");
            Client client3 = new Client(name3);
            client3.start();
        } catch (Exception e) {
            System.out.println("An error has occurred: " + e.getMessage());
        }

    }
}
