import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Mutable data type that uses a 2d-tree of points in the unit square.  A 2d-tree is a generalization of a BST to
 * two-dimensional keys.  The idea is to build a BST with points in the nodes, using the x- and y-coordinates of the
 * points as keys in strictly alternating sequence.
 *
 * @author Kevin Crosby
 */
public class KdTree {
  private Node root;
  private int size;

  private static final Map<Boolean, Comparator<Point2D>> comparator =
      new HashMap<Boolean, Comparator<Point2D>>() {{
        put(true, Point2D.X_ORDER);
        put(false, Point2D.Y_ORDER);
      }};

  private static int compare(final Point2D point1, final Point2D point2, final boolean isVertical) {
    return comparator.get(isVertical).compare(point1, point2);
  }

  private static class Node {
    private Point2D point;     // the point
    private RectHV rectangle;  // the axis-aligned rectangle corresponding to this node
    private Node left;         // the left/bottom subtree
    private Node right;        // the right/top subtree

    private Node(final Point2D point, final RectHV rectangle) {
      this(point, rectangle, null, null);
    }

    private Node(final Point2D point, final RectHV rectangle, final Node left, final Node right) {
      this.point = point;
      this.rectangle = rectangle;
      this.left = left;
      this.right = right;
    }
  }

  /**
   * Construct an empty Kd-Tree of points.
   */
  public KdTree() {
    root = null;
    size = 0;
  }

  /**
   * Is the set empty?
   *
   * @return True if set is empty, false otherwise.
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Number of points in the set.
   *
   * @return Size of set of points.
   */
  public int size() {
    return size;
  }

  /**
   * Add the point to the set (if it is not already in the set).
   *
   * @param point Point to add to the set.
   */
  public void insert(final Point2D point) { // logarithmic in N
    if (point == null) throw new NullPointerException();

    final RectHV rectangle = new RectHV(0, 0, 1, 1);
    root = insert(root, point, rectangle, true);
  }

  private Node insert(final Node node, final Point2D point, final RectHV rectangle, final boolean isVertical) {
    if (node == null) {
      ++size;
      return new Node(point, rectangle);
    }
    int cmp = compare(point, node.point, isVertical);
    RectHV newRectangle = null;
    if (cmp < 0) {
      if (node.left == null) {
        newRectangle = makeRectangle(node.point, node.rectangle, isVertical, true);
      }
      node.left = insert(node.left, point, newRectangle, !isVertical);
    } else if (cmp > 0) {
      if (node.right == null) {
        newRectangle = makeRectangle(node.point, node.rectangle, isVertical, false);
      }
      node.right = insert(node.right, point, newRectangle, !isVertical);
    } else {
      StdOut.println("Point " + point + " already inserted!");  // point already inserted, do nothing
    }
    return node;
  }

  private static RectHV makeRectangle(final Point2D point, final RectHV rectangle, final boolean isVertical, final boolean isLeft) {
    double xmin = rectangle.xmin(), xmax = rectangle.xmax(), ymin = rectangle.ymin(), ymax = rectangle.ymax();
    if (isVertical) {
      double x = point.x();
      if (isLeft) {
        xmax = x;
      } else { // if is right
        xmin = x;
      }
    } else { // if is horizontal
      double y = point.y();
      if (isLeft) {
        ymax = y;
      } else { // if is right
        ymin = y;
      }
    }
    return new RectHV(xmin, ymin, xmax, ymax);
  }

  /**
   * Does the tree contain point?
   *
   * @param point Point to test.
   * @return True if point is in tree, false otherwise.
   */
  public boolean contains(final Point2D point) { // logarithmic in N
    if (point == null) throw new NullPointerException();
    return contains(root, point, true);
  }

  /**
   * Does the subtree contain point?
   *
   * @param node       Root of subtree.
   * @param point      Point to test.
   * @param isVertical True if node is vertical, false if node is horizontal.
   * @return True if point is in tree, false otherwise.
   */
  private boolean contains(final Node node, final Point2D point, final boolean isVertical) {
    if (node == null) return false;
    int cmp = compare(point, node.point, isVertical);
    if (cmp < 0) {
      return contains(node.left, point, !isVertical);
    } else if (cmp > 0) {
      return contains(node.right, point, !isVertical);
    } else {
      return true;
    }
  }

  /**
   * Draw all points to standard draw.
   */
  public void draw() {
    draw(root, true);
    StdDraw.show(0);
  }

  private void draw(final Node node, final boolean isVertical) {
    if (node == null) return;
    Point2D point1, point2;
    if (isVertical) {
      StdDraw.setPenColor(StdDraw.RED);
      double x = node.point.x(), ymin = node.rectangle.ymin(), ymax = node.rectangle.ymax();
      point1 = new Point2D(x, ymin);
      point2 = new Point2D(x, ymax);
    } else {
      StdDraw.setPenColor(StdDraw.BLUE);
      double y = node.point.y(), xmin = node.rectangle.xmin(), xmax = node.rectangle.xmax();
      point1 = new Point2D(xmin, y);
      point2 = new Point2D(xmax, y);
    }
    StdDraw.setPenRadius();
    point1.drawTo(point2);

    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.01);
    node.point.draw();

    draw(node.left, !isVertical);
    draw(node.right, !isVertical);
  }

  /**
   * All points that are inside the rectangle.
   *
   * @param rect Rectangle with points of interest.
   * @return Iterable of points of interest within rectangle.
   */
  public Iterable<Point2D> range(final RectHV rect) { // logarithmic in N
    if (rect == null) throw new NullPointerException();
    return null;
  }

  //private Iterable<Point2D> range(final Node node, final RectHV rect, final boolean isVertical) {
//    int cmp = compare(point, node.rectangle, isVertical);
//    if (cmp < 0) {
//      return range(node.left, rectangle, !isVertical);
//    } else if (cmp > 0) {
//      return range(node.right, point, !isVertical);
//    } else {
//      return node.point;
//    }
  //}

  /**
   * A nearest neighbor in the set to point; null if the set is empty.
   *
   * @param point Point to find nearest neighbor from.
   * @return Nearest neighbor.
   */
  public Point2D nearest(final Point2D point) { // logarithmic in N
    if (point == null) throw new NullPointerException();
    if (isEmpty()) return null;
    return nearest(root, point, null, Double.POSITIVE_INFINITY, true);
  }

  private Point2D nearest(final Node node, final Point2D point, Point2D bestPoint, double minDistanceSquared, final boolean isVertical) {
    if (node == null) return null;
    double distanceSquared = point.distanceSquaredTo(node.point);
    if (minDistanceSquared > distanceSquared) {
      bestPoint = node.point;
      minDistanceSquared = distanceSquared;
    }
    int cmp = compare(point, node.point, isVertical);
    if (cmp < 0) {
      Point2D leftPoint = nearest(node.left, point, bestPoint, minDistanceSquared, !isVertical);
      if (leftPoint != null && minDistanceSquared > point.distanceSquaredTo(leftPoint)) {
        return leftPoint;
      } else {
        Point2D rightPoint = nearest(node.right, point, bestPoint, minDistanceSquared, !isVertical);
        if (rightPoint != null && minDistanceSquared > point.distanceSquaredTo(rightPoint)) {
          return rightPoint;
        }
      }
    } else if (cmp > 0) {
      Point2D rightPoint = nearest(node.right, point, bestPoint, minDistanceSquared, !isVertical);
      if (rightPoint != null && minDistanceSquared > point.distanceSquaredTo(rightPoint)) {
        return rightPoint;
      } else {
        Point2D leftPoint = nearest(node.left, point, bestPoint, minDistanceSquared, !isVertical);
        if (leftPoint != null && minDistanceSquared > point.distanceSquaredTo(leftPoint)) {
          return leftPoint;
        }
      }
    } else {
      return node.point;
    }
    return bestPoint;
  }

  /**
   * Unit testing of the methods (optional).
   *
   * @param args Input arguments.
   */
  public static void main(String[] args) {
    String filename = args[0];
    In in = new In(filename);

    StdDraw.show(0);

    KdTree kdtree = new KdTree();
    while (!in.isEmpty()) {
      double x = in.readDouble();
      double y = in.readDouble();
      Point2D p = new Point2D(x, y);
      kdtree.insert(p);
    }
    kdtree.draw();
  }
}
