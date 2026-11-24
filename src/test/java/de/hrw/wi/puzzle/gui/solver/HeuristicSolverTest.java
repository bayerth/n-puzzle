package de.hrw.wi.puzzle.gui.solver;

import de.hrw.wi.java.puzzle.Board8Puzzle;
import de.hrw.wi.java.puzzle.HeuristicStateSpaceSolver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeuristicSolverTest {

    @Test
    void goalBoardHasAOneStateSolution() {
        Board8Puzzle board = new Board8Puzzle(new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        });

        List<Board8Puzzle> solution =
                new HeuristicStateSpaceSolver().simpleSolve(board, HeuristicStateSpaceSolver.manhattenBoard);

        assertEquals(1, solution.size());
        assertTrue(solution.get(solution.size() - 1).isGoal());
    }

    @Test
    void oneMoveBoardReturnsGoal() {
        Board8Puzzle board = new Board8Puzzle(new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 0, 8}
        });

        List<Board8Puzzle> solution =
                new HeuristicStateSpaceSolver().simpleSolve(board, HeuristicStateSpaceSolver.manhattenBoard);

        assertFalse(solution.isEmpty());
        assertEquals(2, solution.size());
        assertTrue(solution.get(solution.size() - 1).isGoal());
    }

    @Test
    void hammingAndManhattanHeuristicsFindSameGoal() {
        Board8Puzzle board = new Board8Puzzle(new int[][]{
                {1, 2, 3},
                {4, 0, 6},
                {7, 5, 8}
        });

        List<Board8Puzzle> hammingSolution =
                new HeuristicStateSpaceSolver().simpleSolve(board, HeuristicStateSpaceSolver.hammingBoard);
        List<Board8Puzzle> manhattanSolution =
                new HeuristicStateSpaceSolver().simpleSolve(board, HeuristicStateSpaceSolver.manhattenBoard);

        assertTrue(hammingSolution.get(hammingSolution.size() - 1).isGoal());
        assertTrue(manhattanSolution.get(manhattanSolution.size() - 1).isGoal());
    }
}
