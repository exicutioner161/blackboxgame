import java.util.Scanner;

public class BlackBoxDriver {
    private static final String SEPARATOR = "------------------------";
    private static boolean justShot = false;
    private static boolean justGuessed = false;
    private static boolean correctGuess = false;
    private static char output = ' ';

    private BlackBoxDriver() {
    }

    private static void confirmGuess() {
        if (justGuessed) {
            if (correctGuess) {
                System.out.println("Your guess was correct!");
            } else {
                System.out.println("Your guess was incorrect.");
            }
        }
    }

    private static boolean guessedAllMirrors(Grid grid) {
        if (grid.getMirrorsLeft() <= 0) {
            System.out.println("\nCongratulations, you won!");
            return true;
        }
        return false;
    }

    private static void printAllPreviousPaths(Grid grid) {
        System.out.println(SEPARATOR);
        for (String str : grid.getCachedShots()) {
            System.out.println(str);
        }
        System.out.println(SEPARATOR);
    }

    private static void printGrid(Grid grid) {
        System.out.println(grid.toString());
    }

    private static void printStats(Grid grid) {
        System.out.println("Shots left: " + grid.getShotsLeft()
                + "\nGuesses left: " + grid.getGuessesLeft()
                + "\nMirrors left: " + grid.getMirrorsLeft()
                + "\n" + SEPARATOR);
    }

    private static void printShotDestination() {
        if (justShot) {
            System.out.println("Landed at station: " + output);
        }
    }

    private static void showOptions() {
        System.out.println("""
                Choose an option:
                1. Shoot laser
                2. Guess mirror location
                3. Print shot history
                4. Print grid
                Q. Quit game""");
    }

    private static boolean outOfShotsAndOrGuesses(Grid grid) {
        if (grid.getGuessesLeft() <= 0) {
            if (grid.getShotsLeft() <= 0) {
                System.out.println("You lost! You have run out of shots and guesses.");
            } else {
                System.out.println("You lost! You have run out of guesses.");
            }
            return true;
        }
        return false;
    }

    private static boolean handleInputOrQuit(Scanner input, Grid grid) {
        while (true) {
            String in = input.nextLine();
            switch (in) {
                case "1" -> {
                    handleShootingInput(input, grid);
                    return false;
                }
                case "2" -> {
                    handleGuessingInput(input, grid);
                    return false;
                }
                case "3" -> {
                    printAllPreviousPaths(grid);
                    showOptions();
                    continue;
                }
                case "4" -> {
                    printGrid(grid);
                    showOptions();
                    continue;
                }
                case "Q", "q" -> {
                    return true;
                }
                default -> System.out.println("Invalid option: " + in + ". Please try again.");
            }
            System.out.println(SEPARATOR);
        }
    }

    private static void handleShootingInput(Scanner input, Grid grid) {
        justGuessed = false;
        justShot = true;
        while (true) {
            System.out.println("Which station would you like to shoot from?");
            String in = input.nextLine();
            char character = in.isEmpty() ? ' ' : in.charAt(0);
            if (grid.stations().containsKey(character)) {
                output = grid.shoot(character);
                return;
            }
            System.out.println("Not a station: '" + character + "'");
        }
    }

    private static void handleGuessingInput(Scanner input, Grid grid) {
        justGuessed = true;
        justShot = false;
        correctGuess = false;
        while (true) {
            System.out.println("Enter coordinates in (col,row) or col,row format:");
            String in = input.nextLine();
            if (in.length() <= 1) {
                System.out.println("Input is too short: " + in);
                continue;
            }
            char x = 0;
            char y = 0;
            boolean validInput = false;
            if (in.charAt(0) == '(') {
                x = in.charAt(1);
                y = in.charAt(3);
                validInput = true;
            } else if (in.length() == 3 && in.charAt(1) == ',') {
                x = in.charAt(0);
                y = in.charAt(2);
                validInput = true;
            }
            if (!validInput || String.valueOf(x).equalsIgnoreCase(String.valueOf(y))) {
                System.out.println("Invalid input: " + in);
                continue;
            }
            if (grid.stations().containsKey(x) && grid.stations().containsKey(y)) {
                correctGuess = grid.guess(x, y);
                return;
            }
        }
    }

    private static void runGame(Scanner input, Grid grid) {
        printGrid(grid);
        while (true) {
            if (grid.wasModifiedInLastTurn() || grid.consecNonModMovesMade() > 3) {
                printGrid(grid);
            }
            printStats(grid);
            printShotDestination();
            confirmGuess();
            if (guessedAllMirrors(grid)) {
                return;
            }
            if (outOfShotsAndOrGuesses(grid)) {
                return;
            }
            showOptions();
            if (handleInputOrQuit(input, grid)) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        Grid grid = new Grid();
        try (Scanner input = new Scanner(System.in)) {
            long startTime = System.currentTimeMillis();
            runGame(input, grid);
            long elapsedMillis = System.currentTimeMillis() - startTime;
            double seconds = elapsedMillis / 1000.0;
            double minutes = seconds / 60.0;
            System.out.printf("Elapsed time: %.2f minutes, %.2f seconds, %d milliseconds%n%s%n",
                    minutes, seconds, elapsedMillis, SEPARATOR);
        }
    }
}