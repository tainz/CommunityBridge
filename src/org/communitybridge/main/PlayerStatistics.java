package org.communitybridge.main;

import org.communitybridge.utility.StringUtilities;

public class PlayerStatistics
{
	private String userID;
	private String onlineStatus;
	private int lastOnlineTime;
	private String lastOnlineTimeFormatted;
	private int gameTime;
	private int level;
	private int totalXP;
	private float currentXP;
	private String currentXPFormatted;
	private double health;
	private int lifeTicks;
	private String lifeTicksFormatted;
	private double wallet;

	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}

	public String getOnlineStatus()
	{
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus)
	{
		this.onlineStatus = onlineStatus;
	}

	public int getLastOnlineTime()
	{
		return lastOnlineTime;
	}

	public void setLastOnlineTime(int lastonlineTime)
	{
		this.lastOnlineTime = lastonlineTime;
	}

	public String getLastOnlineTimeFormatted()
	{
		return lastOnlineTimeFormatted;
	}

	public void setLastOnlineTimeFormatted(String lastonlineFormattedTime)
	{
		this.lastOnlineTimeFormatted = lastonlineFormattedTime;
	}

	public int getGameTime()
	{
		return gameTime;
	}

	public void setGameTime(int gameTime)
	{
		this.gameTime = gameTime;
	}

	public String getGameTimeFormatted()
	{
		return StringUtilities.timeElapsedtoString(getGameTime());
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public int getTotalXP()
	{
		return totalXP;
	}

	public void setTotalXP(int totalxp)
	{
		this.totalXP = totalxp;
	}

	public float getCurrentXP()
	{
		return currentXP;
	}

	public void setCurrentXP(float currentxp)
	{
		this.currentXP = currentxp;
	}

	public String getCurrentXPFormatted()
	{
		return ((int)(getCurrentXP() * 100)) + "%";
	}

	public double getHealth()
	{
		return health;
	}

	public void setHealth(double health)
	{
		this.health = health;
	}

	public int getLifeticks()
	{
		return lifeTicks;
	}

	public void setLifeticks(int lifeticks)
	{
		this.lifeTicks = lifeticks;
	}

	public String getLifeTicksFormatted()
	{
		return lifeTicksFormatted;
	}

	public void setLifeTicksFormatted(String lifeticksFormatted)
	{
		this.lifeTicksFormatted = lifeticksFormatted;
	}

	public double getWallet()
	{
		return wallet;
	}

	public void setWallet(double wallet)
	{
		this.wallet = wallet;
	}
}
