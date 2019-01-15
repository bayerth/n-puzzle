package de.hrw.wi.puzzle.gui.window;

/*
Class written by
Changend by Thomas Bayer (Jan. 2019):
- Button Store/Restore added
- Enabled choice of distance (ComboBox filled with Distance, GUIBoard adapted
- minor improvements
 */


import de.hrw.wi.java.puzzle.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static java.lang.String.format;

public class GUI extends javax.swing.JFrame {

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

    private final JButton[] tiles;
    private static int[][] blocks1 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    private int[][] storedBlocks;
    private boolean store = true;
    private boolean solveButton = true;
    private boolean solving = false;
    private int timerSpeed = 500;

    private enum SPEED {
        SLOW, MEDIUM, FAST
    }

    public GUI() {

        // Frame title
        super("8 Puzzle");

        // GUI settings
        initComponents();
        this.setLocationRelativeTo(null); // center the frame in the screen on open
        this.setResizable(false);
        this.setAlwaysOnTop(true);

        // Init global variables
        this.tiles = new JButton[]{Tile_1, Tile_2, Tile_3, Tile_4, Tile_5, Tile_6, Tile_7, Tile_8, Tile_0};
        this.guiBoard = new GUIBoard(blocks1);

        // Init tiles, set the font, disable focus and add the action listener
        for (int i = 0; i < tiles.length; ++i) {
            tiles[i].setFocusable(false);
            tiles[i].setFont(tiles[i].getFont().deriveFont(25.0f));
            tiles[i].addActionListener(new ActionListener() {
                int num;

                ActionListener me(int i) {
                    num = i;
                    return this;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (GUI.this.guiBoard.isSolving()) { return; }
                    GUI.this.guiBoard.tilePressed(num);
                    setStatus(0, 0, 0, 0, 0, GUI.this.guiBoard.manhattan());
                    setText(format("Manhatten = %d, Hamming = %d", GUI.this.guiBoard.manhattan(), GUI.this.guiBoard.hamming()));
                    GUI.this.drawBoard();
                }
            }.me(i));
        }

        // Action Listener Reset
        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.guiBoard.resetBoard();
                setStatus(0, 0, 0, 0, 0, GUI.this.guiBoard.manhattan());
                if (!store) {
                    store = true;
                    //storedBoard = null;
                    buttonMemory.setText(STORE_STATE);
                }
                GUI.this.drawBoard();
            }
        });

        // Action Listener Randomize
        buttonRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (GUI.this.guiBoard.isSolving()) { return; }
                GUI.this.guiBoard.randomizeBoard();
                setStatus(0, 0, 0, 0, 0, GUI.this.guiBoard.manhattan());
                setText(format("Manhatten = %d, Hamming = %d", GUI.this.guiBoard.manhattan(), GUI.this.guiBoard.hamming()));
                GUI.this.drawBoard();
            }
        });

        // Action Listener Solver
        buttonSolve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if (GUI.this.guiBoard.isSolving()) {
//                    GUI.this.guiBoard.stop();
//                    setText(format("Manhatten = %d, Hamming = %d - ABORTED", GUI.this.guiBoard.manhattan(), GUI.this.guiBoard.hamming()));
//                    return;
//                }
                if (solving) {
                    solving = false;
                    setText(format("Manhatten = %d, Hamming = %d - ABORTED", GUI.this.guiBoard.manhattan(), GUI.this.guiBoard.hamming()));
                    return;
                }
                //GUI.this.guiBoard.solve(GUI.this);
                GUI.this.performSolution();
                GUI.this.pack();
            }
        });

        // Action Listener Solving Speed
        buttonSpeedSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (GUI.this.guiBoard.isSolving()) { return; }
                String crnt = ((JButton) e.getSource()).getText();
                switch (crnt) {
                    case "Slow":
                        GUI.this.guiBoard.setTimerSpeed(GUIBoard.SPEED.MEDIUM);
                        GUI.this.buttonSpeedSelect.setText("Medium");
                        break;
                    case "Medium":
                        GUI.this.guiBoard.setTimerSpeed(GUIBoard.SPEED.FAST);
                        GUI.this.buttonSpeedSelect.setText("Fast");
                        break;
                    case "Fast":
                        GUI.this.guiBoard.setTimerSpeed(GUIBoard.SPEED.SLOW);
                        GUI.this.buttonSpeedSelect.setText("Slow");
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
                    GUI.this.storeState();
                }
                else {
                    GUI.this.restoreState();
                    GUI.this.drawBoard();
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

        setStatus(0, 0, 0, 0, 0, GUI.this.guiBoard.manhattan());
        this.drawBoard();
        this.pack(); // resize the frame to fit the new size of the buttons
    }

    // Draw current Board
    public final void drawBoard() {
        int[][] board = this.guiBoard.getCurrentBoard();
        int empty = -1;
        int counter = 0;

        // Label the buttons (tiles)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                if (board[i][j] == 0) { empty = counter; }
                else { tiles[counter].setText(String.valueOf(board[i][j])); }
                counter = counter + 1;
            }
        }

        // Show all the buttons then hide blank one
        for (JButton tile : tiles) { tile.setVisible(true); }
        tiles[empty].setVisible(false);

        // Notify the panel to update
        Main_Middle.repaint();
        Main_Middle.revalidate();
    }

    public void setStatus(String stat) {
        this.Label_Status.setText(stat);
    }

    public void setStatus(long time, int stateCounter, int dupStates, int nrOfMoves, int nrOfRemainingMoves, int distance) {
        String status = format("<html>Time: %d ms<br/>All Nodes: %d <br/>Duplicates: %d<br/>Moves: %d<br/>Rem. moves %d<br/>Distance: %d</html>",
                               time, stateCounter, dupStates, nrOfMoves, nrOfRemainingMoves, distance); //null = placeholder for
        this.setStatus(status);
    }

    public void setText(String text) {
        this.txtpnTextbox.setText(text);
    }

    private void performSolution() {
        List<Board> solutionPath = guiBoard.solve();
        displaySolutionPath(solutionPath, guiBoard.getElapsedTime(), guiBoard.getStateCounter(), guiBoard.getDupStateCounter());
    }

    private void displaySolutionPath(List<Board> solutionPath, long time, int maxStates, int dupCounter) {
        // Save time, moves and nodes on label
        setStatus(time, maxStates, dupCounter, solutionPath.size(), solutionPath.size(), guiBoard.manhattan());

        this.solving = true;
        buttonSolve.setText(CANCEL);
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
                return this;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop solving when all moves are done or button reset pressed (goal state)
                System.out.println("start");
                if (solutionPath.isEmpty() || bc.isGoal()) {
                    System.out.println("exit");
                    GUI.this.solving = false;
                    ((Timer) e.getSource()).stop();
                    GUI.this.buttonSolve.setText(SOLVE);
                    return;
                }
                if (GUI.this.solving == false) {
                    System.out.println("break");
                    ((Timer) e.getSource()).stop();
                    GUI.this.buttonSolve.setText(SOLVE);
                    return;
                }
                // Set guiBoard and update the GUI
                Board saveBoard = this.solutionPath.get(counter++);
                bc.setBlocks(saveBoard.getBlocks());

                GUI.this.setStatus(time, maxStates, dupCounter, nrOfMoves, nrOfMoves - counter,
                                   bc.manhattan() + bc.getWeight());
                GUI.this.setText(format("Manhatten = %d, Hamming = %d, weight = %d", bc.manhattan(), bc.hamming(), bc.getWeight()));
                GUI.this.drawBoard();
            }
        }.me(solutionPath, this.guiBoard, time)).start();
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

    public void toggleSolveButton() {
        if (this.solveButton == true) {
            solveButton = false;
            buttonSolve.setText(CANCEL);
        }
        else {
            solveButton = true;
        }
        buttonSolve.setText(SOLVE);
    }

    private void storeState() {
        this.storedBlocks = guiBoard.getBlocks();
    }

    private void restoreState() {
        if (!store) {
            this.guiBoard = new GUIBoard(storedBlocks);
        }
    }

    /**
     * Code generated From GUI Editor do not modify
     */

    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        Main_Right = new javax.swing.JPanel();
        ButtonsPanel = new javax.swing.JPanel();
        buttonRandom = new JButton();
        buttonReset = new JButton();
        buttonSolve = new JButton();
        buttonMemory = new JButton();
        jPanel2 = new javax.swing.JPanel();
        Label_Status = new javax.swing.JLabel();
        Main_Middle = new javax.swing.JPanel();
        Tile_1 = new JButton();
        Tile_2 = new JButton();
        Tile_3 = new JButton();
        Tile_4 = new JButton();
        Tile_5 = new JButton();
        Tile_6 = new JButton();
        Tile_7 = new JButton();
        Tile_8 = new JButton();
        Tile_0 = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Main_Right.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
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

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 1, 1));
        jPanel2.setOpaque(false);
        jPanel2.add(Label_Status);

        Main_Right.add(jPanel2, BorderLayout.PAGE_START);

        getContentPane().add(Main_Right, BorderLayout.LINE_END);

        Main_Middle.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagLayout gbl_Main_Middle = new GridBagLayout();
        gbl_Main_Middle.rowWeights = new double[]{0.0, 0.0, 0.0};
        gbl_Main_Middle.columnWeights = new double[]{1.0, 0.0, 0.0};
        Main_Middle.setLayout(gbl_Main_Middle);

        Tile_1.setText("1");
        gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints_2.gridx = 0;
        gridBagConstraints_2.gridy = 0;
        gridBagConstraints_2.fill = GridBagConstraints.BOTH;
        gridBagConstraints_2.ipadx = 50;
        gridBagConstraints_2.ipady = 50;
        Main_Middle.add(Tile_1, gridBagConstraints_2);

        Tile_2.setText("2");
        gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints_3.gridx = 1;
        gridBagConstraints_3.gridy = 0;
        gridBagConstraints_3.fill = GridBagConstraints.BOTH;
        gridBagConstraints_3.ipadx = 50;
        gridBagConstraints_3.ipady = 50;
        Main_Middle.add(Tile_2, gridBagConstraints_3);

        Tile_3.setText("3");
        gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints_4.gridx = 2;
        gridBagConstraints_4.gridy = 0;
        gridBagConstraints_4.fill = GridBagConstraints.BOTH;
        gridBagConstraints_4.ipadx = 50;
        gridBagConstraints_4.ipady = 50;
        Main_Middle.add(Tile_3, gridBagConstraints_4);

        Tile_4.setText("4");
        gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints_5.gridx = 0;
        gridBagConstraints_5.gridy = 1;
        gridBagConstraints_5.fill = GridBagConstraints.BOTH;
        gridBagConstraints_5.ipadx = 50;
        gridBagConstraints_5.ipady = 50;
        Main_Middle.add(Tile_4, gridBagConstraints_5);

        Tile_5.setText("5");
        gridBagConstraints_6 = new GridBagConstraints();
        gridBagConstraints_6.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints_6.gridx = 1;
        gridBagConstraints_6.gridy = 1;
        gridBagConstraints_6.fill = GridBagConstraints.BOTH;
        gridBagConstraints_6.ipadx = 50;
        gridBagConstraints_6.ipady = 50;
        Main_Middle.add(Tile_5, gridBagConstraints_6);

        Tile_6.setText("6");
        gridBagConstraints_7 = new GridBagConstraints();
        gridBagConstraints_7.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints_7.gridx = 2;
        gridBagConstraints_7.gridy = 1;
        gridBagConstraints_7.fill = GridBagConstraints.BOTH;
        gridBagConstraints_7.ipadx = 50;
        gridBagConstraints_7.ipady = 50;
        Main_Middle.add(Tile_6, gridBagConstraints_7);

        Tile_7.setText("7");
        gridBagConstraints_8 = new GridBagConstraints();
        gridBagConstraints_8.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints_8.gridx = 0;
        gridBagConstraints_8.gridy = 2;
        gridBagConstraints_8.fill = GridBagConstraints.BOTH;
        gridBagConstraints_8.ipadx = 50;
        gridBagConstraints_8.ipady = 50;
        Main_Middle.add(Tile_7, gridBagConstraints_8);

        Tile_8.setText("8");
        gridBagConstraints_9 = new GridBagConstraints();
        gridBagConstraints_9.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints_9.gridx = 1;
        gridBagConstraints_9.gridy = 2;
        gridBagConstraints_9.fill = GridBagConstraints.BOTH;
        gridBagConstraints_9.ipadx = 50;
        gridBagConstraints_9.ipady = 50;
        Main_Middle.add(Tile_8, gridBagConstraints_9);
        gridBagConstraints_10 = new GridBagConstraints();
        gridBagConstraints_10.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints_10.gridx = 2;
        gridBagConstraints_10.gridy = 2;
        gridBagConstraints_10.fill = GridBagConstraints.BOTH;
        gridBagConstraints_10.ipadx = 50;
        gridBagConstraints_10.ipady = 50;
        Main_Middle.add(Tile_0, gridBagConstraints_10);

        getContentPane().add(Main_Middle, BorderLayout.CENTER);

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
    private javax.swing.JPanel ButtonsPanel;
    private javax.swing.JLabel Label_Status;
    public javax.swing.JPanel Main_Middle;
    private javax.swing.JPanel Main_Right;
    private JButton Tile_0;
    private JButton Tile_1;
    private JButton Tile_2;
    private JButton Tile_3;
    private JButton Tile_4;
    private JButton Tile_5;
    private JButton Tile_6;
    private JButton Tile_7;
    private JButton Tile_8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private GridBagConstraints gbc_btnSolver;
    private GridBagConstraints gbc_btnZufllig;
    private GridBagConstraints gridBagConstraints_1;
    private JTextPane txtpnTextbox;
    private GridBagConstraints gridBagConstraints_2;
    private GridBagConstraints gridBagConstraints_3;
    private GridBagConstraints gridBagConstraints_4;
    private GridBagConstraints gridBagConstraints_5;
    private GridBagConstraints gridBagConstraints_6;
    private GridBagConstraints gridBagConstraints_7;
    private GridBagConstraints gridBagConstraints_8;
    private GridBagConstraints gridBagConstraints_9;
    private GridBagConstraints gridBagConstraints_10;
    private JComboBox comboBoxStrategy;
    // End of variables declaration//GEN-END:variables
}
