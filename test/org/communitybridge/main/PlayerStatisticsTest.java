package org.communitybridge.main;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class PlayerStatisticsTest
{
	private SimpleDateFormat dateFormat;
	private	PlayerStatistics playerStatistics;

	@Before
	public void setup()
	{
		dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		playerStatistics = new PlayerStatistics(dateFormat);
	}

	@Test
	public void testGetGameTimeFormatted()
	{
		playerStatistics.setGameTime(0);
		assertEquals("0 seconds", playerStatistics.getGameTimeFormatted());
	}

	@Test
	public void testGetCurrentXPFormatted()
	{
		playerStatistics.setCurrentXP(0);
		assertEquals("0%", playerStatistics.getCurrentXPFormatted());
	}

	@Test
	public void testGetLastOnlineTimeFormatted()
	{
		playerStatistics.setLastOnlineTime(1406378311373L);
		assertEquals("2014-07-26 12:38:31 PM", playerStatistics.getLastOnlineTimeFormatted());
	}

	@Test
	public void testGetLifeTicksFormatted()
	{
		playerStatistics.setLifeTicks(0);
		assertEquals("0 seconds", playerStatistics.getLifeTicksFormatted());
	}

	@Test
	public void testGetLastOnlineTimeInSeconds() {
		playerStatistics.setLastOnlineTime(1406378311373L);
		assertEquals(1406378311, playerStatistics.getLastOnlineTimeInSeconds());
	}
}