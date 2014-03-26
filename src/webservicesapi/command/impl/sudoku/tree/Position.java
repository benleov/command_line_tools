package webservicesapi.command.impl.sudoku.tree;

public class Position {

    private int x, y, feedX, feedY;

    public Position(int x, int y, int feedX, int feedY) {
        this.x = x;
        this.y = y;
        this.feedX = feedX;
        this.feedY = feedY;
    }

    public int getFeedX() {
        return feedX;
    }

    public int getFeedY() {
        return feedY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
