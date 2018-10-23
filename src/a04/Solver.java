/****************************
 * Author: Spencer Rosenvall
 * Class: CSIS 2420
 * Professor: Frau Posch
 * Assignment: A04_8Puzzle
 ***************************/

package a04;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

/**
 * Class Solver is used to solve the common 8 puzzle. Solver can determine if a
 * particular 8 puzzle is solvable and the solution if it is.
 * 
 * @author SpencerR
 *
 */
public class Solver {
	private int n;
	private Board original;
	private Board completed;
	private MinPQ<SearchNode> pq;
	private MinPQ<SearchNode> pqDuplicate;

	/**
	 * Private class SearchNode implements comparable, which compares the priorities
	 * of two nodes and determines moves based off the Manhattan method.
	 * 
	 * @author SpencerR
	 *
	 */
	private class SearchNode implements Comparable<SearchNode> {
		private int moves;
		private int priority;
		private Board b;
		private SearchNode previous;

		/**
		 * Constructs the private SearchNode using a board, moves, and the previous
		 * SearchNode.
		 * 
		 * @param b
		 * @param moves
		 * @param previous
		 */
		public SearchNode(Board b, int moves, SearchNode previous) {
			this.b = b;
			this.previous = previous;
			this.moves = moves;
			priority = moves + b.manhattan();
		}

		/**
		 * Compares this SeachNode to another but subtracting priorities. If difference
		 * in priorities == 0, they're the same. If greater than 0, this priority is
		 * bigger. If smaller than 0, that priority is bigger.
		 */
		public int compareTo(SearchNode that) {
			return (this.priority - that.priority);
		}
	}

	/**
	 * Constructs a solver for a solution to the 8 puzzle board using the A*
	 * algorithm.
	 * 
	 * @param initial
	 */
	public Solver(Board initial) {
		if (initial == null) {
			throw new NullPointerException();
		}

		this.original = initial;
		n = initial.size();
		pq = new MinPQ<SearchNode>();
		pqDuplicate = new MinPQ<SearchNode>();

		constructBoards(initial);
	}

	/**
	 * Determines the min # moves to solve the initial board. Returns -1 if
	 * unsolvable.
	 * 
	 * @return int
	 */
	public int moves() {
		if (!isSolvable())
			return -1;
		// return end.moves;
		return pq.min().moves;
	}

	/**
	 * Determines the solution to an 8 puzzle board with the shortest possible
	 * solution. Returns null if board is unsolvable.
	 * 
	 * @return Iterable<Board>
	 */
	public Iterable<Board> solution() {
		if (!isSolvable())
			return null;
		Stack<Board> solution = new Stack<Board>();
		SearchNode n = pq.min();
		while (n.previous != null) {
			solution.push(n.b);
			n = n.previous;
		}
		solution.push(original);
		return solution;
	}

	/**
	 * Constructs the initial and completed boards.
	 * 
	 * @param initial
	 */
	private void constructBoards(Board initial) {
		int[][] positions = new int[n][n];
		int k = 1;
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				positions[i][j] = k;
				k++;
			}
		positions[n - 1][n - 1] = 0;
		completed = new Board(positions);

		SearchNode min;
		SearchNode minDuplicate;
		pq.insert(new SearchNode(initial, 0, null));
		pqDuplicate.insert(new SearchNode(initial.copy(), 0, null));
		while (!pq.min().b.equals(completed) && !pqDuplicate.min().b.equals(completed)) {
			min = pq.min();
			minDuplicate = pqDuplicate.min();
			pq.delMin();
			pqDuplicate.delMin();
			for (Board neighbor : min.b.neighbors()) {
				if (min.moves == 0) {
					pq.insert(new SearchNode(neighbor, min.moves + 1, min));
				} else if (!neighbor.equals(min.previous.b)) {
					pq.insert(new SearchNode(neighbor, min.moves + 1, min));
				}
			}
			for (Board board : minDuplicate.b.neighbors()) {
				if (minDuplicate.moves == 0) {
					pqDuplicate.insert(new SearchNode(board, minDuplicate.moves + 1, minDuplicate));
				} else if (!board.equals(minDuplicate.previous.b)) {
					pqDuplicate.insert(new SearchNode(board, minDuplicate.moves + 1, minDuplicate));
				}
			}
		}
	}

	/**
	 * Determines if the board is solvable.
	 * 
	 * @return boolean
	 */
	private boolean isSolvable() {
		if (pq.min().b.equals(completed)) {
			return true;
		}
		if (pqDuplicate.min().b.equals(completed)) {
			return false;
		}
		return false;
	}

	/**
	 * Main method to test the Solver Class.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String[] test = { "src/puzzle04.txt" };
		EightPuzzleTestClient.main(test);
	}
}