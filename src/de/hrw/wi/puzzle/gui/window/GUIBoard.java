package de.hrw.wi.puzzle.gui.window;

//import de.hrw.wi.java.game.Board;

import de.hrw.wi.puzzle.gui.game.Board;
import de.hrw.wi.puzzle.gui.tree.StateSpaceTree;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Stack;

public class GUIBoard extends Board {

    public enum SPEED {
        SLOW, MEDIUM, FAST
    }

    ;

    private int timerSpeed = 500;
    //	private ArrayList<Board> solvedBoard = new ArrayList<Board>();
    private boolean solving = false;

    public GUIBoard(int[][] blocks) {
        super(blocks);
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

    public void solve(GUI gui) {
        this.solving = true;

        StateSpaceTree states = new StateSpaceTree(gui.board);

        // Get time needed to solve
        long time = System.nanoTime();
        List<Board> solutionPath = states.simpleSolve(gui.board);
        time = (System.nanoTime() - time) / 1000000;

        // Save moves into Stack
        Stack<int[][]> nextBoard = new Stack<>();

        for (int i = solutionPath.size() - 1; i > 0; i--) {
            nextBoard.push(solutionPath.get(i).getBlocks());
        }
        nextBoard.add(gui.board.getBlocks());

        // Save time, moves and nodes on label
        String status = String.format("<html>Time: %d ms<br/>Moves: %d<br/>All Nodes: %d <br/>Distance: %d</html>", time,
                                      nextBoard.size() - 1, states.getStateCounter(), null); //null = placeholder for distance
        gui.setStatus(status);

        // Perform moves equal to timerSpeed
        new Timer(this.timerSpeed, new ActionListener() {
            private Stack<int[][]> boards;
            public Board bc;

            public ActionListener me(Stack<int[][]> stk, Board _bc) {
                this.boards = stk;
                this.bc = _bc;
                return this;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop solving when all moves are done or button reset pressed (goal state)
                if (boards.empty() || isGoal()) {
                    GUIBoard.this.solving = false;
                    ((Timer) e.getSource()).stop();
                    return;
                }

                // Set board and update the GUI
                Board saveBoard = new Board(boards.pop());
                bc.setBlocks(saveBoard.getBlocks());

                gui.drawBoard();
            }
        }.me(nextBoard, this)).start();
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
}
