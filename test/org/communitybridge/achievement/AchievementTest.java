package org.communitybridge.achievement;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.communitybridge.main.BukkitWrapper;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AchievementTest
{
	private Player player = mock(Player.class);
	private Environment environment = new Environment();
	private Configuration configuration = mock(Configuration.class);
	private PlayerInventory playerInventory = mock(PlayerInventory.class);
	private PlayerInventory otherInventory = mock(PlayerInventory.class);
	private BukkitWrapper bukkit = mock(BukkitWrapper.class);
	private Server server = mock(Server.class);

	Achievement achievement = new TestableAchievement(environment, bukkit);

	@Before
	public void beforeEach()
	{
		environment.setConfiguration(configuration);
	}

	@Test
	public void rewardPlayerPerformsCashReward()
	{
		Double amount = RandomUtils.nextDouble(10.0, 1000.0);
		configuration.economyEnabled = true;
		Economy economy = mock(Economy.class);
		environment.setEconomy(economy);
		when(economy.depositPlayer(player, amount)).thenReturn(null);
		achievement.setCashReward(amount);
		achievement.rewardPlayer(player, null);
		verify(economy).depositPlayer(player, amount);
	}

	@Test
	public void rewardPlayerNoEconomyNoCashAward()
	{
		Double amount = RandomUtils.nextDouble(10.0, 1000.0);
		configuration.economyEnabled = false;
		Economy economy = mock(Economy.class);
		environment.setEconomy(economy);
		when(economy.depositPlayer(player, amount)).thenReturn(null);
		achievement.setCashReward(amount);
		achievement.rewardPlayer(player, null);
		verifyZeroInteractions(economy);
	}

	@Test
	public void rewardPlayerOneItem()
	{
		configuration.economyEnabled = false;
		Map<Material, Integer> itemRewards = new EnumMap<Material, Integer>(Material.class);
		itemRewards.put(Material.ACACIA_STAIRS, 10);
		achievement.setItemRewards(itemRewards);
		when(player.getInventory()).thenReturn(playerInventory);
		when(playerInventory.addItem(any(ItemStack.class))).thenReturn(null);
		achievement.rewardPlayer(player, null);
		verify(playerInventory).addItem(any(ItemStack.class));
		verify(player).updateInventory();
	}

	@Test
	public void rewardPlayerTwoItems()
	{
		configuration.economyEnabled = false;
		setupRewards();
		when(player.getInventory()).thenReturn(playerInventory);
		when(playerInventory.addItem(any(ItemStack.class))).thenReturn(null);
		achievement.rewardPlayer(player, null);
		verify(playerInventory, times(2)).addItem(any(ItemStack.class));
		verify(player, times(1)).updateInventory();
	}

	@Test
	public void rewardPlayerNoItemDontUpdateInventory()
	{
		configuration.economyEnabled = false;
		achievement.rewardPlayer(player, null);
		verify(player, never()).updateInventory();
	}

	@Test
	public void canRewardAllItemRewardsReturnTrue()
	{
		setupRewards();

		when(bukkit.getServer()).thenReturn(server);
		when(server.createInventory(null, InventoryType.PLAYER)).thenReturn(otherInventory);
		when(player.getInventory()).thenReturn(playerInventory);
		when(playerInventory.getType()).thenReturn(InventoryType.PLAYER);
		assertTrue(achievement.canRewardAllItemRewards(player));
	}

	@Test
	public void canRewardAllItemRewardsReturnFalseWhenTooMany()
	{
		setupRewards();

		HashMap<Integer, ItemStack> rejected = new HashMap<Integer, ItemStack>();
		ItemStack stack = new ItemStack(Material.ACACIA_STAIRS, 64);
		rejected.put(0, stack);

		when(bukkit.getServer()).thenReturn(server);
		when(server.createInventory(null, InventoryType.PLAYER)).thenReturn(otherInventory);
		when(player.getInventory()).thenReturn(playerInventory);
		when(playerInventory.getType()).thenReturn(InventoryType.PLAYER);
		when(otherInventory.addItem(any(ItemStack.class))).thenReturn(rejected);

		assertFalse(achievement.canRewardAllItemRewards(player));
	}

	@Test
	public void loadReadsLimit()
	{
		String path = RandomStringUtils.randomAlphabetic(7);
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		int limit = RandomUtils.nextInt(1,10);
		yamlConfiguration.set(path + ".Limit", limit);
		achievement.load(yamlConfiguration, path);
		assertEquals(limit, achievement.getLimit());
	}

	@Test
	public void loadReadsCashReward()
	{
		String path = RandomStringUtils.randomAlphabetic(7);
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		double cash = RandomUtils.nextDouble(1,10);
		yamlConfiguration.set(path + ".Money", cash);
		achievement.load(yamlConfiguration, path);
		assertEquals(cash, achievement.getCashReward(), 0);
	}

	@Test
	public void loadReadsOneItem()
	{
		String path = RandomStringUtils.randomAlphabetic(7);
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection itemsSection = yamlConfiguration.createSection(path + ".Items");
		Integer expected = RandomUtils.nextInt(1, 10);
		itemsSection.set("EMERALD", expected);
		achievement.load(yamlConfiguration, path);
		Integer actual = achievement.getItemRewards().get(Material.EMERALD);
		assertEquals(expected, actual);
	}

	@Test
	public void loadReadsMultipleItems()
	{
		String path = RandomStringUtils.randomAlphabetic(7);
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection itemsSection = yamlConfiguration.createSection(path + ".Items");
		Integer expectedEmerald = RandomUtils.nextInt(2, 10);
		Integer expectedCoal = RandomUtils.nextInt(2, 25);
		itemsSection.set("EMERALD", expectedEmerald);
		itemsSection.set("COAL", expectedCoal);
		achievement.load(yamlConfiguration, path);
		Integer actual = achievement.getItemRewards().get(Material.EMERALD);
		assertEquals(expectedEmerald, actual);
		actual = achievement.getItemRewards().get(Material.COAL);
		assertEquals(expectedCoal, actual);
	}

	private void setupRewards()
	{
		Map<Material, Integer> itemRewards = new EnumMap<Material, Integer>(Material.class);
		itemRewards.put(Material.ACACIA_STAIRS, 10);
		itemRewards.put(Material.ACTIVATOR_RAIL, 10);
		achievement.setItemRewards(itemRewards);
	}

	public class TestableAchievement extends Achievement
	{
		public TestableAchievement(Environment environment, BukkitWrapper bukkit)
		{
			super(environment);
			this.bukkit = bukkit;
		}

		@Override
		public boolean playerQualifies(Player player, PlayerAchievementState state)
		{
			return false;
		}
	}
}