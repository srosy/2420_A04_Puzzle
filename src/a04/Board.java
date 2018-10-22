/****************************
 * Author: Spencer Rosenvall
 * Class: CSIS 2420
 * Professor: Frau Posch
 * Assignment: A04_8Puzzle
 ***************************/

package a04;

import java.util.Arrays;
import java.util.Stack;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Class Board creates a 1d or 2d board and implements methods used to check for
 * neighboring boards, manhattan distance, and hamming misplaced blocks.
 * 
 * @author SpencerR
 *
 */
public class Board {
	private char[] positions;
	private int n;
	private int empties;

	/**
	 * Constructs a board from an n-by-n array of blocks.
	 * 
	 * @param blocks
	 */
	public Board(int[][] blocks) {
		n = blocks.length;
		this.positions = new char[n * n];
		int k = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				this.positions[k] = (char) blocks[i][j];
				if (blocks[i][j] == 0)
					empties = k;
				k++;
			}
		}
	}

	/**
	 * Returns the size of the board.
	 * 
	 * @return int n
	 */
	public int size() {
		return n;
	}

	/**
	 * Determines the number of misplaced blocks from the ideal position of each
	 * block.
	 * 
	 * @return
	 */
	public int hamming() {
		int numMisplaced = 0; // hamming
		for (int k = 0, ans = 1; k < n * n; k++, ans++) {
			if (positions[k] == 0)
				continue;
			if (positions[k] != ans)
				numMisplaced++;
		}
		return numMisplaced;
	}

	/**
	 * Returns a sum of the Manhattan distances between the blocks and the goal.
	 * 
	 * @return
	 */
	public int manhattan() {
		int sum = 0;
		for (int k = 0; k < n * n; k++) {
			if (positions[k] == 0)
				continue;
			int rowdiff = Math.abs(row(positions[k]) - row(k + 1));
			int coldiff = Math.abs(col(positions[k]) - col(k + 1));
			sum = sum + rowdiff + coldiff;
		}
		return sum;
	}

	/**
	 * Returns true if the positions match the end goal board positions.
	 * 
	 * @return
	 */
	public boolean isGoal() {
		for (int i = 0; i < n * n - 2; i++) {
			if (positions[i] > positions[i + 1])
				return false;
		}
		return true;
	}

	/**
	 * Checks if the board is solvable by trying to solve a solution, solution in
	 * class Solver determines if initial board is solvable.
	 * 
	 * @return
	 */
	public boolean isSolvable() {
		Solver s = new Solver(this);
		if (s.solution() != null)
			return true;
		return false;
	}

	/**
	 * Compares the this board and the other board.
	 */
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null)
			return false;
		if (other.getClass() != this.getClass())
			return false;
		Board that = (Board) other;
		if (!Arrays.equals(this.positions, that.positions))
			return false;
		return true;
	}

	/**
	 * Creates a board stack for all neighboring boards.
	 * 
	 * @return
	 */
	public Iterable<Board> neighbors() {
		Stack<Board> boardStack = new Stack<Board>();
		char[] c;
		if (row(empties + 1) != 1) {
			c = positions.clone();
			swapTop(c, empties);
			Board neighborBoard = new Board(to2D(c));
			boardStack.push(neighborBoard);
		}
		if (row(empties + 1) != n) {
			c = positions.clone();
			swapBottom(c, empties);
			Board neighborBoard = new Board(to2D(c));
			boardStack.push(neighborBoard);
		}
		if (col(empties + 1) != 1) {
			c = positions.clone();
			swapLeft(c, empties);
			Board neighborBoard = new Board(to2D(c));
			boardStack.push(neighborBoard);
		}
		if (col(empties + 1) != n) {
			c = positions.clone();
			swapRight(c, empties);
			Board neighborBoard = new Board(to2D(c));
			boardStack.push(neighborBoard);
		}
		return boardStack;
	}

	// // is the initial board solvable?
	// public boolean isSolvable() {
	// if (pq.min().b.equals(completed)) {
	// return true;
	// }
	// if (pqDuplicate.min().b.equals(completed)) {
	// return false;
	// }
	// return false;
	// }

	/**
	 * Returns a string representation of this board.
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(n + "\n");
		int k = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				s.append(String.format("%2d ", (int) positions[k]));
				k++;
			}
			s.append("\n");
		}
		return s.toString();
	}

	/**
	 * Returns a row from position / board.
	 * 
	 * @param p
	 * @return
	 */
	private int row(int p) {
		return (int) Math.ceil((double) p / (double) n);
	}

	/**
	 * Returns a columns int from position % board.
	 * 
	 * @param p
	 * @return
	 */
	private int col(int p) {
		if (p % n == 0)
			return n;
		return p % n;
	}

	/**
	 * Returns a Board by exchanging any pair of blocks.
	 * 
	 * @return
	 */
	protected Board copy() {
		boolean canSwap = false;
		char[] copy = positions.clone();
		// choose a non-blank block
		int k = 0;
		do {
			k = StdRandom.uniform(n * n);
		} while (positions[k] == 0);
		// choose an exchange direction
		while (canSwap == false) {
			int choice = StdRandom.uniform(4);
			switch (choice) {
			case 0: // swapAbove
				if (row(k + 1) == 1)
					canSwap = false;
				else if (copy[k - n] == 0)
					canSwap = false;
				else {
					swapTop(copy, k);
					canSwap = true;
				}
				break;
			case 1: // swapBelow
				if (row(k + 1) == n)
					canSwap = false;
				else if (copy[k + n] == 0)
					canSwap = false;
				else {
					swapBottom(copy, k);
					canSwap = true;
				}
				break;
			case 2: // swapLeft
				if (col(k + 1) == 1)
					canSwap = false;
				else if (copy[k - 1] == 0)
					canSwap = false;
				else {
					swapLeft(copy, k);
					canSwap = true;
				}
				break;
			case 3: // swapRight
				if (col(k + 1) == n)
					canSwap = false;
				else if (copy[k + 1] == 0)
					canSwap = false;
				else {
					swapRight(copy, k);
					canSwap = true;
				}
				break;
			}
		}
		Board boardCopy = new Board(to2D(copy));
		return boardCopy;
	}

	/**
	 * Swaps the number in positions k and k-n.
	 * 
	 * @param oneDarray
	 * @param k
	 */
	private void swapTop(char[] oneDarray, int k) {
		char temp = oneDarray[k];
		oneDarray[k] = oneDarray[k - n];
		oneDarray[k - n] = temp;
	}

	/**
	 * Swaps the numbers in positions k and k + n.
	 * 
	 * @param oneDarray
	 * @param k
	 */
	private void swapBottom(char[] oneDarray, int k) {
		char temp = oneDarray[k];
		oneDarray[k] = oneDarray[k + n];
		oneDarray[k + n] = temp;
	}

	/**
	 * Swaps the numbers in positions k and k - 1.
	 * 
	 * @param oneDarray
	 * @param k
	 */
	private void swapLeft(char[] oneDarray, int k) {
		char temp = oneDarray[k];
		oneDarray[k] = oneDarray[k - 1];
		oneDarray[k - 1] = temp;
	}

	/**
	 * Swaps the numbers in positions k and k + 1.
	 * 
	 * @param oneDarray
	 * @param k
	 */
	private void swapRight(char[] oneDarray, int k) {
		char temp = oneDarray[k];
		oneDarray[k] = oneDarray[k + 1];
		oneDarray[k + 1] = temp;
	}

	/**
	 * Returns a int[][] from using a char[] to convert to that int[][].
	 * 
	 * @param oneDarray
	 * @return
	 */
	private int[][] to2D(char[] oneDarray) {
		int k = 0;
		int[][] positions = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				positions[i][j] = oneDarray[k];
				k++;
			}
		return positions;
	}

	/**
	 * Main method for testing operation of class Board.
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		In in = new In(args[0]);
		int n = in.readInt();
		// In in = new In("src/puzzle00.txt");
		// int n = 2;
		int[][] positions = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				positions[i][j] = in.readInt();
		// positions[i][j] = n;
		Board b = new Board(positions);
	}

}