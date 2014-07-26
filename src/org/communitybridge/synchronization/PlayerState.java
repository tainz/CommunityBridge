package org.communitybridge.synchronization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;

public class PlayerState
{
	private String webappPrimaryGroupID;
	private List<String> webappGroupIDs;

	private String permissionsSystemPrimaryGroupName;
	private List<String> permissionsSystemGroupNames;
	public boolean isNewFile;

	private Environment environment;
	private File playerFolder;
	private Player player;

	private File playerFile;
	private File oldPlayerFile;

	private String userID;

	public PlayerState(Environment environment, File playerFolder, Player player, String userID)
	{
		this.environment = environment;
		this.player = player;
		this.userID = userID;
		this.playerFolder = playerFolder;
		this.playerFile = new File(playerFolder, player.getUniqueId().toString() + ".yml");
		this.oldPlayerFile = new File(playerFolder, player.getName() + ".yml");
		this.webappGroupIDs = new ArrayList<String>();
		this.permissionsSystemGroupNames = new ArrayList<String>();
		this.webappPrimaryGroupID = "";
		this.permissionsSystemPrimaryGroupName = "";
	}

	public void generate()
	{
		webappPrimaryGroupID = environment.getWebApplication().getUserPrimaryGroupID(userID);
		webappGroupIDs = environment.getWebApplication().getUserSecondaryGroupIDs(userID);
		permissionsSystemGroupNames = environment.getPermissionHandler().getGroups(player);

		if (environment.getPermissionHandler().supportsPrimaryGroups())
		{
			permissionsSystemPrimaryGroupName = environment.getPermissionHandler().getPrimaryGroup(player);
		}
		else
		{
			for (String groupName : environment.getConfiguration().simpleSynchronizationGroupsTreatedAsPrimary)
			{
				if (permissionsSystemGroupNames.contains(groupName))
				{
					permissionsSystemPrimaryGroupName = groupName;
					permissionsSystemGroupNames.remove(groupName);
				}
			}
		}
	}

	public void load()
	{
		if (playerFile.exists())
		{
			loadFromFile(playerFile);
		}
		else
		{
			if (oldPlayerFile.exists())
			{
				loadFromFile(oldPlayerFile);
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
	}

	public void save() throws IOException
	{
		FileConfiguration playerData = new YamlConfiguration();
		playerData.set("last-known-name", player.getName());
		playerData.set("webapp.primary-group-id", webappPrimaryGroupID);
		playerData.set("webapp.group-ids", webappGroupIDs);
		playerData.set("permissions-system.primary-group-name", permissionsSystemPrimaryGroupName);
		playerData.set("permissions-system.group-names", permissionsSystemGroupNames);

		playerData.save(playerFile);
	}

	public PlayerState copy()
	{
		PlayerState copy = new PlayerState(environment, playerFolder, player, userID);
		copy.isNewFile = isNewFile;
		copy.setPermissionsSystemGroupNames(permissionsSystemGroupNames);
		copy.setPermissionsSystemPrimaryGroupName(permissionsSystemPrimaryGroupName);
		copy.setWebappGroupIDs(webappGroupIDs);
		copy.setWebappPrimaryGroupID(webappPrimaryGroupID);
		return copy;
	}

	private void loadFromFile(File file)
	{
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);
		permissionsSystemGroupNames = playerData.getStringList("permissions-system.group-names");
		permissionsSystemPrimaryGroupName = playerData.getString("permissions-system.primary-group-name", "");
		webappGroupIDs = playerData.getStringList("webapp.group-ids");
		webappPrimaryGroupID = playerData.getString("webapp.primary-group-id", "");
		isNewFile = false;
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
}
