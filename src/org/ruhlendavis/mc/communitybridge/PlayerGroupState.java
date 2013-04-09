package org.ruhlendavis.mc.communitybridge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Class to hold a player's group membership state
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class PlayerGroupState
{
	private String playerName;
	private String fileName;
	private File playerFolder;

	private String webappPrimaryGroupID;
	private List webappGroupIDs;

	private String permissionsSystemPrimaryGroupName;
	private List permissionsSystemGroupNames;


	public PlayerGroupState(String playerName, File playerDataFolder)
	{
		this.playerName = playerName;
		this.fileName = playerName + ".yml";
		this.playerFolder = playerDataFolder;
		this.webappGroupIDs = new ArrayList();
		this.permissionsSystemGroupNames = new ArrayList();
	}

	public List identifyAdditions(PlayerGroupState newState)
	{
		return new ArrayList();
	}

	public List identifyRemovals(PlayerGroupState newState)
	{
		return new ArrayList();
	}

	public boolean generate()
	{
		return true;
	}

	public boolean load()
	{
		File playerFile = new File(playerFolder, fileName);

		if (playerFile.exists())
		{
			FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
			webappPrimaryGroupID = playerData.getString("webapp.primary-group-id", "");
			webappGroupIDs = playerData.getStringList("webapp.group-ids");
			permissionsSystemPrimaryGroupName = playerData.getString("permissions-system.primary-group-name", "");
			permissionsSystemGroupNames = playerData.getStringList("permissions-system.group-ids");
		}
		return true;
	}

	private boolean save()
	{
		return true;
	}
}
