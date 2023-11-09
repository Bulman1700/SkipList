// Johnathon Bulman

// =============================
// SkippityList: TestCase01.java
// =============================
// This test case manually checks the structure of a small skip list.

import java.io.*;
import java.util.*;

public class TestCase01
{
	private static boolean referenceCheck(SkippityList<Integer> s, int level, int [] values)
	{
		Node<Integer> temp = s.head();

		for (int i = 0; i < values.length; i++)
		{
			temp = temp.next(level);
			if (temp.value().compareTo(values[i]) != 0)
				return false;
		}

		// One final check to ensure this is the end of the list on this level.
		if (temp.next(level) == null)
		{
			System.out.println("Reference check: PASS");
			return true;
		}

		System.out.println("Reference check: fail whale :(");
		return false;
	}

	public static void main(String [] args)
	{
		SkippityList<Integer> s = new SkippityList<Integer>();

		s.insert(-1);
		s.insert(993);
		s.insert(-2);
		s.insert(0);
		s.insert(1);

		// Verify the bottom level of the skip list is connected as indicated.
		// This is the only level of the skip list whose structure can be 
        // predetermined (other than null references for levels that exceed 
        // the height of the skip list).

		int level;
		boolean success = true;

		success &= referenceCheck(s, level = 0, new int [] {-2, -1, 0, 1, 993});
		success &= referenceCheck(s, level = 4, new int [] {});
		success &= (s.size() == 5);
		success &= (s.height() == 3);

		System.out.println(success ? "Hooray!" : "fail whale :(");
	}
}