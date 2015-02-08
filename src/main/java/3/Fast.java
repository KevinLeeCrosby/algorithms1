import java.util.Arrays;

/**
 * Given a point p, the following method determines whether p participates in a set of 4 or more collinear points.
 *
 * - Think of p as the origin.
 * - For each other point q, determine the slope it makes with p.
 * - Sort the points according to the slopes they makes with p.
 * - Check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to p. If so, these
 * points, together with p, are collinear.
 *
 * Applying this method for each of the N points in turn yields an efficient algorithm to the problem. The algorithm
 * solves the problem because points that have equal slopes with respect to p are collinear, and sorting brings such
 * points together. The algorithm is fast because the bottleneck operation is sorting.
 *
 * @author Kevin Crosby
 */
public class Fast {
  private static Point[] readPoints(String filename) {
    In in = new In(filename);
    int n = in.readInt();
    Point[] points = new Point[n];

    StdDraw.setXscale(0, 32768); // rescale coordinates
    StdDraw.setYscale(0, 32768);
    StdDraw.show(0); // turn on animation mode

    for (int i = 0; i < n; i++) {
      int x = in.readInt();
      int y = in.readInt();
      Point point = new Point(x, y);
      points[i] = point;
      point.draw();
    }

    StdDraw.show(0); // display to screen all at once

    return points;
  }

  public static void main(String[] args) {
    Point[] points = readPoints(args[0]);
    Arrays.sort(points);

    int n = points.length;
    Point[] slopePoints = Arrays.copyOf(points, n);

    for (int p = 0; p < n; p++) {
      Arrays.sort(slopePoints, points[p].SLOPE_ORDER);
      int count = 0, eq = 1;
      double prevSlope = Double.NEGATIVE_INFINITY;
      boolean show, reset = false;
      for (int q = 1; q < n; q++) {
        double newSlope = points[p].slopeTo(slopePoints[q]);
        if (newSlope == prevSlope) {
          show = ++count >= 3 && q == n - 1;
        } else {
          show = count >= 3;
          reset = true;
        }
        if (show) {
          int end = eq + count;
          Arrays.sort(slopePoints, eq, end);
          if (points[p].compareTo(slopePoints[eq]) < 1) { // avoid horizontal line permutations
            System.out.print(points[p]);
            for (int i = eq; i < end; i++) System.out.print(" -> " + slopePoints[i]);
            System.out.println();
            points[p].drawTo(slopePoints[end - 1]);
            StdDraw.show(0); // display to screen all at once
          }
          reset = true;
        }
        if (reset) {
          count = 1;
          eq = q;
          reset = false;
        }
        prevSlope = newSlope;
      }
    }
  }
}
