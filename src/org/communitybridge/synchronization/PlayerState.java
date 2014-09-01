package org.communitybridge.synchronization;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;
import org.communitybridge.synchronization.dao.MoneyDao;
import org.communitybridge.utility.Log;

public class PlayerState implements Cloneable
{
	private String webappPrimaryGroupID = "";
	private List<String> webappGroupIDs = new ArrayList<String>();

	private String permissionsSystemPrimaryGroupName = "";
	private List<String> permissionsSystemGroupNames= new ArrayList<String>();

	private boolean moneyConfigurationChanged;
	private String moneyConfigurationState = "";
	private double minecraftMoney = 0;
	private double webApplicationMoney = 0;

	private boolean newFile;

	private FileConfiguration playerData = new YamlConfiguration();
	private MoneyDao money = new MoneyDao();

	public void generate(Environment environment, Player player, String userId) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		if (environment.getConfiguration().economyEnabled && environment.getConfiguration().getMoney().isEnabled())
		{
			moneyConfigurationState = environment.getConfiguration().getMoney().getConfigurationState();
			minecraftMoney = environment.getEconomy().getBalance(player);
			webApplicationMoney = money.getBalance(environment, userId);
		}
		if (environment.getConfiguration().groupSynchronizationActive)
		{
			permissionsSystemGroupNames = environment.getPermissionHandler().getGroups(player);
			permissionsSystemPrimaryGroupName = getPrimaryGroupName(player, environment);
			if (environment.getConfiguration().webappSecondaryGroupEnabled)
			{
				webappGroupIDs = environment.getWebApplication().getUserSecondaryGroupIDs(userId);
			}
			if (environment.getConfiguration().webappPrimaryGroupEnabled)
			{
				webappPrimaryGroupID = environment.getWebApplication().getUserPrimaryGroupID(userId);
			}
		}
	}

	public void load(File file)
	{
		if (file.exists())
		{
			playerData = YamlConfiguration.loadConfiguration(file);
			permissionsSystemGroupNames = playerData.getStringList("permissions-system.group-names");
			permissionsSystemPrimaryGroupName = playerData.getString("permissions-system.primary-group-name", "");
			webappGroupIDs = playerData.getStringList("webapp.group-ids");
			webappPrimaryGroupID = playerData.getString("webapp.primary-group-id", "");
			newFile = false;

			moneyConfigurationState = playerData.getString("money.configuration-state", "");
			minecraftMoney = playerData.getDouble("money.minecraft", 0.0);
			webApplicationMoney = playerData.getDouble("money.web-application", 0.0);
		}
		else
		{
			newFile = true;
			moneyConfigurationChanged = true;
			moneyConfigurationState = "";
			minecraftMoney = 0.0;
			webApplicationMoney = 0.0;
			permissionsSystemGroupNames = new ArrayList<String>();
			permissionsSystemPrimaryGroupName = "";
			webappPrimaryGroupID = "";
			webappGroupIDs = new ArrayList<String>();
		}
	}

	public void save(Player player, File file, Log log)
	{
		playerData.set("last-known-name", player.getName());
		playerData.set("permissions-system.primary-group-name", permissionsSystemPrimaryGroupName);
		playerData.set("permissions-system.group-names", permissionsSystemGroupNames);
		playerData.set("webapp.primary-group-id", webappPrimaryGroupID);
		playerData.set("webapp.group-ids", webappGroupIDs);
		playerData.set("money.configuration-state", moneyConfigurationState);
		playerData.set("money.minecraft", minecraftMoney);
		playerData.set("money.web-application", webApplicationMoney);

		try
		{
			playerData.save(file);
		}
		catch (IOException exception)
		{
			log.severe("Exception while saving player state for " + player.getName() + ": " + exception.getMessage());
		}
	}

	@Override
	public PlayerState clone()
	{
		PlayerState copy = new PlayerState();

		copy.setMoneyConfigurationChanged(moneyConfigurationChanged);
		copy.setMoneyConfigurationState(moneyConfigurationState);
		copy.setMinecraftMoney(minecraftMoney);
		copy.setWebApplicationMoney(webApplicationMoney);

		copy.setNewFile(newFile);
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

	public boolean isNewFile()
	{
		return newFile;
	}

	public void setNewFile(boolean newFile)
	{
		this.newFile = newFile;
	}

	public double getMinecraftMoney()
	{
		return minecraftMoney;
	}

	public void setMinecraftMoney(double wallet)
	{
		this.minecraftMoney = wallet;
	}

	public double getWebApplicationMoney()
	{
		return webApplicationMoney;
	}

	public void setWebApplicationMoney(double wallet)
	{
		this.webApplicationMoney = wallet;
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

	public boolean hasMoneyConfigurationChanged()
	{
		return moneyConfigurationChanged;
	}

	public void setMoneyConfigurationChanged(boolean moneyConfigurationChanged)
	{
		this.moneyConfigurationChanged = moneyConfigurationChanged;
	}

	public String getMoneyConfigurationState()
	{
		return moneyConfigurationState;
	}

	public void setMoneyConfigurationState(String moneyConfigurationState)
	{
		this.moneyConfigurationState = moneyConfigurationState;
	}
}
