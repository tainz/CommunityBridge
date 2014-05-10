package org.communitybridge.main;

public class Synchronizer
{
	private Environment environment;

	public Synchronizer(Environment environment)
	{
		this.environment = environment;
	}

	protected boolean isValidDirection(String direction, String validDirection)
	{
		return direction.startsWith("two") || direction.startsWith(validDirection);
	}
}
