import java.util.HashMap;
import java.util.Map;

/**
 * 8-tile puzzle board (works for larger grids)
 *
 * @author Kevin Crosby
 */
public class Board {
  private final int N;
  private final int N2;
  private final char[] grid;
  private final int blank;
  private final static Map<char[], Integer> manhattan = new HashMap<>();

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
    for(int r = 0; r < N; r++) {
      for(int c = 0; c < N; c++) {
        int i = r * N + c;
        grid[i] = (char) blocks[r][c];
        if(blocks[r][c] == 0) { blnk = i; }
      }
    }
    blank = blnk;
  }

  /**
   * Construct a board from another board.
   *
   * @param board Board to copy.
   */
  private Board(final Board board) {
    N = board.N;
    N2 = board.N2;
    blank = board.blank;

    grid = new char[N2];
    System.arraycopy(board.grid, 0, grid, 0, N2);
  }

  /**
   * Construct a board from another board, with exchanged adjacent tiles.
   *
   * @param board Board to copy.
   */
  private Board(final Board board, final int i, final int j) {
    N = board.N;
    N2 = board.N2;
    int dr = i / N - j / N, dc = i % N - j % N;
    assert Math.abs(dr) != 1 || Math.abs(dc) != 1 : "Board constructor tiles are not adjacent!";

    char[] grd = new char[N2];
    System.arraycopy(board.grid, 0, grd, 0, N2);
    exch(grd, i, j);
    grid = grd;
    if(i == board.blank || j == board.blank) {
      blank = board.blank + j - i;
    } else {
      blank = board.blank;
    }
    assert this.grid[blank] == 0 : "Blank is in the wrong place!";

    int h = manhattan.get(board.grid), dh = manhattan(i) + manhattan(j) - manhattan(board, i) - manhattan(board, j);
    manhattan.put(grd, h + dh);
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
    for(int i = 0; i < N2; i++) {
      if(i == blank) { continue; }
      if(grid[i] != i + 1) { number++; }
    }
    return number;
  }

  /**
   * Sum of Manhattan distances between tiles and goal.
   *
   * @return Manhattan distance to goal.
   */
  public int manhattan() {
    return manhattan(this);
  }

  private static int manhattan(final Board board) {
    int N2 = board.N2;
    char[] grid = board.grid;
    int blank = board.blank;
    if(!manhattan.containsKey(grid)) {
      int sum = 0;
      for(int i = 0; i < N2; i++) {
        if(i == blank) { continue; }
        sum += board.manhattan(i);
      }
      manhattan.put(grid, sum);
    }
    return manhattan.get(grid);
  }

  /**
   * Manhattan distances between tiles and goal.
   *
   * @param i Tile to compute distance.
   * @return Manhattan distance for tile to final position in goal.
   */
  private int manhattan(final int i) {
    return manhattan(this, i);
  }

  private static int manhattan(final Board board, final int i) {
    if(board == null) { return 0; }
    int N = board.N;
    char[] grid = board.grid;
    int blank = board.blank;
    int distance = 0;
    if(i != blank && grid[i] != i + 1) {
      int v = grid[i];
      int r = i / N, c = i % N;
      int dr = Math.abs((v - 1) / N - r), dc = Math.abs((v - 1) % N - c);
      distance = dr + dc;
    }
    return distance;
  }

  /**
   * Is this board the goal board?
   *
   * @return True if board is goal. False otherwise.
   */
  public boolean isGoal() {
    if(blank != N2 - 1) { return false; }
    for(int i = 0; i < N2; i++) {
      if(i == blank) {
        continue; // blank already checked
      } else if(grid[i] != i + 1) {
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
    for(int r = 0; r < N; r++) {
      for(int c = 0; c < N - 1; c++) {
        int i = r * N + c;
        if(i == blank || i + 1 == blank) { continue; }
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
    if(y == this) { return true; }
    if(y == null) { return false; }
    if(y.getClass() != getClass()) { return false; }
    Board that = (Board) y;
    if(blank != that.blank) { return false; }
    for(int i = 0; i < N2; i++) {
      if(grid[i] != that.grid[i]) { return false; }
    }
    return true;
  }

  private void exch(char[] a, final int i, final int j) {
    char v = a[i];
    a[i] = a[j];
    a[j] = v;
  }

  public Iterable<Board> neighbors() {    // all neighboring boards
    Stack<Board> neighbors = new Stack<>();

    int r = blank / N, c = blank % N;

    for(int dr = -1; dr <= +1; dr += 2) {
      int rp = r + dr;
      if(rp >= 0 && rp < N) {
        int i = r * N + c, j = rp * N + c;
        Board neighbor = new Board(this, i, j);
        neighbors.push(neighbor);
      }
    }

    for(int dc = -1; dc <= +1; dc += 2) {
      int cp = c + dc;
      if(cp >= 0 && cp < N) {
        int i = r * N + c, j = r * N + cp;
        Board neighbor = new Board(this, i, j);
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
    for(int r = 0; r < N; r++) {
      for(int c = 0; c < N; c++) {
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

  }
}
