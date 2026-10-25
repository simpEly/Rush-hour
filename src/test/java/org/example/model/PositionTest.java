package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
    @Test
    void shouldComparePositionsByCoordinates() {
        Position position = new Position(2, 3);
        Position samePosition = new Position(2, 3);

        assertEquals(position, samePosition);
        assertEquals(samePosition, position);
        assertEquals(position.hashCode(), samePosition.hashCode());
        assertNotEquals(position, new Position(2, 4));
        assertNotEquals(position, new Position(1, 3));
        assertFalse(position.equals(null));
        assertFalse(position.equals("(2, 3)"));
    }

    @Test
    void shouldDescribeCoordinates() {
        assertEquals("(2, 3)", new Position(2, 3).toString());
    }
}
