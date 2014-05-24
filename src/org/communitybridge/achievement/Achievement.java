package org.communitybridge.achievement;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.communitybridge.main.CommunityBridge;

public abstract class Achievement
{
	protected int limit;
	protected double cashReward;
	protected Map<Material, Integer> itemRewards = new EnumMap<Material, Integer>(Material.class);

	public abstract boolean playerQualifies(Player player, PlayerAchievementState state);

	public void rewardPlayer(Player player, PlayerAchievementState state)
	{
		if (CommunityBridge.config.economyEnabled)
		{
			CommunityBridge.economy.depositPlayer(player.getName(), cashReward);
		}

		for (Entry<Material, Integer> entry : itemRewards.entrySet())
		{
			ItemStack stack = new ItemStack(entry.getKey(), entry.getValue());
			player.getInventory().addItem(stack);
		}
		player.updateInventory();
	}

	protected boolean canRewardAllItemRewards(Player player)
	{
		final Inventory testInventory = Bukkit.getServer().createInventory(null, player.getInventory().getType());
		testInventory.setContents(player.getInventory().getContents());

		for (Entry<Material, Integer> entry : itemRewards.entrySet())
		{
			ItemStack stack = new ItemStack(entry.getKey(), entry.getValue());
			if (!testInventory.addItem(stack).isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	public void loadFromYamlPath(YamlConfiguration config, String path)
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
