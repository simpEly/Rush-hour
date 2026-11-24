
package org.example.service;

import org.example.engine.GameFactory;
import org.example.model.SavedGame;
import org.example.model.Turn;

import java.io.IOException;
import java.util.Scanner;

public class LevelSelector {


    public static SavedGame selectGame(Scanner scanner) {
        int levelCount = 6;
        SavedGame savedGame = null;

        while (savedGame == null) {
            System.out.print(
                    "Choose a level (1-" + levelCount
                            + "), or 0 to load saved game: "
            );

            int level;

            try {
                level = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (level < 0 || level > levelCount) {
                System.out.println("Invalid level. Please try again.");
                continue;
            }

            if (level == 0) {
                System.out.print("Enter saved game name: ");
                String name = scanner.nextLine().trim();

                if (!name.matches("[a-zA-Z0-9_-]+")) {
                    System.out.println("Invalid save name.");
                    continue;
                }

                String path = "saves/" + name + ".txt";

                try {
                    savedGame = GameFactory.loadGameFromFile(path);
                } catch (IOException | IllegalArgumentException e) {
                    System.out.println(
                            "Cannot load saved game: " + e.getMessage()
                    );
                }
            } else {
                savedGame = handleLoadLevel(level, scanner);
            }
        }


        return savedGame;
    }

    private static SavedGame handleLoadLevel(int level, Scanner scanner) {
        String path = "list of level/level" + level + ".txt";

        try {
            return GameFactory.loadGameFromFile(path);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Cannot load level: " + e.getMessage());
        }

        while (true) {
            System.out.println("1. Play default level");
            System.out.println("2. Choose another level");
            System.out.print("Your choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                return new SavedGame(
                        GameFactory.createDefaultBoard(), Turn.HUMAN
                );
            } else if (choice.equals("2")) {
                return null;
            }

            System.out.println("Invalid choice.");
        }
    }

}
