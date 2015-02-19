/**
 * @author Kevin Crosby
 */
public class Board {
  private final int N;
  private final int[][] grid;

  /**
   * Construct a board from an N-by-N array of blocks
   * (where blocks[i][j] = block in row i, column j).
   *
   * @param blocks Array of blocks.
   */
  public Board(int[][] blocks) {
    N = blocks.length;

    grid = new int[N][N];
    for (int r = 0; r < N; r++) {
      System.arraycopy(blocks[r], 0, grid[r], 0, N);
    }
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
    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N; c++) {
        if (grid[r][c] == 0) continue;
        if (grid[r][c] != N * r + c + 1) number++;
      }
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
    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N; c++) {
        if (grid[r][c] == 0) continue;
        if (grid[r][c] != N * r + c + 1) {
          int v = grid[r][c];
          int dr = Math.abs((v - 1) / N - r);
          int dc = Math.abs((v - 1) % N - c);
          sum += dr + dc;
        }
      }
    }
    return sum;
  }

  /**
   * Is this board the goal board?
   *
   * @return True if board is goal. False otherwise.
   */
  public boolean isGoal() {
    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N; c++) {
        if (grid[r][c] == 0) {
          if (r != N - 1 || c != N - 1) return false;
        } else if (grid[r][c] != N * r + c + 1) {
          return false;
        }
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
    Board twin = new Board(grid);
    boolean loop = true;
    for (int r = 0; loop; r++) {
      for (int c = 0; c < N - 1; c++) {
        if (twin.grid[r][c] == 0 || twin.grid[r][c + 1] == 0) continue;
        exch(twin.grid, r, r, c, c + 1);
        loop = false;
      }
    }
    return twin;
  }

  /**
   * Does this board equal y?
   *
   * @param y Object to compare.
   * @return True if object equals board.  False otherwise.
   */
  public boolean equals(Object y) {
    if (y == this) return true;
    if (y == null) return false;
    if (y.getClass() != getClass()) return false;
    Board that = (Board) y;
    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N; c++) {
        if (grid[r][c] != that.grid[r][c]) return false;
      }
    }
    return true;
  }

  private void exch(int[][] a, int r, int rp, int c, int cp) {
    int v = a[r][c];
    a[r][c] = a[rp][cp];
    a[rp][cp] = v;
  }

  public Iterable<Board> neighbors() {    // all neighboring boards
    Queue<Board> neighbors = new Queue<>();

    // find blank
    int r, c = 0;
    for (r = 0; r < N; r++) {
      for (c = 0; c < N; c++) {
        if (grid[r][c] == 0) break;
      }
    }

    for (int dr = -1; dr <= +1; dr += 2) {
      int rp = r + dr;
      if (rp >= 0 && rp < N) {
        exch(grid, r, rp, c, c);
        Board neighbor = new Board(grid);
        neighbors.enqueue(neighbor);
        exch(grid, r, rp, c, c);
      }
    }

    for (int dc = -1; dc <= +1; dc += 2) {
      int cp = c + dc;
      if (cp >= 0 && cp < N) {
        exch(grid, r, r, c, cp);
        Board neighbor = new Board(grid);
        neighbors.enqueue(neighbor);
        exch(grid, r, r, c, cp);
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
        sb.append(String.format("%2d ", grid[r][c]));
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

  }
}
