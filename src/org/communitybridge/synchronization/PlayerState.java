package org.communitybridge.synchronization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;
import org.communitybridge.utility.Log;

public class PlayerState
{
	private String webappPrimaryGroupID = "";
	private List<String> webappGroupIDs = new ArrayList<String>();

	private String permissionsSystemPrimaryGroupName = "";
	private List<String> permissionsSystemGroupNames= new ArrayList<String>();

	private double minecraftWallet = 0;
	private double webApplicationWallet = 0;

	private boolean isNewFile;

	private FileConfiguration playerData = new YamlConfiguration();

	public void generate(Environment environment, Player player, String userID)
	{
		if (environment.getConfiguration().economyEnabled && environment.getConfiguration().walletEnabled)
		{
			minecraftWallet = environment.getEconomy().getBalance(player);
			webApplicationWallet = environment.getWebApplication().getBalance(userID);
		}
		if (environment.getConfiguration().groupSynchronizationActive)
		{
			permissionsSystemGroupNames = environment.getPermissionHandler().getGroups(player);
			permissionsSystemPrimaryGroupName = getPrimaryGroupName(player, environment);
			if (environment.getConfiguration().webappSecondaryGroupEnabled)
			{
				webappGroupIDs = environment.getWebApplication().getUserSecondaryGroupIDs(userID);
			}
			if (environment.getConfiguration().webappPrimaryGroupEnabled)
			{
				webappPrimaryGroupID = environment.getWebApplication().getUserPrimaryGroupID(userID);
			}
		}
	}

	public void load(File file)
	{
		if (file.exists())
		{
			playerData = YamlConfiguration.loadConfiguration(file);
			minecraftWallet = playerData.getDouble("minecraft-money", 0.0);
			permissionsSystemGroupNames = playerData.getStringList("permissions-system.group-names");
			permissionsSystemPrimaryGroupName = playerData.getString("permissions-system.primary-group-name", "");
			webappGroupIDs = playerData.getStringList("webapp.group-ids");
			webappPrimaryGroupID = playerData.getString("webapp.primary-group-id", "");
			isNewFile = false;
		}
		else
		{
			isNewFile = true;
			permissionsSystemGroupNames = new ArrayList<String>();
			permissionsSystemPrimaryGroupName = "";
			webappPrimaryGroupID = "";
			webappGroupIDs = new ArrayList<String>();
		}
	}

	public void save(Player player, File file, Log log)
	{
		playerData.set("last-known-name", player.getName());
		playerData.set("minecraft-money", minecraftWallet);
		playerData.set("permissions-system.primary-group-name", permissionsSystemPrimaryGroupName);
		playerData.set("permissions-system.group-names", permissionsSystemGroupNames);
		playerData.set("webapp.primary-group-id", webappPrimaryGroupID);
		playerData.set("webapp.group-ids", webappGroupIDs);

		try
		{
			playerData.save(file);
		}
		catch (IOException exception)
		{
			log.severe("Exception while saving player state for " + player.getName() + ": " + exception.getMessage());
		}
	}

	public PlayerState copy()
	{
		PlayerState copy = new PlayerState();
		copy.isNewFile = isNewFile;
		copy.setMinecraftWallet(minecraftWallet);
		copy.setPermissionsSystemGroupNames(permissionsSystemGroupNames);
		copy.setPermissionsSystemPrimaryGroupName(permissionsSystemPrimaryGroupName);
		copy.setWebappGroupIDs(webappGroupIDs);
		copy.setWebappPrimaryGroupID(webappPrimaryGroupID);
		return copy;
	}

	public String getWebappPrimaryGroupID()
	{
		return webappPrimaryGroupID;
	}

	public void setWebappPrimaryGroupID(String webappPrimaryGroupID)
	{
		this.webappPrimaryGroupID = webappPrimaryGroupID;
	}

	public List<String> getWebappGroupIDs()
	{
		return webappGroupIDs;
	}

	public void setWebappGroupIDs(List<String> webappGroupIDs)
	{
		this.webappGroupIDs = webappGroupIDs;
	}

	public String getPermissionsSystemPrimaryGroupName()
	{
		return permissionsSystemPrimaryGroupName;
	}

	public void setPermissionsSystemPrimaryGroupName(String permissionsSystemPrimaryGroupName)
	{
		this.permissionsSystemPrimaryGroupName = permissionsSystemPrimaryGroupName;
	}

	public List<String> getPermissionsSystemGroupNames()
	{
		return permissionsSystemGroupNames;
	}

	public void setPermissionsSystemGroupNames(List<String> permissionsSystemGroupNames)
	{
		this.permissionsSystemGroupNames = permissionsSystemGroupNames;
	}

	public boolean isIsNewFile()
	{
		return isNewFile;
	}

	public double getMinecraftWallet()
	{
		return minecraftWallet;
	}

	public void setMinecraftWallet(double wallet)
	{
		this.minecraftWallet = wallet;
	}

	public double getWebApplicationWallet()
	{
		return webApplicationWallet;
	}

	public void setWebApplicationWallet(double wallet)
	{
		this.webApplicationWallet = wallet;
	}

	private String getPrimaryGroupName(Player player, Environment environment)
	{
		if (environment.getPermissionHandler().supportsPrimaryGroups())
		{
			return environment.getPermissionHandler().getPrimaryGroup(player);
		}
		else
		{
			for (String groupName : environment.getConfiguration().simpleSynchronizationGroupsTreatedAsPrimary)
			{
				if (permissionsSystemGroupNames.contains(groupName))
				{
					permissionsSystemGroupNames.remove(groupName);
					return groupName;
				}
			}
			return "";
		}
	}
}
