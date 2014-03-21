package org.communitybridge.main;

import java.text.SimpleDateFormat;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlayerStatisticsTest
{
	private	PlayerStatistics playerStatistics = new PlayerStatistics(new SimpleDateFormat());

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

	// TODO: Make this test work on all time zones.
	@Test
	public void testGetLastOnlineTimeFormatted()
	{
		playerStatistics.setLastOnlineTime(0);
		assertEquals("12/31/69 6:00 PM", playerStatistics.getLastOnlineTimeFormatted());
	}

	@Test
	public void testGetLifeTicksFormatted()
	{
		playerStatistics.setLifeTicks(0);
		assertEquals("0 seconds", playerStatistics.getLifeTicksFormatted());
	}
}