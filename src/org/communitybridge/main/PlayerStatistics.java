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

	public String getLastOnlineTimeFormatted()
	{
		return lastOnlineTimeFormatted;
	}

	public void setLastOnlineTime(int lastonlineTime)
	{
		this.lastOnlineTime = lastonlineTime;
	}

	public void setLastOnlineTimeFormatted(String lastonlineFormattedTime)
	{
		this.lastOnlineTimeFormatted = lastonlineFormattedTime;
	}

	public int getGameTime()
	{
		return gameTime;
	}

	public String getGameTimeFormatted()
	{
		return StringUtilities.timeElapsedtoString(getGameTime());
	}

	public void setGameTime(int gameTime)
	{
		this.gameTime = gameTime;
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

	public String getCurrentXPFormatted()
	{
		return ((int)(getCurrentXP() * 100)) + "%";
	}

	public void setCurrentXP(float currentxp)
	{
		this.currentXP = currentxp;
	}

	public double getHealth()
	{
		return health;
	}

	public void setHealth(double health)
	{
		this.health = health;
	}

	public int getLifeTicks()
	{
		return lifeTicks;
	}

	public String getLifeTicksFormatted()
	{
		return StringUtilities.timeElapsedtoString((int)(getLifeTicks() / 20));
	}

	public void setLifeTicks(int lifeticks)
	{
		this.lifeTicks = lifeticks;
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
