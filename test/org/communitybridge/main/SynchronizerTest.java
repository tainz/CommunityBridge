package org.communitybridge.main;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class SynchronizerTest
{
	TestableSynchronizer synchronizer = new TestableSynchronizer();

	@Test
	public void isValidDirectionReturnsTrueForTwo()
	{
		assertTrue(synchronizer.isValidDirection("two", "aaa"));
	}

	@Test
	public void isValidDirectionReturnsTrueForWebWhenWeb()
	{
		assertTrue(synchronizer.isValidDirection("web", "web"));
	}

	@Test
	public void isValidDirectionReturnsTrueForMinWhenMin()
	{
		assertTrue(synchronizer.isValidDirection("min", "min"));
	}

	@Test
	public void isValidDirectionReturnsFalseForSomethingElseAgainstMin()
	{
		assertFalse(synchronizer.isValidDirection("aaa" + RandomStringUtils.randomAlphabetic(3), "min"));
	}

	@Test
	public void isValidDirectionReturnsFalseForSomethingElseAgainstWeb()
	{
		assertFalse(synchronizer.isValidDirection("aaa" + RandomStringUtils.randomAlphabetic(3), "web"));
	}

	public class TestableSynchronizer extends Synchronizer
	{
	}
}