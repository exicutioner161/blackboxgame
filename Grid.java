import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Grid {
    private static final Random random = new Random();
    private static final int INITIAL_ARRAY_SIZE = 12;
    private static final int NUM_MIRRORS = 10;
    private static final int INITIAL_GUESSES = 20;
    private static final int INITIAL_SHOTS = 25;
    private static final Map<Character, int[]> STATIONS = new HashMap<>();
    private final Cell[][] arr;
    private final List<String> shotsMade;
    private int mirrorsLeft;
    private int guessesLeft;
    private int shotsLeft;
    private int consecNonModMovesMade;
    private boolean modifiedInLastTurn;

    public Grid() {
        this.arr = new Cell[INITIAL_ARRAY_SIZE][INITIAL_ARRAY_SIZE];
        this.shotsMade = new ArrayList<>();
        this.mirrorsLeft = NUM_MIRRORS;
        this.guessesLeft = INITIAL_GUESSES;
        this.shotsLeft = INITIAL_SHOTS;
        this.consecNonModMovesMade = 0;
        this.modifiedInLastTurn = false;
        createNewGrid();
    }

    public char shoot(char character) {
        modifiedInLastTurn = false;
        if (shotsLeft <= 0) {
            System.out.println("Invalid: You have 0 shots left.");
            return ' ';
        }
        shotsLeft--;
        consecNonModMovesMade++;
        int[] coords = STATIONS.get(character);
        Point point = new Point(coords[0], coords[1]);
        Laser laser = new Laser(arr, point, getStationDirection(point));
        return cacheShot(character, laser.move());
    }

    private int getStationDirection(Point coords) {
        return switch (coords.x) {
            case 0 -> 1;
            case 11 -> 3;
            default -> getYDirection(coords.y);
        };
    }

    private int getYDirection(int y) {
        return switch (y) {
            case 0 -> 2;
            case 11 -> 0;
            default -> throw new IllegalArgumentException("Invalid y value: " + y);
        };
    }

    private char cacheShot(char source, char dest) {
        shotsMade.add(source + " -> " + dest);
        return dest;
    }

    public boolean guess(char x, char y) {
        modifiedInLastTurn = false;
        if (guessesLeft <= 0) {
            System.out.println("Invalid: You have 0 guesses left.");
        }
        guessesLeft--;
        int col = STATIONS.get(x)[1];
        int row = STATIONS.get(y)[0];
        if (arr[row][col].isMirror()) {
            arr[row][col].setHidden(false);
            mirrorsLeft--;
            modifiedInLastTurn = true;
            consecNonModMovesMade = 0;
            return true;
        }
        consecNonModMovesMade++;
        return false;
    }

    public final void createNewGrid() {
        createEmptyGrid();
        placeCorners();
        placeStations();
        placeMirrors();
    }

    private void createEmptyGrid() {
        for (Cell[] row : arr) {
            for (int c = 0; c < row.length; c++) {
                row[c] = new Cell('.', false);
            }
        }
    }

    private void placeCorners() {
        arr[0][0].setChar('*');
        arr[0][11].setChar('*');
        arr[11][0].setChar('*');
        arr[11][11].setChar('*');
    }

    private char lowercaseChar(int i) {
        return (char) (97 + i);
    }

    private char uppercaseChar(int i) {
        return (char) (65 + i);
    }

    private void placeRowStations() {
        for (int i = 0; i < arr[0].length - 2; i++) {
            arr[0][i + 1].setChar(lowercaseChar(i));
            arr[11][i + 1].setChar(uppercaseChar(i));
            STATIONS.put(arr[0][i + 1].getChar(), new int[] { 0, i + 1 });
            STATIONS.put(arr[11][i + 1].getChar(), new int[] { 11, i + 1 });
        }
    }

    private void placeColumnStations() {
        for (int i = 0; i < arr.length - 2; i++) {
            int shortenedLen = arr[0].length - 1;
            arr[i + 1][0].setChar(lowercaseChar(i + 10));
            arr[i + 1][shortenedLen].setChar(uppercaseChar(i + 10));
            STATIONS.put(arr[i + 1][0].getChar(), new int[] { i + 1, 0 });
            STATIONS.put(arr[i + 1][shortenedLen].getChar(), new int[] { i + 1, shortenedLen });
        }
    }

    private void placeStations() {
        placeRowStations();
        placeColumnStations();
    }

    private int[] randomCoords() {
        return new int[] { random.nextInt(1, 10), random.nextInt(1, 10) };
    }

    private int[] handleCoordCollisions(List<int[]> randomCoords) {
        int[] coords = randomCoords();
        while (randomCoords.contains(coords)) {
            coords = randomCoords();
        }
        return coords;
    }

    private List<int[]> getRandomCoords() {
        List<int[]> randomCoords = new ArrayList<>();
        for (int i = 0; i < NUM_MIRRORS; i++) {
            int[] coords = handleCoordCollisions(randomCoords);
            randomCoords.add(coords);
        }
        return randomCoords;
    }

    private char randomMirror() {
        return random.nextInt(0, 2) == 0 ? '/' : '\\';
    }

    private void placeMirrors() {
        List<int[]> randomMirrorCoords = getRandomCoords();
        for (int[] coords : randomMirrorCoords) {
            arr[coords[0]][coords[1]].setChar(randomMirror()).setHidden(true);
        }
    }

    public Cell[][] getArr() {
        return arr;
    }

    public int getMirrorsLeft() {
        return mirrorsLeft;
    }

    public void setMirrorsLeft(int mirrorsLeft) {
        this.mirrorsLeft = mirrorsLeft;
    }

    public int getGuessesLeft() {
        return guessesLeft;
    }

    public void setGuessesLeft(int guessesLeft) {
        this.guessesLeft = guessesLeft;
    }

    public int getShotsLeft() {
        return shotsLeft;
    }

    public void setShotsLeft(int shotsLeft) {
        this.shotsLeft = shotsLeft;
    }

    public Map<Character, int[]> stations() {
        return STATIONS;
    }

    public List<String> getCachedShots() {
        return shotsMade;
    }

    public int consecNonModMovesMade() {
        return consecNonModMovesMade;
    }

    public void setConsecNonModMovesMade(int consecNonModMovesMade) {
        this.consecNonModMovesMade = consecNonModMovesMade;
    }

    public boolean wasModifiedInLastTurn() {
        return modifiedInLastTurn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Cell[] row : getArr()) {
            for (int i = 0; i < row.length - 1; i++) {
                sb.append(row[i]).append("  ");
            }
            sb.append(row[11]).append("\n");
        }
        return sb.toString();
    }
}