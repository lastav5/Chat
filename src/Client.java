import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.net.Socket;


public class Client extends Thread {
    private String name;
    private JFrame jframe;
    private JTextArea chat_text_area = null;
    private JTextField text_field = null;
    private JButton sendBtn = null;
    private JButton logoutBtn = null;
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private boolean is_logged_in = false;

    public Client(String name){
        this.name = name;
    }

    private void setupUI() {
        jframe = new JFrame(name);

        chat_text_area = new JTextArea("",7, 20);
        chat_text_area.setSize(200, 300);
        chat_text_area.setBackground(Color.lightGray);
        chat_text_area.setEditable(false);

        text_field = new JTextField(10);
        text_field.setSize(100, 50);

        sendBtn = new JButton("Send");
        sendBtn.setSize(300, 100);

        logoutBtn = new JButton("Log Out");
        logoutBtn.setSize(300, 100);

        JPanel send_panel = new JPanel();
        JLabel label = new JLabel("Say anything...");

        send_panel.add(label);
        send_panel.add(text_field);
        send_panel.add(sendBtn);
        send_panel.add(logoutBtn);

        jframe.getContentPane().add(BorderLayout.SOUTH, send_panel);
        jframe.add(new JScrollPane(chat_text_area), BorderLayout.CENTER);

        jframe.setSize(400,550);
        jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jframe.setVisible(true);
    }

    private void setActionListeners() {
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                out.println(name+": "+text_field.getText());
                text_field.setText("");
            }
        });

        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                is_logged_in = false;
                jframe.dispatchEvent(new WindowEvent(jframe, WindowEvent.WINDOW_CLOSING));
                closeConnection();
            }
        });
    }

    private void connectToServer() throws Exception {
        try {
            socket = new Socket("localhost", 7777);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // First message to server must be the client's name
            out.println(name);

            Thread.sleep(100);

            // Client then receives a list of the current chat members
            // Note: in.readLine() is blocking until it gets input from the server
            String[] clientsNamesList = in.readLine().split(";");
            for (String currentClientName : clientsNamesList) {
                chat_text_area.append("'" + currentClientName + "'" + " is in the chat.\n");
            }

            is_logged_in = true;
        } catch (Exception e) {
            System.out.println("An error has occurred while initiating the connection to the server: " + e.getMessage());
            throw e;
        }
    }

    private void receiveMessages() throws Exception {
        try {
            String message;
            while (is_logged_in) {
                // The server returns the messages formatted, so no need to format them here
                message = in.readLine();
                chat_text_area.append(message + "\n");
            }
        } catch (Exception e) {
            System.out.println("An error has occurred while receiving messages from the server: " + e.getMessage());
            throw e;
        }
    }

    private void closeConnection() {

        //Send a message that the user is leaving
        out.println(name+" has left the chat.");
        try {
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            System.out.println("An error has occurred while closing PrintWriter: " + e.getMessage());
        }

        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            System.out.println("An error has occurred while closing BufferedReader: " + e.getMessage());
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            System.out.println("An error has occurred while closing the connection to the server: " + e.getMessage());
        }
    }

    public void run() {
        try {
            setupUI();
            setActionListeners();
            connectToServer();
            receiveMessages();
        } catch (Exception e) {
            System.out.println("An error has occurred in a user thread: " + e.getMessage());
        } finally {
            //closeConnection();
        }

    }
}
