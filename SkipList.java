// Johnathon Bulman

// SkipList.java
// =============
// Skip list data structure that holds generics.

import java.util.*;

class Node<T>
{
  T value;              // User data
  ArrayList<Node<T>> h; // Stores 'next' references.

  // New node with speicified 'height'.
  Node(int height)
  {
    this.h = new ArrayList<Node<T>>(height);

    // All 'next' references initialized to null.
    for (int i = 0; i < height; i++)
      this.h.add(null);
  }

  // New node with specified 'height' and 'data' parameters.
  Node(T data, int height)
  {
    this.value = data; // User data
    this.h = new ArrayList<Node<T>>(height);

    // All 'next' references initialized to null.
    for (int i = 0; i < height; i++)
      this.h.add(null);
  }

  // Returns user data stored at this node.
  public T value()
  {
    return this.value;
  }

  public int height()
  {
    return this.h.size();
  }

  public int maxLevel()
  {
    return height() - 1;
  }

  // Returns a 'next' reference at a given level.
  public Node<T> next(int lvl)
  {
    // Check bounds.
    if (lvl < 0 || lvl > maxLevel())
    return null;

    return this.h.get(lvl);
  }

  // Sets the next reference at the given level within this node to 'node'.
  public void setNext(int level, Node<T> node)
  {
    this.h.set(level, node);
  }

  // Grow this node by exactly one level.
  public void grow()
  {
    this.h.add(null);
  }

  // Grow this node by exactly one level with probability 50%.
  public boolean maybeGrow()
  {
    if (coinToss())
    {
      grow();
      return true;
    }
    return false;
  }

  // Virtual coin flip
  private boolean coinToss()
  {
    Random rand = new Random();
    boolean heads = true, tails = false;
    return (rand.nextInt(2) == 1) ? heads : tails;
  }

  // Reduces this node's index to the value given in 'level' parameter.
  public void trim(int level)
  {
    while (level <= maxLevel())
      this.h.remove(maxLevel());
  }
}

// SkipList Class
// Notes:
//  * Expected height of the skip list is the ceiling of log2(n), where n is the number of elements
//    in the skip list (excluding the head node).
//  * A skip list with exactly one element has a height of 1.
//  * A skip list with zero elements has a height of 1 (head node initial height).
public class SkipList<AnyType extends Comparable<AnyType>>
{
  private Node<AnyType> head; // Head node.
  private int size;           // Size of skip list.

  // initializes a skip list with a height of 1.
  SkipList()
  {
    this(1);
  }

  // initializes a skip list with a height of 'height'.
  SkipList(int height)
  {
    this.head = new Node<>(height);
  }

  // Returns the size of the skip list.
  public int size()
  {
    return this.size;
  }

  // Returns the height of the skip list.
  public int height()
  {
    return this.head.height();
  }

  // Returns the head of the skip list.
  public Node<AnyType> head()
  {
    return this.head;
  }

  // Insert 'data' into the skip list (in sorted order).
  // Duplicate values shall be inserted before the first occurrence of that
  // value in the skip list.
  public void insert(AnyType data)
  {
    insert(data, generateRandomHeight(height()));
  }

  // Insert data into the skip list using a pre-determined height.
  public void insert(AnyType data, int height)
  {
    int level = head().maxLevel();

    Node<AnyType> curr = head(), next = curr.next(level); // Temp nodes
    Node<AnyType> insert = new Node<>(data, height); // To be inserted

    // Iterate through the skip list.
    while (level > -1)
    {
      if (next != null && data.compareTo(next.value()) > 0)
      {
        curr = next;
        next = curr.next(level);
        continue;
      }

      // Insert by level.
      if (level <= insert.maxLevel())
      {
        insert.setNext(level, next);
        curr.setNext(level, insert);
      }

      // Decrement level.
      next = curr.next(--level);
    }

    // Check if skip list needs to grow.
    if (height() < getMaxHeight(++this.size))
      growSkipList();

  }

  // Delete a single occurrence of 'data' from the skip list.
  // Duplicate values will not be deleted.
  public void delete(AnyType data)
  {
     // Find first occurence of data.
     Node<AnyType> deleted = getFirst(data);
     if (deleted == null) // Data not found.
       return;

     int level = head().maxLevel(); // Head height - 1
     // Temp nodes for traversal.
     Node<AnyType> curr = head(), next = curr.next(level);

     // Iterate through the skip list by level.
     while (level > -1)
     {
       if (next != null && data.compareTo(next.value()) > 0)
       {
         curr = next;
         next = curr.next(level);
         continue;
       }

       // Delete 'deleted' node one level at a time.
       if (level <= deleted.maxLevel())
       {
         curr.setNext(level, deleted.next(level));
         next = curr.next(level);
       }

       // Decrement level.
       next = curr.next(--level);
     }

     // Trim the skip list if necessary.
     if (height() > getMaxHeight(--this.size))
       trimSkipList();

  }

  // Returns a reference to a node in the skip list that contains the first
  // occurrence of 'data'.
  private Node<AnyType> getFirst(AnyType data)
  {
    int level = head().maxLevel();
    Node<AnyType> curr = head(), next = curr.next(level); // Temp nodes
    Node<AnyType> get = null;

    while (level > -1)
    {
      if (next != null && data.compareTo(next.value()) > 0)
      {
        curr = next;
        next = curr.next(level);
        continue;
      }

      if (next != null && data.compareTo(next.value()) == 0)
        get = next;

      next = curr.next(--level);
    }

    return get;
  }

  // Returns true if the skip list contains 'data'.
  public boolean contains(AnyType data)
  {
    int level = head().maxLevel();
    Node<AnyType> curr = head(), next = curr.next(level); // Temp nodes

    while (level > -1)
    {
      if (next != null && data.compareTo(next.value()) > 0)
      {
        curr = next;
        next = curr.next(level);
        continue;
      }

      if (next != null && data.compareTo(next.value()) == 0)
        return true;


      next = curr.next(--level);
    }
    return false;
  }

  // Returns a Node that contains 'data'; false otherwise.
  public Node<AnyType> get(AnyType data)
  {
    int level = head().maxLevel();
    Node<AnyType> curr = head(), next = curr.next(level); // Temp nodes

    while (level > -1)
    {
      if (next != null && data.compareTo(next.value()) > 0)
      {
        curr = next;
        next = curr.next(level);
        continue;
      }

      if (next != null && data.compareTo(next.value()) == 0)
        return next;

      next = curr.next(--level);
    }
    return null;
  }

  // Returns max height of a skip list with 'n' nodes.
  private static int getMaxHeight(int n)
  {
    if (n <= 2)
      return 1;

    return (int)Math.ceil(Math.log(n) / Math.log(2));
  }

  // Returns a random height that does not exceed maxHeight.
  private static int generateRandomHeight(int maxHeight)
  {
    Random rand = new Random();
    int height = 1; // Starting height.

    while (rand.nextInt(2) == 1 && height < maxHeight)
      ++height;

    return height;
  }

  // Grows the skip list.
  private void growSkipList()
  {
    // Get the max Level of the skip list.
    int maxLevel = head().maxLevel();
    // Temp nodes to iterate through the list.
    Node<AnyType> curr = head(), next = head().next(maxLevel);

    // Grow the head of the skip list by 1.
    head().grow();

    while (next != null)
    {
      if (next.maybeGrow()) // 50% chance.
      {
        // Hook up references with same level.
        curr.setNext(maxLevel + 1, next);
        curr = next;
      }
      next = next.next(maxLevel); // Iterate.
    }
  }

  // Trims skip list.
  private void trimSkipList()
  {
    trimSkipListHelper(head(), getMaxHeight(size()));
  }

  private void trimSkipListHelper(Node<AnyType> node, int level)
  {
    if (node == null)
      return;

    trimSkipListHelper(node.next(level), level);
    node.trim(level);
  }
}
