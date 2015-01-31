import static java.lang.Math.sqrt;

/**
 * Monte Carlo Simulation for Percolation.
 *
 * @author Kevin Crosby
 */
public class PercolationStats {
  private final int N, T;
  private final double[] a;

  private final int[] sites; // to keep track of opened and blocked sites
  private final int[] indices; // to keep track of indices of opened and blocked sites
  private int numberOpened; // to keep track of opened and blocked sites

  /**
   * Perform T independent experiments on an N-by-N grid.
   *
   * @param n Length and width of square grid.
   * @param t Number of experiments to run.
   */
  public PercolationStats(final int n, final int t) {
    if (n <= 0) { throw new IllegalArgumentException("Need N > 0!"); }
    if (t <= 0) { throw new IllegalArgumentException("Need T > 0!"); }

    N = n;
    T = t;
    a = new double[T];

    sites = new int[N * N];
    indices = new int[N * N];

    init();
  }

  /**
   * Initialize fields.
   */
  private void init() {
    numberOpened = 0;

    for (int p = 0; p < N * N; p++) { // sites
      sites[p] = p;
      indices[p] = p;
    }
  }

  /**
   * Sample mean of percolation threshold.
   *
   * @return Mean of a.
   */
  public double mean() {
    return StdStats.mean(a);
  }

  /**
   * Sample standard deviation of percolation threshold.
   *
   * @return Sample standard deviation of a.
   */
  public double stddev() {
    return T > 1 ? StdStats.stddev(a) : Double.NaN;
  }

  /**
   * Low endpoint of 95% confidence interval.
   *
   * @return Low endpoint of 95% confidence interval.
   */
  public double confidenceLo() {
    return mean() - 1.96 * stddev() / sqrt(T);
  }

  /**
   * High endpoint of 95% confidence interval.
   *
   * @return High endpoint of 95% confidence interval.
   */
  public double confidenceHi() {
    return mean() + 1.96 * stddev() / sqrt(T);
  }

  /**
   * Print statistics.
   *
   */
  private void printStats() {
    System.out.println(
        String.format("mean                    = %f\nstddev                  = %f\n95%% confidence interval = %f, %f",
            mean(), stddev(), confidenceLo(), confidenceHi())
    );
  }

  /**
   * Rearrange site and index from blocked to open.
   *
   * @param site1 Site to rearrange with site at index.
   * @param index2 Index to rearrange with index at site.
   */
  private void rearrange(final int site1, final int index2) {
    int index1 = indices[site1];
    int site2 = sites[index2];

    int site = sites[index1];
    sites[index1] = sites[index2];
    sites[index2] = site;

    int index = indices[site1];
    indices[site1] = indices[site2];
    indices[site2] = index;
  }

  /**
   * Open random blocked site.
   */
  private void openRandom(final Percolation percolation) {
    int index = StdRandom.uniform(numberOpened, N * N);
    int p = sites[index]; // site
    assert index == indices[p] : "Sites and indices are out of sync";
    int i = p / N + 1; // row of grid (1 ≤ i ≤ N).
    int j = p % N + 1; // column of grid (1 ≤ j ≤ N).
    percolation.open(i, j);
    rearrange(p, numberOpened++);
  }

  /**
   * Add fraction opened to statistics.
   */
  private void addStatistic(final int t) {
    a[t] = (double)numberOpened / (double)(N * N);
  }

  /**
   * Test client.
   *
   * @param args Size of square grid and number of tests.
   */
  public static void main(String[] args) {
    PercolationStats ps;
    switch (args.length) {
      case 2:
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        ps = new PercolationStats(n, t);
        break;
      default:
        System.err.println("Usage:\n  PercolationStats [N] [T]\n  (N = length and width of grid, T = number of tests)");
        return;
    }

    final int N = ps.N, T = ps.T;

    for (int t = 0; t < T; t++) {
      ps.init();
      Percolation percolation = new Percolation(N);
      ps.openRandom(percolation); // to handle case of N = 1
      while (!percolation.percolates()) {
        ps.openRandom(percolation);
      }
      ps.addStatistic(t);
    }

    ps.printStats();
  }
}