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
import org.communitybridge.utility.Log;

public class PlayerListener implements Listener
{
	private Configuration config;
	private Log log;
	private WebApplication webapp;

	private UserPlayerLinker userPlayerLinker;

	public PlayerListener(Environment environment, WebApplication webapp)
	{
		this.config = environment.getConfiguration();
		this.log = environment.getLog();
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
		Player player = Bukkit.getPlayerExact(event.getName());
		userPlayerLinker.removeUserIDFromCache(player);

		if (userPlayerLinker.getUserID(player).isEmpty())
		{
			preLoginUnregisteredPlayer(event);
		}
		else
		{
			preLoginRegisteredPlayer(player, event);
		}
	} // onPlayerPreLogin

	/**
	 * This method is called by CraftBukkit as the player joins the server.
	 *
	 * @param PlayerJoinEvent The event object (see CraftBukkit API).
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		String playerName = player.getName();

		if (webapp.isPlayerRegistered(playerName))
		{
			if (config.linkingNotifyRegistered)
			{
				String message = ChatColor.GREEN + config.messages.get("link-registered-player");
				player.sendMessage(message);
			}

			if (!config.groupSynchronizationActive && !config.linkingRegisteredGroup.isEmpty())
			{
				maybeSwitchToRegistered(playerName, player);
			}
			webapp.onJoin(player);
		}
		else
		{
			if (config.linkingNotifyUnregistered)
			{
				String message = ChatColor.RED + config.messages.get("link-unregistered-player");
				player.sendMessage(message);
			} // if config.linkingNotifyUnregistered

			if (!config.linkingUnregisteredGroup.isEmpty())
			{
				CommunityBridge.permissionHandler.addToGroup(playerName, config.linkingUnregisteredGroup);
				if (config.linkingNotifyPlayerGroup)
				{
					String message = ChatColor.RED + config.messages.get("link-notify-player-group-change");
					message = message.replace("~GROUPNAME~", config.linkingUnregisteredGroup);
					player.sendMessage(message);
				}
			}
		} // if isPlayerRegistered

	}

	/**
	 * This method is called by CraftBukkit when a player quits/disconnects.
	 *
	 * @param PlayerQuitEvent The event object (see CraftBukkit API).
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		webapp.onQuit(event.getPlayer());
	} // onPlayerQuit

	/**
	 * Checks if changing the player's group from the unregistered group to
	 * the registered group is merited and does so if it is.
	 *
	 * @param playerName - name of the player to check and possibly move.
	 * @param player - player object representing the player.
	 */
	private void maybeSwitchToRegistered(String playerName, Player player)
	{
		// We don't use the linking registered group if it is empty or group
		// synchronization is active.
		if (config.groupSynchronizationActive || config.linkingRegisteredGroup.isEmpty())
		{
			return;
		}

		// if this rule is turned on, we won't change groups unless they're
		// a member of the unregistered group or they have no groups.
		if (config.linkingRegisteredFormerUnregisteredOnly && !CommunityBridge.permissionHandler.isMemberOfGroup(playerName, config.linkingUnregisteredGroup) && CommunityBridge.permissionHandler.getGroupsPure(playerName).length != 0)
		{
			return;
		}

		CommunityBridge.permissionHandler.switchGroup(playerName, config.linkingUnregisteredGroup, config.linkingRegisteredGroup);

		if (config.linkingNotifyPlayerGroup)
		{
			String message = ChatColor.RED + config.messages.get("link-notify-player-group-change");
			message = message.replace("~GROUPNAME~", config.linkingRegisteredGroup);
			player.sendMessage(message);
		}
	}

	private void preLoginRegisteredPlayer(Player player, AsyncPlayerPreLoginEvent event)
	{
		log.fine(player.getName() + " linked to web application user ID #" + userPlayerLinker.getUserID(player) + ".");

		if (config.avatarEnabled && config.requireAvatar && webapp.playerHasAvatar(player.getName()) == false)
		{
			kickPlayer(event, "require-avatar-message");
		}

		if (config.postCountEnabled && config.requireMinimumPosts && webapp.getUserPostCount(player.getName()) < config.requirePostsPostCount)
		{
			kickPlayer(event, "require-minimum-posts-message");
		}
	}

	private void preLoginUnregisteredPlayer(AsyncPlayerPreLoginEvent event)
	{
		if (config.linkingKickUnregistered)
		{
			event.setKickMessage(config.messages.get("link-unregistered-player"));
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
		}

		if (config.requireAvatar)
		{
			kickPlayer(event, "require-avatar-message");
		}

		if (config.requireMinimumPosts)
		{
			kickPlayer(event, "require-minimum-posts-message");
		}
	}

	private void kickPlayer(AsyncPlayerPreLoginEvent event, String messageKey)
	{
		event.setKickMessage(config.messages.get(messageKey));
		event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
	}
} // PlayerListener class
