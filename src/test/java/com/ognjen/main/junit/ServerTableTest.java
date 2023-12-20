package com.ognjen.main.junit;

import com.ognjen.main.db.ServerTable;
import com.ognjen.main.server.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ServerTableTest {

    @Test
    void testInsertServerIntoDatabase() throws SQLException, IOException {
        // Mock dependencies
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        // Create a Server instance for testing
        Server server = new Server(1, "localhost", 8083, "TestServer");

        // Mock the behavior of the connection.prepareStatement()
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);

        // Call the method you want to test
        ServerTable.insertServerIntoDatabase(connection, server);

        // Verify that the appropriate methods were called on the mocks
        verify(connection, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).setInt(eq(1), eq(1));
        verify(preparedStatement, times(1)).setString(eq(2), eq("localhost"));
        verify(preparedStatement, times(1)).setInt(eq(3), eq(8083));
        verify(preparedStatement, times(1)).setString(eq(4), eq("TestServer"));
        verify(preparedStatement, times(1)).execute();
    }
}
