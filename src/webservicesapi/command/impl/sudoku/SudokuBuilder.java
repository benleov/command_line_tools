package webservicesapi.command.impl.sudoku;

import webservicesapi.command.impl.sudoku.tree.Position;
import webservicesapi.command.impl.sudoku.tree.PositionNode;

import java.util.*;

/**
 */
public class SudokuBuilder {

    public static final int ROW_SIZE = 9;
    public static final int ROW_COUNT = 9;
    public static final int BOX_SIZE = 3;

    private static final Random random = new Random(new Date().getTime());

    public SudokuBuilder() {
    }

    /**
     * Verify that the specified answer conforms to all the rules of Sudoku.
     *
     * @param game The answer to verify.
     * @return
     */
    public boolean verifyAnswer(int[][] game) {
        boolean valid = true;

        outer:
        for (int y = 0; y < game.length; y++) {
            for (int x = 0; x < game[y].length; x++) {

                valid = testRow(x, y, game[x][y], true, game);

                if (valid) {
                    valid = testCol(x, y, game[x][y], true, game);

                    if (valid) {
                        valid = testBox(x, y, game[x][y], SudokuBuilder.BOX_SIZE, game);
                    }
                }

                if (!valid) {
                    break outer;
                }
            }
        }

        return valid;
    }

    /**
     * Builds a sudoku game.
     *
     * @return
     */
    public SudokuGame buildGame() {

        SudokuGame game = buildTree();

        while (true) {

            if (game == null) {
                game = buildTree();
            }

            int unplaced = game.getUnplacedCount();

            if (unplaced == 0) {
                break;
            } else {
                game = buildTree(game);
            }
        }

        return game;


    }

    /**
     * @return Returns all the numbers that our sudoku game should contain. Each row contains 1 - 9, and is valid
     *         across rows and columns, but not 3x3 boxes.
     */
    public int[][] buildFeed() {

        int[][] grid = new int[ROW_COUNT][];

        for (int x = 0; x < ROW_COUNT; x++) {
            grid[x] = buildRow(x);
        }

        return grid;
    }

    public void randomiseFeed(int[][] feed) {


        int iterations = random.nextInt(100);

        for (int next = 0; next < iterations; ++next) {
            int x = random.nextInt(feed.length);
            int y = random.nextInt(feed.length);

            int newY = random.nextInt(feed.length);
            int newX = random.nextInt(feed.length);

            // swap

            int temp = feed[x][y];
            feed[x][y] = feed[newX][newY];
            feed[newX][newY] = temp;
        }
    }


    public int[][] buildBlank(int size) {
        int[][] game = new int[size][];

        // init game so its the same size as the feeds grid

        for (int x = 0; x < size; x++) {
            game[x] = new int[size];
        }

        return game;
    }


    private int[] buildRow(int offset) {

        int[] row = new int[ROW_SIZE];

        for (int x = 0; x < row.length; x++) {
            row[x] = ((x + offset) % 9) + 1;
        }

        return row;

    }

    // try rebuild the tree

    public SudokuGame buildTree(SudokuGame game) {

        PositionNode curr = game.getNodes();

        while (curr != null) {

            Set<Position> positions = curr.getAvailablePositions();

            if (positions.size() > 0) {

                // revert the position of this node
                // removePosition from game

                game.getAnswer()[curr.getCurrentPosition().getX()][curr.getCurrentPosition().getY()] = 0;

                // put in new position
                // TODO: we cant just overwrite an existing value here! We have to find a free one that
                // is valid as the grid may now be more full than when this value was placed.

                Iterator<Position> i = positions.iterator();

                Position valid = null;
                while (i.hasNext()) {
                    Position pos = i.next();
                    i.remove();
                    if (game.getAnswer()[pos.getX()][pos.getY()] == 0) {

                        // check if the position is still valid. if it is, revert it.
                        if (isValid(pos.getX(), pos.getY(), curr.getValue(), BOX_SIZE, game.getAnswer())) {
                            valid = pos;
                            curr.setCurrentPosition(valid);
                            game.getAnswer()[valid.getX()][valid.getY()] = curr.getValue();
                        }
                        break;
                    }
                }

                if (valid != null) {
                    return buildTree(valid.getFeedX(), valid.getFeedY(), game.getFeed(),
                            game.getAnswer(), game.getNodes());
                } else {
                    curr = curr.getParent();
                }


            } else {
                curr = curr.getParent();
            }
        }
        return null;
    }

    public SudokuGame buildTree() {
        int[][] feed = buildFeed();
        randomiseFeed(feed);
        int[][] game = buildBlank(feed.length);
        return buildTree(0, 0, feed, game, null);
    }

    /**
     * Build a position tree, going as far as we can until we come to a dead end
     *
     * @return
     */
    public SudokuGame buildTree(int feedX, int feedY, int[][] feed, int[][] game, PositionNode curr) {

        // create destination

        PositionNode parent = null;
        int noPlace = 0;

        if (curr == null) {
            curr = new PositionNode();
        } else {
            parent = curr;
        }

        outer:

        for (; feedY < feed.length; feedY++) {
            for (; feedX < feed[feedY].length; feedX++) {

                // find a list of positions that this value can go in

                int value = feed[feedX][feedY];

                List<Position> pos = findPositions(0, 0, feedX, feedY, BOX_SIZE, value, game);

                // we set the value and the positions to this current node


                if (pos.size() > 0) {

                    curr = new PositionNode();

                    curr.setValue(value);

                    // link in the parent

                    curr.setParent(parent);

                    Position next = pos.get(random.nextInt(pos.size()));

                    curr.removePosition(next);
                    curr.addAll(pos);
                    curr.setCurrentPosition(next);

                    // update our game

                    game[next.getX()][next.getY()] = value;

                    // set up the parent link for the next node

                    parent = curr;

                    // create a new current, for the next loop


                } else {
                    noPlace = feed[feedX][feedY];
                    break outer;
                }
            }

            feedX = 0;
        }

        return new SudokuGame(curr, feed, game, noPlace);
    }


    /**
     * Test if the valid is valid on this row
     *
     * @param x
     * @param y
     * @param value
     * @param game
     * @return
     */
    public boolean testCol(int x, int y, int value, boolean placed, int[][] game) {

        if (placed) {

            for (int newX = 0; newX < game[x].length; newX++) {
                if ((newX != x) && game[newX][y] == value) {
                    return false;
                }
            }
        } else {
            for (int newX = 0; newX < game[x].length; newX++) {
                if (game[newX][y] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean testRow(int x, int y, int value, boolean placed, int[][] game) {

        if (placed) {
            for (int newY = 0; newY < game[y].length; newY++) {

                if (newY != y && game[x][newY] == value) {
                    return false;
                }
            }
        } else {
            for (int newY = 0; newY < game[y].length; newY++) {

                if (game[x][newY] == value) {
                    return false;
                }
            }
        }


        return true;
    }

    /**
     * Tests if the specified value is valid within this box (not that the box itself is valid).
     *
     * @param x
     * @param y
     * @param value
     * @param boxSize
     * @param game
     * @return
     */
    public boolean testBox(int x, int y, int value, int boxSize, int[][] game) {

        boolean valid = true;
        // check 3x3 box, first get box offset

        int xOffset = (x / boxSize) * boxSize;
        int yOffset = (y / boxSize) * boxSize;


        outer:
        for (int yBox = yOffset; yBox < (yOffset + boxSize); yBox++) {
            for (int xBox = xOffset; xBox < (xOffset + boxSize); xBox++) {

                if (!(xBox == x && yBox == y) && game[xBox][yBox] == value) {

                    valid = false;
                    break outer;
                }
            }
        }

        return valid;
    }

    public boolean isValid(int x, int y, int value, int boxSize, int[][] game) {
        boolean valid = testCol(x, y, value, false, game);

        if (valid) {

            // check column

            valid = testRow(x, y, value, false, game);

            if (valid) {

                return testBox(x, y, value, boxSize, game);

            }
        }
        return false;
    }

    // return a list of the empty positions that this value can be placed within the current game

    private List<Position> findPositions(int startX, int startY, int feedX, int feedY,
                                         int boxSize, int value, int[][] game) {

        List<Position> positions = new ArrayList<Position>();

        // go through the entire grid

        for (int y = startY; y < game.length; y++) {
            for (int x = startX; x < game[y].length; x++) {

                // check for empty spots

                if (game[x][y] == 0) {

                    // check row
                    if (isValid(x, y, value, boxSize, game)) {
                        positions.add(new Position(x, y, feedX, feedY));
                    }

                } // otherwise something already placed in square
            }
        }

        return positions;
    }


    public static int[][] copyOf(int[][] ori) {

        int[][] copy = new int[ori.length][];

        // init game grid

        for (int x = 0; x < ori.length; x++) {
            copy[x] = Arrays.copyOf(ori[x], ori[x].length);
        }

        return copy;
    }

}
