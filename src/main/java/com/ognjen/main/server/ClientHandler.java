package com.ognjen.main.server;
import com.ognjen.main.db.DatabaseConnection;
import com.ognjen.main.db.MessageTable;
import com.ognjen.main.db.UserTable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

// Handling request from each client.
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Server server;
    private PrintWriter out;

    private String username;
    private Boolean admin;
    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
            try {
                var in = new Scanner(clientSocket.getInputStream());
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                while(true) {
                    try {
                        Object receivedObject = in.nextLine();
                        System.out.println(receivedObject);
                        if (receivedObject.toString().equals("getMessages")) {
                            if (server.getClientsConnected().get(this.username) == null) {
                                server.addClientToServer(out, this.username);
                            }
//                          access to chat history only if client is admin
                            if (this.admin) {
                                for (String message : server.getChatMessages()) {
                                    out.println("message " + message);
                                }
                            }
                        }else if (receivedObject.toString().startsWith("isAdmin")) {
                            this.username = ((String) receivedObject).split(" ")[1];
                            Connection connection = DatabaseConnection.connect();
                            this.admin = UserTable.isUserAdmin(connection, this.username);
                            out.println(this.admin);
                            connection.close();
                        }else if (receivedObject.toString().startsWith("message")) {
                            String message = ((String) receivedObject).split(" ")[1] + ((String) receivedObject).split(" ")[2];
                            broadcastMessage(message);
                        }else {
                            if ("terminate".equals(receivedObject)) {
                                System.out.println("Terminating session");
                                removeClient(clientSocket, username);
                            }
                        }
                    }catch (Exception e) {
                        removeClient(clientSocket, username);
                        break;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }
    private synchronized void broadcastMessage(String message) throws IOException, SQLException {
        Connection connection = DatabaseConnection.connect();
        for (Map.Entry<String, PrintWriter> entry : server.getClientsConnected().entrySet()) {
            PrintWriter recipientOutput = entry.getValue();
            server.addChatMessage(message);
            MessageTable.insertMessageIntoDatabase(connection, message, server.getId());
            recipientOutput.println("message " + message);
            recipientOutput.flush();
        }
        connection.close();
        // Close the server-side socket to signal the end of the stream
        // This is crucial for the client to detect the end of the stream
    }
    public void removeClient(Socket client, String username) {
        server.removeClientFromServer(username);
        System.out.println("Client disconnected: " + client.getInetAddress().toString());
    }
}