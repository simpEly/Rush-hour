package org.example.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

class PlayerRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldPersistWinsAndKeepThemWhenInitializingAgain() throws SQLException {
        Path database = tempDir.resolve("scores.db");
        PlayerRepository repository = new PlayerRepository(database);
        repository.createTable();
        repository.addWin("O'Neil");
        repository.addWin("O'Neil");

        new PlayerRepository(database).createTable();

        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             var statement = connection.createStatement();
             var scores = statement.executeQuery("SELECT name, wins FROM players")) {
            assertTrue(scores.next());
            assertEquals("O'Neil", scores.getString("name"));
            assertEquals(2, scores.getInt("wins"));
            assertFalse(scores.next());
        }
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    void shouldDisplayScoresByWinsThenByName() {
        PlayerRepository repository = new PlayerRepository(tempDir.resolve("scores.db"));
        repository.createTable();
        repository.addWin("Zoe");
        repository.addWin("Bob");
        repository.addWin("Amy");
        repository.addWin("Zoe");

        var output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try (var capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            repository.showHighScores();
        } finally {
            System.setOut(original);
        }

        List<String> rows = output.toString(StandardCharsets.UTF_8).lines()
                .skip(3)
                .map(line -> line.strip().replaceAll("\\s+", " "))
                .toList();
        assertEquals(List.of("Zoe 2", "Amy 1", "Bob 1"), rows);
    }
}
