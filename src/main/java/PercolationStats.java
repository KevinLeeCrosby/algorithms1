/**
 * Monte Carlo Simulation for Percolation.
 *
 * @author Kevin Crosby
 */
public class PercolationStats {
  private final int N, T;
  private final double[] a;

  /**
   * Perform T independent experiments on an N-by-N grid.
   *
   * @param n Length and width of square grid.
   * @param τ Number of experiments to run.
   */
  public PercolationStats(final int n, final int τ) {
    if (n <= 0) {
      throw new IllegalArgumentException("Need N > 0!");
    }
    if (τ <= 0) {
      throw new IllegalArgumentException("Need T > 0!");
    }

    N = n;
    T = τ;
    a = new double[T];

    int N2 = N * N;

    int[] sites = new int[N2]; // to keep track of opened and blocked sites
    for (int p = 0; p < N2; p++) {
      sites[p] = p;
    }

    for (int t = 0; t < T; t++) {
      StdRandom.shuffle(sites);
      int numberOpened = 0;
      Percolation percolation = new Percolation(N);
      do {
        int p = sites[numberOpened++];
        int i = p / N + 1; // row of grid (1 ≤ i ≤ N).
        int j = p % N + 1; // column of grid (1 ≤ j ≤ N).
        percolation.open(i, j);
      } while (!percolation.percolates());
      a[t] = (double) numberOpened / (double) N2;
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
    return mean() - 1.96 * stddev() / Math.sqrt(T);
  }

  /**
   * High endpoint of 95% confidence interval.
   *
   * @return High endpoint of 95% confidence interval.
   */
  public double confidenceHi() {
    return mean() + 1.96 * stddev() / Math.sqrt(T);
  }

  /**
   * Print statistics.
   */
  private void printStats() {
    System.out.println(
        String.format("mean                    = %f\nstddev                  = %f\n95%% confidence interval = %f, %f",
            mean(), stddev(), confidenceLo(), confidenceHi())
    );
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

    ps.printStats();
  }
}