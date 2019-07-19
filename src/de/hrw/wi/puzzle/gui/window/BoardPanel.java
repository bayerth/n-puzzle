package de.hrw.wi.puzzle.gui.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {

    private GUIBoard8Puzzle board;
    private List<JButton> tileList;
    private int dimX, dimY;
    private boolean solving = false;
    private OnClickInteractionListener mListener;

    public interface OnClickInteractionListener {
        void onClickAction(int tileNumber);
    }

    // --------------------------------------------------------------------------------------------
    // API
    // --------------------------------------------------------------------------------------------

    public BoardPanel(int dimension, OnClickInteractionListener listener) {
        super();
        init(dimension, dimension, listener);
    }

    public BoardPanel(int dimension) {
        super();
        init(dimension, dimension, null);
    }

    public BoardPanel(int dimX, int dimY) {
        super();
        init(dimX, dimY, null);
    }

    public void init(int dimX, int dimY, OnClickInteractionListener listener) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.mListener = listener;
        tileList = new ArrayList<>(dimX * dimY);
        board = new GUIBoard8Puzzle(dimX);
    }

    public void setBoard(int[][] blocks) {
        this.board.setBlocks(blocks);
    }

    public GUIBoard8Puzzle getBoard() {
        return board;
    }

    public int[][] getBlocks() {
        return this.board.getBlocks();
    }

    public void reset() {
        board.resetBoard();
    }

    public void randomize() {
        board.randomizeBoard();
    }

    public final void draw() {
        int empty = -1;
        int counter = 0;

        // Label the buttons (tiles)
        String formatString;
        if (dimX * dimY < 10) { formatString = "%d"; }
        else {formatString = "%02d";}
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                if (board.getBlocks()[i][j] == 0) { empty = counter; }
                else { tileList.get(counter).setText(String.format(formatString, board.getBlocks()[i][j]));}
                counter++;
            }
        }

        // Show all the buttons then hide blank one
        for (JButton tile : tileList) { tile.setVisible(true); }
        tileList.get(empty).setVisible(false);

        // Notify the panel to update
        repaint();
        revalidate();
    }

    public final void drawBoard(int[][] board) {
        int empty = -1;
        int counter = 0;

        // Label the buttons (tiles)
        String formatString;
        if (dimX * dimY < 10) { formatString = "%d"; }
        else {formatString = "%02d";}
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                if (board[i][j] == 0) { empty = counter; }
                else { tileList.get(counter).setText(String.format(formatString, board[i][j]));}
                counter++;
            }
        }

        // Show all the buttons then hide blank one
        for (JButton tile : tileList) { tile.setVisible(true); }
        tileList.get(empty).setVisible(false);

        // Notify the panel to update
        repaint();
        revalidate();
    }

    public List<JButton> getTileList() {
        return tileList;
    }

    private void setLayout() {
        GridBagLayout gbl_Main_Middle = new GridBagLayout();
        gbl_Main_Middle.rowWeights = new double[]{0.0, 0.0, 0.0};
        gbl_Main_Middle.columnWeights = new double[]{1.0, 0.0, 0.0};

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbl_Main_Middle = new GridBagLayout();
        gbl_Main_Middle.rowWeights = new double[]{0.0, 0.0, 0.0};
        gbl_Main_Middle.columnWeights = new double[]{1.0, 0.0, 0.0};
        setLayout(gbl_Main_Middle);
    }

    public void generateButtons() {
        setLayout();
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                JButton tile = new JButton();
                addToLayout(tile, x, y);
                tile.addActionListener(getListener(x, y));
                tileList.add(tile);
            }
        }
    }

    protected ActionListener getListener(int x, int y) {
        return new ActionListener() {
            int tileNumber;

            ActionListener me(int i) {
                tileNumber = i;
                return this;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (BoardPanel.this.solving) { return; }
                BoardPanel.this.board.tilePressed(tileNumber);
                if (mListener != null) { mListener.onClickAction(tileNumber); }
                BoardPanel.this.draw();
            }
        }.me(y * dimX + x);
    }

    // --------------------------------------------------------------------------------------------
    // private
    // --------------------------------------------------------------------------------------------

    private void addToLayout(JButton tile, int x, int y) {
        tile.setText(x + ":" + y);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints.gridx = x;
        gridBagConstraints.gridy = y;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.ipady = 50;
        this.add(tile, gridBagConstraints);
    }
}
