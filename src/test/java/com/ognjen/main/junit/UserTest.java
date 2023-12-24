//https://www.baeldung.com/spring-jpa-test-in-memory-database
//https://stackoverflow.com/questions/44626347/junit-5-jdbc-statements-testing
// Klasa za testiranje metoda klase User.

package com.ognjen.main.junit;

import com.ognjen.main.server.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        User user = new User(id, name);

        // Act
        UUID newId = UUID.randomUUID();
        String newName = "Jane Doe";
        user.setId(newId);
        user.setName(newName);

        // Assert
        assertEquals(newId, user.getId());
        assertEquals(newName, user.getName());
    }

    @Test
    void testToString() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        User user = new User(id, name);

        // Act
        String toStringResult = user.toString();

        // Assert
        assertEquals("User{id=" + id + ", name='" + name + "'}", toStringResult);
    }

    @Test
    void testEquals() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        User user1 = new User(id, name);
        User user2 = new User(id, name);

        // Assert
        assertEquals(user1, user2);
    }

    @Test
    void testNotEquals() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        String name1 = "John Doe";
        String name2 = "Jane Doe";
        User user1 = new User(id1, name1);
        User user2 = new User(id2, name2);

        // Assert
        assertNotEquals(user1, user2);
    }
}
