/**
 * Represents a set of points in the unit square.  Implemented with a red-black BST.
 *
 * @author Kevin Crosby
 */
public class PointSET {
  private final SET<Point2D> set;

  /**
   * Construct an empty set of points.
   */
  public PointSET() {
    set = new SET<>();
  }

  /**
   * Is the set empty?
   *
   * @return True if set is empty, false otherwise.
   */
  public boolean isEmpty() {
    return set.isEmpty();
  }

  /**
   * Number of points in the set.
   *
   * @return Size of set of points.
   */
  public int size() {
    return set.size();
  }

  /**
   * Add the point to the set (if it is not already in the set).
   *
   * @param point Point to add to the set.
   */
  public void insert(final Point2D point) { // logarithmic in N
    if (point == null) { throw new NullPointerException(); }
    set.add(point);
  }

  /**
   * Does the set contain point?
   *
   * @param point Point to test.
   * @return True if point is in the set, false otherwise.
   */
  public boolean contains(final Point2D point) { // logarithmic in N
    if (point == null) { throw new NullPointerException(); }
    return set.contains(point);
  }

  /**
   * Draw all points to standard draw.
   */
  public void draw() {
    StdDraw.setXscale(-0.01, 1.01);
    StdDraw.setYscale(-0.01, 1.01);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius();
    new RectHV(0.0, 0.0, 1.0, 1.0).draw();

    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.01);

    for (final Point2D point : set) {
      point.draw(); // StdDraw.point(point.x(), point.y());
    }
  }

  /**
   * All points that are inside the rectangle.
   *
   * @param rect Rectangle with points of interest.
   * @return Iterable of points of interest within rectangle.
   */
  public Iterable<Point2D> range(final RectHV rect) { // linear in N
    if (rect == null) { throw new NullPointerException(); }

    Queue<Point2D> points = new Queue<>();
    for (final Point2D point : set) {
      if (rect.contains(point)) {
        points.enqueue(point);
      }
    }

    return points;
  }

  /**
   * A nearest neighbor in the set to point p; null if the set is empty.
   *
   * @param p Point to find nearest neighbor from.
   * @return Nearest neighbor.
   */
  public Point2D nearest(final Point2D p) { // linear in N
    if (p == null) { throw new NullPointerException(); }
    if (isEmpty()) { return null; }

    double minDistanceSquared = Double.POSITIVE_INFINITY;
    Point2D nearestNeighbor = null;
    for (final Point2D point : set) {
      double distanceSquared = point.distanceSquaredTo(p);
      if (minDistanceSquared > distanceSquared) {
        minDistanceSquared = distanceSquared;
        nearestNeighbor = point;
      }
    }
    return nearestNeighbor;
  }

  /**
   * Unit testing of the methods (optional).
   *
   * @param args Input arguments.
   */
  public static void main(String[] args) {
    String filename = args[0];
    In in = new In(filename);

    StdDraw.show();

    // initialize the two data structures with point from standard input
    PointSET brute = new PointSET();
    while (!in.isEmpty()) {
      double x = in.readDouble();
      double y = in.readDouble();
      Point2D p = new Point2D(x, y);
      brute.insert(p);
      StdDraw.clear();
      brute.draw();
      StdDraw.show();
    }
  }
}
