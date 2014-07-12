package org.communitybridge.main;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
	{
		String uuid = event.getUniqueId().toString();
		String name = event.getName();
		userPlayerLinker.removeUserIDFromCache(uuid, name);

		String userID = userPlayerLinker.getUserID(uuid, name);
		if (userID.isEmpty())
		{
			preLoginUnregisteredPlayer(event);
		}
		else
		{
			preLoginRegisteredPlayer(userID, event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		if (userPlayerLinker.getUserID(player).isEmpty())
		{
			joinUnregistered(player);
		}
		else
		{
			joinRegistered(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if (configuration.syncDuringQuit)
		{
			webapp.runSynchronizePlayer(event.getPlayer(), false);
		}
	} // onPlayerQuit

	private void preLoginRegisteredPlayer(String userID, AsyncPlayerPreLoginEvent event)
	{
		log.fine(event.getName() + " linked to web application user ID #" + userID + ".");

		if (configuration.avatarEnabled && configuration.requireAvatar && webapp.playerHasAvatar(userID) == false)
		{
			kickPlayer(event, "require-avatar-message");
		}

		if (configuration.postCountEnabled && configuration.requireMinimumPosts && webapp.getUserPostCount(userID) < configuration.requirePostsPostCount)
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

	private void joinUnregistered(Player player)
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

			if (configuration.linkingUnregisterFormerRegistered)
			{
				permissionHandler.removeFromGroup(player, configuration.linkingRegisteredGroup);
			}
		}
	}

	private void joinRegistered(Player player)
	{
		if (configuration.linkingNotifyRegistered)
		{
			String message = ChatColor.GREEN + configuration.messages.get("link-registered-player");
			player.sendMessage(message);
		}

		maybeSwitchToRegistered(player);

		webapp.onJoin(player);
	}

	private void maybeSwitchToRegistered(Player player)
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
}
