import static java.lang.Math.sqrt;

/**
 * @author Kevin Crosby
 */
public class PercolationStats {
  private final int N, T;
  private Double mu, sigma, lo, hi;

  // perform T independent experiments on an N-by-N grid
  public PercolationStats(final int n, final int t) {
    if (n <= 0) throw new IllegalArgumentException("Need N > 0!");
    if (t <= 0) throw new IllegalArgumentException("Need T > 0!");

    N = n;
    T = t;
    mu = null;
    sigma = null;
    lo = null;
    hi = null;

  }

  //  // sample mean of percolation threshold
//  public double mean(double[] a) {
//    if (mu == null) mu = StdStats.mean(a);
//    return mu;
//  }
//
//  //  // sample standard deviation of percolation threshold
//  public double stddev(double[] a) {
//    if (sigma == null) sigma = T > 1 ? StdStats.stddev(a) : Double.NaN;
//    return sigma;
//  }
//
//  //
////  // low endpoint of 95% confidence interval
//  public double confidenceLo(double[] a) {
//    if (lo == null) lo = mean(a) - 1.96 * stddev(a) / sqrt(T);
//    return lo;
//  }
//
//  //
////  // high endpoint of 95% confidence interval
//  public double confidenceHi(double[] a) {
//    if (hi == null) hi = mean(a) + 1.96 * stddev(a) / sqrt(T);
//    return hi;
//  }
//
//  // test client
//  public static void main(String[] args) {
//
//  }
}