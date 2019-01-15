/*
Class written by
Changend by Thomas Bayer (Jan. 2019):
- StateSpaceTree replaced by HeuristicStateSpaceSolver (and AStateSpaceSolver)
- Enabled choice of distance
- minor improvements
 */
package de.hrw.wi.puzzle.gui.window;

//import de.hrw.wi.java.statespace.Board;

import de.hrw.wi.java.puzzle.Board;
import de.hrw.wi.java.puzzle.HeuristicStateSpaceSolver;
import de.hrw.wi.java.statespace.IEvaluate;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static de.hrw.wi.puzzle.gui.window.GUI.*;
import static java.lang.String.format;

public class GUIBoard extends Board {

    public enum SPEED {
        SLOW, MEDIUM, FAST
    }

    private int timerSpeed = 500;
    private HeuristicStateSpaceSolver solver;
    private IEvaluate<Board> distance;
    private long elapsedTime;
    private int stateCounter;
    private int dupStateCounter;
    private boolean solving = false;

    public GUIBoard(int[][] blocks) {
        super(blocks);
        solver = new HeuristicStateSpaceSolver();
    }

    public GUIBoard(Board b) {
        super(b);
    }

    public GUIBoard(boolean start) {
        super(start);
    }

    public void tilePressed(int btn) {
        Board board = null;
        int[] space = spaceLocation();
        // right
        if ((btn % 3) == space[1] + 1 && (btn / 3) == space[0]) {
            board = this.move(0, 1);
        }
        // left
        else if ((btn % 3) == space[1] - 1 && (btn / 3) == space[0]) {
            board = this.move(0, -1);
        }
        // up
        else if ((btn % 3) == space[1] && (btn / 3) == space[0] + 1) {
            board = this.move(1, 0);
        }
        // down
        else if ((btn % 3) == space[1] && (btn / 3) == space[0] - 1) {
            board = this.move(-1, 0);
        }

        // save new state
        if (board != null) {
            init(board.getBlocks());
        }
    }


    public List<Board> solve() {
        // Get time needed to solve
        elapsedTime = System.nanoTime();
        List<Board> solutionPath = solver.simpleSolve(this, distance);
        elapsedTime = (System.nanoTime() - elapsedTime) / 1000000;

        stateCounter = solver.getStateCounter();
        dupStateCounter = solver.getDupStateCounter();
        return solutionPath;
    }

    public void solve(GUI gui) {
        this.solving = true;

        //AStateSpaceSolver<Board> solver = new HeuristicStateSpaceSolver(gui.guiBoard);

        // Get time needed to solve
        long time = System.nanoTime();
        //List<Board> solutionPath = states.simpleSolve(gui.guiBoard);
        List<Board> solutionPath = solver.simpleSolve(gui.guiBoard, distance);
        time = (System.nanoTime() - time) / 1000000;

        // Save time, moves and nodes on label
        gui.setStatus(time, solver.getStateCounter(), solver.getDupStateCounter(), solutionPath.size(), solutionPath.size(),
                      gui.guiBoard.manhattan());

        // Perform moves equal to timerSpeed
        new Timer(this.timerSpeed, new ActionListener() {
            List<Board> solutionPath;
            private Board bc;
            private int counter = 0;
            private long time;
            private int nrOfMoves;

            public ActionListener me(List<Board> solutionPath, Board bc, long time) {
                this.bc = bc;
                this.solutionPath = solutionPath;
                this.time = time;
                nrOfMoves = solutionPath.size();
                gui.toggleSolveButton();
                return this;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop solving when all moves are done or button reset pressed (goal state)
                if (solutionPath.isEmpty() || isGoal()) {
                    GUIBoard.this.solving = false;
                    ((Timer) e.getSource()).stop();
                    return;
                }
                if (GUIBoard.this.solving == false) {
                    ((Timer) e.getSource()).stop();
                    return;
                }
                // Set guiBoard and update the GUI
                Board saveBoard;
                saveBoard = this.solutionPath.get(counter++);
                bc.setBlocks(saveBoard.getBlocks());

                gui.setStatus(time, solver.getStateCounter(), solver.getDupStateCounter(), nrOfMoves, nrOfMoves - counter,
                              bc.manhattan() + bc.getWeight());
                gui.setText(format("Manhatten = %d, Hamming = %d, weight = %d", bc.manhattan(), bc.hamming(), bc.getWeight()));
                gui.drawBoard();
            }
        }.me(solutionPath, this, time)).start();
    }

    public boolean isSolving() {
        return this.solving;
    }

    public void setTimerSpeed(SPEED speed) {
        switch (speed) {
            case SLOW:
                this.timerSpeed = 500;
                break;
            case MEDIUM:
                this.timerSpeed = 250;
                break;
            case FAST:
                this.timerSpeed = 50;
                break;
        }
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

    public void stop() {
        this.solving = false;
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

    public IEvaluate<Board> getDistance() {
        return distance;
    }
}
