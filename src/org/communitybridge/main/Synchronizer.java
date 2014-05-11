package org.communitybridge.main;

public class Synchronizer
{
	@SuppressWarnings("unused")
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
