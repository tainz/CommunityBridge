package org.communitybridge.main;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public abstract class Achievement
{
	private int limit;
	private double cashReward;
	private Map<Material, Integer> itemRewards = new EnumMap<Material, Integer>(Material.class);
	
	public abstract boolean playerQualifies(Player player);

	void loadFromYamlPath(YamlConfiguration config, String path)
	{
		limit = config.getInt(path + ".Limit", 1);
		cashReward = config.getDouble(path + ".Money", 0.0);
		
		ConfigurationSection itemsSection = config.getConfigurationSection(path + ".Items");
		
		if (itemsSection == null)
		{
			return;
		}

		Set<String> itemSet = itemsSection.getKeys(false);
		
		for (String key : itemSet)
		{
			Material material = Material.getMaterial(key);
			if (material == null)
			{
				CommunityBridge.log.warning("Invalid material in achievements file");
				continue;
			}
		  int amount = itemsSection.getInt(key, 1);
			itemRewards.put(material, amount);
		}
	}

	public int getLimit()
	{
		return limit;
	}

	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	public double getCashReward()
	{
		return cashReward;
	}

	public void setCashReward(double cashReward)
	{
		this.cashReward = cashReward;
	}

	public Map<Material, Integer> getItemRewards()
	{
		return itemRewards;
	}

	public void setItemRewards(Map<Material, Integer> itemRewards)
	{
		this.itemRewards = itemRewards;
	}
}
