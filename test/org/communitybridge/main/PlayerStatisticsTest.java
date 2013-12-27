/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.communitybridge.main;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class PlayerStatisticsTest
{
	private	PlayerStatistics playerStatistics = new PlayerStatistics();

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

//	@Test
//	public void testGetLastOnlineTimeFormatted()
//	{
//		System.out.println("getLastOnlineTimeFormatted");
//		PlayerStatistics instance = new PlayerStatistics();
//		String expResult = "";
//		String result = instance.getLastOnlineTimeFormatted();
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}

	@Test
	public void testGetLifeTicksFormatted()
	{
		playerStatistics.setLifeTicks(0);
		assertEquals("0 seconds", playerStatistics.getLifeTicksFormatted());
	}
}