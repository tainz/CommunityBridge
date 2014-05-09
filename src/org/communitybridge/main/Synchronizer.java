package org.communitybridge.main;

import org.communitybridge.linker.UserPlayerLinker;

public class Synchronizer
{
	private Environment environment;
	protected UserPlayerLinker userPlayerLinker;

	public Synchronizer(Environment environment)
	{
		this.environment = environment;
		this.userPlayerLinker = new UserPlayerLinker(environment);
	}

	protected boolean isValidDirection(String direction, String validDirection)
	{
		return direction.startsWith("two") || direction.startsWith(validDirection);
	}
}
