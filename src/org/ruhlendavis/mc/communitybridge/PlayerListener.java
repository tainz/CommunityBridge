package org.ruhlendavis.mc.communitybridge;

import java.net.MalformedURLException;
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
		String playerName = event.getPlayer().getName();
		if (webapp.isPlayerRegistered(playerName))
		{
			if (config.linkingNotifyRegistered)
			{
				String message = ChatColor.GREEN + config.messages.get("link-registered-player");
				event.setJoinMessage(message);
			}
		}
		else
		{
			if (config.linkingNotifyUnregistered)
			{
				String message = ChatColor.RED + config.messages.get("link-unregistered-player");
				event.setJoinMessage(message);
			} // if config.linkingNotifyUnregistered
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
		Player player = event.getPlayer();

		webapp.onQuit(player.getName());
	} // onPlayerQuit
} // PlayerListener class


//		if (config.statisticsEnabled)
//		{
//			int id = Main.getUserId(player.getName());
//			if (id > 0)
//			{
//				Main.updateStatistics(id, player);
//
//				if (config.onlineStatusEnabled)
//				{
//					try
//					{
//						if (config.multiTables && config.multiTablesUseKey)
//						{
//							Main.sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.multi_table_value_field + " = '" + config.onlineStatusValueOffline + "' WHERE " + config.multi_table_user_id_field + " = '" + id + "' and " + config.multi_table_key_field +" = '" + config.onlineStatusColumnOrKey + "'");
//						}
//						else if(config.multiTables)
//						{
//							Main.sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.onlinestatusColumn + " = '" + config.onlineStatusValueOffline + "' WHERE " + config.multi_table_user_id_field + " = '" + id + "'");
//						}
//						else
//						{
//							Main.sql.updateQuery("UPDATE " + config.users_table + " SET " + config.onlinestatusColumn + " = '" + config.onlineStatusValueOnline + "' WHERE " + config.user_id_field + " = '" + id + "'");
//						}
//					}
//					catch (MalformedURLException e)
//					{
//						e.printStackTrace();
//					}
//					catch (InstantiationException e)
//					{
//						e.printStackTrace();
//					}
//					catch (IllegalAccessException e)
//					{
//						Main.log.severe("Broken Set User Offline SQL Query, check your config.yml");
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	private void resetOnlineStatus()
//  {
//		try {
//			if (config.multiTables)
//      {
//				if (config.multiTablesUseKey)
//        {
//					sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.multi_table_value_field + " = '" + config.onlineStatusValueOffline + "' WHERE " + config.multi_table_key_field + " = '" + config.onlineStatusColumnOrKey + "'");
//				}
//        else
//        {
//					sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.onlinestatusColumn + " = '" + config.onlineStatusValueOffline + "' WHERE " + config.onlinestatusColumn + " = '" + config.onlineStatusValueOnline + "'");
//				}
//			}
//      else
//      {
//				sql.updateQuery("UPDATE " + config.users_table + " SET " + config.onlinestatusColumn + " = '" + config.onlineStatusValueOffline + "'  WHERE " + config.onlinestatusColumn + " = '" + config.onlineStatusValueOnline + "'");
//			}
//		}
//		catch (MalformedURLException e)
//		{
//			log.severe("Error in ResetOnlineStatus: " + e.getMessage());
//			disablePlugin();
//		} catch (InstantiationException e) {
//			log.severe("Error in ResetOnlineStatus: " + e.getMessage());
//			disablePlugin();
//		} catch (IllegalAccessException e) {
//			log.severe("Error in ResetOnlineStatus: " + e.getMessage());
//			disablePlugin();
//		}
//	}
//
