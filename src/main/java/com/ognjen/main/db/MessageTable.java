package com.ognjen.main.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageTable {
    public static void createTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS message (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "message TEXT NOT NULL," +
                    "creationDate DATETIME NOT NULL," +
                    "server_id INT," +  // Foreign key referencing the server table
                    "FOREIGN KEY (server_id) REFERENCES server(id))";
            statement.execute(sql);
            System.out.println("Message Table created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertMessageIntoDatabase(Connection connection, String message, Integer server_id) throws SQLException {
        String insertDataSQL = "INSERT INTO message (message, creationDate, server_id) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL)) {
            // Set values for the prepared statement
            preparedStatement.setString(1, message);
            // Set the current timestamp for the creationDate
            preparedStatement.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            preparedStatement.setInt(3, server_id);

            // Execute the insert statement
            preparedStatement.execute();
        }
    }

    public static List<String> getServerMessages(Connection connection, Integer server_id_p) throws SQLException {
        try (connection) {
            List<String> l = new ArrayList<>();
            String sql = "SELECT * FROM message WHERE server_id=" + server_id_p;

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                // Loop through the result set
                while (resultSet.next()) {
                    // Retrieve data from each row
                    String message = resultSet.getString("message");
                    l.add(message);
                }
                return l;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
