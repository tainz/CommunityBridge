package net.netmanagers.community;

import java.net.MalformedURLException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Main.syncPlayer(p, true);
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event) {

		if (Main.config.statisticsTrackingEnabled) {
			Player p = event.getPlayer();
			int id = Main.getUserId(p.getName());
			if(id > 0){
				Main.updateStatistics(id, p);

				if(Main.config.onlinestatusEnabled){
					try {
						if (Main.config.multiTables && Main.config.multiTablesUseKey){
							Main.sql.updateQuery("UPDATE " + Main.config.multi_table + " SET " + Main.config.multi_table_value_field + " = '" + Main.config.onlinestatusValueOffline + "' WHERE " + Main.config.multi_table_user_id_field + " = '" + id + "' and " + Main.config.multi_table_key_field +" = '" + Main.config.onlinestatusKeyValue + "'");
						}else if(Main.config.multiTables){
							Main.sql.updateQuery("UPDATE " + Main.config.multi_table + " SET " + Main.config.onlinestatusColumn + " = '" + Main.config.onlinestatusValueOffline + "' WHERE " + Main.config.multi_table_user_id_field + " = '" + id + "'");
						}else{
							Main.sql.updateQuery("UPDATE " + Main.config.users_table + " SET " + Main.config.onlinestatusColumn + " = '" + Main.config.onlinestatusValueOnline + "' WHERE " + Main.config.user_id_field + " = '" + id + "'");
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						Main.log.severe("Broken Set User Offline SQL Query, check your config.yml");
						e.printStackTrace();
					}
				}
			}
		}
	}
}