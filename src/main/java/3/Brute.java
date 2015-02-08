import java.util.Arrays;

/**
 * Brute force examination.
 *
 * Examines 4 points at a time and checks whether they all lie on the same line segment, printing out any such line
 * segments to standard output and drawing them using standard drawing. To check whether the 4 points p, q, r, and s
 * are collinear, check whether the slopes between p and q, between p and r, and between p and s are all equal.
 *
 * The order of growth of the running time of your program should be N4 in the worst case and it should use space
 * proportional to N.
 *
 * @author Kevin Crosby
 */
public class Brute {
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

    for (int p = 0; p < n - 3; p++) {
      for (int q = p + 1; q < n - 2; q++) {
        for (int r = q + 1; r < n - 1; r++) {
          if (points[p].SLOPE_ORDER.compare(points[q], points[r]) != 0) continue;
          for (int s = r + 1; s < n; s++) {
            if (points[p].SLOPE_ORDER.compare(points[r], points[s]) != 0) continue;
            System.out.println(points[p] + " -> " + points[q] + " -> " + points[r] + " -> " + points[s]);
            points[p].drawTo(points[s]);
            StdDraw.show(0); // display to screen all at once
          }
        }
      }
    }
  }
}
