package org.ruhlendavis.mc.communitybridge;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ruhlendavis.mc.utility.Log;

public class PlayerListener implements Listener
{
	private Configuration config;
	private Log log;
	private WebApplication webapp;


	/**
	 * Constructor
	 *
	 * @param Log The log object passed in from onEnable().
	 * @param Configuration The configuration object passed in from onEnable().
	 * @param WebApplication The web application object passed in from onEnable().
	 */
	public PlayerListener(Log log, Configuration config, WebApplication webapp)
	{
		this.config = config;
		this.log = log;
		this.webapp = webapp;
	}

	/**
	 * This method is called by CraftBukkit as the player connects to the server.
	 * We perform the initial linking here so that we can reject the login if
	 * linking-kick-unregistered is turned on.
	 *
	 * @param AsyncPlayerPreLoginEvent The event object (see CraftBukkit API).
	 */
	@EventHandler
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
	{
		String playerName = event.getName();
		webapp.loadUserIDfromDatabase(playerName);

		if (webapp.isPlayerRegistered(playerName))
		{
			log.fine(playerName + " linked to web application user ID #" + webapp.getUserID(playerName) + ".");

			if (config.requireAvatar && webapp.playerHasAvatar(playerName) == false)
			{
				event.setKickMessage(config.messages.get("require-avatar-message"));
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			}

			if (config.requireMinimumPosts && webapp.getUserPostCount(playerName) < config.requirePostsPostCount)
			{
				event.setKickMessage(config.messages.get("require-minimum-posts-message"));
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			}
		} // if isPlayerRegistered
		else
		{
			if (config.linkingKickUnregistered)
			{
				event.setKickMessage(config.messages.get("link-unregistered-player"));
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
			} // if config.linkingKickUnregistered

			if (config.requireAvatar)
			{
				event.setKickMessage(config.messages.get("require-avatar-message"));
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			}

			if (config.requireMinimumPosts)
			{
				event.setKickMessage(config.messages.get("require-minimum-posts-message"));
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			}
		} // if isPlayerRegistered
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
				event.setJoinMessage(message);
			}

			if (!config.groupSynchronizationActive && !config.linkingRegisteredGroup.isEmpty())
			{
				if (!config.linkingRegisteredFormerUnregisteredOnly || CommunityBridge.permissionHandler.isMemberOfGroup(playerName, config.linkingUnregisteredGroup) || CommunityBridge.permissionHandler.getGroupsPure(playerName).length == 0)
				{
					CommunityBridge.permissionHandler.removeFromGroup(playerName, config.linkingUnregisteredGroup);
					CommunityBridge.permissionHandler.addToGroup(playerName, config.linkingRegisteredGroup);
					if (config.linkingNotifyPlayerGroup)
					{
						String message = ChatColor.RED + config.messages.get("link-notify-player-group-change");
						message = message.replace("~GROUPNAME~", config.linkingRegisteredGroup);
						player.sendMessage(message);
					}
				}
			}
			webapp.onJoin(player);
		}
		else
		{
			if (config.linkingNotifyUnregistered)
			{
				String message = ChatColor.RED + config.messages.get("link-unregistered-player");
				event.setJoinMessage(message);
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
} // PlayerListener class
