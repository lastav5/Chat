import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private ServerSocket serverSocket = null;
    private List<ClientHandler> clients = null;

    public Server() {
        clients = new ArrayList<ClientHandler>();
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(7777);
        } catch (IOException e) {
            System.out.println("Cannot listen on this port. Error message: " + e.getMessage());
            return;
        }

        while (true) try {
            Socket clientSocket = serverSocket.accept(); //waits for a new client to connect
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clientHandler.start();
            clients.add(clientHandler);
            //distributeMessage(clientHandler.getName()+" has joined the chat.");
        } catch (Exception e) {
            System.out.println("An error has occurred while accepting a connection: " + e.getMessage());
            closeConnection();
        }
    }

    public String getClientsNamesList() {
        String clientsNamesList = "";
        for (ClientHandler client : clients) {
            clientsNamesList += client.getClientName().replace(';', ' ') + ";";
        }

        return clientsNamesList;
    }

    public void distributeMessage(String message) {
        for (ClientHandler client : clients) {
            client.getClientWriter().println(message);
        }
    }

    //sends to all clients except the given client
    public void distributeMessageToOthers(String message, ClientHandler ch) {
        for (ClientHandler client : clients) {
            if(client != ch) {
                client.getClientWriter().println(message);
            }
        }
    }

    public void removeClientHandlerFromList(ClientHandler ch){
        clients.remove(ch);
    }

    private void closeConnection() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("An error has occurred while closing the server socket: " + e.getMessage());
        }
    }
}
