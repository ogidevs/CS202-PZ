package com.ognjen.main.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Handling the database connection
// Change to fit database needs...
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/cs202-pz";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection connect() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create a connection
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // TEST DATABASE CONNECTION and insert X num of chatrooms to Database
    public static void main(String[] args) throws IOException, SQLException {
        Connection connection = connect();
        BasicSetup.setupDataBase(connection);
        close(connection);
    }
}