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
		Main.SyncPlayer(p, true);
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event) {						
		
		if (Main.basic_tracking) {			
			Player p = event.getPlayer();			
			int id = Main.getUserId(p.getName());			
			if(id > 0){
				Main.UpdateTrackingStats(id, p);
				
				if(Main.onlinestatus_enabled){
					try {
						if (Main.multi_tables && Main.multi_tables_use_key){
							Main.sql.updateQuery("UPDATE " + Main.multi_table + " SET " + Main.multi_table_value_field + " = '" + Main.onlinestatus_valueoffline + "' WHERE " + Main.multi_table_user_id_field + " = '" + id + "' and " + Main.multi_table_key_field +" = '" + Main.onlinestatus_key_value + "'");
						}else if(Main.multi_tables){
							Main.sql.updateQuery("UPDATE " + Main.multi_table + " SET " + Main.onlinestatus_field + " = '" + Main.onlinestatus_valueoffline + "' WHERE " + Main.multi_table_user_id_field + " = '" + id + "'");
						}else{
							Main.sql.updateQuery("UPDATE " + Main.users_table + " SET " + Main.onlinestatus_field + " = '" + Main.onlinestatus_valueonline + "' WHERE " + Main.user_id_field + " = '" + id + "'");
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