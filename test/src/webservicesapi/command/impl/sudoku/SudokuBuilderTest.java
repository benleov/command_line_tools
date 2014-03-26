package webservicesapi.command.impl.sudoku;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SudokuBuilderTest {

    @BeforeMethod(groups = "sudoku")
    private void testSudokuSetup() {
        // setup
    }

    /**
     * Tests the row and column checker
     */
    @Test(groups = "sudoku")
    public void testRowAndColumn() {

        SudokuBuilder builder = new SudokuBuilder();

        // base for our numbers. this grid should be valid across its rows and columns

        int[][] feed = builder.buildFeed();

        for (int x = 0; x < feed.length; x++) {
            for (int y = 0; y < feed[x].length; y++) {
                assertTrue(builder.testRow(x, y, feed[x][y], true, feed));
                assertTrue(builder.testCol(x, y, feed[x][y], true, feed));
            }
        }

    }

    @Test(groups = "sudoku")
    public void testValidGameManually() {

        int[][] game = {
                {2, 7, 9, 5, 6, 1, 3, 8, 4},
                {8, 5, 3, 4, 2, 7, 9, 1, 6},
                {4, 1, 6, 3, 9, 8, 2, 7, 5},
                {5, 4, 1, 9, 7, 6, 8, 2, 3},
                {7, 3, 8, 2, 5, 4, 1, 6, 9},
                {6, 9, 2, 1, 8, 3, 5, 4, 7},
                {1, 6, 5, 8, 4, 9, 7, 3, 2},
                {9, 8, 4, 7, 3, 2, 6, 5, 1},
                {3, 2, 7, 6, 1, 5, 4, 9, 8}
        };

        SudokuBuilder builder = new SudokuBuilder();

        for (int x = 0; x < game.length; x++) {
            for (int y = 0; y < game[x].length; y++) {
                assertTrue(builder.testRow(x, y, game[x][y], true, game));
                assertTrue(builder.testCol(x, y, game[x][y], true, game));
                assertTrue(builder.testBox(x, y, game[x][y], SudokuBuilder.BOX_SIZE, game));
            }
        }
    }

    @Test(groups = "sudoku")
    public void testInvalidGame() {

        SudokuBuilder builder = new SudokuBuilder();

        int[][] valid = {
                {2, 7, 9, 5, 6, 1, 3, 8, 4},
                {8, 5, 3, 4, 2, 7, 9, 1, 6},
                {4, 1, 6, 3, 9, 8, 2, 7, 5},
                {5, 4, 1, 9, 7, 6, 8, 2, 3},
                {7, 3, 8, 2, 5, 4, 1, 6, 9},
                {6, 9, 2, 1, 8, 3, 5, 4, 7},
                {1, 6, 5, 8, 4, 9, 7, 3, 2},
                {9, 8, 4, 7, 3, 2, 6, 5, 1},
                {3, 2, 7, 6, 1, 5, 4, 9, 8}
        };

        assertTrue(builder.verifyAnswer(valid));

        int[][] invalid = {
                {2, 7, 9, 5, 6, 1, 3, 8, 4},
                {8, 5, 3, 4, 2, 7, 9, 1, 6},
                {4, 1, 6, 3, 9, 8, 2, 7, 5},
                {5, 4, 1, 9, 7, 6, 8, 2, 3},
                {7, 3, 8, 2, 5, 2, 1, 6, 9},
                {6, 9, 2, 1, 8, 3, 5, 4, 7},
                {1, 6, 5, 8, 4, 9, 7, 3, 2},
                {9, 8, 4, 7, 3, 2, 6, 5, 1},
                {3, 2, 7, 6, 1, 5, 4, 9, 8}
        };

        assertFalse(builder.verifyAnswer(invalid));
    }


    @Test(groups = "sudoku")
    public void testInvalidGameManually() {
        int[][] game = {
                {2, 7, 9, 5, 6, 1, 3, 8, 4},
                {8, 5, 3, 4, 2, 7, 9, 1, 6},
                {4, 1, 6, 3, 9, 8, 2, 7, 5},
                {5, 4, 1, 9, 7, 6, 8, 2, 3},
                {7, 3, 8, 2, 5, 4, 1, 6, 9},
                {6, 9, 2, 1, 8, 3, 5, 4, 7},
                {1, 6, 5, 8, 4, 9, 7, 3, 2},
                {9, 8, 4, 7, 3, 2, 6, 5, 1},
                {3, 2, 7, 6, 1, 5, 4, 9, 9}
        };

        SudokuBuilder builder = new SudokuBuilder();

        // test row and col methods individually

        for (int y = 0; y < game.length; y++) {
            for (int x = 0; x < game[y].length; x++) {

                if ((x == 8 && y == 8) || (x == 8 && y == 7) || (x == 4 && y == 8)) {

                    if (x == 8 && y == 7) {
                        assertFalse(builder.testRow(x, y, game[x][y], true, game));
                        assertTrue(builder.testCol(x, y, game[x][y], true, game));
                        assertFalse(builder.testBox(x, y, game[x][y], SudokuBuilder.BOX_SIZE, game));

                    } else if (x == 8 && y == 8) {
                        assertFalse(builder.testRow(x, y, game[x][y], true, game));
                        assertFalse(builder.testCol(x, y, game[x][y], true, game));
                        assertFalse(builder.testBox(x, y, game[x][y], SudokuBuilder.BOX_SIZE, game));
                    } else {
                        assertTrue(builder.testBox(x, y, game[x][y], SudokuBuilder.BOX_SIZE, game));
                    }

                } else {

                    assertTrue(builder.testRow(x, y, game[x][y], true, game));
                    assertTrue(builder.testCol(x, y, game[x][y], true, game));
                    assertTrue(builder.testBox(x, y, game[x][y], SudokuBuilder.BOX_SIZE, game));
                }
            }
        }
    }

    @Test(groups = "sudoku")
    public void testTree() {

        SudokuBuilder builder = new SudokuBuilder();
        SudokuGame game = builder.buildGame();

        assertTrue(builder.verifyAnswer(game.getAnswer()));

        // TODO: keep at least 17 numbers,
        // TODO: gets in loop plus sometimes grid returned is not complete
        // then attempt to solve.

//|0|5 2 3 1 4 8 6 7 9 
//|1|4 6 7 9 3 5 2 8 1
//|2|9 8 1 7 2 6 4 5 3
//|3|8 5 9 6 1 2 3 4 7
//|4|7 4 2 3 8 9 5 1 6
//|5|1 3 6 4 5 7 9 2 8
//|6|6 7 8 2 9 4 1 3 5
//|7|3 9 4 5 7 1 8 6 2
//|8|2 1 5 8 6 3 7 9 4

        System.out.println(game.toString());


    }


}
