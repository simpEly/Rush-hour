package org.example;

import org.example.database.PlayerRepository;
import org.example.model.SavedGame;
import org.example.service.Game;
import org.example.service.LevelSelector;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        PlayerRepository repository = new PlayerRepository();
        repository.createTable();

        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine().trim();

        while (playerName.isEmpty()) {
            System.out.print("Name cannot be empty. Enter your name: ");
            playerName = scanner.nextLine().trim();
        }

        repository.showHighScores();

        SavedGame savedgame = LevelSelector.selectGame(scanner);

        Game game = new Game(savedgame, scanner, playerName, repository);
        game.start();
    }
}