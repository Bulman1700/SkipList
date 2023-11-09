# SkippityList

This is my implementation of a generic probablistic skip list data structure (SkippityList), written in java. 

Supports: 

    SkippityList()
    SkippityList(int height)

    int size()
    int height()
    void insert(AnyType data)
    void delete(AnyType data)
    boolean contains(AnyType data)

An empty SkippityList has a height of 1 (the head node).

When instatiating the SkippityList class, you can either initialize the skip list with a height of 1 (default), or include an integer 'height' parameter that sets the head node to a specified height.
