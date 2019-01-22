package de.hrw.wi.puzzle.gui.window;

/**
 * Class written by
 * Changed by Thomas Bayer (Jan. 2019):
 * - Button Store/Restore added
 * - Enabled choice of distance (ComboBox filled with Distance, GUIBoard adapted
 * - minor improvements
 */

// todo adapt to dimension

import de.hrw.wi.java.puzzle.Board8Puzzle;

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
    public static final String CANCEL = "Cancel";

    public GUIBoard guiBoard;

    private JMenuBar menuBar;
    private JMenu menu, submenu;
    private JMenuItem menuItem;

    private BoardPanel boardPanel;
    private List<JButton> tileList;
    private int[][] storedBlocks;
    private boolean store = true;
    private boolean solveButton = true;
    private boolean solving = false;
    private int timerSpeed = 500;
    private int dimension = 3;

    private enum SPEED {
        SLOW, MEDIUM, FAST
    }

    public DynamicPuzzleGUI() {
        super("X Puzzle - (C) Bayer/Hierlemann 2019 - HS Weingarten");
        initGUI(GUIBoard.DEFAULT_DIMENSION);
    }

    public DynamicPuzzleGUI(int dimension) {
        // Frame title
        super("X Puzzle - (C) Bayer/Hierlemann 2019 - HS Weingarten");
        initGUI(dimension);
    }

    public void initGUI(int dimension) {
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

        this.guiBoard = new GUIBoard(dimension);

        // Action Listener Reset
        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DynamicPuzzleGUI.this.guiBoard.resetBoard();
                setStatus(0, 0, 0, 0, 0, DynamicPuzzleGUI.this.guiBoard.manhattan());
                if (!store) {
                    store = true;
                    //storedBoard = null;
                    buttonMemory.setText(STORE_STATE);
                }
                DynamicPuzzleGUI.this.drawBoard();
            }
        });

        // Action Listener Randomize
        buttonRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (DynamicPuzzleGUI.this.solving) { return; }
                DynamicPuzzleGUI.this.guiBoard.randomizeBoard();
                setStatus(0, 0, 0, 0, 0, DynamicPuzzleGUI.this.guiBoard.manhattan());
                setText(format("Manhatten = %d, Hamming = %d", DynamicPuzzleGUI.this.guiBoard.manhattan(), DynamicPuzzleGUI.this.guiBoard.hamming()));
                DynamicPuzzleGUI.this.drawBoard();
            }
        });

        // Action Listener Solver
        buttonSolve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (solving) {
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
            public void actionPerformed(ActionEvent e) {
                if (DynamicPuzzleGUI.this.solving) { return; }
                String crnt = ((JButton) e.getSource()).getText();
                switch (crnt) {
                    case "Slow":
                        DynamicPuzzleGUI.this.setTimerSpeed(SPEED.MEDIUM);
                        DynamicPuzzleGUI.this.buttonSpeedSelect.setText("Medium");
                        break;
                    case "Medium":
                        DynamicPuzzleGUI.this.setTimerSpeed(SPEED.FAST);
                        DynamicPuzzleGUI.this.buttonSpeedSelect.setText("Fast");
                        break;
                    case "Fast":
                        DynamicPuzzleGUI.this.setTimerSpeed(SPEED.SLOW);
                        DynamicPuzzleGUI.this.buttonSpeedSelect.setText("Slow");
                        break;
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

        setStatus(0, 0, 0, 0, 0, DynamicPuzzleGUI.this.guiBoard.manhattan());
        this.drawBoard();
        this.boardPanel.drawBoard(guiBoard.getBlocks());
        this.pack(); // resize the frame to fit the new size of the buttons
    }

    public void initMenuBar() {

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

                ActionListener me(int i) {
                    num = i;
                    return this;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (DynamicPuzzleGUI.this.solving) { return; }
                    DynamicPuzzleGUI.this.guiBoard.tilePressed(num);
                    setStatus(0, 0, 0, 0, 0, DynamicPuzzleGUI.this.guiBoard.manhattan());
                    setText(format("Manhatten = %d, Hamming = %d", DynamicPuzzleGUI.this.guiBoard.manhattan(),
                                   DynamicPuzzleGUI.this.guiBoard.hamming()));
                    DynamicPuzzleGUI.this.drawBoard();
                }
            }.me(counter));
            counter++;
        }
    }

    // Draw current Board8Puzzle
    public final void drawBoard() {
        boardPanel.drawBoard(guiBoard.getCurrentBoard());
    }

    public void setStatus(String stat) {
        this.Label_Status.setText(stat);
    }

    public void setStatus(long time, int stateCounter, int dupStates, int nrOfMoves, int nrOfRemainingMoves, int distance) {
        String status = format("<html>Time: %,d ms<br/>All Nodes: %,d <br/>Duplicates: %,d<br/>Moves: %,d<br/>Rem. moves %,d<br/>Distance: %d</html>",
                               time, stateCounter, dupStates, nrOfMoves, nrOfRemainingMoves, distance);
        this.setStatus(status);
    }

    public void setText(String text) {
        this.txtpnTextbox.setText(text);
    }

    private void performSolution() {
        List<Board8Puzzle> solutionPath = guiBoard.solve();
        displaySolutionPath(solutionPath, guiBoard.getElapsedTime(), guiBoard.getStateCounter(), guiBoard.getDupStateCounter());
    }

    private void displaySolutionPath(List<Board8Puzzle> solutionPath, long time, int maxStates, int dupCounter) {
        // Save time, moves and nodes on label
        setStatus(time, maxStates, dupCounter, solutionPath.size(), solutionPath.size(), guiBoard.manhattan());

        this.solving = true;
        buttonSolve.setText(CANCEL);
        // Perform moves equal to timerSpeed
        new Timer(this.timerSpeed, new ActionListener() {
            List<Board8Puzzle> solutionPath;
            private Board8Puzzle bc;
            private int counter = 0;
            private long time;
            private int nrOfMoves;

            public ActionListener me(List<Board8Puzzle> solutionPath, Board8Puzzle bc, long time) {
                this.bc = bc;
                this.solutionPath = solutionPath;
                this.time = time;
                nrOfMoves = solutionPath.size();
                return this;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop solving when all moves are done or button reset pressed (goal state)
                if (solutionPath.isEmpty() || bc.isGoal()) {
                    DynamicPuzzleGUI.this.solving = false;
                    ((Timer) e.getSource()).stop();
                    DynamicPuzzleGUI.this.buttonSolve.setText(SOLVE);
                    return;
                }
                if (DynamicPuzzleGUI.this.solving == false) {
                    ((Timer) e.getSource()).stop();
                    DynamicPuzzleGUI.this.buttonSolve.setText(SOLVE);
                    return;
                }
                // Set guiBoard and update the GUI
                Board8Puzzle saveBoard = this.solutionPath.get(counter++);
                bc.setBlocks(saveBoard.getBlocks());

                DynamicPuzzleGUI.this.setStatus(time, maxStates, dupCounter, nrOfMoves, nrOfMoves - counter,
                                                bc.manhattan() + bc.getWeight());
                DynamicPuzzleGUI.this.setText(format("Manhatten = %d, Hamming = %d, weight = %d", bc.manhattan(), bc.hamming(), bc.getWeight()));
                DynamicPuzzleGUI.this.drawBoard();
            }
        }.me(solutionPath, this.guiBoard, time)).start();
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
            this.guiBoard = new GUIBoard(storedBlocks);
        }
    }

    private void clearComponents() {
        this.getContentPane().removeAll();
    }


    /**
     * Code generated From GUI Editor do not modify
     */

    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

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
        gbc_btnZufllig = new GridBagConstraints();
        gbc_btnZufllig.insets = new Insets(0, 0, GRIDBAG_BOTTOM, 0);
        gbc_btnZufllig.gridx = 0;
        gbc_btnZufllig.gridy = 0;
        gbc_btnZufllig.fill = GridBagConstraints.HORIZONTAL;
        ButtonsPanel.add(buttonRandom, gbc_btnZufllig);

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

        txtpnTextbox = new JTextPane();
        txtpnTextbox.setText("Textbox");
        getContentPane().add(txtpnTextbox, BorderLayout.SOUTH);

        pack();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton buttonRandom;
    private JButton buttonReset;
    private JButton buttonSolve;
    private JButton buttonMemory;
    private JButton buttonSpeedSelect;
    private JPanel ButtonsPanel;
    private JLabel Label_Status;
    private JPanel Main_Right;

    private JPanel jPanel2;
    private GridBagConstraints gbc_btnSolver;
    private GridBagConstraints gbc_btnZufllig;
    private GridBagConstraints gridBagConstraints_1;
    private JTextPane txtpnTextbox;
    private GridBagConstraints gridBagConstraints_2;
    private JComboBox comboBoxStrategy;
    private JTextField copyright;
    // End of variables declaration//GEN-END:variables
}
