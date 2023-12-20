package com.ognjen.main.junit;

import com.ognjen.main.db.MessageTable;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.*;

class MessageTableTest {
    @Test
    void testInsertMessageIntoDatabase() throws SQLException {
        // Mock dependencies
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        // Mock the behavior of the connection.prepareStatement()
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);

        // Call the method you want to test
        MessageTable.insertMessageIntoDatabase(connection, "Test Message", 1);

        // Verify that the appropriate methods were called on the mocks
        verify(connection, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).setString(eq(1), eq("Test Message"));
        verify(preparedStatement, times(1)).setTimestamp(eq(2), any(java.sql.Timestamp.class));
        verify(preparedStatement, times(1)).setInt(eq(3), eq(1));
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void testGetServerMessages() throws SQLException {
        // Mock dependencies
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        // Mock the behavior of the connection.prepareStatement()
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);

        // Mock the behavior of the preparedStatement.executeQuery()
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Mock the behavior of the resultSet.next() and resultSet.getString()
        when(resultSet.next()).thenReturn(true).thenReturn(false); // Simulate one row
        when(resultSet.getString("message")).thenReturn("Test Message");

        // Call the method you want to test
        List<String> messages = MessageTable.getServerMessages(connection, 1);

        // Verify that the appropriate methods were called on the mocks
        verify(connection, times(1)).prepareStatement(any(String.class));
    }
    // Add more tests for other methods
}