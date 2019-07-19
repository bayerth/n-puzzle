package de.hrw.wi.puzzle.gui.deprecated;

/**
 * Class written by
 * Changed by Thomas Bayer (Jan. 2019):
 * - Button Store/Restore added
 * - Enabled choice of distance (ComboBox filled with Distance, GUIBoard8Puzzle adapted
 * - minor improvements
 */

// todo: add slider

import de.hrw.wi.java.puzzle.Board8Puzzle;
import de.hrw.wi.java.puzzle.HeuristicStateSpaceSolver;
import de.hrw.wi.java.statespace.AStateSpaceSolver;
import de.hrw.wi.puzzle.gui.window.BoardPanel;
import de.hrw.wi.puzzle.gui.window.GUIBoard8Puzzle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class DynamicPuzzleGUI extends JFrame {

    public static final String NONE = "none";
    public static final String HAMMING = "Hamming";
    public static final String WEIGHTED_HAMMING = "WeightedHamming";
    public static final String MANHATTEN = "Manhatten";
    public static final String WEIGHTED_MANHATTEN = "WeightedManhatten";
    public static final int GRIDBAG_BOTTOM = 6;
    public static final String STORE_STATE = "Store";
    public static final String RESTORE_STATE = "Restore";
    public static final String SOLVE = "Solve";
    public static final String PAUSE = "Pause";
    public static final String NEXT_STEP = "Next";
    public static final String CONTINUE = "Continue";
    public static final String STEPWISE = "Stepwise";
    public static final String FAST = "Fast";
    public static final String MEDIUM = "Medium";
    public static final String SLOW = "Slow";
    public static final String INFO_STRING = "Manhatten = %d, Hamming = %d, weight = %d";

    private GUIBoard8Puzzle guiBoard;
    private int[][] currentState;
    private BoardPanel boardPanel;
    private List<JButton> tileList;

    private int[][] storedBlocks;
    private boolean store = true;
    private boolean solveButton = true;
    private boolean solving = false;
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
    private int nrOfMoves = 0;

    private enum SPEED {
        SLOW, MEDIUM, FAST
    }

    public DynamicPuzzleGUI(AStateSpaceSolver solver) {
        super("X Puzzle - (C) Bayer/Hierlemann 2019 - HS Weingarten");
        initGUI(GUIBoard8Puzzle.DEFAULT_DIMENSION);
    }

    public DynamicPuzzleGUI() {
        super("X Puzzle - (C) Bayer/Hierlemann 2019 - HS Weingarten");
        initGUI(GUIBoard8Puzzle.DEFAULT_DIMENSION);
    }

    public DynamicPuzzleGUI(int dimension) {
        // Frame title
        super("X Puzzle - (C) Bayer/Hierlemann 2019 - HS Weingarten");
        initGUI(dimension);
    }

    // Draw current Board8Puzzle
    public final void drawBoard() {
        boardPanel.drawBoard(this.guiBoard.getCurrentBoard());
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

    private void performSolution() {
        if (firstStep) { // no solution available, compute it
            this.solutionPath = solver.simpleSolve(this.guiBoard);
            this.solutionPath = guiBoard.solve();
            this.nrOfMoves = solutionPath.size();
            this.stateCounter = guiBoard.getStateCounter();
            this.dupStateCounter = guiBoard.getDupStateCounter();
            this.elapsedTime = guiBoard.getElapsedTime();
            this.firstStep = false;

            initSlider();
            slider.updateUI();

        }
        if (this.solutionPath.size() == 0) { // empty solution, i.e., initial state = goal
            return;
        }
        if (this.stepByStepDisplay) { // step by step display
            this.solving = true;
            buttonSolve.setText(NEXT_STEP);
            if (ignoreFirstRun) { // first run, show no new step
                setStatus(this.guiBoard.manhattan() + this.guiBoard.getWeight());
            }
            else { // show next step
                displayCurrentSolutionStep();
                DynamicPuzzleGUI.this.stepCounter++; // todo: adapt to slider
            }
            ignoreFirstRun = false;
        }
        else { // continuous mode for showing the solution path
            displaySolutionPath(this.solutionPath.subList(stepCounter, solutionPath.size()), elapsedTime, stateCounter, dupStateCounter);
        }
    }

    private void displaySolutionPath(List<Board8Puzzle> solutionPath, long time, int maxStates, int dupCounter) {
        // Save time, moves and nodes on label
        setStatus(guiBoard.manhattan());

        this.solving = true;
        buttonSolve.setText(PAUSE);
        // Perform moves equal to timerSpeed
        new Timer(this.timerSpeed, new ActionListener() {
            DynamicPuzzleGUI GUI;
            List<Board8Puzzle> solutionPath;
            private Board8Puzzle bc;
            private long time;
            private int nrOfMoves;

            @NotNull
            public ActionListener me(DynamicPuzzleGUI GUI, @NotNull List<Board8Puzzle> solutionPath, Board8Puzzle bc) {
                this.GUI = GUI;
                this.bc = bc;
                this.solutionPath = solutionPath;
                nrOfMoves = solutionPath.size();
                return this;
            }

            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                // Stop solving when all moves are done or button reset pressed (goal state)
                if (bc.isGoal()) {
                    DynamicPuzzleGUI.this.solving = false;
                    ((Timer) e.getSource()).stop();
                    DynamicPuzzleGUI.this.stepCounter = 0;
                    DynamicPuzzleGUI.this.firstStep = true;
                    DynamicPuzzleGUI.this.buttonSolve.setText(SOLVE);
                    return;
                }
                if (DynamicPuzzleGUI.this.solving == false) {
                    ((Timer) e.getSource()).stop();
                    DynamicPuzzleGUI.this.buttonSolve.setText(CONTINUE);
                    return;
                }
                // Set guiBoard and update the GUI
                displayCurrentSolutionStep();
                slider.setValue(stepCounter);
                DynamicPuzzleGUI.this.stepCounter++;
            }
        }.me(this, solutionPath, this.guiBoard)).start();
    }

    private void displayCurrentSolutionStep() {
        Board8Puzzle saveBoard = this.solutionPath.get(stepCounter);
        this.guiBoard.setBlocks(saveBoard.getBlocks());
        this.setStatus(this.guiBoard.manhattan() + this.guiBoard.getWeight());
        this.setText(format(INFO_STRING, this.guiBoard.manhattan(), this.guiBoard.hamming(), saveBoard.getWeight()));
        this.drawBoard();
    }

    private void setTimerSpeed(SPEED speed) {
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
        this.storedBlocks = guiBoard.getBlocks();
    }

    private void restoreState() {
        if (!store) {
            this.guiBoard = new GUIBoard8Puzzle(storedBlocks);
        }
    }

    private void clearComponents() {
        this.getContentPane().removeAll();
    }


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

        this.guiBoard = new GUIBoard8Puzzle(dimension);

        // Action Listener Reset

        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DynamicPuzzleGUI.this.guiBoard.resetBoard();
                DynamicPuzzleGUI.this.resetStepByStepState();
                DynamicPuzzleGUI.this.firstStep = true;
                DynamicPuzzleGUI.this.slider.setMaximum(0);
                setStatus(DynamicPuzzleGUI.this.guiBoard.manhattan());
                if (!store) {
                    store = true;
                    //storedBoard = null;
                    buttonMemory.setText(STORE_STATE);
                }
                //DynamicPuzzleGUI.this.drawBoard();
                drawBoard();
                DynamicPuzzleGUI.this.pack();
            }
        });

        // Action Listener Randomize
        buttonRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (DynamicPuzzleGUI.this.solving) { return; }
                DynamicPuzzleGUI.this.guiBoard.randomizeBoard();
                DynamicPuzzleGUI.this.resetStepByStepState();
                setStatus(DynamicPuzzleGUI.this.guiBoard.manhattan());
                setText(format("Manhatten = %d, Hamming = %d", DynamicPuzzleGUI.this.guiBoard.manhattan(), DynamicPuzzleGUI.this.guiBoard.hamming()));
                DynamicPuzzleGUI.this.drawBoard();
            }
        });

        // Action Listener Solver
        buttonSolve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stepByStepDisplay) { // step by step display
                    if (stepCounter > 0 && stepCounter >= solutionPath.size()) { // goal reached
                        resetStepByStepState();
                        return;
                    }
                }
                else if (solving) { // timer for displaying solution is active
                    solving = false;
                    setText(format("Manhatten = %d, Hamming = %d - ABORTED", DynamicPuzzleGUI.this.guiBoard.manhattan(),
                                   DynamicPuzzleGUI.this.guiBoard.hamming()));
                    return;
                }
                DynamicPuzzleGUI.this.performSolution();
                DynamicPuzzleGUI.this.pack();
            }
        });

        // Action Listener Solving Speed
        buttonSpeedSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                if (DynamicPuzzleGUI.this.solving) { return; }
                String crnt = ((JButton) e.getSource()).getText();
                switch (crnt) {
                    case "Slow":
                        DynamicPuzzleGUI.this.setTimerSpeed(SPEED.MEDIUM);
                        DynamicPuzzleGUI.this.buttonSpeedSelect.setText(MEDIUM);
                        DynamicPuzzleGUI.this.stepByStepDisplay = false;
                        break;
                    case "Medium":
                        DynamicPuzzleGUI.this.setTimerSpeed(SPEED.FAST);
                        DynamicPuzzleGUI.this.buttonSpeedSelect.setText(FAST);
                        DynamicPuzzleGUI.this.stepByStepDisplay = false;
                        break;
                    case "Fast":
                        DynamicPuzzleGUI.this.setTimerSpeed(SPEED.SLOW);
                        DynamicPuzzleGUI.this.buttonSpeedSelect.setText(STEPWISE);
                        DynamicPuzzleGUI.this.slider.setEnabled(true);
                        DynamicPuzzleGUI.this.stepByStepDisplay = true;
                        break;
                    case "Step":
                        DynamicPuzzleGUI.this.stepByStepDisplay = false;
                        DynamicPuzzleGUI.this.slider.setEnabled(false);
                        DynamicPuzzleGUI.this.stepCounter = 0;
                        DynamicPuzzleGUI.this.buttonSpeedSelect.setText(SLOW);
                        // todo: slider. no action listener, no ui, passivate!,
                }
            }
        });

        // Action Listener Store/Read
        buttonMemory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (store) {
                    store = false;
                    buttonMemory.setText(RESTORE_STATE);
                    DynamicPuzzleGUI.this.storeState();
                }
                else {
                    DynamicPuzzleGUI.this.restoreState();
                    // passivate slider, enable solution
                    DynamicPuzzleGUI.this.drawBoard();
                }
            }
        });

        // ActionListener for Strategy selection
        comboBoxStrategy.addActionListener(e -> {
            String strategyString = (String) comboBoxStrategy.getSelectedItem();
            this.setText(strategyString);
            guiBoard.setDistance(strategyString);
        });
        comboBoxStrategy.setSelectedIndex(0);

        setStatus(DynamicPuzzleGUI.this.guiBoard.manhattan());
        this.drawBoard();
        this.boardPanel.drawBoard(guiBoard.getBlocks());
        this.pack(); // resize the frame to fit the new size of the buttons
    }

    private void initSlider() {
        slider.setMinimum(0);
        slider.setMaximum(this.nrOfMoves - 1);
        slider.setValue(0);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        if (slider.getChangeListeners().length == 0 && this.stepByStepDisplay) {
            slider.addChangeListener(e -> {
                System.out.println("value = " + ((JSlider) e.getSource()).getValue());
                DynamicPuzzleGUI.this.stepCounter = ((JSlider) e.getSource()).getValue();
                displayCurrentSolutionStep();
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
                                          "Randomize: Choose a random state which is solvable\n Reset: Clear store and" +
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

    private void setDimension(int dimension) {
        System.out.println("dim = " + dimension);
        this.initGUI(dimension);
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
                    if (DynamicPuzzleGUI.this.solving) { return; }
                    DynamicPuzzleGUI.this.guiBoard.tilePressed(num);
                    setStatus(DynamicPuzzleGUI.this.guiBoard.manhattan());
                    setText(format("Manhatten = %d, Hamming = %d", DynamicPuzzleGUI.this.guiBoard.manhattan(),
                                   DynamicPuzzleGUI.this.guiBoard.hamming()));
                    DynamicPuzzleGUI.this.drawBoard();
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
        boardPanel = new BoardPanel(dimension);
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

    private void resetStepByStepState() {
        ignoreFirstRun = true;
        stepCounter = 0;
        buttonSolve.setText(SOLVE);
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
