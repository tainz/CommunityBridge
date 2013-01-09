package net.netmanagers.community;

import java.net.MalformedURLException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
									Main.sql.updateQuery("UPDATE " + Main.config.users_table + " SET " + Main.config.onlinestatusColumn + " = '" + Main.config.onlinestatusValueOnline + "' WHERE " + Main.config.user_id_field + " = '" + id + "'");
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