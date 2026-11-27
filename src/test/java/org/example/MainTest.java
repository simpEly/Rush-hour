package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import org.example.database.PlayerRepository;
import org.example.engine.GameFactory;
import org.example.model.SavedGame;
import org.example.model.Turn;
import org.example.service.Game;
import org.example.service.LevelSelector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

class MainTest {
    @Test
    @ResourceLock("java.lang.System.in")
    void shouldInitializeScoresRetryBlankNameAndStartSelectedGame() {
        InputStream originalInput = System.in;
        SavedGame selected = new SavedGame(GameFactory.createDefaultBoard(), Turn.COMPUTER);
        var selectorInput = new AtomicReference<Scanner>();

        try (var repositories = mockConstruction(PlayerRepository.class);
             var selector = mockStatic(LevelSelector.class);
             var games = mockConstruction(Game.class, (game, construction) -> {
                 assertSame(selected, construction.arguments().get(0));
                 assertSame(selectorInput.get(), construction.arguments().get(1));
                 assertEquals("Ada Lovelace", construction.arguments().get(2));
                 assertSame(repositories.constructed().getFirst(), construction.arguments().get(3));
             })) {
            selector.when(() -> LevelSelector.selectGame(any(Scanner.class))).thenAnswer(call -> {
                var repository = repositories.constructed().getFirst();
                var startup = inOrder(repository);
                startup.verify(repository).createTable();
                startup.verify(repository).showHighScores();

                Scanner scanner = call.getArgument(0);
                assertEquals("2", scanner.nextLine());
                selectorInput.set(scanner);
                return selected;
            });
            System.setIn(new ByteArrayInputStream(
                    "\n \t\n  Ada Lovelace  \n2\n".getBytes(StandardCharsets.UTF_8)));

            Main.main(new String[0]);

            assertEquals(1, repositories.constructed().size());
            assertEquals(1, games.constructed().size());
            selector.verify(() -> LevelSelector.selectGame(selectorInput.get()));
            verify(games.constructed().getFirst()).start();
        } finally {
            System.setIn(originalInput);
        }
    }
}
