package com.ognjen.main.db;

import com.ognjen.main.server.User;
import java.sql.*;
import java.util.Base64;
import java.util.UUID;
public class UserTable {
    public static void createTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS user (" +
                    "id VARCHAR(120) PRIMARY KEY," +
                    "username TEXT NOT NULL," +
                    "password TEXT NOT NULL," +
                    "admin INT DEFAULT 0)";
            statement.execute(sql);
            System.out.println("User Table created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean insertUserIntoDatabase(Connection connection, UUID uuid, String username, String password) throws SQLException {
        String insertDataSQL = "INSERT INTO user (id, username, password) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL)) {
            System.out.println(uuid.toString() + username + password);
            // Set values for the prepared statement
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, encodeBase64(password));
            // Execute the insert statement
            preparedStatement.execute();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean validateUser(Connection connection, String username, String password) throws SQLException {
        String sql = "SELECT 1 FROM user WHERE username=? and password=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, encodeBase64(password));

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean isUserAdmin(Connection connection, String username) {
        String sql = "SELECT 1 FROM user WHERE username=? and admin=1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    public static User registerUser(Connection connection, String username, String password) throws SQLException {
        if (!validateUser(connection, username, password)) { // check if user exists
            User u = new User(UUID.randomUUID(), username);
            if (insertUserIntoDatabase(connection, u.getId(), u.getName(), password)) {
                return u;
            }else {
                return null;
            }
        }
        return null;
    }
    public static boolean setAdmin(Connection connection, String username, String password) {
        String sql = "SELECT 1 FROM user WHERE username=? and password=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, encodeBase64(password));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return setAdminVerified(connection, username, password);
                }
            }catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    public static boolean setAdminVerified(Connection connection, String username, String password) {
        String insertDataSQL = "UPDATE `user` SET admin = 1 WHERE username=\"" + username + "\" and password=\"" + encodeBase64(password) + "\";";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL)) {
            preparedStatement.execute();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String encodeBase64(String originalString) {
        // Encoding
        byte[] encodedBytes = Base64.getEncoder().encode(originalString.getBytes());
        return new String(encodedBytes);
    }

    public static String decodeBase64(String encodedString) {
        // Decoding
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }
}
