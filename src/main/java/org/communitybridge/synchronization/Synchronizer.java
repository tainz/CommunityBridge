package org.communitybridge.synchronization;

import org.communitybridge.main.Environment;

public class Synchronizer
{
	protected Environment environment;

	public Synchronizer(Environment environment)
	{
		this.environment = environment;
	}

	protected boolean isValidDirection(String direction, String validDirection)
	{
		return direction.startsWith("two") || direction.startsWith(validDirection);
	}
}
