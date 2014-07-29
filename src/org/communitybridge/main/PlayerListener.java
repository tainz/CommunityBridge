package org.communitybridge.main;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
	private Environment environment;

	public PlayerListener(Environment environment)
	{
		this.environment = environment;
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
		environment.getUserPlayerLinker().removeUserIDFromCache(uuid, name);

		String userID = environment.getUserPlayerLinker().getUserID(uuid, name);
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

		if (environment.getUserPlayerLinker().getUserID(player).isEmpty())
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
		if (environment.getConfiguration().syncDuringQuit)
		{
			environment.getWebApplication().runSynchronizePlayer(event.getPlayer(), false);
		}
	} // onPlayerQuit

	private void preLoginRegisteredPlayer(String userID, AsyncPlayerPreLoginEvent event)
	{
		environment.getLog().fine(event.getName() + " linked to web application user ID #" + userID + ".");

		if (environment.getConfiguration().avatarEnabled && environment.getConfiguration().requireAvatar && environment.getWebApplication().playerHasAvatar(userID) == false)
		{
			kickPlayer(event, "require-avatar-message");
		}

		if (environment.getConfiguration().postCountEnabled && environment.getConfiguration().requireMinimumPosts && environment.getWebApplication().getUserPostCount(userID) < environment.getConfiguration().requirePostsPostCount)
		{
			kickPlayer(event, "require-minimum-posts-message");
		}
	}

	private void preLoginUnregisteredPlayer(AsyncPlayerPreLoginEvent event)
	{
		if (environment.getConfiguration().linkingKickUnregistered)
		{
			event.setKickMessage(environment.getConfiguration().messages.get("link-unregistered-player"));
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
		}

		if (environment.getConfiguration().requireAvatar)
		{
			kickPlayer(event, "require-avatar-message");
		}

		if (environment.getConfiguration().requireMinimumPosts)
		{
			kickPlayer(event, "require-minimum-posts-message");
		}
	}

	private void kickPlayer(AsyncPlayerPreLoginEvent event, String messageKey)
	{
		event.setKickMessage(environment.getConfiguration().messages.get(messageKey));
		event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
	}

	private void joinUnregistered(Player player)
	{
		if (environment.getConfiguration().linkingNotifyUnregistered)
		{
			String message = ChatColor.RED + environment.getConfiguration().messages.get("link-unregistered-player");
			player.sendMessage(message);
		} // if config.linkingNotifyUnregistered

		if (!environment.getConfiguration().linkingUnregisteredGroup.isEmpty())
		{
			environment.getPermissionHandler().addToGroup(player, environment.getConfiguration().linkingUnregisteredGroup);
			if (environment.getConfiguration().linkingNotifyPlayerGroup)
			{
				String message = ChatColor.RED + environment.getConfiguration().messages.get("link-notify-player-group-change");
				message = message.replace("~GROUPNAME~", environment.getConfiguration().linkingUnregisteredGroup);
				player.sendMessage(message);
			}

			if (environment.getConfiguration().linkingUnregisterFormerRegistered)
			{
				environment.getPermissionHandler().removeFromGroup(player, environment.getConfiguration().linkingRegisteredGroup);
			}
		}
	}

	private void joinRegistered(Player player)
	{
		if (environment.getConfiguration().linkingNotifyRegistered)
		{
			String message = ChatColor.GREEN + environment.getConfiguration().messages.get("link-registered-player");
			player.sendMessage(message);
		}

		maybeSwitchToRegistered(player);

		if (environment.getConfiguration().syncDuringJoin)
		{
			environment.getWebApplication().runSynchronizePlayer(player, true);
		}
	}

	private void maybeSwitchToRegistered(Player player)
	{
		// We don't use the linking registered group if it is empty or group
		// synchronization is active.
		if (environment.getConfiguration().groupSynchronizationActive || environment.getConfiguration().linkingRegisteredGroup.isEmpty())
		{
			return;
		}

		// if this rule is turned on, we won't change groups unless they're
		// a member of the unregistered group or they have no groups.
		if (environment.getConfiguration().linkingRegisteredFormerUnregisteredOnly && !environment.getPermissionHandler().isMemberOfGroup(player, environment.getConfiguration().linkingUnregisteredGroup) && !environment.getPermissionHandler().getGroupsPure(player).isEmpty())
		{
			return;
		}

		environment.getPermissionHandler().switchGroup(player, environment.getConfiguration().linkingUnregisteredGroup, environment.getConfiguration().linkingRegisteredGroup);

		if (environment.getConfiguration().linkingNotifyPlayerGroup)
		{
			String message = ChatColor.RED + environment.getConfiguration().messages.get("link-notify-player-group-change");
			message = message.replace("~GROUPNAME~", environment.getConfiguration().linkingRegisteredGroup);
			player.sendMessage(message);
		}
	}
}
