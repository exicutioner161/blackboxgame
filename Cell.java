public class Cell {
    private static final char DISPLAY_CHAR = '.';
    private char trueCharacter;
    private boolean hidden;

    public Cell(char character, boolean hidden) {
        this.trueCharacter = character == DISPLAY_CHAR ? DISPLAY_CHAR : character;
        this.hidden = hidden;
    }

    public char getDisplayChar() {
        return DISPLAY_CHAR;
    }

    public char getChar() {
        return trueCharacter;
    }

    public Cell setChar(char character) {
        this.trueCharacter = character;
        return this;
    }

    public boolean isMirror() {
        return trueCharacter == '\\' || trueCharacter == '/';
    }

    public boolean isHidden() {
        return hidden;
    }

    public Cell setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public String toString() {
        return hidden ? String.valueOf(DISPLAY_CHAR) : String.valueOf(trueCharacter);
    }
}