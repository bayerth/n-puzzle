package de.hrw.wi.puzzle.gui.tree;


import de.hrw.wi.puzzle.gui.game.Board;

import java.util.*;

public class StateSpaceSolver {

    private Board root;
    private Map<Board, Board> predecessor;
    private int stateCounter;
    private int dupStateCounter;

    public StateSpaceSolver(Board initialState) {
        this.root = initialState;
        predecessor = new HashMap<>();
        stateCounter = 0;
        dupStateCounter = 0;
    }

    public Board solve() {
        System.out.println(root.toString());
        return bfs(root, null);
    }

    private Board bfs(Board board, ICommand<Board> cmd) {
        Queue<Board> queue = new LinkedList<>(); // First In First Out

        if (board.isGoal()) { return board; }

        queue.add(board);
        while (queue.size() > 0) {
            Board currentBoard = queue.poll(); // erstes Element wird entfernt
            System.out.println("-------------");
            List<Board> newStates = this.generateStates(currentBoard);

            //newStates.forEach(b->System.out.println(b.toString()));
            System.out.println(this.stateCounter + ", " + dupStateCounter);

            // check for goal
            for (Board b : newStates) {
                if (b.isGoal()) {
                    System.out.println(b.toString());
                    int nrOfMoves = 1;
                    Board predecessor = currentBoard;
                    while (!predecessor.equals(board)) {
                        nrOfMoves++;
                        System.out.println(predecessor.toString());
                        predecessor = this.predecessor.get(predecessor);
                        if (predecessor == null) { return b; }
                    }
                    System.out.println(nrOfMoves);
                    return b;
                }
            }

            // analyze new boards
            for (Board board1 : newStates) {
                queue.add(board1);
            }
        }
        return null;
    }

    private List<Board> generateStates(Board currentBoard) {
        List<Board> newStates = new LinkedList<>();

        Board newBoard = currentBoard.move(-1, 0);
        addToNewBoards(currentBoard, newStates, newBoard);
        newBoard = currentBoard.move(1, 0);
        addToNewBoards(currentBoard, newStates, newBoard);
        newBoard = currentBoard.move(0, -1);
        addToNewBoards(currentBoard, newStates, newBoard);
        newBoard = currentBoard.move(0, 1);
        addToNewBoards(currentBoard, newStates, newBoard);
        return newStates;
    }

    private void addToNewBoards(Board currentBoard, List<Board> newStates, Board newBoard) {
        //if (newBoard != null && !visitedStates.contains(newBoard)) {
        if (newBoard != null) {
            if (predecessor.keySet().contains(newBoard)) { dupStateCounter += 1; }
            else {
                newStates.add(newBoard);
                predecessor.put(newBoard, currentBoard);
                stateCounter += 1;
            }
        }
    }
}
