package com.ognjen.main.junit;

import com.ognjen.main.db.UserTable;
import com.ognjen.main.server.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class UserTableTest {

    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        // Establish a connection to an in-memory H2 database
        connection = DriverManager.getConnection("jdbc:mysql://root@localhost:3306/cs202-pz");
        // Create the user table
        UserTable.createTable(connection);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // Close the connection after all tests
        connection.close();
    }

    @Test
    void testInsertUserIntoDatabase() throws SQLException {
        UUID uuid = UUID.randomUUID();
        String username = "testUser";
        String password = "testPassword";

        assertTrue(UserTable.insertUserIntoDatabase(connection, uuid, username, password));

        // Validate that the user is inserted by checking if it can be retrieved
        assertTrue(UserTable.validateUser(connection, username, password));
    }

    @Test
    void testValidateUser() throws SQLException {
        UUID uuid = UUID.randomUUID();
        String username = "testUser";
        String password = "testPassword";

        // Insert a user into the database
        UserTable.insertUserIntoDatabase(connection, uuid, username, password);

        // Validate the user
        assertTrue(UserTable.validateUser(connection, username, password));
    }

    @Test
    void testIsUserAdmin() throws SQLException {
        UUID uuid = UUID.randomUUID();
        String username = "adminUser";
        String password = "adminPassword";

        // Insert an admin user into the database
        UserTable.insertUserIntoDatabase(connection, uuid, username, password);
        UserTable.setAdminVerified(connection, username);

        // Check if the user is an admin
        assertTrue(UserTable.isUserAdmin(connection, username));

        // Insert a non-admin user into the database
        UUID nonAdminUuid = UUID.randomUUID();
        String nonAdminUsername = "nonAdminUser";
        String nonAdminPassword = "nonAdminPassword";
        UserTable.insertUserIntoDatabase(connection, nonAdminUuid, nonAdminUsername, nonAdminPassword);

        // Check if the non-admin user is not an admin
        assertFalse(UserTable.isUserAdmin(connection, nonAdminUsername));
    }

    @Test
    void testRegisterUser() throws SQLException {
        String username = "newUser1";
        String password = "newUserPassword1";

        // Register a new user
        User registeredUser = UserTable.registerUser(connection, username, password);

        // Validate that the user is registered by checking if it can be retrieved
        assertNotNull(registeredUser);
        assertTrue(UserTable.validateUser(connection, username, password));
    }

    @Test
    void testSetAdmin() throws SQLException {
        UUID uuid = UUID.randomUUID();
        String username = "adminUser";
        String password = "adminPassword";

        // Insert a user into the database
        UserTable.insertUserIntoDatabase(connection, uuid, username, password);

        // Set the user as admin
        assertTrue(UserTable.setAdmin(connection, username));

        // Check if the user is an admin
        assertTrue(UserTable.isUserAdmin(connection, username));
    }
}
