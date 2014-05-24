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
	private File playerFolder;
	private String name;
	private String uuid;

	private File playerFile;
	private File oldPlayerFile;

	public String webappPrimaryGroupID;
	public List<String> webappGroupIDs;

	public String permissionsSystemPrimaryGroupName;
	public List<String> permissionsSystemGroupNames;

	public boolean isNewFile;

	public PlayerGroupState(File playerFolder, String uuid, String name)
	{
		this.name = name;
		this.uuid = uuid;
		this.playerFolder = playerFolder;
		this.playerFile = new File(playerFolder, uuid + ".yml");
		this.oldPlayerFile = new File(playerFolder, name + ".yml");
		this.webappGroupIDs = new ArrayList<String>();
		this.permissionsSystemGroupNames = new ArrayList<String>();
		this.webappPrimaryGroupID = "";
		this.permissionsSystemPrimaryGroupName = "";
	}

	public void generate()
	{
		webappPrimaryGroupID = CommunityBridge.webapp.getUserPrimaryGroupID(name);
		webappGroupIDs = CommunityBridge.webapp.getUserSecondaryGroupIDs(name);
		permissionsSystemGroupNames = new ArrayList<String>(Arrays.asList(CommunityBridge.permissionHandler.getGroups(name)));

		if (CommunityBridge.permissionHandler.supportsPrimaryGroups())
		{
			permissionsSystemPrimaryGroupName = CommunityBridge.permissionHandler.getPrimaryGroup(name);
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
		playerData.set("last-known-name", name);
		playerData.set("webapp.primary-group-id", webappPrimaryGroupID);
		playerData.set("webapp.group-ids", webappGroupIDs);
		playerData.set("permissions-system.primary-group-name", permissionsSystemPrimaryGroupName);
		playerData.set("permissions-system.group-names", permissionsSystemGroupNames);

		playerData.save(playerFile);
	}

	public PlayerGroupState copy()
	{
		PlayerGroupState copy = new PlayerGroupState(playerFolder, uuid, name);
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
