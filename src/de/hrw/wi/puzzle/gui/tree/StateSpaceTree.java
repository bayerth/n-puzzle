package de.hrw.wi.puzzle.gui.tree;


import de.hrw.wi.puzzle.gui.game.Board;

import java.util.*;

public class StateSpaceTree {

    public static final int DEFAULT_DISTANCE = 10000;
    private TreeNode<Board> root;
    private Map<Board, Board> predecessor;
    private int stateCounter;
    private int dupStateCounter;

    private Comparator<TreeNode<Board>> hammingComparator = Comparator.comparingInt(o -> o.getPayload().hamming());
    private Comparator<TreeNode<Board>> hammingPathComparator = Comparator.comparingInt(o -> {return o.getPayload().hamming() + o.getWeight();});
    private Comparator<TreeNode<Board>> manhattenComparator = Comparator.comparingInt(o -> o.getPayload().manhattan());

    private INodeEvaluate<Board> hamming = o -> {return (((Board) o.getPayload()).hamming());};
    private INodeEvaluate<Board> hammingPath = o -> {return (((Board) o.getPayload()).hamming() + o.getWeight());};
    private INodeEvaluate<Board> manhatten = o -> {return (((Board) o.getPayload()).manhattan());};
    private INodeEvaluate<Board> manhattenPath = o -> {return (((Board) o.getPayload()).manhattan() + o.getWeight());};

    private IEvaluate<Board> hammingBoard = o -> {return (o.hamming());};
    private IEvaluate<Board> hammingWeightBoard = o -> {return (o.hamming() + o.getWeight());};
    private IEvaluate<Board> manhattenBoard = o -> {return (o.manhattan());};
    private IEvaluate<Board> manhattenWeightBoard = o -> {return (o.manhattan() + o.getWeight());};

    //private Comparator<TreeNode<Board>> comparator = Comparator.comparingInt()

    public StateSpaceTree(Board initialState) {
        this.root = new TreeNode<>(initialState);
        predecessor = new HashMap<>();
        predecessor.put(initialState, null);
        stateCounter = 0;
        dupStateCounter = 0;
    }

    public List<Board> simpleSolve(Board b) {
        System.out.println(root.getPayload().toString());
        return simpleSolve(b, manhattenBoard);
    }

    public List<Board> simpleSolve(Board b, IEvaluate<Board> heuristic) {
        System.out.println(b.toString());
        Board goal = heuristicSolve(b, heuristic);
        //printSolutionPath(goal, b);
        return builtSolutionPath(goal, b);
    }

    public Board solve() {
        return solve(root.getPayload());
    }

    public Board solve(Board b) {
        Board goal = solve(b, manhattenPath);
        printSolutionPath(goal, root.getPayload());
        return goal;
    }

    public Board solve(Board b, INodeEvaluate<Board> heuristic) {
        System.out.println(root.getPayload().toString());
        //return dfs(root, heuristic)
        TreeNode<Board> start = root;
        if (start.getPayload().equals(b)) { return bfsOptimizedSol(root, heuristic); }
        start = this.containsBoard(b);
        if (start == null) { return null; }
        return bfsOptimizedSol(start, heuristic);
    }

    public int getStateCounter() {
        return stateCounter;
    }

    public int getDupStateCounter() {
        return dupStateCounter;
    }
// --------------------------------------------------------------------------------------------
    // private
    // --------------------------------------------------------------------------------------------

    private Board bfsOptimizedSol(TreeNode<Board> node, INodeEvaluate<Board> heuristic) {
        Queue<TreeNode<Board>> queue;
        if (heuristic == null) { queue = new LinkedList<>();} // First In First Out
        else { queue = new PriorityQueue<>(25000, Comparator.comparingInt(heuristic::evaluate)); }
        node.setMarked();

        if (node.getPayload().isGoal()) { return node.getPayload(); }

        queue.add(node);
        int distance = DEFAULT_DISTANCE;
        int max = DEFAULT_DISTANCE;
        while (queue.size() > 0) {
            System.out.println(queue.size());
            TreeNode<Board> n = queue.poll(); // erstes Element wird entfernt

            distance = n.getPayload().manhattan() + n.getWeight();
            //System.out.println("------------- dist = " + distance);
            System.out.println("------------- dist = " + distance + " , max = " + max);
            // generate all possible states, which have not been evaluated before
            Board currentBoard = n.getPayload();
            //List<Board> newStates = this.generateStates(currentBoard);
            List<Board> newStates = new ArrayList<>(4);
            currentBoard.neighbors().forEach(neighbor -> addToNewBoards(currentBoard, newStates, neighbor));
            for (Board b : newStates) { if (b.hamming() < max) { max = b.hamming(); }}
            System.out.println(this.stateCounter + ", " + dupStateCounter);

            // add states as child nodes (leafs)
            newStates.forEach(b -> n.addChild(b));

            // analyze children (breadth first search)
            for (TreeNode<Board> child : n.getChildren()) {
                if (child.isMarked() == false) {
                    child.setMarked();
                    if (child.getPayload().isGoal()) { return child.getPayload(); }
                    queue.add(child);
                }
            }
        }
        return null;
    }

    private Board heuristicSolve(Board board, IEvaluate<Board> heuristic) {
        Queue<Board> queue;
        if (heuristic == null) { queue = new LinkedList<>();} // First In First Out
        else { queue = new PriorityQueue<>(25000, Comparator.comparingInt(heuristic::evaluate)); }

        if (board.isGoal()) { return board; }

        queue.add(board);
        int distance = DEFAULT_DISTANCE;
        int max = DEFAULT_DISTANCE;
        while (queue.size() > 0) {
            System.out.println(queue.size());
            Board currentBoard = queue.poll(); // erstes Element wird entfernt

            distance = currentBoard.manhattan() + currentBoard.getWeight();
            //System.out.println("------------- dist = " + distance);
            System.out.println("------------- dist = " + distance + " , max = " + max);
            // generate all possible states, which have not been evaluated before
            //List<Board> newStates = this.generateStates(currentBoard);
            List<Board> newStates = new ArrayList<>(4);
            currentBoard.neighbors().forEach(neighbor -> addToNewBoards(currentBoard, newStates, neighbor));
            for (Board b : newStates) { if (b.hamming() < max) { max = b.hamming(); }}
            System.out.println(this.stateCounter + ", " + dupStateCounter);

            // analyze children (breadth first search)
            for (Board child : newStates) {
                if (child.isGoal()) { return child; }
                queue.add(child);
            }
        }
        return null;
    }


    private Board breadthFirstGeneration(TreeNode<Board> node) {
        Queue<TreeNode<Board>> queue = new LinkedList<>(); // First In First Out
        node.setMarked();

        // Ziel erreicht
        if (node.getPayload().isGoal()) { return node.getPayload(); }
        queue.add(node);

        while (queue.size() > 0) {
            System.out.println(queue.size());
            TreeNode<Board> n = queue.poll(); // erstes Element wird entfernt

            System.out.println("-------------");

            // generate all possible states, which have not been evaluated before
            Board currentBoard = n.getPayload();
            List<Board> newStates = this.generateStates(currentBoard);
            System.out.println(this.stateCounter + ", " + dupStateCounter);

            // add states as child nodes (leafs)
            newStates.forEach(b -> n.addChild(b));

            // analyze children
            for (TreeNode<Board> child : n.getChildren()) {
                if (child.getPayload().isGoal()) { return child.getPayload(); }
                queue.add(child);
            }
        }
        return null;
    }

    private Board dfs(TreeNode<Board> node, INodeEvaluate<Board> heuristic) {
        node.setMarked();

        Board payload = node.getPayload();

        if (payload.isGoal()) {
            System.out.println(payload.toString());
            return payload;
        }
        System.out.println("-------------, h = " + payload.hamming() + ", m = " + payload.manhattan());
        // generate all possible states, which have not been evaluated before
        Board currentBoard = payload;
        List<Board> newStates = this.generateStates(currentBoard);

        //newStates.forEach(b->System.out.println(b.toString()));
        System.out.println("states / duplicates " + this.stateCounter + ", " + dupStateCounter);

        // add states as child nodes (leafs)
        newStates.forEach(b -> node.addChild(b));
        List<TreeNode<Board>> children = node.getChildren();
        children.forEach(c -> System.out.print(c.getPayload().manhattan() + " "));
        System.out.println("");
        children.sort(Comparator.comparingInt(heuristic::evaluate));
        //children.sort((a,b)->Integer.compare(heuristic.evaluate(a), heuristic.evaluate(b)));
        for (TreeNode<Board> currentNode : children) {
            if (heuristic.evaluate(node) >= heuristic.evaluate(currentNode)) {
                currentBoard = dfs(currentNode, heuristic);
                if (currentBoard != null) {
                    return currentBoard;
                }
            }
        }
        return null;
    }


    private Board dfs(TreeNode<Board> node, Comparator<TreeNode<Board>> comparator) {
        node.setMarked();

        Board payload = node.getPayload();

        if (payload.isGoal()) {
            System.out.println(payload.toString());
            return payload;
        }
        System.out.println("-------------, h = " + payload.hamming() + ", m = " + payload.manhattan());
        // generate all possible states, which have not been evaluated before
        Board currentBoard = payload;
        List<Board> newStates = this.generateStates(currentBoard);

        //newStates.forEach(b->System.out.println(b.toString()));
        System.out.println("states / duplicates " + this.stateCounter + ", " + dupStateCounter);

        // add states as child nodes (leafs)
        newStates.forEach(b -> node.addChild(b));
        List<TreeNode<Board>> children = node.getChildren();
        children.forEach(c -> System.out.print(c.getPayload().manhattan() + " "));
        System.out.println("");
        children.sort(comparator);
        for (TreeNode<Board> currentNode : children) {
            if (node.getPayload().manhattan() >= currentNode.getPayload().manhattan()) {
                currentBoard = dfs(currentNode, comparator);
                if (currentBoard != null) {
                    return currentBoard;
                }
            }
        }
        return null;
    }

    private List<Board> builtSolutionPath(Board b, Board root) {
        List<Board> solutionPath = new LinkedList<>();
        if (b != null) {
            Board predecessor = b;
            while (!predecessor.equals(root)) {
                solutionPath.add(predecessor);
                predecessor = this.predecessor.get(predecessor);
                if (predecessor == null) { break; }
            }
            Collections.reverse(solutionPath);
        }
        return solutionPath;
    }

    private void printSolutionPath(Board b, Board root) {
        if (b == null) {
            System.out.println("no solution found");
            return;
        }
        System.out.println(b.toString());
        int nrOfMoves = 1;
        Board predecessor = b;
        while (!predecessor.equals(root)) {
            nrOfMoves++;
            System.out.println("-- " + predecessor.hamming() + ", " + predecessor.manhattan());
            System.out.println(predecessor.toString());
            predecessor = this.predecessor.get(predecessor);
            if (predecessor == null) { break; }
        }
        System.out.println("Number of moves: " + nrOfMoves);
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
        if (newBoard != null) {
            if (predecessor.keySet().contains(newBoard)) { dupStateCounter += 1; }
            else {
                newBoard.setWeight(currentBoard.getWeight() + 1);
                newStates.add(newBoard);
                predecessor.put(newBoard, currentBoard);
                stateCounter += 1;
            }
        }
    }


    public boolean contains(Board b) {
        return breadthFirstSearch(this.root, b) != null;
    }

    private TreeNode<Board> containsBoard(Board b) {
        if (!predecessor.keySet().contains(b)) { return null; }
        return breadthFirstSearch(this.root, b);
    }

    private TreeNode<Board> breadthFirstSearch(TreeNode<Board> start, Board board) {
        Queue<TreeNode<Board>> queue = new LinkedList<>(); // First In First Out

        // Ziel erreicht
        if (start.getPayload().equals(board)) { return start; }
        queue.add(start);

        while (!queue.isEmpty()) {
            System.out.println(queue.size());
            TreeNode<Board> n = queue.poll(); // erstes Element wird entfernt

            System.out.println("-------------");

            // analyze children
            for (TreeNode<Board> child : n.getChildren()) {
                if (child.getPayload().equals(board)) { return child; }
                queue.add(child);
                if (child.isMarked() == false) {
                    child.setMarked();

                }
            }
        }
        return null;
    }
}
