/**
 * @author Kevin Crosby
 */
public class Solver {
  private final Stack<Board> solution;
  private boolean infeasible;

  private class Node implements Comparable<Node> {
    private final Board board;
    private final Node parent;
    private final int g, h;
    private final boolean twin;

    private Node(final Board board) {
      this(board, false);
    }

    private Node(final Board board, boolean twin) {
      this.board = board;
      this.parent = null;
      this.twin = twin;
      g = 0;
      h = heuristic();
    }

    private Node(final Board board, final Node parent) {
      assert parent != null : "Parent cannot be null in this constructor!";
      this.board = board;
      this.parent = parent;
      twin = parent.twin;
      g = parent.g + 1;
      h = heuristic();
    }

    private int heuristic() {
      return board.manhattan();
    }

    private int priority() {
      return g + h;
    }

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
    //heuristics = new HashMap<>();
    solution = new Stack<>();
    infeasible = false;

    int enqueued = 0, dequeued = 0, maxPQSize = 0;

    boolean solved = false;

    MinPQ<Node> open = new MinPQ<>();

    Node node = new Node(initial);
    open.insert(node);
    open.insert(new Node(initial.twin(), true));
    enqueued += 2;

    while (!open.isEmpty()) {
      node = open.delMin();
      dequeued++;

      boolean isGoal = node.board.isGoal();
      solved = isGoal && !node.twin;
      infeasible = isGoal && node.twin;
      if (solved || infeasible) { break; }

      for (Board neighbor : node.board.neighbors()) {
        if (node.parent != null && neighbor.equals(node.parent.board)) {
          continue;
        }  // critical optimization
        open.insert(new Node(neighbor, node));
        enqueued++;
      }

      maxPQSize = Math.max(maxPQSize, open.size());
    }

    assert solved ^ infeasible : "Puzzle must either be solved or infeasible, not both or neither!";

    if (solved) {
      solution.push(node.board);
      while (node.parent != null) {
        node = node.parent;
        solution.push(node.board);
      }
      StdOut.println("Enqueues: " + enqueued + "\tDequeues: " + dequeued + "\tMaxPQ size: " + maxPQSize);
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
    for (int i = 0; i < N; i++) { for (int j = 0; j < N; j++) { blocks[i][j] = in.readInt(); } }
    return new Board(blocks);
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
