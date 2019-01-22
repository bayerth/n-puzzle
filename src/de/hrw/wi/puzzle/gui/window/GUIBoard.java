/**
 * Class written by
 * Changend by Thomas Bayer (Jan. 2019):
 * - StateSpaceTree replaced by HeuristicStateSpaceSolver (and AStateSpaceSolver)
 * - Enabled choice of distance
 * - minor improvements
 */
package de.hrw.wi.puzzle.gui.window;

//import de.hrw.wi.java.statespace.Board8Puzzle;

import de.hrw.wi.java.puzzle.Board8Puzzle;
import de.hrw.wi.java.puzzle.HeuristicStateSpaceSolver;
import de.hrw.wi.java.statespace.IEvaluate;

import java.util.List;

import static de.hrw.wi.puzzle.gui.window.GUI.*;

public class GUIBoard extends Board8Puzzle {

    public static final int DEFAULT_DIMENSION = 3;
    private HeuristicStateSpaceSolver solver;
    private IEvaluate<Board8Puzzle> distance;

    private long elapsedTime;
    private int stateCounter;
    private int dupStateCounter;
    private boolean solving = false;

    public GUIBoard() {
        super(DEFAULT_DIMENSION);
        solver = new HeuristicStateSpaceSolver();
    }

    public GUIBoard(int dimension) {
        super(dimension);
        solver = new HeuristicStateSpaceSolver();
    }

    public GUIBoard(int[][] blocks) {
        super(blocks);
        solver = new HeuristicStateSpaceSolver();
    }

    public GUIBoard(Board8Puzzle b) {
        super(b);
    }

    public GUIBoard(boolean start) {
        super(start);
    }

    public void tilePressed(int btn) {
        Board8Puzzle board = null;
        int[] space = spaceLocation();
        // tile to the right is free
        if ((btn % dimension()) == space[1] + 1 && (btn / dimension()) == space[0]) {
            board = this.move(0, 1);
        }
        // tile to the left is free
        else if ((btn % dimension()) == space[1] - 1 && (btn / dimension()) == space[0]) {
            board = this.move(0, -1);
        }
        // up
        else if ((btn % dimension()) == space[1] && (btn / dimension()) == space[0] + 1) {
            board = this.move(1, 0);
        }
        // down
        else if ((btn % dimension()) == space[1] && (btn / dimension()) == space[0] - 1) {
            board = this.move(-1, 0);
        }

        // save new state
        if (board != null) {
            init(board.getBlocks());
        }
    }


    public List<Board8Puzzle> solve() {
        // Get time needed to solve
        elapsedTime = System.nanoTime();
        List<Board8Puzzle> solutionPath = solver.simpleSolve(this, distance);
        elapsedTime = (System.nanoTime() - elapsedTime) / 1000000;

        stateCounter = solver.getStateCounter();
        dupStateCounter = solver.getDupStateCounter();
        return solutionPath;
    }


    public void setDistance(String distanceName) {
        switch (distanceName) {
            case NONE:
                distance = null;
                break;
            case HAMMING:
                distance = HeuristicStateSpaceSolver.hammingBoard;
                break;
            case WEIGHTED_HAMMING:
                distance = HeuristicStateSpaceSolver.hammingWeightBoard;
                break;
            case MANHATTEN:
                distance = HeuristicStateSpaceSolver.manhattenBoard;
                break;
            case WEIGHTED_MANHATTEN:
                distance = HeuristicStateSpaceSolver.hammingWeightBoard;
                break;
            default:
                distance = null;
                break;
        }
    }


    public long getElapsedTime() {
        return elapsedTime;
    }

    public int getStateCounter() {
        return stateCounter;
    }

    public int getDupStateCounter() {
        return dupStateCounter;
    }

    public IEvaluate<Board8Puzzle> getDistance() {
        return distance;
    }
}
