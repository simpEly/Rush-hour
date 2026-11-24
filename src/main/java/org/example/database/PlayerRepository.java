package org.example.database;

import java.nio.file.Path;
import java.sql.*;

public class PlayerRepository {

    private final String url;

    public PlayerRepository() {
        this(Path.of("rushhour.db"));
    }

    PlayerRepository(Path databaseFile) {
        url = "jdbc:sqlite:" + databaseFile;
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS players (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    wins INTEGER NOT NULL DEFAULT 0
                )
                """;

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void addWin(String playerName) {
        String sql = """
            INSERT INTO players (name, wins)
            VALUES (?, 1)
            ON CONFLICT(name)
            DO UPDATE SET wins = wins + 1
            """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, playerName);
            statement.executeUpdate();

            System.out.println("Win recorded for " + playerName);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void showHighScores() {
        String sql = """
            SELECT name, wins
            FROM players
            ORDER BY wins DESC, name ASC
            """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            System.out.println("===== HIGH SCORES =====");
            System.out.printf("%-20s %s%n", "Player", "Wins");
            System.out.println("--------------------------");

            while (result.next()) {
                String name = result.getString("name");
                int wins = result.getInt("wins");

                System.out.printf("%-20s %d%n", name, wins);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}