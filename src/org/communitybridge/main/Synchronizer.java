package org.communitybridge.main;

abstract public class Synchronizer
{
	protected boolean isValidDirection(String direction, String validDirection)
	{
		return direction.startsWith("two") || direction.startsWith(validDirection);
	}
}
