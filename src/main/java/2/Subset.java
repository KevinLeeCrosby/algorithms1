/**
 * Print subset of strings from standard input.
 *
 * @author Kevin Crosby
 */
public class Subset {

  /**
   * To run...
   * 1. Compile in IDE.
   * 2. In terminal ...
   * a. set LIB=%HOME%\prj\algorithms1\lib
   * b. cd %HOME%\prj\algorithms1\target\classes
   * c. echo A B C D E F G H I | java -cp ".;%LIB%\stdlib.jar;%LIB%\algs4.jar" Subset 3
   *
   * @param args Size of subset.
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage:\n  echo A B C D E F G H I | java Subset n");
      System.exit(0);
    }

    RandomizedQueue<String> bag = new RandomizedQueue<>();
    while (!StdIn.isEmpty()) {
      String item = StdIn.readString();
      bag.enqueue(item);
    }
    int n = bag.size();

    final int k = Integer.parseInt(args[0]);
    if (k < 0 || k > n - 1) {
      throw new IndexOutOfBoundsException("Need 0 ≤ k ≤ " + n);
    }

    for (int i = 0; i < k; i++) {
      String item = bag.dequeue();
      System.out.println(item);
    }
  }
}
