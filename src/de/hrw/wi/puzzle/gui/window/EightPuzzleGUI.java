package de.hrw.wi.puzzle.gui.window;

import de.hrw.wi.java.puzzle.Board8Puzzle;
import de.hrw.wi.java.puzzle.HeuristicStateSpaceSolver;
import de.hrw.wi.java.statespace.AStateSpaceSolver;
import de.hrw.wi.java.statespace.IEvaluate;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class EightPuzzleGUI extends JFrame implements BoardPanel.OnClickInteractionListener {

    public static final String NONE = "none";
    public static final String HAMMING = "Hamming";
    public static final String WEIGHTED_HAMMING = "WeightedHamming";
    public static final String MANHATTEN = "Manhatten";
    public static final String WEIGHTED_MANHATTEN = "WeightedManhatten";

    private static final int GRIDBAG_BOTTOM = 6;
    private static final String STORE_STATE = "Store";
    private static final String RESTORE_STATE = "Restore";
    private static final String SOLVE = "Solve";
    private static final String PAUSE = "Pause";
    private static final String CONTINUE = "Continue";
    private static final String ABORT = "Abort";
    private static final String STEPWISE = "Stepwise";
    private static final String FAST = "Fast";
    private static final String MEDIUM = "Medium";
    private static final String SLOW = "Slow";
    private static final String INFO_STRING = "Manhatten = %d, Hamming = %d, weight = %d";
    public static final String STOP = "STOP";
    public static final String START = "Start";
    public static final String INFO_TEXT_TEMPLATE = "Manhatten = %d, Hamming = %d";

    private int[][] currentState;
    private BoardPanel boardPanel;
    private List<JButton> tileList;
    private IEvaluate<Board8Puzzle> heuristic;
    private AStateSpaceSolver.ISolverInformationProvider<Board8Puzzle> informationProvider = new AStateSpaceSolver.ISolverInformationProvider<Board8Puzzle>() {
        @Override
        public void setNrOfVisitetStates(int nrOfVisitedStates) {
            EightPuzzleGUI.this.stateCounter = nrOfVisitedStates;
        }

        @Override
        public void setNrOfDuplicatedStates(int nrOfDuplicatedStates) {
            EightPuzzleGUI.this.dupStateCounter = nrOfDuplicatedStates;
        }

        @Override
        public void setQueueSize(int queueSize) {
            EightPuzzleGUI.this.queueSize = queueSize;
        }

        @Override
        public void setDistance(int distance) {
            EightPuzzleGUI.this.currentDistance = distance;
        }

        @Override
        public void getLevel() {
            EightPuzzleGUI.this.level = level;
        }
    };

    private int[][] storedBlocks;
    private boolean stateStored = false;
    private volatile boolean isSolving = false;
    private volatile boolean displayingSolution = false;
    private boolean solutionValid = false;
    private int timerSpeed = 500;
    private boolean stepByStepDisplay = false;
    private boolean ignoreFirstRun = true;
    private boolean firstStep = true;
    private int stepCounter = 0;
    private int dimension = 3;
    private HeuristicStateSpaceSolver solver;
    private List<Board8Puzzle> solutionPath;
    private long elapsedTime = 0;
    private int stateCounter = 0;
    private int dupStateCounter = 0;
    private int currentDistance = 0;
    private int queueSize = 0;
    private int level = 0;
    private int nrOfMoves = 0;

    @Override
    public void onClickAction(int tileNumber) {
        setInfoText();
    }

    private enum SPEED {
        SLOW, MEDIUM, FAST
    }

    public EightPuzzleGUI(AStateSpaceSolver solver) {
        super("X Puzzle - (C) Bayer/Hierlemann 2019 - HS Weingarten");
        initGUI(GUIBoard8Puzzle.DEFAULT_DIMENSION);
    }

    public EightPuzzleGUI() {
        super("X Puzzle - (C) Bayer/Hierlemann 2019 - HS Weingarten");
        initGUI(GUIBoard8Puzzle.DEFAULT_DIMENSION);
    }

    public EightPuzzleGUI(int dimension) {
        // Frame title
        super("X Puzzle - (C) Bayer/Hierlemann 2019 - HS Weingarten");
        initGUI(dimension);
    }

    // -- Functionality ---------------------------------------------------------------------------

    // Draw current Board8Puzzle
    public final void drawBoard() {
        boardPanel.draw();
    }

    public final void draw(int[][] board) {
        boardPanel.setBoard(board);
        boardPanel.draw();
    }

    public void setStatus(String stat) {
        this.Label_Status.setText(stat);
    }

    public void setStatus(int distance) {
        String status = format("<html>Time: %,d ms<br/>All Nodes: %,d <br/>Duplicates: %,d<br/>Moves: %,d<br/>Rem. moves %,d<br/>Distance: %d</html>",
                               elapsedTime, stateCounter, dupStateCounter, nrOfMoves, nrOfMoves - stepCounter, distance);
        this.setStatus(status);
    }

    public void setText(String text) {
        this.txtpnTextbox.setText(text);
    }

    private void setInfoText() {
        setText(format(INFO_TEXT_TEMPLATE, boardPanel.getBoard().manhattan(), boardPanel.getBoard().hamming()));
    }

    private void displaySolutionPath() {

        // Perform moves equal to timerSpeed
        new Timer(this.timerSpeed, new ActionListener() {
            EightPuzzleGUI GUI;
            List<Board8Puzzle> solutionPath;
            private Board8Puzzle bc;
            private long time;
            private int nrOfMoves;

            @NotNull
            public ActionListener me(EightPuzzleGUI GUI, @NotNull List<Board8Puzzle> solutionPath) {
                if (solutionPath == null || solutionPath.size() == 0) { displayingSolution = false; }
                else {
                    this.GUI = GUI;
                    this.bc = solutionPath.get(stepCounter);
                    this.solutionPath = solutionPath;
                    nrOfMoves = solutionPath.size();
                }
                return this;
            }

            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                // Stop displayingSolution when all moves are done or button reset pressed (goal state)
                if (EightPuzzleGUI.this.displayingSolution == false) {
                    ((Timer) e.getSource()).stop();
                    EightPuzzleGUI.this.buttonSolve.setText(CONTINUE);
                    return;
                }
                if (bc.isGoal()) {
                    EightPuzzleGUI.this.displayingSolution = false;
                    ((Timer) e.getSource()).stop();
                    EightPuzzleGUI.this.stepCounter = 0;
                    EightPuzzleGUI.this.buttonSolve.setText(SOLVE);
                    return;
                }
                // Set guiBoard and update the GUI
                bc = solutionPath.get(stepCounter);
                displayCurrentSolutionStep(EightPuzzleGUI.this.stepCounter);
                slider.setValue(stepCounter);
                EightPuzzleGUI.this.stepCounter++;
            }
        }.me(this, solutionPath)).start();
    }

    private void displayCurrentSolutionStep(int index) {
        Board8Puzzle currentBoard = this.solutionPath.get(index);
        currentState = currentBoard.getBlocks();
        this.setStatus(currentBoard.manhattan() + currentBoard.getWeight());
        this.setText(format(INFO_STRING, currentBoard.manhattan(), currentBoard.hamming(), currentBoard.getWeight()));
        //this.drawBoard();
        this.draw(currentState);
    }

    private void setTimerSpeed(EightPuzzleGUI.SPEED speed) {
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

    private void storeState() {
        this.storedBlocks = boardPanel.getBlocks().clone();
    }

    private void restoreState() {
        if (stateStored) {
            this.boardPanel.setBoard(storedBlocks);
        }
    }

    private void clearComponents() {
        this.getContentPane().removeAll();
    }

    private void setDimension(int dimension) {
        System.out.println("dim = " + dimension);
        this.initGUI(dimension);
    }

    private void resetStepByStepState() {
        ignoreFirstRun = true;
        stepCounter = 0;
        solutionValid = false;
        buttonSolve.setText(SOLVE);
    }

    public void setHeuristic(String distanceName) {
        switch (distanceName) {
            case NONE:
                heuristic = null;
                break;
            case HAMMING:
                heuristic = HeuristicStateSpaceSolver.hammingBoard;
                break;
            case WEIGHTED_HAMMING:
                heuristic = HeuristicStateSpaceSolver.hammingWeightBoard;
                break;
            case MANHATTEN:
                heuristic = HeuristicStateSpaceSolver.manhattenBoard;
                break;
            case WEIGHTED_MANHATTEN:
                heuristic = HeuristicStateSpaceSolver.hammingWeightBoard;
                break;
            default:
                heuristic = null;
                break;
        }
    }

    // -- Initialization --------------------------------------------------------------------------

    public void initGUI(int dimension) {
        solver = new HeuristicStateSpaceSolver();

        //Create the menu bar.
        initMenuBar();
        menuBar.add(menu);

        this.dimension = dimension;
        // GUI settings
        tileList = new ArrayList<>(dimension * dimension);
        clearComponents();
        initComponents();
        this.setLocationRelativeTo(null); // center the frame in the screen on open
        this.setResizable(false);
        this.setAlwaysOnTop(true);

        //this.generateButtons();
        initButtons();

        // init slider


        // Action Listener Reset

        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //EightPuzzleGUI.this.guiBoard.resetBoard();
                EightPuzzleGUI.this.boardPanel.reset();
                EightPuzzleGUI.this.resetStepByStepState();
                EightPuzzleGUI.this.firstStep = true;
                EightPuzzleGUI.this.slider.setMaximum(0);
                //setStatus(EightPuzzleGUI.this.guiBoard.manhattan());
                if (stateStored) {
                    stateStored = false;
                    EightPuzzleGUI.this.storedBlocks = null;
                    buttonMemory.setText(STORE_STATE);
                }
                EightPuzzleGUI.this.drawBoard();
                EightPuzzleGUI.this.pack();
            }
        });

        // Action Listener Randomize
        buttonRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (EightPuzzleGUI.this.displayingSolution) { return; }
                EightPuzzleGUI.this.boardPanel.randomize();
                EightPuzzleGUI.this.resetStepByStepState();
                //setStatus(EightPuzzleGUI.this.guiBoard.manhattan());
                setInfoText();
                EightPuzzleGUI.this.drawBoard();
            }
        });

        // Action Listener Solver
        buttonSolve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isSolving) { // stop the solver
                    System.out.println("Abort!");
                    solver.stop();
                    isSolving = false;
                    buttonSolve.setText(SOLVE);
                    return;
                }
                if (!solutionValid) { // compute a solution
                    isSolving = true;
                    buttonSolve.setText(ABORT);

                    // start a new thread
                    Runnable solverRunnable = new Runnable() {
                        @Override
                        public void run() {
                            computeSolution();
                            if (isSolving) {
                                initSlider();
                                solutionValid = true;
                                isSolving = false;
                                buttonSolve.setText(START);
                            }
                        }
                    };
                    Thread computeThread = new Thread(solverRunnable);
                    computeThread.start();

                }
                else {
                    if (!displayingSolution) { // display the solution
                        buttonSolve.setText(PAUSE);
                        displayingSolution = true;
                        displaySolutionPath();
                    }
                    else if (displayingSolution) {// pause
                        displayingSolution = false;
                        buttonSolve.setText(CONTINUE);
                    }
                }
                EightPuzzleGUI.this.pack();
            }
        });

        // Action Listener Solving Speed
        buttonSpeedSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                if (EightPuzzleGUI.this.displayingSolution) { return; }
                String crnt = ((JButton) e.getSource()).getText();
                switch (crnt) {
                    case SLOW:
                        EightPuzzleGUI.this.setTimerSpeed(EightPuzzleGUI.SPEED.MEDIUM);
                        EightPuzzleGUI.this.buttonSpeedSelect.setText(MEDIUM);
                        EightPuzzleGUI.this.stepByStepDisplay = false;
                        break;
                    case MEDIUM:
                        EightPuzzleGUI.this.setTimerSpeed(EightPuzzleGUI.SPEED.FAST);
                        EightPuzzleGUI.this.buttonSpeedSelect.setText(FAST);
                        break;
                    case FAST:
                        EightPuzzleGUI.this.stepByStepDisplay = true;
                        EightPuzzleGUI.this.setTimerSpeed(EightPuzzleGUI.SPEED.SLOW);
                        EightPuzzleGUI.this.buttonSpeedSelect.setText(STEPWISE);
                        break;
                    case STEPWISE:
                        EightPuzzleGUI.this.stepByStepDisplay = false;
                        EightPuzzleGUI.this.stepCounter = 0;
                        EightPuzzleGUI.this.buttonSpeedSelect.setText(SLOW);
                        // todo: slider. no action listener, no ui, passivate!,
                }
            }
        });

        // Action Listener Store/Read
        buttonMemory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!stateStored) {
                    stateStored = true;
                    buttonMemory.setText(RESTORE_STATE);
                    //EightPuzzleGUI.this.storeState();
                    EightPuzzleGUI.this.storedBlocks = boardPanel.getBlocks().clone();
                }
                else {
                    EightPuzzleGUI.this.restoreState();
                    // passivate slider, enable solution
                    EightPuzzleGUI.this.drawBoard();
                }
            }
        });

        // ActionListener for Strategy selection
        comboBoxStrategy.addActionListener(e -> {
            String strategyString = (String) comboBoxStrategy.getSelectedItem();
            this.setText(strategyString);
            EightPuzzleGUI.this.setHeuristic(strategyString);
        });
        comboBoxStrategy.setSelectedIndex(0);

        setStatus(EightPuzzleGUI.this.boardPanel.getBoard().manhattan());
        this.drawBoard();
        this.boardPanel.draw();
        this.pack(); // resize the frame to fit the new size of the buttons
    }


    private void computeSolution() {
        elapsedTime = 0;
        long time = System.nanoTime();
        solutionPath = solver.simpleSolve(boardPanel.getBoard(), heuristic);
        time = (System.nanoTime() - time) / 1000000;
        elapsedTime = time;
        nrOfMoves = solutionPath.size();
        stateCounter = solver.getStateCounter();
        dupStateCounter = solver.getDupStateCounter();
        setStatus(0);
        solutionValid = true;
    }


    private void initSlider() {
        slider.setMinimum(0);
        slider.setMaximum(this.nrOfMoves - 1);
        slider.setValue(stepCounter);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        if (slider.getChangeListeners().length == 0) {
            slider.addChangeListener(e -> {
                System.out.println("value = " + ((JSlider) e.getSource()).getValue());
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) { // change induced by sliding (not by setValue())
                    EightPuzzleGUI.this.displayingSolution = false;
                    EightPuzzleGUI.this.stepCounter = ((JSlider) e.getSource()).getValue();
                    displayCurrentSolutionStep(stepCounter);
                }
            });
        }
    }

    private void initMenuBar() {

        menuBar = new JMenuBar();

        // dimension
        menu = new JMenu("Dimension");
        menu.setMnemonic(KeyEvent.VK_D);

        int[] shortCuts = {KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9};

        for (int i = 0; i < 7; i++) {
            menuItem = new JMenuItem(Integer.toString(i + 3), shortCuts[i]);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(shortCuts[i], ActionEvent.ALT_MASK));
            menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
            int dim = i + 3;
            menuItem.addActionListener(e -> {this.setDimension(dim);});
            menu.add(menuItem);
        }
        menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
        menuBar.add(menu);

        // helo
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menuItem = new JMenuItem("Buttons", KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.ALT_MASK));
        menuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                                          "Randomize: Choose a random state which is solvable\n Reset: Clear stateStored and" +
                                          " init board with " +
                                          "goal state \n" +
                                          "Solve: Find a solution for the current state. Uses the heuristic from the " +
                                          "dropdown-list\n" +
                                          "Slow: choose speed\nStore current state - can be reloaded");
        });
        menu.add(menuItem);
        menuItem = new JMenuItem("Info", KeyEvent.VK_I);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
        menuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                                          "A solution is found by a variant of BFS. A priority queue based on the " +
                                          "selected heuristic is used. If no heuristic is choosen, the solver uses " +
                                          "BFS");
        });
        menu.add(menuItem);
        menuItem = new JMenuItem("About", KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                                          "Solver written by Thomas Bayer, GUI written by Antony Hierlemann and " +
                                          "Thomas Bayer. The classes for the board are taken from: \nContact " +
                                          "thombay@gmail.com");
        });
        menu.add(menuItem);
        menuBar.add(menu);

        this.setJMenuBar(menuBar);
    }


    private void initButtons() {
        int counter = 0;
        for (JButton tile : boardPanel.getTileList()) {
            tile.setFocusable(false);
            tile.setFont(tile.getFont().deriveFont(25.0f));
            tile.addActionListener(new ActionListener() {
                int num;

                @NotNull ActionListener me(int i) {
                    num = i;
                    return this;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (EightPuzzleGUI.this.displayingSolution) { return; }
                    //EightPuzzleGUI.this.guiBoard.tilePressed(num);
                    //setStatus(EightPuzzleGUI.this.guiBoard.manhattan());
                    //setText(format("Manhatten = %d, Hamming = %d", EightPuzzleGUI.this.guiBoard.manhattan(),
                    //               EightPuzzleGUI.this.guiBoard.hamming()));
                    //EightPuzzleGUI.this.drawBoard();
                }
            }.me(counter));
            counter++;
        }
    }


    /**
     * Code generated From GUI Editor do not modify
     */

    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        GridBagConstraints gridBagConstraints_1;
        GridBagConstraints gbc_btnSolver;
        GridBagConstraints gbc_btnRandom;
        JPanel ButtonsPanel;
        JPanel Main_Right;
        JPanel jPanel2;

        Main_Right = new JPanel();
        ButtonsPanel = new JPanel();
        buttonRandom = new JButton();
        buttonReset = new JButton();
        buttonSolve = new JButton();
        buttonMemory = new JButton();
        jPanel2 = new JPanel();
        Label_Status = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Main_Right.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        Main_Right.setLayout(new BorderLayout());

        ButtonsPanel.setOpaque(false);
        GridBagLayout gbl_ButtonsPanel = new GridBagLayout();
        gbl_ButtonsPanel.columnWeights = new double[]{1.0};
        ButtonsPanel.setLayout(gbl_ButtonsPanel);

        buttonRandom.setFont(new Font("Ubuntu", 0, 16)); // NOI18N
        buttonRandom.setText("Randomize");
        buttonRandom.setFocusable(false);
        gbc_btnRandom = new GridBagConstraints();
        gbc_btnRandom.insets = new Insets(0, 0, GRIDBAG_BOTTOM, 0);
        gbc_btnRandom.gridx = 0;
        gbc_btnRandom.gridy = 0;
        gbc_btnRandom.fill = GridBagConstraints.HORIZONTAL;
        ButtonsPanel.add(buttonRandom, gbc_btnRandom);

        buttonReset.setFont(new Font("Ubuntu", 0, 16)); // NOI18N
        buttonReset.setText("Reset");
        buttonReset.setFocusable(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, GRIDBAG_BOTTOM, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        ButtonsPanel.add(buttonReset, gridBagConstraints);

        buttonSolve.setFont(new Font("Ubuntu", 0, 16)); // NOI18N
        buttonSolve.setText(SOLVE);
        buttonSolve.setFocusable(false);
        gbc_btnSolver = new GridBagConstraints();
        gbc_btnSolver.insets = new Insets(0, 0, GRIDBAG_BOTTOM, 0);
        gbc_btnSolver.gridx = 0;
        gbc_btnSolver.gridy = 2;
        gbc_btnSolver.fill = GridBagConstraints.HORIZONTAL;
        ButtonsPanel.add(buttonSolve, gbc_btnSolver);

        Main_Right.add(ButtonsPanel, BorderLayout.CENTER);
        buttonSpeedSelect = new JButton();
        buttonSpeedSelect.setFont(new Font("Dialog", Font.PLAIN, 16));

        buttonSpeedSelect.setText("Slow");
        buttonSpeedSelect.setFocusable(false);
        gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.insets = new Insets(0, 0, GRIDBAG_BOTTOM, 0);
        gridBagConstraints_1.gridx = 0;
        gridBagConstraints_1.gridy = 3;
        gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
        ButtonsPanel.add(buttonSpeedSelect, gridBagConstraints_1);

        buttonMemory.setFont(new Font("Ubuntu", 0, 16)); // NOI18N
        buttonMemory.setText(STORE_STATE);
        buttonMemory.setFocusable(false);
        gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.insets = new Insets(0, 0, GRIDBAG_BOTTOM, 0);
        gridBagConstraints_1.gridx = 0;
        gridBagConstraints_1.gridy = 4;
        gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
        ButtonsPanel.add(buttonMemory, gridBagConstraints_1);

        comboBoxStrategy = new JComboBox();
        comboBoxStrategy.setFont(new Font("Dialog", Font.PLAIN, 16));
        comboBoxStrategy.setModel(
                new DefaultComboBoxModel(new String[]{NONE, HAMMING, WEIGHTED_HAMMING, MANHATTEN, WEIGHTED_MANHATTEN}));
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.fill = GridBagConstraints.BOTH;
        gbc_comboBox.insets = new Insets(0, 0, GRIDBAG_BOTTOM, 0);
        gbc_comboBox.gridx = 0;
        gbc_comboBox.gridy = 5;
        ButtonsPanel.add(comboBoxStrategy, gbc_comboBox);

        jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 1, 1, 1));
        jPanel2.setOpaque(false);
        jPanel2.add(Label_Status);

        Main_Right.add(jPanel2, BorderLayout.PAGE_START);

        getContentPane().add(Main_Right, BorderLayout.LINE_END);

        // init tile panel
        boardPanel = new BoardPanel(dimension, this);
        getContentPane().add(this.boardPanel, BorderLayout.CENTER);
        boardPanel.generateButtons();

        // init Statusline
        JPanel bottomPanel = new JPanel(new BorderLayout());

        txtpnTextbox = new JTextPane();
        txtpnTextbox.setText("Textbox");
        bottomPanel.add(txtpnTextbox, BorderLayout.NORTH);
        //getContentPane().add(txtpnTextbox, BorderLayout.SOUTH);

        // slider
        slider = new JSlider(JSlider.HORIZONTAL);
        //getContentPane().add(slider, BorderLayout.SOUTH);
        bottomPanel.add(slider, BorderLayout.SOUTH);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        pack();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton buttonRandom;
    private JButton buttonReset;
    private JButton buttonSolve;
    private JButton buttonMemory;
    private JButton buttonSpeedSelect;
    private JLabel Label_Status;
    private JTextPane txtpnTextbox;
    private JComboBox comboBoxStrategy;
    private JTextField copyright;

    private JMenuBar menuBar;
    private JMenu menu, submenu;
    private JMenuItem menuItem;
    private JSlider slider;

    // End of variables declaration//GEN-END:variables

}
