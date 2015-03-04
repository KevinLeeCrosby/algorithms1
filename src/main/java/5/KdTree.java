import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    int result = comparator.get(isVertical).compare(point1, point2);
    if (result == 0) {
      result = comparator.get(!isVertical).compare(point1, point2);
    }
    return result;
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
    if (point == null) { throw new NullPointerException(); }

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
    if (point == null) { throw new NullPointerException(); }
    return contains(root, point, true);
  }

  private boolean contains(final Node node, final Point2D point, final boolean isVertical) {
    if (node == null) { return false; }
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
    StdDraw.setXscale(-0.01, 1.01);
    StdDraw.setYscale(-0.01, 1.01);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius();
    root.rectangle.draw();

    draw(root, true);
  }

  private void draw(final Node node, final boolean isVertical) {
    if (node == null) { return; }
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
   * To find all points contained in a given query rectangle, start at the root and recursively search for points in
   * both subtrees using the following pruning rule: if the query rectangle does not intersect the rectangle
   * corresponding to a node, there is no need to explore that node (or its subtrees). A subtree is searched only if it
   * might contain a point contained in the query rectangle.
   *
   * @param rectangle Rectangle with points of interest.
   * @return Iterable of points of interest within rectangle.
   */
  public Iterable<Point2D> range(final RectHV rectangle) { // logarithmic in N
    if (rectangle == null) { throw new NullPointerException(); }
    return range(root, rectangle, true);
  }

  private Set<Point2D> range(final Node node, final RectHV rectangle, final boolean isVertical) {
    if (node == null || !node.rectangle.intersects(rectangle)) { return new HashSet<>(); }

    Set<Point2D> points = new HashSet<>();
    if (rectangle.contains(node.point)) { points.add(node.point); }
    points.addAll(range(node.left, rectangle, !isVertical));
    points.addAll(range(node.right, rectangle, !isVertical));

    return points;
  }

  /**
   * A nearest neighbor in the set to point; null if the set is empty.
   *
   * To find a closest point to a given query point, start at the root and recursively search in both subtrees using
   * the following pruning rule: if the closest point discovered so far is closer than the distance between the query
   * point and the rectangle corresponding to a node, there is no need to explore that node (or its subtrees). That is,
   * a node is searched only if it might contain a point that is closer than the best one found so far. The
   * effectiveness of the pruning rule depends on quickly finding a nearby point. To do this, organize your recursive
   * method so that when there are two possible subtrees to go down, you always choose the subtree that is on the same
   * side of the splitting line as the query point as the first subtree to explore—the closest point found while
   * exploring the first subtree may enable pruning of the second subtree.
   *
   * @param point Point to find nearest neighbor from.
   * @return Nearest neighbor.
   */
  public Point2D nearest(final Point2D point) { // logarithmic in N
    if (point == null) { throw new NullPointerException(); }
    if (isEmpty()) { return null; }
    return nearest(root, point, null, Double.POSITIVE_INFINITY, true);
  }

  private Point2D nearest(final Node node, final Point2D point, Point2D bestPoint, double minDistanceSquared, final boolean isVertical) {
    if (node == null || minDistanceSquared < node.rectangle.distanceSquaredTo(point)) { return null; }

    int cmp = compare(point, node.point, isVertical);
    Node nearNode, farNode;
    if (cmp < 0) {
      nearNode = node.left;
      farNode = node.right;
    } else if (cmp > 0) {
      nearNode = node.right;
      farNode = node.left;
    } else {
      return node.point;
    }

    double distanceSquared = node.point.distanceSquaredTo(point);
    if (minDistanceSquared > distanceSquared) {
      minDistanceSquared = distanceSquared;
      bestPoint = node.point;
    }
    Point2D nearPoint = nearest(nearNode, point, bestPoint, minDistanceSquared, !isVertical);
    if (nearPoint != null) {
      distanceSquared = nearPoint.distanceSquaredTo(point);
      if (minDistanceSquared > distanceSquared) {
        minDistanceSquared = distanceSquared;
        bestPoint = nearPoint;
      }
    }
    Point2D farPoint = nearest(farNode, point, bestPoint, minDistanceSquared, !isVertical);
    if (farPoint != null) {
      distanceSquared = farPoint.distanceSquaredTo(point);
      if (minDistanceSquared > distanceSquared) {
        bestPoint = farPoint;
      }
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

    StdDraw.show();

    KdTree kdtree = new KdTree();
    while (!in.isEmpty()) {
      double x = in.readDouble();
      double y = in.readDouble();
      Point2D p = new Point2D(x, y);
      kdtree.insert(p);
      StdDraw.clear();
      kdtree.draw();
      StdDraw.show();
    }
  }
}
