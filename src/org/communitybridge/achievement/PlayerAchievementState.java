package org.communitybridge.achievement;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.communitybridge.main.CommunityBridge;

public class PlayerAchievementState
{
	String playerName;
	String fileName;
	File playerFolder;
	
	private int avatarAchievements;
	private Map<String, Integer> groupAchievements = new HashMap<String, Integer>();
	private Map<String, Integer> postCountAchievements = new HashMap<String, Integer>();
	private Map<SectionPostCountTuple, Integer> sectionPostCountAchievements = new HashMap<SectionPostCountTuple, Integer>();
	
	public PlayerAchievementState(String playerName, File playerDataFolder)
	{
		this.playerName = playerName;
		this.fileName = playerName + ".achievements.yml";
		this.playerFolder = playerDataFolder;
		this.avatarAchievements = 0;
	}
	
	public void load()
	{
		File playerFile = new File(playerFolder, fileName);

		if (playerFile.exists())
		{
			YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
			
			avatarAchievements = playerData.getInt("avatar");
			
			loadGroupAchievementCounts(playerData);
			loadPostCountAchievementCounts(playerData);
			loadSectionPostCountAchievementCounts(playerData);
		}
	}
	
	public void save()
	{
		File playerFile = new File(playerFolder, fileName);

		FileConfiguration playerData = new YamlConfiguration();
		
		playerData.set("avatar", avatarAchievements);
		
		saveGroupAchievements(playerData);
		savePostCountAchievements(playerData);
		saveSectionPostCountAchievements(playerData);
		try
		{
			playerData.save(playerFile);
		}
		catch (IOException exception)
		{
			CommunityBridge.log.severe("Error when saving achivement data for player:" + exception.getMessage());
		}
	}

	private void loadGroupAchievementCounts(YamlConfiguration playerData)
	{
		ConfigurationSection groupsSection = playerData.getConfigurationSection("groups");
		Set<String> groupNames = groupsSection.getKeys(false);
		
		for (String groupName : groupNames)
		{
			int count = groupsSection.getInt(groupName);
			groupAchievements.put(groupName, count);
		}
	}

	private void loadPostCountAchievementCounts(YamlConfiguration playerData) throws NumberFormatException
	{
		ConfigurationSection postCountSection = playerData.getConfigurationSection("post-counts");
		Set<String> postCounts = postCountSection.getKeys(false);
		
		for (String postCount : postCounts)
		{
			int count = postCountSection.getInt(postCount);
			postCountAchievements.put(postCount, count);
		}
	}

	private void loadSectionPostCountAchievementCounts(YamlConfiguration playerData) throws NumberFormatException
	{
		ConfigurationSection sectionPostCountSection = playerData.getConfigurationSection("section-post-counts");
		Set<String> sectionIDs = sectionPostCountSection.getKeys(false);
		for (String sectionID : sectionIDs)
		{
			ConfigurationSection postCountSection = sectionPostCountSection.getConfigurationSection(sectionID);
			Set<String> postCounts = postCountSection.getKeys(false);
		
			for (String postCount : postCounts)
			{
				SectionPostCountTuple sectionPostCount = new SectionPostCountTuple(sectionID, Integer.parseInt(postCount));
				int count = postCountSection.getInt(postCount);
				sectionPostCountAchievements.put(sectionPostCount, count);
			}
		}
	}

	private void saveGroupAchievements(FileConfiguration playerData)
	{
		for (Entry<String, Integer>  entry : groupAchievements.entrySet())
		{
			playerData.set("groups." + entry.getKey(), entry.getValue());
		}
	}

	private void savePostCountAchievements(FileConfiguration playerData)
	{
		for (Entry<String, Integer>  entry : postCountAchievements.entrySet())
		{
			playerData.set("post-counts." + entry.getKey(), entry.getValue());
		}
	}

	private void saveSectionPostCountAchievements(FileConfiguration playerData)
	{
		for (Entry<SectionPostCountTuple, Integer>  entry : sectionPostCountAchievements.entrySet())
		{
			String path = "section-post-counts." + entry.getKey().getSectionID() + "." + entry.getKey().getPostCount();
			playerData.set(path, entry.getValue());
		}
	}

	public void avatarIncrement()
	{
		avatarAchievements++;
	}
	
	public void groupIncrement(String groupName)
	{
		Integer count = groupAchievements.get(groupName);
		
		if (count == null)
		{
			count = new Integer(1);
		}
		else
		{
			count++;
		}
		
		groupAchievements.put(groupName, count);
	}
	
	public void postCountIncrement(String postCount)
	{
		Integer count = postCountAchievements.get(postCount);
		
		if (count == null)
		{
			count = new Integer(1);
		}
		else
		{
			count++;
		}
		
		postCountAchievements.put(postCount, count);
	}
	
	public void sectionPostCountIncrement(String sectionID, int postCount)
	{
		SectionPostCountTuple spt = new SectionPostCountTuple(sectionID, postCount);
		Integer count = sectionPostCountAchievements.get(spt);
		
		if (count == null)
		{
			count = new Integer(1);
		}
		else
		{
			count++;
		}
		
		sectionPostCountAchievements.put(spt, count);
	}

	public int getAvatarAchievements()
	{
		return avatarAchievements;
	}

	public Integer getGroupAchievement(String groupName)
	{
		return groupAchievements.get(groupName);
	}

	public Integer getPostCountAchievements(String postCount)
	{
		return postCountAchievements.get(postCount);
	}

	public Integer getSectionPostCountAchievements(String sectionID, int postCount)
	{
		SectionPostCountTuple spt = new SectionPostCountTuple(sectionID, postCount);
		return sectionPostCountAchievements.get(spt);
	}
}
