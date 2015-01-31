/**
 * Given a composite systems comprised of randomly distributed insulating and metallic materials: what fraction of the
 * materials need to be metallic so that the composite system is an electrical conductor? Given a porous landscape with
 * water on the surface (or oil below), under what conditions will the water be able to drain through to the bottom (or
 * the oil to gush through to the surface)? Scientists have defined an abstract process known as percolation to model
 * such situations.
 *
 * @author Kevin Crosby
 */
public class Percolation {
  private final int N; // length and width of square grid
  private final WeightedQuickUnionUF WEIGHTED_QUICK_UNION, WEIGHTED_QUICK_UNION_BACKWASH;

  private final boolean grid[][]; // open is true, full is false;

  private final int TOP_VIRTUAL, BOTTOM_VIRTUAL; // virtual nodes to facilitate percolation determination

  /**
   * Create N-by-N grid, with all sites blocked.
   *
   * @param n Length and width of square grid.
   */
  public Percolation(final int n) {
    if (n <= 0) {
      throw new IllegalArgumentException("Need N > 0!");
    }

    N = n;
    WEIGHTED_QUICK_UNION = new WeightedQuickUnionUF(N * N + 1);
    WEIGHTED_QUICK_UNION_BACKWASH = new WeightedQuickUnionUF(N * N + 2); // with backwash

    grid = new boolean[N][N];

    TOP_VIRTUAL = N * N; // top virtual node
    BOTTOM_VIRTUAL = TOP_VIRTUAL + 1; // bottom virtual node

    for (int r = 0; r < N; r++) { // rows
      for (int c = 0; c < N; c++) { // columns
        grid[r][c] = false; // Initialize all sites to be blocked.
      }
    }
  }

  /**
   * Open site (row i, column j) if it is not open already
   *
   * @param i Row of grid (1 ≤ i ≤ N).
   * @param j Column of grid (1 ≤ j ≤ N).
   */
  public void open(final int i, final int j) {
    if (i < 1 || i > N || j < 1 || j > N) {
      throw new IndexOutOfBoundsException("Need 1 ≤ i,j ≤ " + N);
    }

    final int r = i - 1, c = j - 1;
    if (grid[r][c]) {
      return;
    }

    grid[r][c] = true;
    int p = r * N + c;

    for (int dr = -1; dr <= +1; dr += 2) { // rows
      int rp = r + dr;
      if (rp >= 0 && rp < N && grid[rp][c]) {
        int q = rp * N + c;
        union(p, q);
      }
    }

    for (int dc = -1; dc <= +1; dc += 2) { // columns
      int cp = c + dc;
      if (cp >= 0 && cp < N && grid[r][cp]) {
        int q = r * N + cp;
        union(p, q);
      }
    }

    // connect top virtual node, if necessary
    if (r == 0) {
      WEIGHTED_QUICK_UNION.union(p, TOP_VIRTUAL);
      WEIGHTED_QUICK_UNION_BACKWASH.union(p, TOP_VIRTUAL);
    }

    // connect bottom virtual node, if necessary
    if (r == N - 1) {
      WEIGHTED_QUICK_UNION_BACKWASH.union(p, BOTTOM_VIRTUAL);
    }
  }

  /**
   * Is site (row i, column j) open?
   *
   * @param i Row of grid (1 ≤ i ≤ N).
   * @param j Column of grid (1 ≤ j ≤ N).
   * @return True if site is open.  False otherwise.
   */
  public boolean isOpen(final int i, final int j) {
    if (i < 1 || i > N || j < 1 || j > N) {
      throw new IndexOutOfBoundsException("Need 1 ≤ i,j ≤ " + N);
    }

    return grid[i - 1][j - 1];
  }

  /**
   * Is site (row i, column j) full?
   *
   * @param i Row of grid (1 ≤ i ≤ N).
   * @param j Column of grid (1 ≤ j ≤ N).
   * @return True if site is full or blocked.  False otherwise.
   */
  public boolean isFull(final int i, final int j) {
    if (i < 1 || i > N || j < 1 || j > N) {
      throw new IndexOutOfBoundsException("Need 1 ≤ i,j ≤ " + N);
    }

    final int r = i - 1, c = j - 1, p = r * N + c;

    return connected(TOP_VIRTUAL, p);
  }

  /**
   * Does the system percolate?
   *
   * @return True if site percolates.  False otherwise.
   */
  public boolean percolates() {
    return WEIGHTED_QUICK_UNION_BACKWASH.connected(TOP_VIRTUAL, BOTTOM_VIRTUAL);
  }

  /**
   * Weighted quick union.
   *
   * @param p First site to take union of.
   * @param q Second site to take union of.
   */
  private void union(final int p, final int q) {
    WEIGHTED_QUICK_UNION.union(p, q);
    WEIGHTED_QUICK_UNION_BACKWASH.union(p, q);
  }

  /**
   * Check if sites p and q are connected.
   *
   * @param p First site to check connection of.
   * @param q Second site to check connection of.
   * @return True if nodes are connected.  False otherwise.
   */
  private boolean connected(final int p, final int q) {
    return WEIGHTED_QUICK_UNION.connected(p, q);
  }

  /**
   * The component identifier for the component containing site.
   *
   * @param p Site to determine component identifier from.
   * @return Component identifier.
   */
  private int find(int p) {
    return WEIGHTED_QUICK_UNION.find(p);
  }

  /**
   * Count how many sites are open.
   *
   * @return Count of open sites.
   */
  private int count() {
    return WEIGHTED_QUICK_UNION.count();
  }

  /**
   * Test client.
   *
   * @param args Input arguments.
   */
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
