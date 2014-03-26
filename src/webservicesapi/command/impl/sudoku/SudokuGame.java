package webservicesapi.command.impl.sudoku;

import webservicesapi.command.impl.sudoku.tree.PositionNode;

/**
 */
public class SudokuGame {

    private PositionNode last;
    private int[][] answer;
    private int[][] feed;
    private int unplaceable;

    public SudokuGame(PositionNode last, int[][] feed, int[][] game, int unplaceable) {
        this.last = last;
        this.feed = feed;

        this.answer = game;
        this.unplaceable = unplaceable;
    }


    public int getUnplaceable() {
        return unplaceable;
    }

    public int getUnplacedCount() {

        int count = 0;

        for (int y = 0; y < answer.length; y++) {
            for (int x = 0; x < answer[y].length; x++) {
                if (answer[x][y] == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public PositionNode getNodes() {
        return last;
    }

    public int[][] getAnswer() {
        return answer;
    }

    public int[][] getFeed() {
        return feed;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder();
        string.append("(x)- - - - - - - - -\n");

        if (answer != null) {
            for (int x = 0; x < answer.length; x++) {
                string.append("|" + x + "|");
                for (int y = 0; y < answer[x].length; y++) {
                    string.append(answer[x][y]);
                    string.append(" ");
                }
                string.append("\n");
            }
        }
        string.append("(x)- - - - - - - - -\n");

        return string.toString();
    }

}
