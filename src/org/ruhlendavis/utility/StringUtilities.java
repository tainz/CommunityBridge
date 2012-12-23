package org.ruhlendavis.utility;

/**
 * A small collection of string related methods that don't belong anywhere else.
 *
 * By design, this class cannot be instantiated nor can it be sub-classed.
 *
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public final class StringUtilities
{
	/**
	 *  Private constructor prevents instantiation of the class.
	 */
	private StringUtilities() {}

	/**
	 * Returns:
	 *   1 -- if left is greater than right
	 *   0 -- if they are equal
	 *  -1 -- if left is less than right
	 * @param leftVersion
	 * @param rightVersion
	 */
	public static int compareVersion(String leftVersion, String rightVersion) throws IllegalArgumentException
	{
		if (leftVersion == null || rightVersion == null
			||leftVersion.isEmpty() || rightVersion.isEmpty())
		{
			throw new IllegalArgumentException();
		}
		leftVersion = leftVersion.replace("-", ".");
		rightVersion = rightVersion.replace("-", ".");
		String[] leftParts = leftVersion.split("\\.");
		String[] rightParts = rightVersion.split("\\.");
		int leftLength = leftParts.length;
		int rightLength = rightParts.length;

		// Which is shortest?
    int length = leftLength < rightLength ? leftLength : rightLength;

		// Determine if they are equal up to the point they are the same length.
		for (int i = 0; i < length; i++)
		{
			int leftPart = Integer.parseInt(leftParts[i]);
			int rightPart = Integer.parseInt(rightParts[i]);

			if (leftPart > rightPart)
			{
				return 1;
			}
			else if (leftPart < rightPart)
			{
				return -1;
			}
		}

		// So far, they're equal, therefore the longer is the greater
		if (leftLength > rightLength)
		{
			return 1;
		}
		else if (leftLength < rightLength)
		{
			return -1;
		}

		// Same length, so equal.
		return 0;
	}

	/**
	 *  Finds the first character in a String that matches any of the characters in
	 *  another String, starting with the first character of the string.
	 *
	 *  @param string String containing the string to search.
	 *  @param characters String containing the characters to search for.
	 *  @return int indicating the location in the search string of a character
	 *          found or -1 if not found.
	 *  @see find_first_of(String, String, int)
	 */
	public static int find_first_of(String string, String characters)
	{
		return find_first_of(string, characters, 0);
	}

	/**
	 *  Finds the first character in a String that matches any of the characters in
	 *  another String
	 *  @param string String containing the string to search.
	 *  @param characters String containing the characters to search for.
	 *  @param startingPoint int indicating where in the search string to start.
	 *  @return int indicating the location in the search string of a character
	 *          found or -1 if not found.
	 */
	public static int find_first_of(String string, String characters, int startingPoint)
	{
		for (int position = startingPoint; position < string.length(); position++)
		{
			if (characters.indexOf(string.charAt(position)) > -1)
			{
				return position;
			}
		}
		return -1;
	}
}
