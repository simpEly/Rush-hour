package org.example.service;

import org.example.engine.GameFactory;
import org.example.model.SavedGame;
import org.example.model.Turn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class LevelSelector {
    private static final Logger logger = LoggerFactory.getLogger(LevelSelector.class);


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
                logger.debug("Level selection was not an integer");
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (level < 0 || level > levelCount) {
                logger.debug("Level selection out of range: {}", level);
                System.out.println("Invalid level. Please try again.");
                continue;
            }

            if (level == 0) {
                logger.debug("Player chose to load a named save");
                System.out.print("Enter saved game name: ");
                String name = scanner.nextLine().trim();

                if (!name.matches("[a-zA-Z0-9_-]+")) {
                    logger.debug("Rejected invalid save name");
                    System.out.println("Invalid save name.");
                    continue;
                }

                String path = "saves/" + name + ".txt";

                try {
                    savedGame = GameFactory.loadGameFromFile(path);
                } catch (IOException | IllegalArgumentException e) {
                    logger.warn("Could not load named save from {}", path, e);
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
        logger.debug("Player selected level {}", level);
        String path = "List of level/level" + level + ".txt";

        try {
            return GameFactory.loadGameFromFile(path);
        } catch (IOException | IllegalArgumentException e) {
            logger.warn("Could not load level {}", level, e);
            System.out.println("Cannot load level: " + e.getMessage());
        }

        while (true) {
            System.out.println("1. Play default level");
            System.out.println("2. Choose another level");
            System.out.print("Your choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                logger.info("Playing the default board after level {} failed to load", level);
                return new SavedGame(
                        GameFactory.createDefaultBoard(), Turn.HUMAN
                );
            } else if (choice.equals("2")) {
                logger.debug("Returning to level selection after level {} failed to load", level);
                return null;
            }

            logger.debug("Rejected invalid level fallback choice");
            System.out.println("Invalid choice.");
        }
    }

}
