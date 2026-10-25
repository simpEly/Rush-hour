package org.example.model;

public final class Position {
    private final int column;
    private final int row;
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }
    public int getColumn() {
        return column;
    }
    public int getRow() {
        return row;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position other = (Position) o;
        return  column == other.column &&
                row == other.row;
    }
    @Override
    public int hashCode() {
        return 31 * row + column;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
