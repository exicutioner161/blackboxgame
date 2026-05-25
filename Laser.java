import java.awt.Point;

public class Laser {
    private static final int NORTH = 0;
    private static final int EAST = 1;
    private static final int SOUTH = 2;
    private static final int WEST = 3;
    private final Cell[][] arr;
    public final Point coords;
    private int direction;

    public Laser(Cell[][] arr, Point coords, int direction) {
        this.arr = arr;
        this.coords = coords;
        this.direction = direction;
    }

    public char move() {
        while (true) {
            moveOneUnit();
            Cell currentCell = arr[coords.x][coords.y];
            boolean finishedPath = checkCurrentCell(currentCell.getChar());
            if (finishedPath) {
                return currentCell.getChar();
            }
        }
    }

    private void moveOneUnit() {
        switch (direction) {
            case NORTH -> moveNorth();
            case EAST -> moveEast();
            case SOUTH -> moveSouth();
            default -> moveWest();
        }
    }

    private boolean checkCurrentCell(char currentChar) {
        switch (currentChar) {
            case '*' -> throw new IllegalStateException("Moved into a corner");
            case '/', '\\' -> switchDirections(currentChar);
            case '.' -> {
                // Keep moving
            }
            default -> {
                return true;
            }
        }
        return false;
    }

    private void moveNorth() {
        coords.y--;
    }

    private void moveEast() {
        coords.x++;
    }

    private void moveWest() {
        coords.x--;
    }

    private void moveSouth() {
        coords.y++;
    }

    public void switchDirections(char mirror) {
        direction = switch (mirror) {
            case '/' -> forwardSlashSwitch();
            case '\\' -> backSlashSwtich();
            default -> throw new IllegalArgumentException("Not a mirror: " + mirror);
        };
    }

    private int forwardSlashSwitch() {
        return switch (direction) {
            case NORTH -> EAST;
            case EAST -> NORTH;
            case SOUTH -> WEST;
            default -> SOUTH;
        };
    }

    private int backSlashSwtich() {
        return switch (direction) {
            case NORTH -> WEST;
            case EAST -> SOUTH;
            case SOUTH -> EAST;
            default -> NORTH;
        };
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}