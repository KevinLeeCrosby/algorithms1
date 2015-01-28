/**
 * @author Kevin Crosby
 */
public class Percolation {
  private final int N;
  private final WeightedQuickUnionUF W, WB;

  private final boolean grid[][]; // open is true, full is false;

  private final int topVirtual, bottomVirtual;

  // create N-by-N grid, with all sites blocked
  public Percolation(final int n) {
    if (n <= 0) throw new IllegalArgumentException("Need N > 0!");

    N = n;
    W = new WeightedQuickUnionUF(N * N);
    WB = new WeightedQuickUnionUF(N * N + 2); // with backwash

    grid = new boolean[N][N];

    topVirtual = N * N;
    bottomVirtual = topVirtual + 1;

    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N; c++) {
        grid[r][c] = false; // Initialize all sites to be blocked.
      }
    }

    // connect virtual nodes
    for (int p = topVirtual, r = 0; p <= bottomVirtual; p++, r = N - 1) {
      for (int c = 0; c < N; c++) {
        int q = r * N + c;
        WB.union(p, q);
      }
    }
  }

  // open site (row i, column j) if it is not open already
  public void open(final int i, final int j) {
    if (i < 1 || i > N || j < 1 || j > N)
      throw new IndexOutOfBoundsException("Need 1 ≤ i,j ≤ " + N);

    final int r = i - 1, c = j - 1;
    if (grid[r][c]) return;

    int p = r * N + c;
    grid[r][c] = true;
    for (int dr = -1; dr <= +1; dr += 2) {
      int rp = r + dr;
      if (rp >= 0 && rp < N && grid[rp][c]) {
        int q = rp * N + c;
        union(p, q);
      }
    }
    for (int dc = -1; dc <= +1; dc += 2) {
      int cp = c + dc;
      if (cp >= 0 && cp < N && grid[r][cp]) {
        int q = r * N + cp;
        union(p, q);
      }
    }
  }

  // is site (row i, column j) open?
  public boolean isOpen(final int i, final int j) {
    if (i < 1 || i > N || j < 1 || j > N)
      throw new IndexOutOfBoundsException("Need 1 ≤ i,j ≤ " + N);

    return grid[i - 1][j - 1];
  }

  // is site (row i, column j) full?
  public boolean isFull(final int i, final int j) {
    if (i < 1 || i > N || j < 1 || j > N)
      throw new IndexOutOfBoundsException("Need 1 ≤ i,j ≤ " + N);

    return !isOpen(i, j);
  }

  // does the system percolate?
  public boolean percolates() {
    return WB.connected(topVirtual, bottomVirtual);
  }

  // weighted quick union
  private void union(final int p, final int q) {
    W.union(p, q);
    WB.union(p, q);
  }

  // check if sites p and q are connected
  private boolean connected(final int p, final int q) {
    return W.connected(p, q);
  }

  // the component identifier for the component containing site
  public int find(int p) {
    return W.find(p);
  }

  // count how many sites are open
  public int count() {
    return W.count();
  }

  // test client (optional)
  public static void main(String[] args) {
    final int n = args.length > 0 ? Integer.parseInt(args[0]) : 3;

    Percolation g = new Percolation(n);

    int j = n / 2 + 1;
    for (int i = 1; i <= n; i++) {
      System.out.println(String.format("isOpen(%d, %d) = %s", i, j, g.isOpen(i, j)));
      System.out.println(String.format("isFull(%d, %d) = %s", i, j, g.isFull(i, j)));
      System.out.println(String.format("count() = %d", g.count()));

      g.open(i, j);
      System.out.println(String.format("open(%d, %d)", i, j));
      System.out.println(String.format("isOpen(%d, %d) = %s", i, j, g.isOpen(i, j)));
      System.out.println(String.format("isFull(%d, %d) = %s", i, j, g.isFull(i, j)));
      System.out.println(String.format("count() = %d", g.count()));

      System.out.println(String.format("percolates() = %s", g.percolates()));

      System.out.println("--------------------------------------------------------------");
    }
  }
}
