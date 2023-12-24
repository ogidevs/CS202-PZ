package com.ognjen.main.server;

import com.ognjen.main.client.DataContainer;
import com.ognjen.main.db.BasicSetup;
import com.ognjen.main.db.ServerTable;
import com.ognjen.main.db.DatabaseConnection;
import com.ognjen.main.db.UserTable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// What is this app:
// This is a simulation of a system where there is one central server (ServerHub) which handles clients, and redirects them
// to different servers (ChatRooms) across the globe. There is one central database.
// Every ChatRoom is a standalone server, with its own ip and port.
// The traditional system that works this way is made separately, meaning, Server, ClientApp and ServerHub are all different
// apps working together. For simplicity reasons, in this project, everything is bundled together, thus, simulation.

// Class used to establish the initial connection between the client and server hub.

public class ServerHub {
    private static final Integer SERVER_HUB_PORT = 5555;
    private static List<Server> serverList;

    public static void main(String[] args) throws IOException {
        ServerHub s = new ServerHub();
        System.out.println("ServerHub: Creating servers...");
        System.out.println("ServerHub: Setting up database...");
        try {
            Connection connection = DatabaseConnection.connect();
            BasicSetup.setupDataBase(connection);
            serverList = ServerTable.getAllEntriesFromServerTable(connection);
            if (serverList == null || serverList.size() == 0) {
                ServerTable.insertServerIntoDatabase(connection, new Server(1, "localhost", 12345, "Server #1"));
                ServerTable.insertServerIntoDatabase(connection, new Server(2, "localhost", 12346, "Server #2"));
                ServerTable.insertServerIntoDatabase(connection, new Server(3, "localhost", 12347, "Server #3"));
                ServerTable.insertServerIntoDatabase(connection, new Server(4, "localhost", 12348, "Server #4"));
            }
            for (Server server : serverList) {
                new Thread(server).start();
            }
            DatabaseConnection.close(connection);
            System.out.println("ServerHub: Database setup finished!");
            s.start(SERVER_HUB_PORT);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("ServerHub: Database error, probably offline!");
            System.exit(0);
        }

    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ServerHub is running on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("ServerHub: Client connected from " + clientSocket.getInetAddress());
                ObjectOutputStream clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream clientInput = new ObjectInputStream(clientSocket.getInputStream());
                Object clientMessage = clientInput.readObject();

                if ("getServers".equals(clientMessage)) {
                    System.out.println("ServerHub: Sending list of servers to client: " + clientSocket.getInetAddress());
                    clientOutput.writeObject(getAvailableServers());
                }else if (clientMessage instanceof DataContainer) {
                    String username = ((DataContainer) clientMessage).getName();
                    String password = ((DataContainer) clientMessage).getPassword();
                    String type = ((DataContainer) clientMessage).getType();
                    if (type.equals("login")) {
                        Connection connection = DatabaseConnection.connect();
                        if (UserTable.validateUser(connection, username, password)) {
                            for (Server server : serverList) {
                                if (server.getId() == ((DataContainer) clientMessage).getServer().getId()) {
                                    if (server.getClientsConnected().size() > 0 && server.getClientFromServer(username) != null) {
                                        clientOutput.writeObject("failed_login");
                                        clientOutput.flush(); // Ensure data is sent immediately
                                    }else {
                                        System.out.println("ServerHub: Redirecting client to " + server);
                                        System.out.println("ServerHub: Client redirected, closing connection: " + clientSocket.getInetAddress());
                                        clientOutput.writeObject("success_login");
                                        clientOutput.flush(); // Ensure data is sent immediately
                                        clientSocket.close();
                                    }
                                }
                            }
                        }else {
                            clientOutput.writeObject("failed_login");
                            clientOutput.flush(); // Ensure data is sent immediately
                        }
                        connection.close();
                    } else if (type.equals("register")) {
                        Connection connection = DatabaseConnection.connect();
                        if (UserTable.registerUser(connection, username, password) != null) {
                            clientOutput.writeObject("success_register");
                            clientOutput.flush(); // Ensure data is sent immediately
                        }else {
                            clientOutput.writeObject("failed_register");
                            clientOutput.flush(); // Ensure data is sent immediately
                        }
                        connection.close();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            System.out.println("connection lost to one client!");
        }
    }

    public synchronized List<Server> getAvailableServers() {
        return new ArrayList<>(serverList);
    }
}
