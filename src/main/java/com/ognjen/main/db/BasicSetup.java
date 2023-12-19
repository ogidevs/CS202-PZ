package com.ognjen.main.db;

import java.sql.Connection;

//Set up the database with different tables, create if not already present.
public class BasicSetup {
    public static void setupDataBase(Connection connection) {
        System.out.println("Connected to the database");

        // Create a table
        ServerTable.createTable(connection);
        UserTable.createTable(connection);
        MessageTable.createTable(connection);

        System.out.println("Disconnected from the database");
    }
}
