package com.ognjen.main.db;

import com.ognjen.main.server.Server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

//Set up the database with different tables, create if not already present.
public class BasicSetup {
    public static void setupDataBase(Connection connection) throws SQLException, IOException {
        System.out.println("Connected to the database");

        // Create a table
        ServerTable.createTable(connection);
        UserTable.createTable(connection);
        MessageTable.createTable(connection);

        System.out.println("Disconnected from the database");
    }
}
