/**
 * @author Kevin Crosby
 */
public class Solver {
  private final MinPQ<Board> pq = new MinPQ<>();

  /**
   * Find a solution to the initial board (using the A* algorithm).
   *
   * @param initial Initial board to start solving.
   */
  public Solver(Board initial) {
    if (initial == null) throw new NullPointerException("Cannot have a null initial board!");


  }

  /**
   * Is the initial board solvable?
   *
   * @return True if solvable.  False otherwise.
   */
  public boolean isSolvable() {
    return false;
  }

  /**
   * Min number of moves to solve initial board; -1 if unsolvable
   *
   * @return Minimum number of moves or -1 if unsolvable.
   */
  public int moves() {
    return -1;
  }

  /**
   * Sequence of boards in a shortest solution; null if unsolvable.
   *
   * @return Iterable of boards in sequence.
   */
  public Iterable<Board> solution() {
    return null;
  }

  /**
   * Solve a slider puzzle.
   *
   * @param args Input arguments.
   */
  public static void main(String[] args) {
    // create initial board from file
    In in = new In(args[0]);
    int N = in.readInt();
    int[][] blocks = new int[N][N];
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++)
        blocks[i][j] = in.readInt();
    Board initial = new Board(blocks);

    // solve the puzzle
    Solver solver = new Solver(initial);

    // print solution to standard output
    if (!solver.isSolvable())
      StdOut.println("No solution possible");
    else {
      StdOut.println("Minimum number of moves = " + solver.moves());
      for (Board board : solver.solution())
        StdOut.println(board);
    }
  }
}
