import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * RandomizedQueue implemented as an array.
 *
 * @author Kevin Crosby
 */
public class RandomizedQueue<Item> implements Iterable<Item> {
  private int n, capacity;
  private int head, tail;
  private Item[] a;

  /**
   * Construct an empty randomized queue.
   */
  @SuppressWarnings("unchecked")
  public RandomizedQueue() {
    n = 0;
    capacity = 2;
    head = 0;
    tail = 0;
    a = (Item[]) new Object[capacity];
  }

  /**
   * Is the queue empty?
   *
   * @return True if queue is empty.  False otherwise.
   */
  public boolean isEmpty() {
    return n == 0;
  }

  /**
   * Return the number of items on the queue.
   *
   * @return Size of the queue.
   */
  public int size() {
    return n;
  }

  /**
   * Add the item.
   *
   * @param item Item to add.
   */
  public void enqueue(Item item) {
    if (item == null) throw new NullPointerException("Cannot add null to queue!");
    if (n == capacity) resize(2 * capacity); // double size of array if necessary
    a[tail] = item;
    tail = (tail + 1) % capacity;
    n++;
  }

  /**
   * Delete and return a random item.
   *
   * @return Random item.
   */
  public Item dequeue() {
    if (isEmpty()) throw new NoSuchElementException("Queue underflow!");
    int r = StdRandom.uniform(n);
    int i = (head + r) % capacity;
    Item item = a[i];
    a[i] = a[head];
    a[head] = null; // avoid loitering
    head = (head + 1) % capacity;
    n--;
    if (n > 0 && n == capacity / 4) resize(capacity / 2); // shrink size of array if necessary
    return item;
  }

  /**
   * Return (but do not delete) a random item.
   *
   * @return Random item.
   */
  public Item sample() {
    if (isEmpty()) throw new NoSuchElementException("Queue underflow!");
    int r = StdRandom.uniform(n);
    return a[(head + r) % capacity];
  }

  /**
   * Resize the underlying array holding the elements.
   *
   * @param newCapacity New capacity of array.
   */
  @SuppressWarnings("unchecked")
  private void resize(int newCapacity) {
    assert newCapacity >= n;
    Item[] temp = (Item[]) new Object[newCapacity];
    for (int i = head, j = 0; j < n; i = (i + 1) % capacity, j++) {
      temp[j] = a[i];
    }
    a = temp;
    head = 0;
    tail = head + n;
    capacity = newCapacity;
  }

  /**
   * Print queue to standard out.
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
   * Return an independent iterator over items in random order.
   *
   * @return Item iterator.
   */
  public Iterator<Item> iterator() {
    return new RandomArrayIterator();
  }

  /**
   * RandomArrayIterator
   */
  private class RandomArrayIterator implements Iterator<Item> {
    private int i;
    private Item[] b;

    @SuppressWarnings("unchecked")
    public RandomArrayIterator() {
      i = 0;
      b = (Item[]) new Object[n]; // to make iterators independent
      for (int i = head, j = 0; j < n; i = (i + 1) % capacity, j++) {
        b[j] = a[i];
      }
      StdRandom.shuffle(b);
    }

    public boolean hasNext() {
      return i < n;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public Item next() {
      if (!hasNext()) throw new NoSuchElementException();
      return b[i++];
    }
  }

  /**
   * Unit testing.
   *
   * @param args Input arguments.
   */
  public static void main(String[] args) {
    RandomizedQueue<Integer> queue = new RandomizedQueue<>();

    for (int i = 0; i < 8; i++) {
      queue.enqueue(i);
      queue.print();
    }

    for (int i : queue) {
      System.out.print(i + ": ");
      for (int j : queue) {
        System.out.print(j + ", ");
      }
      System.out.println();
    }

    queue.print();
    for (int i = 0; i < 4; i++) {
      queue.dequeue();
      queue.print();
    }

    for (int i = 8; i < 13; i++) {
      queue.enqueue(i);
      queue.print();
    }

    for (int i : queue) {
      System.out.print(i + ": ");
      for (int j : queue) {
        System.out.print(j + ", ");
      }
      System.out.println();
    }

    queue.print();
    for (int i = 0; i < 9; i++) {
      queue.dequeue();
      queue.print();
    }
  }
}
