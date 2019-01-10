package de.hrw.wi.puzzle.gui.game;

import de.hrw.wi.java.game.AState;

import java.util.LinkedList;
import java.util.Random;

public class Board extends AState {
	private static final int SPACE = 0;
	public static final int DIMENSION = 3;

	private int[][] blocks;
	private int[][] goal = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };
	private int spaceRow;
	private int spaceColumn;



	// --------------------------------------------------------------------------------------------
	// API
	// --------------------------------------------------------------------------------------------

	public Board(int[][] blocks) {
		super(0);
		init(blocks);
	}

	protected void init(int[][] blocks) {
		this.blocks = copy(blocks);
		int[] spaceLocation = this.spaceLocation();
		this.spaceRow = spaceLocation[0];
		this.spaceColumn = spaceLocation[1];
	}

	public Board(Board b) {
		super(0);
		init(b.blocks);
	}

	public void setCurrentBoard(Board b) {
		this.blocks = b.blocks;
	}

	public Board(boolean start) {
		super(0);
		blocks = new int[DIMENSION][DIMENSION];

		int[] numbers = new int[DIMENSION];
		for (int i = 0; i < DIMENSION; i++) {
			numbers[i] = i;
		}

		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks.length; col++) {
				int index = new Double(Math.random() * numbers.length).intValue();
				if (index == numbers.length) {
					index--;
				}
				blocks[row][col] = numbers[index];
			}
		}

	}

	public Board move(int i, int j) {
		Board newBoard = null;
		if (i > 0 || i < 0) {
			if (spaceRow + i >= 0 && spaceRow + i < DIMENSION) {
				newBoard = new Board(this.blocks);
				newBoard.blocks = newBoard.swap(spaceRow + i, spaceColumn, spaceRow, spaceColumn);
				newBoard.spaceRow = spaceRow + i;
				newBoard.spaceColumn = spaceColumn;
			}
		} else if (j > 0 || j < 0) {
			if (spaceColumn + j >= 0 && spaceColumn + j < DIMENSION) {
				newBoard = new Board(this.blocks);
				newBoard.blocks = newBoard.swap(spaceRow, spaceColumn + j, spaceRow, spaceColumn);
				newBoard.spaceRow = spaceRow;
				newBoard.spaceColumn = spaceColumn + j;
			}
		}
		return newBoard;
	}

	public int dimension() {
		return blocks.length;
	}

	public int hamming() {
		int count = 0;
		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks.length; col++) {
				if (blockIsNotInPlace(row, col)) {
					count++;
				}
			}
		}
		return count;
	}

	public boolean isGoal() {
		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks.length; col++) {
				if (blockIsInPlace(row, col)) {
					return false;
				}
			}
		}
		return true;
	}

	public Board twin() {
		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks.length - 1; col++) {
				if (!isSpace(block(row, col)) && !isSpace(block(row, col + 1))) {
					return new Board(swap(row, col, row, col + 1));
				}
			}
		}
		throw new RuntimeException();
	}

	public Iterable<Board> neighbors() {
		LinkedList<Board> neighbors = new LinkedList<Board>();

		int[] location = spaceLocation();
		int spaceRow = location[0];
		int spaceCol = location[1];

		if (spaceRow > 0) {
			neighbors.add(new Board(swap(spaceRow, spaceCol, spaceRow - 1, spaceCol)));
		}
		if (spaceRow < dimension() - 1) {
			neighbors.add(new Board(swap(spaceRow, spaceCol, spaceRow + 1, spaceCol)));
		}
		if (spaceCol > 0) {
			neighbors.add(new Board(swap(spaceRow, spaceCol, spaceRow, spaceCol - 1)));
		}
		if (spaceCol < dimension() - 1) {
			neighbors.add(new Board(swap(spaceRow, spaceCol, spaceRow, spaceCol + 1)));
		}

		return neighbors;
	}

	public int getSpaceRow() {
		return spaceRow;
	}

	public int getSpaceColumn() {
		return spaceColumn;
	}

	public int manhattan() {
		int sum = 0;
		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks.length; col++) {
				sum += calculateDistances(row, col);
			}
		}
		return sum;
	}

	public boolean equals(Object y) {
		if (y == this) {
			return true;
		}
		if (y == null || !(y instanceof Board) || ((Board) y).blocks.length != blocks.length) {
			return false;
		}
		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks.length; col++) {
				if (((Board) y).blocks[row][col] != block(row, col)) {
					return false;
				}
			}
		}
		return true;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		// str.append("dim=" + dimension() + "\n");
		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks.length; col++) {
				str.append(String.format("%2d ", block(row, col)));
			}
			str.append("\n");
		}
		return str.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	public void resetBoard() {
		init(goal);
	}

	public void randomizeBoard() {
		int randomBlocks[][];
		// check if random created state is solvable
		while (!isSolvable(randomBlocks = getRandomBlocks()))
			;
		init(randomBlocks);
	}
	
	public int[][] getCurrentBoard() {
		return blocks.clone();
	}

	public int[][] getBlocks() {
		return blocks;
	}

	public void setBlocks(int[][] blocks) {
		init(blocks);
	}
	// --------------------------------------------------------------------------------------------
	// private
	// --------------------------------------------------------------------------------------------

	private int[][] copy(int[][] blocks) {
		int[][] copy = new int[blocks.length][blocks.length];
		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks.length; col++) {
				copy[row][col] = blocks[row][col];
			}
		}
		return copy;
	}

	private boolean blockIsNotInPlace(int row, int col) {
		int block = block(row, col);

		return !isSpace(block) && block != goalFor(row, col);
	}

	private int goalFor(int row, int col) {
		return row * dimension() + col + 1;
	}

	private boolean isSpace(int block) {
		return block == SPACE;
	}

	private int calculateDistances(int row, int col) {
		int block = block(row, col);

		return (isSpace(block)) ? 0 : Math.abs(row - row(block)) + Math.abs(col - col(block));
	}

	private int block(int row, int col) {
		return blocks[row][col];
	}

	private int row(int block) {
		return (block - 1) / dimension();
	}

	private int col(int block) {
		return (block - 1) % dimension();
	}

	private boolean blockIsInPlace(int row, int col) {
		int block = block(row, col);

		return !isSpace(block) && block != goalFor(row, col);
	}

	private int[][] swap(int row1, int col1, int row2, int col2) {
		int[][] copy = copy(blocks);
		int tmp = copy[row1][col1];
		copy[row1][col1] = copy[row2][col2];
		copy[row2][col2] = tmp;

		return copy;
	}

	protected int[] spaceLocation() {
		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks.length; col++) {
				if (isSpace(block(row, col))) {
					int[] location = new int[2];
					location[0] = row;
					location[1] = col;

					return location;
				}
			}
		}
		throw new RuntimeException();
	}

	private int[][] getRandomBlocks() {
		// save numbers
		int[][] randomBlocks = new int[3][3];
		// check number not already used (no duplicates)
		boolean[] control = new boolean[9];
		Random rand = new Random();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int k;
				// check number is not already used
				while (control[k = rand.nextInt(9)])
					;
				control[k] = true;
				randomBlocks[i][j] = k;
			}
		}
		return randomBlocks;
	}

	private boolean isSolvable(int block[][]) {
		int inversion = 0;
		int[] checkBlock = new int[9];
		int counter = 0;

		// Convert to int[] for easier check
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				checkBlock[counter] = block[row][col];
				counter++;
			}
		}

		// Check if current state is solvable
		for (int i = 0; i < checkBlock.length; i++) {
			for (int j = i + 1; j < checkBlock.length; j++) {
				if (checkBlock[j] != 0) { // ignore 0
					if (checkBlock[j] < checkBlock[i]) {
						inversion++;
					}
				}
			}
		}
		return (inversion % 2 == 0);
	}

}