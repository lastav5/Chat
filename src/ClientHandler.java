import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private String clientName = "";
    private Socket socket = null;
    private Server server = null;
    private BufferedReader clientReader = null;
    private PrintWriter clientWriter = null;


    public String getClientName() {
        return clientName;
    }

    public PrintWriter getClientWriter() {
        return clientWriter;
    }

    public ClientHandler(Socket socket, Server serverParam) throws Exception {
        try {
            clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientWriter = new PrintWriter(socket.getOutputStream(), true);
            server = serverParam;
        } catch (Exception e) {
            System.out.println("An error has occurred while initializing ClientHandler: " + e.getMessage());
            throw e;
        }
    }

    public void run() {
        try {
            // Get client's name
            clientName = clientReader.readLine();

            // Send a list of the current chat members to the client
            clientWriter.println(server.getClientsNamesList());

            // Send a message to all clients that this client has joined
            server.distributeMessageToOthers(clientName+" has joined the chat", this);

            String message;
            // Handle messages from the client
            while ((message = clientReader.readLine())!=null) {
                server.distributeMessage(message);
            }

            // stream has ended
            //remove this client from the server's clients list
            server.removeClientHandlerFromList(this);
        } catch (Exception e) {
            System.out.println("An error has occurred while running the ClientHandler thread: " + e.getMessage());
            System.exit(1);
        }
    }
}
