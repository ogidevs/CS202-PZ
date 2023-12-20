package com.ognjen.main.server;

import com.ognjen.main.db.MessageTable;
import com.ognjen.main.db.DatabaseConnection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

// Server class, which is used to simulate a working Server on some X location, working in a thread for simulation purpose.
public class Server implements Serializable, Runnable {
    private int id;
    private String host;
    private int port;
    private String name;
    private transient ServerSocket serverSocket;
    private List<String> chatMessages;
    private transient Map<String, PrintWriter> clientsConnected = new HashMap<>();

    public Server(Integer id, String host, int port, String name) throws IOException, SQLException {
        this.id = id;
        this.host = host;
        this.port = port;
        this.name = name;
        this.serverSocket = new ServerSocket(this.port);
        Connection connection = DatabaseConnection.connect();
        chatMessages = MessageTable.getServerMessages(connection, id);
        connection.close();
    }

//  Establish the connection with the client and handle the request in ClientHandler, in a separate thread.
//  This connection is kept alive until the client leaves the room.
    @Override
    public void run() {
        try {
            System.out.println(this.name + " is listening on port " + this.port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(this.getName() + ": Client connected from " + clientSocket.getInetAddress());
                ClientHandler c = new ClientHandler(clientSocket, this);
                new Thread(c).start();
            }
        } catch (IOException e) {
//          handle losing the connection.
            System.out.println(this.getName() + ": Connection lost to one client!");
            e.printStackTrace();
        }
    }

    // Method to stop the server and release resources
    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public List<String> getChatMessages() {
        return chatMessages;
    }
    public void setChatMessages(List<String> s) {
        chatMessages = s;
    }
    public void addChatMessage(String s) {
        chatMessages.add(s);
    }
    public void purgeChatHistory() {
        chatMessages.clear();
    }

    public Map<String, PrintWriter> getClientsConnected() {
        return clientsConnected;
    }
    public void addClientToServer(PrintWriter client, String username) {
        this.clientsConnected.put(username, client);
    }
    public PrintWriter getClientFromServer(String username) {
        return this.clientsConnected.get(username);
    }
    public void removeClientFromServer(String username) {
        this.clientsConnected.remove(username);
    }
    public void setClientsConnected(Map<String, PrintWriter> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "name='" + name + '\'' +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
