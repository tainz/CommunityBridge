package org.communitybridge.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerGroupState
{
	private String playerName;
	private String fileName;
	private File playerFolder;

	public String webappPrimaryGroupID;
	public List<String> webappGroupIDs;

	public String permissionsSystemPrimaryGroupName;
	public List<String> permissionsSystemGroupNames;

	public boolean isNewFile;

	public PlayerGroupState(String playerName, File playerDataFolder)
	{
		this.playerName = playerName;
		this.fileName = playerName + ".yml";
		this.playerFolder = playerDataFolder;
		this.webappGroupIDs = new ArrayList<String>();
		this.permissionsSystemGroupNames = new ArrayList<String>();
	}

	public void generate()
	{
		webappPrimaryGroupID = CommunityBridge.webapp.getUserPrimaryGroupID(playerName);
		webappGroupIDs = CommunityBridge.webapp.getUserSecondaryGroupIDs(playerName);
		permissionsSystemGroupNames = new ArrayList<String>(Arrays.asList(CommunityBridge.permissionHandler.getGroups(playerName)));

		if (CommunityBridge.permissionHandler.supportsPrimaryGroups())
		{
			permissionsSystemPrimaryGroupName = CommunityBridge.permissionHandler.getPrimaryGroup(playerName);
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
		File playerFile = new File(playerFolder, fileName);

		if (playerFile.exists())
		{
			FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
			webappPrimaryGroupID = playerData.getString("webapp.primary-group-id", "");
			webappGroupIDs = playerData.getStringList("webapp.group-ids");
			permissionsSystemPrimaryGroupName = playerData.getString("permissions-system.primary-group-name", "");
			permissionsSystemGroupNames = playerData.getStringList("permissions-system.group-names");
			isNewFile = false;
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

	public void save() throws IOException
	{
		File playerFile = new File(playerFolder, fileName);

		FileConfiguration playerData = new YamlConfiguration();
		playerData.set("webapp.primary-group-id", webappPrimaryGroupID);
		playerData.set("webapp.group-ids", webappGroupIDs);
		playerData.set("permissions-system.primary-group-name", permissionsSystemPrimaryGroupName);
		playerData.set("permissions-system.group-names", permissionsSystemGroupNames);

		playerData.save(playerFile);
	}

	public PlayerGroupState copy()
	{
		PlayerGroupState copy = new PlayerGroupState(playerName, playerFolder);
		copy.isNewFile = isNewFile;
		copy.permissionsSystemGroupNames.addAll(permissionsSystemGroupNames);
		copy.permissionsSystemPrimaryGroupName = permissionsSystemPrimaryGroupName;
		copy.webappGroupIDs.addAll(webappGroupIDs);
		copy.webappPrimaryGroupID = webappPrimaryGroupID;
		return copy;
	}
}
