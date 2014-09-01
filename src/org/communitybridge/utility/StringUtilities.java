package org.communitybridge.utility;

import java.util.List;

public final class StringUtilities
{
	public final static int NOT_FOUND = -1;

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
		if (leftVersion == null)
		{
			leftVersion = "";
		}

		if (rightVersion == null)
		{
			rightVersion = "";
		}

		leftVersion = leftVersion.replace("-", ".").replaceAll("[^0-9\\.]", "");
		rightVersion = rightVersion.replace("-", ".").replaceAll("[^0-9\\.]", "");

		if (leftVersion.isEmpty() && rightVersion.isEmpty())
		{
			return 0;
		}
		else if (leftVersion.isEmpty())
		{
			return -1;
		}
		else if (rightVersion.isEmpty())
		{
			return 1;
		}

		String[] leftParts = leftVersion.split("\\.");
		String[] rightParts = rightVersion.split("\\.");
		int leftLength = leftParts.length;
		int rightLength = rightParts.length;

		// Which is shortest?
    int shortest = leftLength < rightLength ? leftLength : rightLength;

		// Determine if they are equal up to the point they are the same length.
		for (int i = 0; i < shortest; i++)
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

		if (shortest < leftParts.length)
		{
			for (int i = shortest; i < leftParts.length; i++)
			{
				if (Integer.parseInt(leftParts[i]) > 0)
				{
					return 1;
				}
			}
		}
		else if (shortest < rightParts.length)
		{
			for (int i = shortest; i < rightParts.length; i++)
			{
				if (Integer.parseInt(rightParts[i]) > 0)
				{
					return -1;
				}
			}
		}

		// Same length, so equal.
		return 0;
	}

	/**
	 * Finds the first position, of any of the characters in searchCharacters, if none of the characters are found StringUtilities.NOT_FOUND (-1) is returned.
	 *
	 * @param stringToSearch string to be searched, e.g., the haystack.
	 * @param searchCharacters string containing the characters to search for, e.g., the needles.
	 * @return StringUtilities.NOT_FOUND (-1) if not found, otherwise the position of a character within the search string.
	 * @throws IllegalArgumentException If stringToSearch or searchCharacters is null. Or if searchCharacters is empty.
	 */
	public static int find_first_of(String stringToSearch, String searchCharacters) throws IllegalArgumentException
	{
		return StringUtilities.find_first_of(stringToSearch, searchCharacters, 0);
	}
	/**
	 * Finds the first position after a specified starting position, of any of the characters in searchCharacters, if none of the characters are found StringUtilities.NOT_FOUND (-1) is returned.
	 *
	 * @param stringToSearch string to be searched, e.g., the haystack.
	 * @param searchCharacters string containing the characters to search for, e.g., the needles.
	 * @param startingPosition integer of where to begin the search in the string.
	 * @return StringUtilities.NOT_FOUND (-1) if not found, otherwise the position of a character within the search string.
	 * @throws IllegalArgumentException If stringToSearch or searchCharacters is null. Or if searchCharacters is empty.
	 */
	public static int find_first_of(String stringToSearch, String searchCharacters, int startingPosition) throws IllegalArgumentException
	{
		if (stringToSearch == null)
		{
			throw new IllegalArgumentException("stringToSearch cannot be null.");
		}

		if (searchCharacters == null)
		{
			throw new IllegalArgumentException("searchCharacters cannot be null.");
		}

		if (searchCharacters.isEmpty())
		{
			throw new IllegalArgumentException("At least one character required for search.");
		}


		for (int position = startingPosition; position < stringToSearch.length(); position++)
		{
			if (searchCharacters.indexOf(stringToSearch.charAt(position)) > -1)
			{
				return position;
			}
		}

		return NOT_FOUND;
	}

	public static String joinStrings(List<String> stringList, String conjunction)
	{
		StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;

		for (String string : stringList)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				stringBuilder.append(conjunction);
			}
			stringBuilder.append(string);
		}

		return stringBuilder.toString();
	}

	public static String timeElapsedToString(long time)
	{
		int count = 0;

		if (time == 0)
		{
			return "0 seconds";
		}

		String result = "";

		if (time >= 86400)
		{
			long days = time / 86400;
			time = time - days * 86400;
			result = result + days + (days == 1 ? " day" : " days");
			count++;
		}

		if (time >= 3600)
		{
			if (count >= 1)
			{
				result = result + ", ";
			}
			long hours = time / 3600;
			time = time - hours * 3600;
			result = result + hours + (hours == 1 ? " hour" : " hours");
			count++;
		}

		if (time >= 60)
		{
			if (count >= 1)
			{
				result = result + ", ";
			}
			long minutes = time / 60;
			time = time - minutes * 60;
			result = result + minutes + (minutes == 1 ? " minute" : " minutes");
			count++;
		}

		if (time > 0)
		{
			if (count >= 1)
			{
				result = result + ", ";
			}
			result = result + time + (time == 1 ? " second" : " seconds");
		}

		return result;
	}

	public static String rot13(String original)
	{
		String result = "";

		if (original != null)
		{
			original = original.trim();
			for (char c : original.toCharArray())
			{
				if       (c >= 'a' && c <= 'm') c += 13;
				else if  (c >= 'A' && c <= 'M') c += 13;
				else if  (c >= 'n' && c <= 'z') c -= 13;
				else if  (c >= 'N' && c <= 'Z') c -= 13;
				result = result + String.valueOf(c);
			}
		}

		return result;
	}
}
