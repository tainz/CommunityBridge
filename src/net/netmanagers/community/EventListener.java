package net.netmanagers.community;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener
{
	private Main plugin;

	EventListener(Main plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
	{
		int id = Main.getUserId(event.getName());
		if (id == 0)
		{
			if (Main.config.kick_unregistered)
			{
				event.setKickMessage(Main.config.unregistered_message);
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
			}
		}
		else
		{
			if (Main.config.useBanned || Main.config.banlistTableEnabled)
			{
				try
				{
					ResultSet res = Main.sql.sqlQuery("SELECT * FROM " + Main.config.users_table	 + " WHERE " + Main.config.user_id_field + " = '" + id + "'");
					if (res.next())
					{
						if (Main.config.useBanned)
						{
							boolean banned = res.getBoolean(Main.config.is_banned_field);

							if (banned)
							{
								event.setKickMessage("You have been banned from the site.");
								event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
							}
						}

						if (Main.config.banlistTableEnabled)
						{
							if (res.getString(Main.config.groups_id_field).equalsIgnoreCase(Main.config.banned_users_group))
							{
									event.setKickMessage("You have been banned from the site.");
									event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
							}
						}
					}
					else
					{
						Main.log.severe("res.next() failure during prelogin.");
					}
				}
				catch (SQLException error)
				{
					Main.log.severe("Error during preloginevent: " + error.getMessage());
				}
				catch (MalformedURLException error)
				{
					Main.log.severe("Error during preloginevent: " + error.getMessage());
				}
				catch (InstantiationException error)
				{
					Main.log.severe("Error during preloginevent: " + error.getMessage());
				}
				catch (IllegalAccessException error)
				{
					Main.log.severe("Error during preloginevent: " + error.getMessage());
				}
			} // if config.use banned or banlist
		} // if id == 0 else
	} // method

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run()
			{
					Main.SyncPlayer(p, true);
			}
		});
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event)
	{
		final	Player p = event.getPlayer();
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (Main.config.statisticsTrackingEnabled)
				{
					int id = Main.getUserId(p.getName());

					if (id > 0)
					{
						Main.UpdateTrackingStats(id, p);

						if (Main.config.onlinestatusEnabled)
						{
							String errorBase = "Error during onPlayerDisconnect():";
							try
							{
								if (Main.config.multiTables && Main.config.multiTablesUseKey)
								{
									Main.sql.updateQuery("UPDATE " + Main.config.multi_table + " SET " + Main.config.multi_table_value_field + " = '" + Main.config.onlinestatusValueOffline + "' WHERE " + Main.config.multi_table_user_id_field + " = '" + id + "' and " + Main.config.multi_table_key_field +" = '" + Main.config.onlinestatusKeyValue + "'");
								}
								else if (Main.config.multiTables)
								{
									Main.sql.updateQuery("UPDATE " + Main.config.multi_table + " SET " + Main.config.onlinestatusColumn + " = '" + Main.config.onlinestatusValueOffline + "' WHERE " + Main.config.multi_table_user_id_field + " = '" + id + "'");
								}
								else
								{
									Main.sql.updateQuery("UPDATE " + Main.config.users_table + " SET " + Main.config.onlinestatusColumn + " = '" + Main.config.onlinestatusValueOffline + "' WHERE " + Main.config.user_id_field + " = '" + id + "'");
								}
							}
							catch (MalformedURLException e)
							{
								Main.log.warning(errorBase + e.getMessage());
							}
							catch (InstantiationException e)
							{
								Main.log.warning(errorBase + e.getMessage());
							}
							catch (IllegalAccessException e)
							{
								Main.log.severe("Broken Set User Offline SQL Query, check your config.yml");
								Main.log.warning(errorBase + e.getMessage());
							}
						} // if onlinestatusEnabled
					} // if ID
				} // if config....
			} // run
		}); // Scheduler
	} // onPlayerDisconnect
} // Class