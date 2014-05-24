package org.communitybridge.groupsynchronizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.communitybridge.main.CommunityBridge;

public class PlayerGroupState
{
	public String webappPrimaryGroupID;
	public List<String> webappGroupIDs;

	public String permissionsSystemPrimaryGroupName;
	public List<String> permissionsSystemGroupNames;
	public boolean isNewFile;

	private File playerFolder;
	private Player player;

	private File playerFile;
	private File oldPlayerFile;

	private String userID;

	public PlayerGroupState(File playerFolder, Player player, String userID)
	{
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
		webappPrimaryGroupID = CommunityBridge.webapp.getUserPrimaryGroupID(userID);
		webappGroupIDs = CommunityBridge.webapp.getUserSecondaryGroupIDs(userID);
		permissionsSystemGroupNames = CommunityBridge.permissionHandler.getGroups(player);

		if (CommunityBridge.permissionHandler.supportsPrimaryGroups())
		{
			permissionsSystemPrimaryGroupName = CommunityBridge.permissionHandler.getPrimaryGroup(player);
		}
		else
		{
			for (String groupName : CommunityBridge.config.simpleSynchronizationGroupsTreatedAsPrimary)
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
				webappPrimaryGroupID = "";
				webappGroupIDs = new ArrayList<String>();
				permissionsSystemPrimaryGroupName = "";
				permissionsSystemGroupNames = new ArrayList<String>();
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

	public PlayerGroupState copy()
	{
		PlayerGroupState copy = new PlayerGroupState(playerFolder, player, userID);
		copy.isNewFile = isNewFile;
		copy.permissionsSystemGroupNames.addAll(permissionsSystemGroupNames);
		copy.permissionsSystemPrimaryGroupName = permissionsSystemPrimaryGroupName;
		copy.webappGroupIDs.addAll(webappGroupIDs);
		copy.webappPrimaryGroupID = webappPrimaryGroupID;
		return copy;
	}

	private void loadFromFile(File file)
	{
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);
		webappPrimaryGroupID = playerData.getString("webapp.primary-group-id", "");
		webappGroupIDs = playerData.getStringList("webapp.group-ids");
		permissionsSystemPrimaryGroupName = playerData.getString("permissions-system.primary-group-name", "");
		permissionsSystemGroupNames = playerData.getStringList("permissions-system.group-names");
		isNewFile = false;
	}
}
