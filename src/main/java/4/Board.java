/**
 * 8-tile puzzle board (works for larger grids)
 *
 * @author Kevin Crosby
 */
public class Board {
  protected final int N;
  protected final int N2;
  protected final char[] grid;
  protected final int blank;

  /**
   * Construct a board from an N-by-N array of blocks
   * (where blocks[i][j] = block in row i, column j).
   *
   * @param blocks Array of blocks.
   */
  public Board(final int[][] blocks) {
    N = blocks.length;
    N2 = N * N;
    int blnk = 0;

    grid = new char[N2];
    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N; c++) {
        int i = r * N + c;
        grid[i] = (char) blocks[r][c];
        if (blocks[r][c] == 0) { blnk = i; }
      }
    }
    blank = blnk;
  }

  /**
   * Construct a board from another board.
   *
   * @param board Board to copy.
   */
  protected Board(final Board board) {
    N = board.N;
    N2 = board.N2;
    blank = board.blank;

    grid = new char[N2];
    System.arraycopy(board.grid, 0, grid, 0, N2);
  }

  /**
   * Construct a board from another board, with exchanged adjacent blocks.
   *
   * @param board Board to copy.
   * @param i     First block to swap.
   * @param j     Second block to swap.
   */
  protected Board(final Board board, final int i, final int j) {
    N = board.N;
    N2 = board.N2;
    int dr = i / N - j / N, dc = i % N - j % N;
    assert Math.abs(dr) != 1 || Math.abs(dc) != 1 : "Board constructor blocks are not adjacent!";

    char[] grd = new char[N2];
    System.arraycopy(board.grid, 0, grd, 0, N2);
    exch(grd, i, j);
    grid = grd;

    if (i == board.blank) {
      blank = board.blank + j - i;
    } else if (j == board.blank) {
      blank = board.blank + i - j;
    } else {
      blank = board.blank;
    }
    assert this.grid[blank] == 0 : "Blank is in the wrong place!";
  }

  /**
   * Board dimension.
   *
   * @return Size of board.
   */
  public int dimension() {
    return N;
  }

  /**
   * Number of blocks out of place.
   *
   * @return Hamming distance to goal.
   */
  public int hamming() {
    int number = 0;
    for (int i = 0; i < N2; i++) {
      if (i == blank) { continue; }
      if (grid[i] != i + 1) { number++; }
    }
    return number;
  }

  /**
   * Sum of Manhattan distances between blocks and goal.
   *
   * @return Manhattan distance to goal.
   */
  public int manhattan() {
    int sum = 0;
    for (int i = 0; i < N2; i++) {
      if (i == blank) { continue; }
      sum += manhattan(i);
    }
    return sum;
  }

  /**
   * Manhattan distance between a block and it's desired position.
   *
   * @param i Block to compute distance.
   * @return Manhattan distance for tile to final position in goal.
   */
  protected int manhattan(final int i) {
    int distance = 0;
    if (i != blank && grid[i] != i + 1) {
      int v = grid[i];
      int r = i / N, c = i % N;
      int dr = Math.abs((v - 1) / N - r), dc = Math.abs((v - 1) % N - c);
      distance = dr + dc;
    }
    return distance;
  }

  /**
   * Compute linear conflicts in board.
   *
   * @return Twice the number of inversions per row and column.
   */
  protected int linearConflicts() {
    int count = 0;
    for (int r = 0; r < N; r++) {
      for (int c1 = 0; c1 < N - 1; c1++) {
        int i = r * N + c1;
        if (i != blank && (grid[i] - 1) / N == r) {
          for (int c2 = c1 + 1; c2 < N; c2++) {
            int j = r * N + c2;
            if (j != blank && (grid[j] - 1) / N == r && grid[i] > grid[j]) {
              count += 2;
            }
          }
        }
      }
    }
    for (int c = 0; c < N; c++) {
      for (int r1 = 0; r1 < N - 1; r1++) {
        int i = r1 * N + c;
        if (i != blank && (grid[i] - 1) % N == c) {
          for (int r2 = r1 + 1; r2 < N; r2++) {
            int j = r2 * N + c;
            if (j != blank && (grid[j] - 1) % N == c && grid[i] > grid[j]) {
              count += 2;
            }
          }
        }
      }
    }
    return count;
  }

  /**
   * Compute linear conflicts between two blocks (commutative).
   *
   * @param i First block to find linear conflicts.
   * @param j Second block to find linear conflicts.
   * @return Twice the inversion between blocks.
   */
  protected int linearConflicts(final int i, final int j) {
    int count = 0, r = i / N, c = i % N, rp = j / N, cp = j % N, dr = rp - r, dc = cp - c;
    assert Math.abs(dr) != 1 || Math.abs(dc) != 1 : "Linear conflict blocks are not adjacent!";
    if (i != blank && j != blank) { // if blank is not swapped
      if (dr == 0 && (grid[i] - 1) / N == r && (grid[j] - 1) / N == r) {  // same row
        if ((c < cp && grid[i] > grid[j]) || (c > cp && grid[i] < grid[j])) {
          count += 2;
        }
      } else if (dc == 0 && (grid[i] - 1) % N == c && (grid[j] - 1) % N == c) {  // same column
        if ((r < rp && grid[i] > grid[j]) || (r > rp && grid[i] < grid[j])) {
          count += 2;
        }
      }
    }
    if (dr == 0) {  // check for changes in columns c and cp
      for (int cc = Math.min(c, cp); cc <= Math.max(c, cp); cc += Math.abs(dc)) {
        int ic = r * N + cc;
        if (ic != blank && (grid[ic] - 1) % N == cc) {
          for (int rr = 0; rr < N; rr++) {
            if (rr == r) { continue; }
            int jc = rr * N + cc;
            if (jc != blank && (grid[jc] - 1) % N == cc) {
              if ((r < rr && grid[ic] > grid[jc]) || (r > rr && grid[ic] < grid[jc])) {
                count += 2;
              }
            }
          }
        }
      }
    } else if (dc == 0) {  // check for changes in rows r and rp
      for (int rr = Math.min(r, rp); rr <= Math.max(r, rp); rr += Math.abs(dr)) {
        int ir = rr * N + c;
        if (ir != blank && (grid[ir] - 1) / N == rr) {
          for (int cc = 0; cc < N; cc++) {
            if (cc == c) { continue; }
            int jr = rr * N + cc;
            if (jr != blank && (grid[jr] - 1) / N == rr) {
              if ((c < cc && grid[ir] > grid[jr]) || (c > cc && grid[ir] < grid[jr])) {
                count += 2;
              }
            }
          }
        }
      }
    }
    return count;
  }

  /**
   * Is this board solvable?
   *
   * @return True if board is solvable. False otherwise.
   */
  protected boolean isSolvable() {
    int inversions = 0;
    for (int i = 0; i < N2 - 1; i++) {
      if (i == blank) { continue; }
      for (int j = i + 1; j < N2; j++) {
        if (j == blank) { continue; }
        if (grid[i] > grid[j]) {
          inversions++;
        }
      }
    }
    if (N % 2 == 1) { // if board size is odd
      return inversions % 2 == 0;
    } else {  // if board size is even
      return ((blank / N) % 2 == 0) == (inversions % 2 == 1);
    }
  }


  /**
   * Is this board the goal board?
   *
   * @return True if board is goal. False otherwise.
   */
  public boolean isGoal() {
    if (blank != N2 - 1) { return false; }
    for (int i = 0; i < N2; i++) {
      if (i != blank && grid[i] != i + 1) {
        return false;
      }
    }
    return true;
  }

  /**
   * A board that is obtained by exchanging two adjacent blocks in the same row.
   *
   * @return Board with two adjacent blocks transposed.
   */
  public Board twin() {
    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N - 1; c++) {
        int i = r * N + c;
        if (i == blank || i + 1 == blank) { continue; }
        return new Board(this, i, i + 1);
      }
    }
    return null;
  }

  /**
   * Does this board equal y?
   *
   * @param y Object to compare.
   * @return True if object equals board.  False otherwise.
   */
  public boolean equals(final Object y) {
    if (y == this) { return true; }
    if (y == null) { return false; }
    if (y.getClass() != getClass()) { return false; }
    Board that = (Board) y;
    if (blank != that.blank) { return false; }
    for (int i = 0; i < N2; i++) {
      if (grid[i] != that.grid[i]) { return false; }
    }
    return true;
  }

  /**
   * Exchange blocks in grid.
   * @param a Grid.
   * @param i First block to exchange.
   * @param j Second block to exchange.
   */
  protected void exch(char[] a, final int i, final int j) {
    char v = a[i];
    a[i] = a[j];
    a[j] = v;
  }

  /**
   * Compute all neighboring boards.
   *
   * @return All neighboring boards.
   */
  public Iterable<Board> neighbors() {
    Stack<Board> neighbors = new Stack<>();

    int r = blank / N, c = blank % N;
    Board neighbor;

    for (int dr = -1; dr <= +1; dr += 2) {
      int rp = r + dr;
      if (rp >= 0 && rp < N) {
        int i = r * N + c, j = rp * N + c;
        neighbor = new Board(this, i, j);
        neighbors.push(neighbor);
      }
    }

    for (int dc = -1; dc <= +1; dc += 2) {
      int cp = c + dc;
      if (cp >= 0 && cp < N) {
        int i = r * N + c, j = r * N + cp;
        neighbor = new Board(this, i, j);
        neighbors.push(neighbor);
      }
    }
    return neighbors;
  }

  /**
   * String representation of this board.
   *
   * @return String representing board.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(N).append("\n");
    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N; c++) {
        int i = N * r + c;
        sb.append(String.format("%2d ", (int) grid[i]));
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Unit tests (not graded).
   *
   * @param args Input arguments.
   */
  public static void main(String[] args) {
    int[][] initial = {{12, 1, 10, 2}, {7, 0, 4, 14}, {5, 11, 9, 15}, {8, 13, 6, 3}};

    Board board = new Board(initial);
    StdOut.println(board);

    StdOut.println("Is solvable?  " + board.isSolvable());

    for (Board neighbor : board.neighbors()) {
      StdOut.println(neighbor);
    }
  }
}
