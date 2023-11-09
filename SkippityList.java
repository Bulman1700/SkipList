// Johnathon Bulman

// SkippityList.java
// =============
// Skip list data structure that holds generics.

import java.util.*;

class Node<T>
{
  T value;              // User data
  ArrayList<Node<T>> h; // Stores 'next' references.

  // New node with given 'height'.
  Node(int height)
  {
    this.h = new ArrayList<Node<T>>(height);

    for (int i = 0; i < height; i++)
      this.h.add(null);
  }

  // New node with given height and data.
  Node(T data, int height)
  {
    this.value = data;
    this.h = new ArrayList<Node<T>>(height);

    for (int i = 0; i < height; i++)
      this.h.add(null);
  }

  public T value()
  {
    return this.value;
  }

  // Return height of this node.
  public int height()
  {
    return this.h.size();
  }

  // Return maximum index of this node's reference arraylist.
  public int maxLevel()
  {
    return height() - 1;
  }

  // Return a node's 'next' reference at a given level in its arraylist.
  public Node<T> next(int lvl)
  {
    // Check bounds.
    if (lvl < 0 || lvl > maxLevel())
    return null;

    return this.h.get(lvl);
  }

  // Sets a node's 'next' reference at a given level in its arraylist.
  public void setNext(int level, Node<T> node)
  {
    this.h.set(level, node);
  }

  // Grow this node by exactly one level.
  public boolean grow()
  {
    return this.h.add(null);
  }

  // Grow this node by exactly one level with probability 50%.
  public boolean maybeGrow()
  {
    return (new Random().nextInt(2)) == 1 ? grow() : false;
  }

  // Trim this node's height to a given level.
  public void trim(int level)
  {
    while (level <= maxLevel())
      this.h.remove(maxLevel());
  }
}

// SkippityList Class
// Notes:
//  * Expected height of the skip list is the ceiling of log2(n), where n is the number of elements
//    in the skip list (excluding the head node).
//  * A skip list with exactly one element has a height of 1.
//  * A skip list with zero elements has a height of 1 (head node initial height).
public class SkippityList<AnyType extends Comparable<AnyType>>
{
  private Node<AnyType> head; // Head node.
  private int size;           // Size of skip list.

  // initializes a skip list with a height of 1.
  SkippityList()
  {
    this(1);
  }

  // initializes a skip list with a height of 'height'.
  SkippityList(int height)
  {
    this.head = new Node<>(height);
  }

  public int size()
  {
    return this.size;
  }

  public int height()
  {
    return this.head.height();
  }

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
      growSkippityList();

  }

  // Delete a single occurrence of 'data' from the skip list.
  // Duplicate values will not be deleted.
  public void delete(AnyType data)
  {
     Node<AnyType> deleted = getFirst(data); // Get first occurence
     
     if (deleted == null)
       return;

     int level = head().maxLevel();
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

       // "Skip" over the node thats being deleted
       if (level <= deleted.maxLevel())
       {
         curr.setNext(level, deleted.next(level));
         next = curr.next(level);
       }

       next = curr.next(--level);
     }

     // Trim the skip list if necessary.
     if (height() > getMaxHeight(--this.size))
       trimSkippityList();
  }

  // Returns a reference to the first occurence of 'data'.
  private Node<AnyType> getFirst(AnyType data)
  {
    int level = head().maxLevel();
    Node<AnyType> curr = head(), next = curr.next(level);
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

  // Returns an occurence of 'data'.
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

  // Returns the calculated height of a Skip list with 'n' nodes.
  private static int getMaxHeight(int n)
  {
    if (n <= 2)
      return 1;

    return (int)Math.ceil(Math.log(n) / Math.log(2));
  }

  // Returns a random height that does not exceed the maximum height
  // of a skip list with 'n' nodes.
  private static int generateRandomHeight(int maxHeight)
  {
    Random rand = new Random();
    int height = 1;

    while (rand.nextInt(2) == 1 && height < maxHeight)
      ++height;

    return height;
  }

  private void growSkippityList()
  {
    int maxLevel = head().maxLevel();
    Node<AnyType> curr = head(), next = head().next(maxLevel);

    head().grow();

    // All other nodes besides the head node are
    // subject to a probabilistic growth rate.
    while (next != null)
    {
      if (next.maybeGrow())
      {
        curr.setNext(maxLevel + 1, next);
        curr = next;
      }
      next = next.next(maxLevel);
    }
  }

  // Trims the skip list.
  private void trimSkippityList()
  {
    trimSkippityListHelper(head(), getMaxHeight(size()));
  }


  private void trimSkippityListHelper(Node<AnyType> node, int level)
  {
    if (node == null)
      return;

    trimSkippityListHelper(node.next(level), level);
    node.trim(level);
  }
}
