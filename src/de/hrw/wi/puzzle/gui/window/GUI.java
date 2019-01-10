package de.hrw.wi.puzzle.gui.window;

import de.hrw.wi.puzzle.gui.game.Board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.JTextPane;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class GUI extends javax.swing.JFrame {

	public final GUIBoard board;
	private final JButton tiles[];
	private static int[][] blocks1 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };

	public GUI() {

		// Frame title
		super("8 Puzzle");

		// GUI settings
		initComponents();
		this.setLocationRelativeTo(null); // center the frame in the screen on open
		this.setResizable(false);
		this.setAlwaysOnTop(true);

		// Init global variables
		this.tiles = new JButton[] { Tile_1, Tile_2, Tile_3, Tile_4, Tile_5, Tile_6, Tile_7, Tile_8, Tile_0 };
		this.board = new GUIBoard(blocks1);

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
					if (GUI.this.board.isSolving())
						return;
					GUI.this.board.tilePressed(num);
					GUI.this.drawBoard();
				}
			}.me(i));
		}

		// Action Listener Reset
		Button_Reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUI.this.board.resetBoard();
				GUI.this.drawBoard();
			}
		});

		// Action Listener Randomize
		btnZufllig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GUI.this.board.isSolving())
					return;
				GUI.this.board.randomizeBoard();
				GUI.this.drawBoard();
			}
		});

		// Action Listener Solver
		btnSolver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GUI.this.board.isSolving())
					return;
				GUI.this.board.solve(GUI.this);
				GUI.this.pack();
			}
		});

		// Action Listener Solving Speed
		Button_Speed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GUI.this.board.isSolving())
					return;
				String crnt = ((JButton) e.getSource()).getText();
				switch (crnt) {
				case "Slow":
					GUI.this.board.setTimerSpeed(GUIBoard.SPEED.MEDIUM);
					GUI.this.Button_Speed.setText("Medium");
					break;
				case "Medium":
					GUI.this.board.setTimerSpeed(GUIBoard.SPEED.FAST);
					GUI.this.Button_Speed.setText("Fast");
					break;
				case "Fast":
					GUI.this.board.setTimerSpeed(GUIBoard.SPEED.SLOW);
					GUI.this.Button_Speed.setText("Slow");
					break;
				}
			}
		});

		this.drawBoard();
		this.pack(); // resize the frame to fit the new size of the buttons
	}

	// Draw current Board
	public final void drawBoard() {
		int[][] board = this.board.getCurrentBoard();
		int empty = -1;
		int counter = 0;

		// Label the buttons (tiles)
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {

				if (board[i][j] == 0)
					empty = counter;
				else
					tiles[counter].setText(String.valueOf(board[i][j]));
				counter = counter + 1;
			}
		}

		// Show all the buttons then hide blank one
		for (JButton tile : tiles)
			tile.setVisible(true);
		tiles[empty].setVisible(false);

		// Notify the panel to update
		Main_Middle.repaint();
		Main_Middle.revalidate();
	}

	public void setStatus(String stat) {
		this.Label_Status.setText(stat);
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
		btnZufllig = new JButton();
		Button_Reset = new JButton();
		btnSolver = new JButton();
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
		gbl_ButtonsPanel.columnWeights = new double[] { 1.0 };
		ButtonsPanel.setLayout(gbl_ButtonsPanel);

		btnZufllig.setFont(new Font("Ubuntu", 0, 16)); // NOI18N
		btnZufllig.setText("Randomize");
		btnZufllig.setFocusable(false);
		gbc_btnZufllig = new GridBagConstraints();
		gbc_btnZufllig.insets = new Insets(0, 0, 5, 0);
		gbc_btnZufllig.gridx = 0;
		gbc_btnZufllig.gridy = 0;
		gbc_btnZufllig.fill = GridBagConstraints.HORIZONTAL;
		ButtonsPanel.add(btnZufllig, gbc_btnZufllig);

		Button_Reset.setFont(new Font("Ubuntu", 0, 16)); // NOI18N
		Button_Reset.setText("Reset");
		Button_Reset.setFocusable(false);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		ButtonsPanel.add(Button_Reset, gridBagConstraints);

		btnSolver.setFont(new Font("Ubuntu", 0, 16)); // NOI18N
		btnSolver.setText("Solve");
		btnSolver.setFocusable(false);
		gbc_btnSolver = new GridBagConstraints();
		gbc_btnSolver.insets = new Insets(0, 0, 5, 0);
		gbc_btnSolver.gridx = 0;
		gbc_btnSolver.gridy = 2;
		gbc_btnSolver.fill = GridBagConstraints.HORIZONTAL;
		ButtonsPanel.add(btnSolver, gbc_btnSolver);

		Main_Right.add(ButtonsPanel, BorderLayout.CENTER);
		Button_Speed = new JButton();
		Button_Speed.setFont(new Font("Dialog", Font.PLAIN, 16));

		Button_Speed.setText("Slow");
		Button_Speed.setFocusable(false);
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 3;
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		ButtonsPanel.add(Button_Speed, gridBagConstraints_1);

		comboBox = new JComboBox();
		comboBox.setFont(new Font("Dialog", Font.PLAIN, 16));
		comboBox.setModel(
				new DefaultComboBoxModel(new String[] { "Strategy 1", "Strategy 2", "Strategy 3", "Strategy 4" }));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.BOTH;
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 4;
		ButtonsPanel.add(comboBox, gbc_comboBox);

		jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 1, 1));
		jPanel2.setOpaque(false);
		jPanel2.add(Label_Status);

		Main_Right.add(jPanel2, BorderLayout.PAGE_START);

		getContentPane().add(Main_Right, BorderLayout.LINE_END);

		Main_Middle.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridBagLayout gbl_Main_Middle = new GridBagLayout();
		gbl_Main_Middle.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		gbl_Main_Middle.columnWeights = new double[] { 1.0, 0.0, 0.0 };
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
	private JButton btnZufllig;
	private JButton Button_Reset;
	private JButton btnSolver;
	private JButton Button_Speed;
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
	private JComboBox comboBox;
	// End of variables declaration//GEN-END:variables
}
