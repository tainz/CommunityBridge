package org.communitybridge.utility;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class StringUtilitiesTest
{
	@Test
	public void findFirstOfShouldThrowErrorOnEmptySearchCharacters()
	{
		try
		{
			StringUtilities.find_first_of("alphabet", "");
			fail("Should not search for an empty character list.");
		}
		catch (IllegalArgumentException exception)
		{}
		catch (Exception exception)
		{
			fail("Unexpected exception caught:" + exception.getMessage());
		}
	}

	@Test
	public void findFirstOfShouldThrowErrorOnNullSearchCharacters()
	{
		try
		{
			StringUtilities.find_first_of("a", null);
			fail("Should not search for an empty character list.");
		}
		catch (IllegalArgumentException exception)
		{}
		catch (Exception exception)
		{
			fail("Unexpected exception caught:" + exception.getMessage());
		}
	}

	@Test
	public void findFirstOfShouldThrowErrorForNullSearchString()
	{
		try
		{
			StringUtilities.find_first_of(null, "s");
			fail("Should not search a null string.");
		}
		catch (IllegalArgumentException exception)
		{}
		catch (Exception exception)
		{
			fail("Unexpected exception caught: " + exception.getMessage());
		}
	}

	@Test
	public void findFirstOfShouldReturnNotFoundForEmptySearchStringStartingAtFirstPosition()
	{
		assertEquals(StringUtilities.NOT_FOUND, StringUtilities.find_first_of("","a"));
	}

	@Test
	public void findFirstOfShouldReturnPositionForOneCharcterStartingAtFirstPosition()
	{
		// 0 indexed, so first position is 0.
		assertEquals(0, StringUtilities.find_first_of("alphabet","a"));
	}

	@Test
	public void findFirstOfShouldReturnPositionEitherOfTwoCharactersStartingAtFirstPosition()
	{
		// 0 indexed, expected value is: 2
		assertEquals(2, StringUtilities.find_first_of("alphabet","ph"));
	}

	@Test
	public void findFirstOfShouldReturnPositionForOneCharacterStartingAtSecondPosition()
	{
		assertEquals(2,StringUtilities.find_first_of("abab", "pa", 1));
	}

	/** compareVersion section **/
	@Test
	public void compareVersionWithEmptyArgumentsShouldReturnZero()
	{
		assertEquals(0, StringUtilities.compareVersion("", ""));
		assertEquals(0, StringUtilities.compareVersion(null, ""));
		assertEquals(0, StringUtilities.compareVersion("", null));
		assertEquals(0, StringUtilities.compareVersion(null, null));
	}

	@Test
	public void compareVersionWithEmptyLeftArgumentShouldReturnNegativeOne()
	{
		assertEquals(-1, StringUtilities.compareVersion(null, "1-1.0"));
		assertEquals(-1, StringUtilities.compareVersion("", "1-1.0"));
	}

	@Test
	public void compareVersionWithEmptyRightArgumentShouldReturnOne()
	{
		assertEquals(1, StringUtilities.compareVersion("1.0", null));
		assertEquals(1, StringUtilities.compareVersion("1.0", ""));
	}

	@Test
	public void compareVersionShouldReturnZeroWithEqualSameLengthVersions()
	{
		assertEquals(0, StringUtilities.compareVersion("1.2.3", "1.2.3"));
	}

	@Test
	public void compareVersionShouldReturnZeroWithLeftLongerVersionsEqual()
	{
		assertEquals(0, StringUtilities.compareVersion("1.2.0", "1.2"));
	}

	@Test
	public void compareVersionShouldReturnZeroWithRightLongerVersionsEqual()
	{
		assertEquals(0, StringUtilities.compareVersion("1.2", "1.2.0"));
	}

	@Test
	public void compareVersionShouldReturnOneWithLeftLongerLeftGreater()
	{
		assertEquals(1, StringUtilities.compareVersion("1.2.3", "1.2"));
	}

	@Test
	public void compareVersionShouldReturnNegativeOneWithRightLongerRightGreater()
	{
		assertEquals(-1, StringUtilities.compareVersion("1.2", "1.2.3"));
	}

	@Test
	public void compareVersionShouldReturnOneWithFirstPartLeftGreaterThanRight()
	{
		assertEquals(1, StringUtilities.compareVersion("2.1", "1.1"));
	}

	@Test
	public void compareVersionShouldReturnNegativeOneWithFirstPartRightGreaterThanRight()
	{
		assertEquals(-1, StringUtilities.compareVersion("1.1", "2.1"));
	}

	@Test
	public void compareVersionShouldReturnOneWithLastPartLeftGreaterThanRight()
	{
		assertEquals(1, StringUtilities.compareVersion("1.1", "1.0"));
	}

	@Test
	public void compareVersionShouldReturnNegativeOneWithLastPartRightGreaterThanLeft()
	{
		assertEquals(-1, StringUtilities.compareVersion("1.0", "1.1"));
	}

	@Test
	public void compareVersionShouldReturnOneWithMiddlePartLeftGreaterThanRight()
	{
		assertEquals(1, StringUtilities.compareVersion("1.2.0", "1.1.10"));
	}

	@Test
	public void compareVersionShouldReturnNegativeOneWithMiddlePartRightGreaterThanLeft()
	{
		assertEquals(-1, StringUtilities.compareVersion("1.0.15", "1.1.15"));
	}

	@Test
	public void compareVersionShouldSupportPeriodSeparators()
	{
		assertEquals(0, StringUtilities.compareVersion("1.0", "1.0"));
		assertEquals(1, StringUtilities.compareVersion("1.1", "1.0"));
		assertEquals(-1, StringUtilities.compareVersion("1.0", "1.1"));
	}

	@Test
	public void compareVersionShouldSupportUnseparatedVersions()
	{
		assertEquals(0, StringUtilities.compareVersion("1", "1"));
		assertEquals(1, StringUtilities.compareVersion("11", "10"));
		assertEquals(-1, StringUtilities.compareVersion("10", "11"));
	}

	@Test
	public void compareVersionShouldSupportHyphenSeparators()
	{
		assertEquals(0, StringUtilities.compareVersion("1-0", "1-0"));
		assertEquals(1, StringUtilities.compareVersion("1-1", "1-0"));
		assertEquals(-1, StringUtilities.compareVersion("1-0", "1-1"));
	}

	@Test
	public void compareVersionShouldSupportMixedSeparators()
	{
		assertEquals(0, StringUtilities.compareVersion("1-0.1", "1-0.1"));
		assertEquals(0, StringUtilities.compareVersion("1.0.1", "1-0-1"));
		assertEquals(1, StringUtilities.compareVersion("1-1.0", "1-0.0"));
		assertEquals(-1, StringUtilities.compareVersion("1-0.0", "1-1.0"));
	}

	/*
	 * joinStrings section.
	 */
	@Test
	public void joinStringsShouldReturnEmptyStringWithEmptyInput()
	{
		List<String> emptyList = new ArrayList<String>();

		String result = StringUtilities.joinStrings(emptyList, "");

		assertTrue(result.equals(""));
	}

	@Test
	public void joinStringsShouldJoinTwoStrings()
	{
		String one = "oneString";
		String two = "oneString";
		List<String> twoStrings = new ArrayList<String>();
		twoStrings.add(one);
		twoStrings.add(two);

		String result = StringUtilities.joinStrings(twoStrings, "");

		assertTrue(result.equals(one + two));
	}

	@Test
	public void joinStringsShouldJoinTwoStringsWithConjunction()
	{
		String one = "oneString";
		String two = "twoString";

		List<String> twoStrings = new ArrayList<String>();
		twoStrings.add(one);
		twoStrings.add(two);

		String result = StringUtilities.joinStrings(twoStrings, ", ");

		assertTrue(result.equals(one + ", " + two));
	}

	@Test
	public void timeElapsedToStringShouldReturnZeroSecondsForZero()
	{
		String result = StringUtilities.timeElapsedToString(0);

		assertEquals("0 seconds", result);
	}

	@Test
	public void timeElapsedToStringShouldReturn1SecondFor1()
	{
		String result = StringUtilities.timeElapsedToString(1);
		assertTrue(result.equals("1 second"));
	}

	@Test
	public void timeElapsedToStringShouldHandleMultipleSeconds()
	{
		String result = StringUtilities.timeElapsedToString(2);

		assertTrue(result.equals("2 seconds"));
	}

	@Test
	public void timeElapsedToStringShouldHandleOneMinuteNoSeconds()
	{
		String result = StringUtilities.timeElapsedToString(60);

		assertTrue(result.equals("1 minute"));
	}

	@Test
	public void timeElapsedToStringShouldHandleMultipleMinutesNoSeconds()
	{
		String result = StringUtilities.timeElapsedToString(120);

		assertTrue(result.equals("2 minutes"));
	}

	@Test
	public void timeElapsedToStringShouldHandleMinuteAndSecond()
	{
		String result = StringUtilities.timeElapsedToString(61);

		assertTrue(result.equals("1 minute, 1 second"));
	}

	@Test
	public void timeElapsedToStringShouldHandleMinuteAndSeconds()
	{
		String result = StringUtilities.timeElapsedToString(75);

		assertTrue(result.equals("1 minute, 15 seconds"));
	}

	@Test
	public void timeElapsedToStringShouldHandleMinutesAndSeconds()
	{
		String result = StringUtilities.timeElapsedToString(135);

		assertTrue(result.equals("2 minutes, 15 seconds"));
	}

	@Test
	public void timeElapsedToStringShouldHandle1Hour()
	{
		// 1 hour = 60m, 60 * 60 = 3600 seconds
		String result = StringUtilities.timeElapsedToString(3600);

		assertTrue(result.equals("1 hour"));
	}

	@Test
	public void timeElapsedToStringShouldHandleHours()
	{
		// 2 hour = 120m, 120 * 60 = 7200 seconds
		String result = StringUtilities.timeElapsedToString(7200);

		assertTrue(result.equals("2 hours"));
	}

	@Test
	public void timeElapsedToStringShouldHandleHourAndMinute()
	{
		// 1 hour = 60m, 60 * 60 = 3600 seconds, add 1 minute (60 seconds)
		String result = StringUtilities.timeElapsedToString(3660);

		assertTrue(result.equals("1 hour, 1 minute"));
	}

	@Test
	public void timeElapsedToStringShouldHandle1Day()
	{
		// 60 seconds, 60 minutes, 24 hours = 86,400
		String result = StringUtilities.timeElapsedToString(86400);

		assertTrue(result.equals("1 day"));
	}

	@Test
	public void timeElapsedToStringShouldHandleDays()
	{
		// 60 seconds, 60 minutes, 24 hours = 86,400 * 2 makes two days
		String result = StringUtilities.timeElapsedToString(172800);

		assertTrue(result.equals("2 days"));
	}

	@Test
	public void timeElapsedToStringShouldHandleDaysAndSecond()
	{
		// 60 seconds, 60 minutes, 24 hours = 86,400 * 2 makes two days
		String result = StringUtilities.timeElapsedToString(172801);

		assertTrue(result.equals("2 days, 1 second"));
	}

	@Test
	public void timeElapsedToStringShouldHandleDaysAndHour()
	{
		// 60 seconds, 60 minutes, 24 hours = 86,400 * 2 makes two days
		String result = StringUtilities.timeElapsedToString(176400);

		assertTrue(result.equals("2 days, 1 hour"));
	}
}