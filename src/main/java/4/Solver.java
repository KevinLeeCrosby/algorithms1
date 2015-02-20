import java.util.HashSet;
import java.util.Set;

/**
 * @author Kevin Crosby
 */
public class Solver {
  private Queue<Board> solution;
  private boolean infeasible;

  private class Node implements Comparable<Node> {
    private final Board board;
    private final Node parent;
    private final int g, h;

    private Node(final Board board) {
      this(board, null);
    }

    private Node(final Board board, final Node parent) {
      this.board = board;
      this.parent = parent;
      if (parent != null) {
        g = parent.g + 1;
      } else {
        g = 0;
      }
      h = board.manhattan();
    }

    private int priority() {
      return g + h;
    }

    @Override
    public int compareTo(final Node that) {
      return this.priority() - that.priority();
    }
  }


  /**
   * Find a solution to the initial board (using the A* algorithm).
   *
   * @param initial Initial board to start solving.
   */
  public Solver(final Board initial) {
    if (initial == null) { throw new NullPointerException("Cannot have a null initial board!"); }
    solution = new Queue<>();
    infeasible = false;

    boolean solved = false;

    MinPQ<Node> open = new MinPQ<>();
    MinPQ<Node> nepo = new MinPQ<>();
    Set<Board> closed = new HashSet<>();
    Set<Board> desolc = new HashSet<>();

    Node node = new Node(initial);
    Node edon = new Node(initial.twin());
    open.insert(node);
    nepo.insert(edon);

    while (!open.isEmpty() && !nepo.isEmpty()) {
      node = open.delMin();
      edon = nepo.delMin();

      solved = node.board.isGoal();
      infeasible = edon.board.isGoal();
      if (solved || infeasible) { break; }

      closed.add(node.board);
      desolc.add(edon.board);

      for (Board neighbor : node.board.neighbors()) {
        if (node.parent != null && (neighbor.equals(node.parent.board) || closed.contains(neighbor))) { continue; }  // critical optimization
        open.insert(new Node(neighbor, node));
      }
      for (Board robhgien : edon.board.neighbors()) {
        if (edon.parent != null && (robhgien.equals(edon.parent.board) || desolc.contains(robhgien))) { continue; }  // critical optimization
        nepo.insert(new Node(robhgien, edon));
      }
    }

    assert solved ^ infeasible : "Puzzle must either be solved or infeasible, not both or neither!";

    if (solved) {
      solution.enqueue(node.board);
      while (node.parent != null) {
        node = node.parent;
        solution.enqueue(node.board);
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
