/*************************************************************************
 * Name:   Kevin Crosby
 * Email:  Kevin.L.Crosby@gmail.com
 *
 * Compilation:  javac Point.java
 * Execution:
 * Dependencies: StdDraw.java
 *
 * Description: An immutable data type for points in the plane.
 *
 *************************************************************************/

import java.util.Comparator;

public class Point implements Comparable<Point> {

  /**
   * Formally, the point (x1, y1) is less than the point (x2, y2) if and only if the slope (y1 − y0) / (x1 − x0) is
   * less than the slope (y2 − y0) / (x2 − x0). Treat horizontal, vertical, and degenerate line segments as in the
   * slopeTo() method.
   *
   * NOTE:
   * In general, it is hazardous to compare a and b for equality if either is susceptible to floating-point roundoff
   * error. However, in this case, you are computing b/a, where a and b are integers between -32,767 and 32,767. In
   * Java (and the IEEE 754 floating-point standard), the result of a floating-point operation (such as division) is
   * the nearest representable value. Thus, for example, it is guaranteed that (9.0/7.0 == 45.0/35.0). In other
   * words, it's sometimes OK to compare floating-point numbers for exact equality (but only when you know exactly
   * what you are doing!)
   *
   * @param p1 First point to compare.
   * @param p2 Second point to compare.
   * @return -1, 0, or 1 for less than, equal, or greater than, respectively.
   */
  public final Comparator<Point> SLOPE_ORDER = new Comparator<Point>() {
    @Override
    public int compare(final Point p1, final Point p2) {
      if (slopeTo(p1) < slopeTo(p2)) return -1;
      if (slopeTo(p1) > slopeTo(p2)) return 1;
      return 0;
    }
  };


  private final int x;                              // x coordinate
  private final int y;                              // y coordinate

  // create the point (x, y)
  public Point(int x, int y) {
        /* DO NOT MODIFY */
    this.x = x;
    this.y = y;
  }

  // plot this point to standard drawing
  public void draw() {
        /* DO NOT MODIFY */
    StdDraw.point(x, y);
  }

  // draw line between this point and that point to standard drawing
  public void drawTo(Point that) {
        /* DO NOT MODIFY */
    StdDraw.line(this.x, this.y, that.x, that.y);
  }

  // slope between this point and that point
  public double slopeTo(Point that) {
    // given by the formula (y1 − y0) / (x1 − x0).
    int dy = that.y - y;
    int dx = that.x - x;
    if (dx == 0 && dy == 0)
      return Double.NEGATIVE_INFINITY; // treat the slope of a degenerate line segment (between a point and itself) as negative infinity
    if (dx == 0) return Double.POSITIVE_INFINITY; // treat the slope of a vertical line segment as positive infinity
    if (dy == 0) return +0.0; // treat the slope of a horizontal line segment as positive zero
    return (double) dy / dx;
  }

  // is this point lexicographically smaller than that one?
  // comparing y-coordinates and breaking ties by x-coordinates
  public int compareTo(final Point that) {
    // compare points by their y-coordinates, breaking ties by their x-coordinates.
    // Formally, the invoking point (x0, y0) is less than the argument point (x1, y1)
    // if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
    if (y != that.y) return y - that.y;
    return x - that.x;
  }

  // return string representation of this point
  public String toString() {
        /* DO NOT MODIFY */
    return "(" + x + ", " + y + ")";
  }

  // unit test
  public static void main(String[] args) {
        /* YOUR CODE HERE */
  }
}
