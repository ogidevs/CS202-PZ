package com.ognjen.main.db;

import com.ognjen.main.server.Server;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServerTable {
    public static void createTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS server (" +
                    "id INT PRIMARY KEY," +
                    "hostname TEXT NOT NULL," +
                    "port INT NOT NULL," +
                    "name TEXT NOT NULL)";
            statement.execute(sql);
            System.out.println("Server Table created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertServerIntoDatabase(Connection connection, Server s) throws SQLException {
        String insertDataSQL = "INSERT INTO server (id, hostname, port, name) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL)) {
            // Set values for the prepared statement
            preparedStatement.setInt(1, s.getId());
            preparedStatement.setString(2, s.getHost());
            preparedStatement.setInt(3, s.getPort());
            preparedStatement.setString(4, s.getName());
            // Execute the insert statement
            preparedStatement.execute();
        }
    }
    public static List<Server> getAllEntriesFromServerTable(Connection connection) {
        try (connection) {
            List<Server> l = new ArrayList<>();
            String sql = "SELECT * FROM server";

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                // Loop through the result set
                while (resultSet.next()) {
                    // Retrieve data from each row
                    int id = resultSet.getInt("id");
                    String host = resultSet.getString("hostname");
                    int port = resultSet.getInt("port");
                    String serverName = resultSet.getString("name");
                    l.add(new Server(id, host, port, serverName));
                }
                return l;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
