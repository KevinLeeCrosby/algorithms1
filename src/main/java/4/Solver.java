import java.util.HashMap;
import java.util.Map;

/**
 * 8-tile puzzle solver (works for up to 256x256 in theory, time and memory pending)
 *
 * @author Kevin Crosby
 */
public class Solver {
  private final Stack<Board> solution;
  private boolean infeasible;

  /**
   * Extend Board so that heuristics can be updated and memoized.
   */
  private static class Bored extends Board {
    public static final Map<char[], Integer> heuristics = new HashMap<>();

    /**
     * Construct a board from an N-by-N array of blocks
     * (where blocks[i][j] = block in row i, column j).
     *
     * @param blocks Array of blocks.
     */
    public Bored(final int[][] blocks) {
      super(blocks);
    }

    /**
     * Construct a board from another board.
     *
     * @param board Board to copy.
     */
    protected Bored(final Board board) {
      super(board);
    }

    /**
     * Construct a board from another board, with exchanged adjacent tiles, and update associated heuristic.
     *
     * @param board Board to copy.
     * @param i     First block to swap.
     * @param j     Second block to swap.
     */
    protected Bored(final Board board, final int i, final int j) {
      super(board, i, j);

      Bored brd;
      if (board instanceof Bored) {
        brd = (Bored) board;
      } else {
        brd = new Bored(board);
      }
      int h = brd.heuristic();
      int dm = manhattan(i) + manhattan(j) - board.manhattan(i) - board.manhattan(j);
      //int dl = linearConflicts() - board.linearConflicts();
      int dl = linearConflicts(i, j) - board.linearConflicts(i, j);  // commutative
      int dh = dm + dl;
      heuristics.put(grid, h + dh);
    }

    /**
     * Compute or retrieve heuristic for board.
     *
     * @return Heuristic for board.
     */
    private int heuristic() {
      if (!heuristics.containsKey(grid)) {
        int heuristic = manhattan() + linearConflicts();
        heuristics.put(grid, heuristic);
      }
      return heuristics.get(grid);
    }

    /**
     * A board that is obtained by exchanging two adjacent blocks in the same row.
     *
     * @return Board with two adjacent blocks transposed, with class of Bored, so that heuristics can be updated.
     */
    @Override
    public Board twin() {
      for (int r = 0; r < N; r++) {
        for (int c = 0; c < N - 1; c++) {
          int i = r * N + c;
          if (i == blank || i + 1 == blank) { continue; }
          return new Bored(this, i, i + 1);
        }
      }
      return null;
    }

    /**
     * Compute all neighboring boards.
     *
     * @return All neighboring boards, with class of Bored, so that heuristics can be updated.
     */
    @Override
    public Iterable<Board> neighbors() {
      Stack<Board> neighbors = new Stack<>();

      int r = blank / N, c = blank % N;

      for (int dr = -1; dr <= +1; dr += 2) {
        int rp = r + dr;
        if (rp >= 0 && rp < N) {
          int i = r * N + c, j = rp * N + c;
          Board neighbor = new Bored(this, i, j);
          neighbors.push(neighbor);
        }
      }

      for (int dc = -1; dc <= +1; dc += 2) {
        int cp = c + dc;
        if (cp >= 0 && cp < N) {
          int i = r * N + c, j = r * N + cp;
          Board neighbor = new Bored(this, i, j);
          neighbors.push(neighbor);
        }
      }
      return neighbors;
    }
  }

  /**
   * Node class to put on priority queue.
   */
  private class Node implements Comparable<Node> {
    private final Bored board;
    private final Node parent;
    private final int g, h;
    private final boolean twin;

    /**
     * Construct node from initial board.
     *
     * @param board Board to add to node.
     */
    private Node(final Board board) {
      this(board, false);
    }

    /**
     * Construct node from initial board.
     *
     * @param board Board to add to node.
     * @param twin  True if board is a twin. False otherwise.
     */
    private Node(final Board board, boolean twin) {
      if (board instanceof Bored) {
        this.board = (Bored) board;
      } else {
        this.board = new Bored(board);
      }
      this.parent = null;
      this.twin = twin;
      g = 0;
      h = this.board.heuristic();
    }

    /**
     * Construct Node from board and its parent node.
     *
     * @param board  Board to add to node.
     * @param parent Parent node of board.
     */
    private Node(final Board board, final Node parent) {
      assert parent != null : "Parent cannot be null in this constructor!";
      if (board instanceof Bored) {
        this.board = (Bored) board;
      } else {
        this.board = new Bored(board);
      }
      this.parent = parent;
      twin = parent.twin;
      g = parent.g + 1;
      h = this.board.heuristic();
    }

    /**
     * Compute priority of node.
     *
     * @return priority of node.
     */
    private int priority() {
      return g + h;
    }

    /**
     * Define how to compare nodes.
     *
     * @param that Node to compare to.
     * @return -1 for less than, 0 for equal, and +1 for greater than.
     */
    @Override
    public int compareTo(final Node that) {
      int f1 = this.priority(), f2 = that.priority();
      if (f1 < f2) { return -1; }
      if (f1 > f2) { return +1; }
      if (this.h < that.h) { return -1; }
      if (this.h > that.h) { return +1; }
      return 0;
    }
  }

  /**
   * Find a solution to the initial board (using the A* algorithm).
   *
   * @param initial Initial board to start solving.
   */
  public Solver(final Board initial) {
    if (initial == null) { throw new NullPointerException("Cannot have a null initial board!"); }
    solution = new Stack<>();
    infeasible = !initial.isSolvable();
    if (infeasible) { return; }

    boolean solved = false;

    MinPQ<Node> open = new MinPQ<>();

    Bored start;
    if (initial instanceof Bored) {
      start = (Bored) initial;
    } else {
      start = new Bored(initial);
    }

    Node node = new Node(start);
    open.insert(node);
    //open.insert(new Node(start.twin(), true));

    while (!open.isEmpty()) {
      node = open.delMin();

      boolean isGoal = node.board.isGoal();
      solved = isGoal && !node.twin;
      infeasible = isGoal && node.twin;
      if (solved || infeasible) { break; }

      for (Board neighbor : node.board.neighbors()) {
        if (node.parent == null || !neighbor.equals(node.parent.board)) {
          open.insert(new Node(neighbor, node));
        }
      }
    }

    assert solved ^ infeasible : "Puzzle must either be solved or infeasible, not both or neither!";

    if (solved) {
      solution.push(node.board);
      while (node.parent != null) {
        node = node.parent;
        solution.push(node.board);
      }
    }
  }

  /**
   * Is the initial board solvable?
   *
   * @return True if solvable.  False otherwise.
   */
  public boolean isSolvable() {
    return !infeasible;
  }

  /**
   * Min number of moves to solve initial board; -1 if unsolvable
   *
   * @return Minimum number of moves or -1 if unsolvable.
   */
  public int moves() {
    if (infeasible) { return -1; }
    return solution.size() - 1;
  }

  /**
   * Sequence of boards in a shortest solution; null if unsolvable.
   *
   * @return Iterable of boards in sequence.
   */
  public Iterable<Board> solution() {
    if (infeasible) { return null; }
    return solution;
  }

  /**
   * Create initial board from file.
   *
   * @param filename Filename to load board from.
   * @return Initial board to solve.
   */
  private static Board loadBoard(final String filename) {
    In in = new In(filename);
    int N = in.readInt();
    int[][] blocks = new int[N][N];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        blocks[i][j] = in.readInt();
      }
    }
    return new Bored(blocks);
  }

  /**
   * Solve a slider puzzle.
   *
   * @param args Input arguments.
   */
  public static void main(String[] args) {
    Board initial = loadBoard(args[0]);

    // solve the puzzle
    Solver solver = new Solver(initial);

    // print solution to standard output
    if (!solver.isSolvable()) { StdOut.println("No solution possible"); } else {
      StdOut.println("Minimum number of moves = " + solver.moves());
      for (Board board : solver.solution()) { StdOut.println(board); }
    }
  }
}
