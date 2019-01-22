package de.hrw.wi.puzzle.gui.window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {

    private List<JButton> tileList;
    private int dimX, dimY;

    // --------------------------------------------------------------------------------------------
    // API
    // --------------------------------------------------------------------------------------------

    public BoardPanel(int dimension) {
        super();
        init(dimension, dimension);
    }

    public BoardPanel(int dimX, int dimY) {
        super();
        init(dimX, dimY);
    }

    public void init(int dimX, int dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        tileList = new ArrayList<>(dimX * dimY);
    }

    public void setBoard(int[][] board) {

    }

    public final void drawBoard(int[][] board) {
        int empty = -1;
        int counter = 0;

        // Label the buttons (tiles)
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                if (board[i][j] == 0) { empty = counter; }
                else { tileList.get(counter).setText(String.valueOf(board[i][j]));}
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
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                JButton tile = new JButton();
                addToLayout(tile, i, j);
                tileList.add(tile);
            }
        }

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
