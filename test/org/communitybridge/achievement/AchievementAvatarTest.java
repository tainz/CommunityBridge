package org.communitybridge.achievement;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.communitybridge.linker.UserPlayerLinker;
import org.communitybridge.main.BukkitWrapper;
import org.communitybridge.configuration.Configuration;
import org.communitybridge.main.Environment;
import org.communitybridge.main.WebApplication;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

public class AchievementAvatarTest
{
	private Environment environment = new Environment();
	private Configuration configuration = mock(Configuration.class);
	private Player player = mock(Player.class);
	private WebApplication webApplication = mock(WebApplication.class);
	private UserPlayerLinker linker = mock(UserPlayerLinker.class);
	private PlayerAchievementState state = new PlayerAchievementState("Player", null);
	private String userID = RandomStringUtils.randomAlphabetic(2);
	private PlayerInventory playerInventory = mock(PlayerInventory.class);
	private PlayerInventory otherInventory = mock(PlayerInventory.class);
	private Server server = mock(Server.class);

	private BukkitWrapper bukkit = mock(BukkitWrapper.class);

	private AchievementAvatar achievement = new AchievementAvatar(environment);

	@Before
	public void beforeEach() {
		achievement.bukkit = bukkit;
		environment.setConfiguration(configuration);
		environment.setUserPlayerLinker(linker);
		configuration.avatarEnabled = true;
		environment.setWebApplication(webApplication);
		achievement.setLimit(1);
		when(bukkit.getServer()).thenReturn(server);
		when(server.createInventory(null, InventoryType.PLAYER)).thenReturn(otherInventory);
		when(player.getInventory()).thenReturn(playerInventory);
		when(playerInventory.getType()).thenReturn(InventoryType.PLAYER);
		when(linker.getUserID(player)).thenReturn(userID);
		when(webApplication.playerHasAvatar(userID)).thenReturn(true);
		Map<Material, Integer> itemRewards = new EnumMap<Material, Integer>(Material.class);
		itemRewards.put(Material.ACACIA_STAIRS, 10);
		itemRewards.put(Material.ACTIVATOR_RAIL, 10);
		achievement.setItemRewards(itemRewards);
	}

	@Test
	public void playerQualifiesReturnsTrue()
	{
		assertTrue(achievement.playerQualifies(player, state));
	}

	@Test
	public void playerQualifiesWithAvatarDisabledReturnsFalse()
	{
		configuration.avatarEnabled = false;
		assertFalse(achievement.playerQualifies(player, state));
	}

	@Test
	public void playerQualifiesWithNoUserIDReturnsFalse()
	{
		when(linker.getUserID(player)).thenReturn("");
		assertFalse(achievement.playerQualifies(player, state));
	}

	@Test
	public void playerQualifiesWithNoAvatarReturnsFalse()
	{
		when(webApplication.playerHasAvatar(userID)).thenReturn(false);
		assertFalse(achievement.playerQualifies(player, state));
	}

	@Test
	public void playerQualifiesOverLimitReturnsFalse()
	{
		achievement.setLimit(-1);
		assertFalse(achievement.playerQualifies(player, state));
	}

	@Test
	public void playerQualifiesWhenTooManyReturnsFalse()
	{
		HashMap<Integer, ItemStack> rejected = new HashMap<Integer, ItemStack>();
		ItemStack stack = new ItemStack(Material.ACACIA_STAIRS, 64);
		rejected.put(0, stack);

		when(server.createInventory(null, InventoryType.PLAYER)).thenReturn(otherInventory);
		when(otherInventory.addItem(any(ItemStack.class))).thenReturn(rejected);

		assertFalse(achievement.playerQualifies(player, state));
	}

	@Test
	public void rewardPlayerRewardsPlayer()
	{
		achievement.rewardPlayer(player, state);
		int rewardCount = achievement.getItemRewards().size();
		verify(playerInventory, times(rewardCount)).addItem(any(ItemStack.class));
	}

	@Test
	public void rewardPlayerIncrementsState()
	{
		int expected = state.getAvatarAchievements() + 1;
		achievement.rewardPlayer(player, state);
		assertEquals(expected, state.getAvatarAchievements());
	}
}