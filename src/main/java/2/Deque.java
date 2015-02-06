import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Deque implemented as a doubly linked list.
 *
 * @author Kevin Crosby
 */
public class Deque<Item> implements Iterable<Item> {
  private Node fwd, aft; // sentinel nodes
  private int n;

  private class Node {
    private Item item;
    private Node next, prev;

    private Node() {
      this(null, null, null);
    }

    private Node(final Item item, final Node next, final Node prev) {
      this.item = item;
      this.next = next;
      this.prev = prev;
    }
  }

  /**
   * Construct an empty deque.
   */
  public Deque() {
    fwd = new Node();
    aft = new Node();
    fwd.next = aft;
    fwd.prev = fwd;
    aft.next = aft;
    aft.prev = fwd;
    n = 0;
  }

  /**
   * Is the deque empty?
   *
   * @return True if deque is empty.  False otherwise.
   */
  public boolean isEmpty() {
    return n == 0;
  }

  /**
   * Return the number of items on the deque.
   *
   * @return Size of the deque.
   */
  public int size() {
    return n;
  }

  /**
   * Insert the item at the front.
   *
   * @param item Item to be inserted.
   */
  public void addFirst(Item item) {
    if (item == null) throw new NullPointerException("Cannot add null to deque!");
    Node oldFirst = fwd.next;
    Node first = new Node(item, oldFirst, fwd);
    fwd.next = first;
    oldFirst.prev = first;
    n++;
  }

  /**
   * Insert the item at the end.
   *
   * @param item Item to be inserted.
   */
  public void addLast(Item item) {
    if (item == null) throw new NullPointerException("Cannot add null to deque!");
    Node oldLast = aft.prev;
    Node last = new Node(item, aft, oldLast);
    aft.prev = last;
    oldLast.next = last;
    n++;
  }

  /**
   * Delete and return the item at the front.
   *
   * @return Item to be removed.
   */
  public Item removeFirst() {
    if (isEmpty()) throw new NoSuchElementException("Deque underflow!");
    Node first = fwd.next;
    Item item = first.item;
    fwd.next = first.next;
    first.next.prev = fwd;
    n--;
    return item;
  }

  /**
   * Delete and return the item at the end.
   *
   * @return Item to be removed.
   */
  public Item removeLast() {
    if (isEmpty()) throw new NoSuchElementException("Deque underflow!");
    Node last = aft.prev;
    Item item = last.item;
    aft.prev = last.prev;
    last.prev.next = aft;
    n--;
    return item;
  }

  /**
   * Print deque to standard out.
   */
  private void print() {
    boolean start = true;
    for (Item item : this) {
      if (start) {
        System.out.print(item);
        start = false;
      } else {
        System.out.print(" -> " + item);
      }
    }
    System.out.println();
  }

  /**
   * Return an iterator over items in order from front to end.
   *
   * @return Item iterator.
   */
  public Iterator<Item> iterator() {
    return new ListIterator();
  }

  /**
   * ListIterator
   */
  private class ListIterator implements Iterator<Item> {
    private Node current = fwd.next;

    @Override
    public boolean hasNext() {
      return current != aft;
    }

    @Override
    public Item next() {
      if (!hasNext()) throw new NoSuchElementException();
      Item item = current.item;
      current = current.next;
      return item;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Unit testing.
   *
   * @param args Input arguments.
   */
  public static void main(String[] args) {
    Deque<Integer> deque = new Deque<>();

    for (int i = 0; i < 10; i++) {
      if (i % 2 == 0) {
        deque.addFirst(i);
      } else {
        deque.addLast(i);
      }
      deque.print();
    }

    for (int i : deque) {
      System.out.print(i + ": ");
      for (int j : deque) {
        System.out.print(j + ", ");
      }
      System.out.println();
    }

    for (int i = 0; i < 10; i++) {
      if (i % 2 == 0) {
        deque.removeLast();
      } else {
        deque.removeFirst();
      }
      deque.print();
    }

    for (int i = 0; i < 10; i++) {
      if (i % 2 == 0) {
        deque.addLast(i);
      } else {
        deque.addFirst(i);
      }
      deque.print();
    }

    for (int i : deque) {
      System.out.print(i + ": ");
      for (int j : deque) {
        System.out.print(j + ", ");
      }
      System.out.println();
    }

    for (int i = 0; i < 10; i++) {
      if (i % 2 == 0) {
        deque.removeFirst();
      } else {
        deque.removeLast();
      }
      deque.print();
    }
  }
}
