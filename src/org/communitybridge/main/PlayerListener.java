package org.communitybridge.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.communitybridge.linker.UserPlayerLinker;
import org.communitybridge.permissionhandlers.PermissionHandler;
import org.communitybridge.utility.Log;

public class PlayerListener implements Listener
{
	private Configuration configuration;
	private PermissionHandler permissionHandler;
	private Log log;
	private WebApplication webapp;

	private UserPlayerLinker userPlayerLinker;

	public PlayerListener(Environment environment, WebApplication webapp)
	{
		this.configuration = environment.getConfiguration();
		this.log = environment.getLog();
		this.permissionHandler = environment.getPermissionHandler();
		this.webapp = webapp;
		this.userPlayerLinker = environment.getUserPlayerLinker();
	}

	/**
	 * This method is called by CraftBukkit as the player connects to the server.
	 * We perform the initial linking here so that we can reject the login if
	 * linking-kick-unregistered is turned on.
	 */
	@EventHandler
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
	{
		Player player = Bukkit.getPlayer(event.getUniqueId());
		userPlayerLinker.removeUserIDFromCache(player);

		if (userPlayerLinker.getUserID(player).isEmpty())
		{
			preLoginUnregisteredPlayer(event);
		}
		else
		{
			preLoginRegisteredPlayer(player, event);
		}
	}

	/**
	 * This method is called by CraftBukkit as the player joins the server.
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		String playerName = player.getName();

		if (userPlayerLinker.getUserID(player).isEmpty())
		{
			joinRegisteredPlayer(player, playerName);
		}
		else
		{
			joinUnregisteredPlayer(player, playerName);
		}
	}

	/**
	 * This method is called by CraftBukkit when a player quits/disconnects.
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if (configuration.syncDuringQuit)
		{
			webapp.runSynchronizePlayer(event.getPlayer(), false);
		}
	} // onPlayerQuit

	/**
	 * Checks if changing the player's group from the unregistered group to
	 * the registered group is merited and does so if it is.
	 */
	private void maybeSwitchToRegistered(String playerName, Player player)
	{
		// We don't use the linking registered group if it is empty or group
		// synchronization is active.
		if (configuration.groupSynchronizationActive || configuration.linkingRegisteredGroup.isEmpty())
		{
			return;
		}

		// if this rule is turned on, we won't change groups unless they're
		// a member of the unregistered group or they have no groups.
		if (configuration.linkingRegisteredFormerUnregisteredOnly && !permissionHandler.isMemberOfGroup(player, configuration.linkingUnregisteredGroup) && !permissionHandler.getGroupsPure(player).isEmpty())
		{
			return;
		}

		permissionHandler.switchGroup(player, configuration.linkingUnregisteredGroup, configuration.linkingRegisteredGroup);

		if (configuration.linkingNotifyPlayerGroup)
		{
			String message = ChatColor.RED + configuration.messages.get("link-notify-player-group-change");
			message = message.replace("~GROUPNAME~", configuration.linkingRegisteredGroup);
			player.sendMessage(message);
		}
	}

	private void preLoginRegisteredPlayer(Player player, AsyncPlayerPreLoginEvent event)
	{
		log.fine(player.getName() + " linked to web application user ID #" + userPlayerLinker.getUserID(player) + ".");

		if (configuration.avatarEnabled && configuration.requireAvatar && webapp.playerHasAvatar(player) == false)
		{
			kickPlayer(event, "require-avatar-message");
		}

		if (configuration.postCountEnabled && configuration.requireMinimumPosts && webapp.getUserPostCount(player) < configuration.requirePostsPostCount)
		{
			kickPlayer(event, "require-minimum-posts-message");
		}
	}

	private void preLoginUnregisteredPlayer(AsyncPlayerPreLoginEvent event)
	{
		if (configuration.linkingKickUnregistered)
		{
			event.setKickMessage(configuration.messages.get("link-unregistered-player"));
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
		}

		if (configuration.requireAvatar)
		{
			kickPlayer(event, "require-avatar-message");
		}

		if (configuration.requireMinimumPosts)
		{
			kickPlayer(event, "require-minimum-posts-message");
		}
	}

	private void kickPlayer(AsyncPlayerPreLoginEvent event, String messageKey)
	{
		event.setKickMessage(configuration.messages.get(messageKey));
		event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
	}

	private void joinRegisteredPlayer(Player player, String playerName)
	{
		if (configuration.linkingNotifyUnregistered)
		{
			String message = ChatColor.RED + configuration.messages.get("link-unregistered-player");
			player.sendMessage(message);
		} // if config.linkingNotifyUnregistered

		if (!configuration.linkingUnregisteredGroup.isEmpty())
		{
			permissionHandler.addToGroup(player, configuration.linkingUnregisteredGroup);
			if (configuration.linkingNotifyPlayerGroup)
			{
				String message = ChatColor.RED + configuration.messages.get("link-notify-player-group-change");
				message = message.replace("~GROUPNAME~", configuration.linkingUnregisteredGroup);
				player.sendMessage(message);
			}
		}
	}

	private void joinUnregisteredPlayer(Player player, String playerName)
	{
		if (configuration.linkingNotifyRegistered)
		{
			String message = ChatColor.GREEN + configuration.messages.get("link-registered-player");
			player.sendMessage(message);
		}

		if (!configuration.groupSynchronizationActive && !configuration.linkingRegisteredGroup.isEmpty())
		{
			maybeSwitchToRegistered(playerName, player);
		}
		webapp.onJoin(player);
	}
}
