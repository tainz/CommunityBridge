package net.netmanagers.community;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Logger;

import net.netmanagers.api.Logging;
import net.netmanagers.api.SQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Main extends JavaPlugin {
	public static Logging log;
	public static String thisPluginName = "CommunityBridge";
	public static PluginDescriptionFile pdf;
	public static SQL sql;
	public static File configFile;
	public static FileConfiguration config;

	public static boolean show_config = false;
	public static boolean multi_tables = false;
	public static boolean multi_tables_use_key = false;
	public static boolean use_banned = false;
	public static boolean banlist_table_enabled = false;
	public static boolean groups_table_enabled = false;
	public static boolean basic_tracking = false;
	
  public static boolean auto_sync = false;
  public static boolean auto_remind = false;
  public static String auto_every_unit;
	public static long auto_sync_every;
	public static long auto_remind_every;

  public static boolean secondary_groups = false;
	public static boolean show_primary_group = false;
	public static boolean kick_unregistered = false;
	public static boolean require_avatar = false;
	public static boolean require_minposts = false;
	
	public static String permissions_system;

	public static String users_table;
	public static String banlist_table;
	public static String groups_table;
	public static String multi_table;
	public static String avatar_table;
	public static String minposts_table;
	
	public static String avatar_user_field;
	public static String avatar_field;
	public static String avatar_message;
	public static String minposts_user_field;
	public static String minposts_field;
	public static String minposts_message;
	
	public static String banlist_user_id_field;
	public static String banlist_banned_id_field;
	public static String groups_user_id_field;
	public static String groups_group_id_field;
		
	public static String user_id_field;
	public static String user_name_field;
	public static String groups_id_field;
	public static String secondary_groups_id_field;

	public static String is_banned_field;
	
	public static int banned_users_group;
	public static int default_group;
	public static int minposts_required;
  
	public static String multi_table_key_field;
	public static String multi_table_key_value;	
	public static String multi_table_value_field;	
	public static String multi_table_user_id_field;
	
	public static String registered_message;
	public static String unregistered_message;
	public static String unregistered_messagereminder;
	
	public static boolean onlinestatus_enabled = false;
	public static boolean lastonline_enabled = false;
	public static boolean gametime_enabled = false;
	public static boolean totalxp_enabled = false;
	public static boolean currentxp_enabled = false;
	public static boolean level_enabled = false;
	public static boolean health_enabled = false;
	public static boolean lifeticks_enabled = false;
	public static boolean wallet_enabled = false;
		
	public static String onlinestatus_key_value;
	public static String onlinestatus_field;
	public static String onlinestatus_valueoffline;
	public static String onlinestatus_valueonline;
	public static String lastonline_key_value;
	public static String lastonline_field;	
	public static String gametime_key_value;
	public static String gametime_field;
	public static String totalxp_key_value;	
	public static String totalxp_field;
	public static String currentxp_key_value;	
	public static String currentxp_field;
	public static String level_key_value;	
	public static String level_field;
	public static String health_key_value;	
	public static String health_field;
	public static String lifeticks_key_value;	
	public static String lifeticks_field;
	public static String wallet_key_value;	
	public static String wallet_field;
	
	public static String[] groups;

	@Override
	public void onEnable() {
		pdf = this.getDescription();
		log = new Logging(Logger.getLogger("Minecraft"), pdf);

		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}
		config = new YamlConfiguration();
		try {
			config.load(configFile);
			config.options().copyHeader(true);
		} catch (Exception e) {
			log.info("Error loading config");						
		}
		
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		getCommand("cbban").setExecutor(new Cmds());
		getCommand("cbunban").setExecutor(new Cmds());
		getCommand("cbrank").setExecutor(new Cmds());
		getCommand("cbsync").setExecutor(new Cmds());
		getCommand("cbsyncall").setExecutor(new Cmds());
		
		if (config.get("db-username").equals("username") && config.get("db-password").equals("password")) {
			log.info("Using default config file.");
			getServer().getPluginManager().disablePlugin(this);
		} else {
			permissions_system = config.getString("permissions-system");
			show_config = config.getBoolean("show-config");
			show_primary_group = config.getBoolean("show-primary-group");
			basic_tracking = config.getBoolean("enable-basic-tracking");
			multi_tables = config.getBoolean("multi-tables");
			multi_tables_use_key = config.getBoolean("multi-tables-use-key");
			secondary_groups = config.getBoolean("secondary-groups");
			use_banned = config.getBoolean("use-banned-field");
			kick_unregistered = config.getBoolean("kick-unregistered");

      auto_sync = config.getBoolean("auto-sync");
      auto_remind = config.getBoolean("auto-remind");
      auto_every_unit = config.getString("auto-every-unit", "ticks");

			auto_sync_every = config.getLong("auto-sync-every");
      auto_remind_every = config.getLong("auto-remind-every");
      
			require_avatar = config.getBoolean("profile-requirements.require-avatar");
			avatar_table = config.getString("profile-requirements.require-avatar-table");
			avatar_user_field = config.getString("profile-requirements.require-avatar-user-id-field");
			avatar_field = config.getString("profile-requirements.require-avatar-field");
			avatar_message = config.getString("profile-requirements.require-avatar-message");

			require_minposts = config.getBoolean("profile-requirements.require-minposts");
			minposts_required =  config.getInt("profile-requirements.require-minposts-count");
			minposts_table = config.getString("profile-requirements.require-minposts-table");
			minposts_user_field = config.getString("profile-requirements.require-minposts-user-id-field");
			minposts_field = config.getString("profile-requirements.require-minposts-field");			  
			minposts_message = config.getString("profile-requirements.require-minposts-message");
			
			registered_message = config.getString("registered-message");
			unregistered_message = config.getString("unregistered-message");
			unregistered_messagereminder = config.getString("unregistered-messagereminder");
			
			banlist_table_enabled = config.getBoolean("banlist-table.enabled");
			banlist_table = config.getString("banlist.table-name");
			banlist_user_id_field = config.getString("banlist-table.user-id-field");
			banlist_banned_id_field = config.getString("banlist-table.user-id-field");
			
			groups_table_enabled = config.getBoolean("groups-table.enabled");
			groups_table = config.getString("groups-table.table-name");
			groups_user_id_field = config.getString("groups-table.user-id-field");
			groups_group_id_field = config.getString("groups-table.group-id-field");
						
			users_table = config.getString("users-table.table");
			user_id_field = config.getString("users-table.user-id-field");
			user_name_field = config.getString("users-table.user-name-field");
			
			groups_id_field = config.getString("users-table.groups-id-field");
			secondary_groups_id_field = config.getString("users-table.secondary-groups-id-field");
			
			multi_table = config.getString("multi-table.table");	
			multi_table_user_id_field = config.getString("multi-table.field-user-id-field");
			multi_table_key_field = config.getString("multi-table.field-key-field");			
			multi_table_key_value = config.getString("multi-table.field-key-value");			
			multi_table_value_field = config.getString("multi-table.field-value-field");
			
			onlinestatus_enabled = config.getBoolean("basic-tracking.field-onlinestatus-enabled");
			onlinestatus_key_value = config.getString("basic-tracking.field-onlinestatus-key-value");
			onlinestatus_field = config.getString("basic-tracking.field-onlinestatus-field");
			onlinestatus_valueonline = config.getString("basic-tracking.field-onlinestatus-valueonline");
			onlinestatus_valueoffline = config.getString("basic-tracking.field-onlinestatus-valueoffline");
			lastonline_enabled = config.getBoolean("basic-tracking.field-lastonline-enabled");
			lastonline_key_value = config.getString("basic-tracking.field-lastonline-key-value");			
			lastonline_field = config.getString("basic-tracking.field-lastonline-field");
			wallet_enabled = config.getBoolean("basic-tracking.field-wallet-enabled");
			wallet_key_value = config.getString("basic-tracking.field-wallet-key-value");
			wallet_field = config.getString("basic-tracking.field-wallet-field");
			gametime_enabled = config.getBoolean("basic-tracking.field-gametime-enabled");
			gametime_key_value = config.getString("basic-tracking.field-gametime-key-value");			
			gametime_field = config.getString("basic-tracking.field-gametime-field");
			totalxp_enabled = config.getBoolean("basic-tracking.field-totalxp-enabled");
			totalxp_key_value = config.getString("basic-tracking.field-totalxp-key-value");			
			totalxp_field = config.getString("basic-tracking.field-totalxp-field");
			currentxp_enabled = config.getBoolean("basic-tracking.field-currentxp-enabled");
			currentxp_key_value = config.getString("basic-tracking.field-currentxp-key-value");			
			currentxp_field = config.getString("basic-tracking.field-currentxp-field");
			level_enabled = config.getBoolean("basic-tracking.field-level-enabled");
			level_key_value = config.getString("basic-tracking.field-level-key-value");		
			level_field = config.getString("basic-tracking.field-level-field");
			health_enabled = config.getBoolean("basic-tracking.field-health-enabled");
			health_key_value = config.getString("basic-tracking.field-health-key-value");
			health_field = config.getString("basic-tracking.field-health-field");
			lifeticks_enabled = config.getBoolean("basic-tracking.field-lifeticks-enabled");
			lifeticks_key_value = config.getString("basic-tracking.field-lifeticks-key-value");
			lifeticks_field = config.getString("basic-tracking.field-lifeticks-field");
			
			default_group = config.getInt("users-table.default-group");
      
			
			if (use_banned) {
				is_banned_field = config.getString("users-table.banned-field");
			} else {
				banned_users_group = config.getInt("users-table.banned-users-group");
			}

			if(show_config){
				Main.log.info("Auto Sync   : " + auto_sync);
        Main.log.info("Auto Remind :" + auto_remind);
				Main.log.info("Kick Unregistered : " + kick_unregistered);
				Main.log.info("Multi Tables : " + multi_tables);
				Main.log.info("Basic Tracking : " + basic_tracking);
				Main.log.info("Require Avatar : " + require_avatar);
				Main.log.info("Min Posts : " + require_minposts);
				if(basic_tracking){			
					Main.log.info("Tracking Online Status : " + onlinestatus_enabled);
					Main.log.info("Tracking Last Online   : " + lastonline_enabled);
					Main.log.info("Tracking Game Time     : " + gametime_enabled);
					Main.log.info("Tracking Total XP      : " + totalxp_enabled);
					Main.log.info("Tracking Current XP    : " + currentxp_enabled);
					Main.log.info("Tracking Level         : " + level_enabled);
					Main.log.info("Tracking Health        : " + health_enabled);
					Main.log.info("Tracking Life Ticks    : " + lifeticks_enabled);
					Main.log.info("Tracking Wallet        : " + wallet_enabled);
				}
			}

      // Determine how many groups are configured.
			int count = 0;
			for (String s : config.getKeys(true)) {
				if (s.contains("groups.")) {
					count++;
				}
			}

      groups = new String[count];
      
      // Collect a list of configurated groups.
      count = 0;
      for (String s : config.getKeys(true)) {
				if (s.contains("groups.")) {
					groups[count] = config.getString(s);
          count++;
				}
			}

			sql = new SQL(config.get("db-host") + ":" + config.get("db-port"), config.get("db-database") + "", config.get("db-username") + "", config.get("db-password") + "");
			sql.initialize();			
			if(sql.checkConnection())
      {				
				if (basic_tracking && onlinestatus_enabled)
        {
					ResetOnlineStatus();
        }
				syncAll();
        
				if (auto_sync)
        {
					startSyncing();
        }
        
        if (auto_remind)
        {
          startAutoReminder();
        }
        
				saveConfig();
				
				log.info("Enabled!");
			}else{
				disablePlugin();
			}
		}
	}

	@Override
	public void onDisable() {
		if (sql != null) {
			sql.close();
		}

		log.info("Disabled...");
	}

	private static void disablePlugin(){
		PluginManager pm = Bukkit.getServer().getPluginManager();
		for (Plugin plugin : pm.getPlugins()) {
            if (plugin.getDescription().getName().equalsIgnoreCase(thisPluginName)) {
        		pm.disablePlugin(plugin);
                return;
            }
        }		
	}
	
	private void startSyncing()
  {
    long every = auto_sync_every; // Effectively defaulting to ticks.
    String unit = "ticks";   
    
    if (auto_every_unit.toLowerCase().startsWith("second"))
    {
      every = auto_sync_every * 20; // 20 ticks per second.
      unit = "seconds";
    }
    else if (auto_every_unit.toLowerCase().startsWith("minute"))
    {
      every = auto_sync_every * 1200; // 20 ticks per second, 60 sec per minute
      unit = "minutes";
    }
    else if (auto_every_unit.toLowerCase().startsWith("hour"))
    {
      every = auto_sync_every * 72000; // 20 ticks/s 60s/m, 60m/h
      unit = "hours";
    }   
    
		log.info(String.format("Auto Sync Every: %d %s.", auto_sync_every, unit));
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
    {
      @Override
			public void run()
      {
				syncAll();
			}
		}, every, every);
	}

  private void startAutoReminder()
  {
    long every = auto_remind_every; // Effectively defaulting to ticks.
    
    String unit = "ticks";
    
    if (auto_every_unit.toLowerCase().startsWith("second"))
    {
      every = auto_remind_every * 20; // 20 ticks per second.
      unit = "seconds";
    }
    else if (auto_every_unit.toLowerCase().startsWith("minute"))
    {
      every = auto_remind_every * 1200; // 20 ticks per second, 60 sec/minute
      unit = "minutes";
    }
    else if (auto_every_unit.toLowerCase().startsWith("hour"))
    {
      every = auto_remind_every * 72000; // 20 ticks/s 60s/m, 60m/h
      unit = "hours";
    }
    
    log.info(String.format("Auto Remind Unregistered Every: %d %s.",
                           auto_remind_every, unit));
    getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
    {
      @Override
      public void run()
      {
        remindUnregistered();
      }
   	}, every, every);
  }	
  
	private void ResetOnlineStatus(){
		try {
			if (multi_tables) {
				if(multi_tables_use_key){					
					sql.updateQuery("UPDATE " + multi_table + " SET " + multi_table_value_field + " = '" + onlinestatus_valueoffline + "' WHERE " + multi_table_key_field + " = '" + onlinestatus_key_value + "'");									
				}else{
					sql.updateQuery("UPDATE " + multi_table + " SET " + onlinestatus_field + " = '" + onlinestatus_valueoffline + "' WHERE " + onlinestatus_field + " = '" + onlinestatus_valueonline + "'");
				}
			} else {
				sql.updateQuery("UPDATE " + users_table + " SET " + onlinestatus_field + " = '" + onlinestatus_valueoffline + "'  WHERE " + onlinestatus_field + " = '" + onlinestatus_valueonline + "'");
			}
		} catch (MalformedURLException e) {			
			e.printStackTrace();
			disablePlugin();
		} catch (InstantiationException e) {
			e.printStackTrace();
			disablePlugin();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			disablePlugin();
		}
	}
	
	protected void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getUserId(String username) {
		int userId = 0;
    String query;
		try
    {
			ResultSet res;
			if (multi_tables)
      {
				if(multi_tables_use_key)
        {
          query = "SELECT * FROM " + multi_table
                  + " WHERE " + multi_table_key_field + " = '" 
                  + multi_table_key_value
                  + "' AND LOWER(" + multi_table_value_field + ") = LOWER('"
                  + username
                  + "') ORDER BY " + multi_table_user_id_field + " DESC";
        }
        else 
        {
          query = "SELECT * FROM  "+ multi_table
                  + " WHERE LOWER(" + multi_table_value_field +
                  ") = LOWER('" + username
                  + "') ORDER BY " + multi_table_user_id_field + " DESC";
				}
				res = Main.sql.sqlQuery(query);
				if (res.next())
					userId = res.getInt(multi_table_user_id_field);
			}
      else
      {
        query = "SELECT * FROM " + users_table
                + " WHERE LOWER(" + user_name_field
                + ") = LOWER('" + username
                + "') order by " + user_id_field + " desc";
 				res = Main.sql.sqlQuery(query);
        if (res.next())
					userId = res.getInt(user_id_field);
			}			
			return userId;
		} catch (MalformedURLException e) {
			
		} catch (InstantiationException e) {
			
		} catch (IllegalAccessException e) {
			
		}
    catch (SQLException e)
    {
			log.severe("Broken User ID SQL Query, check your config.yml");
			e.printStackTrace();
			disablePlugin();
		}
    
		return userId;
	}

	public static ResultSet getOnlinePlayerInfo(String username) {
		try {
			ResultSet res;
			if (multi_tables) {
				if(multi_tables_use_key){
					res = Main.sql.sqlQuery("SELECT * FROM " + multi_table + " WHERE " + multi_table_key_field + " = '" + multi_table_key_value + "' AND " + multi_table_value_field + " = '" + username + "'");					
				}else{
					res = Main.sql.sqlQuery("SELECT * FROM  "+ multi_table +" WHERE " + multi_table_value_field + " = '" + username + "'");
				}
			} else {
				res = Main.sql.sqlQuery("SELECT * FROM " + users_table + " WHERE " + user_name_field + " = '" + username + "'");
			}
						
			if (res.next()) {
				return res;
			}
		} catch (MalformedURLException e) {
			
		} catch (InstantiationException e) {
			
		} catch (IllegalAccessException e) {
			
		} catch (SQLException e) {
			log.severe("Broken Get Online Player Info SQL Query, check your config.yml");
			e.printStackTrace();
			disablePlugin();
		}
		return null;
	}

	public static boolean setGroup(int groupId, Player p, boolean n) {
		String cmd = "";
		 try{
			 String gr = config.getString((new StringBuilder("groups.")).append(groupId).toString());
			 if(permissions_system.equalsIgnoreCase("PEX"))
			 {
				 if(!PermissionsEx.getUser(p).getGroupsNames()[0].equalsIgnoreCase(gr))
				 {
					 PermissionsEx.getUser(p).setGroups(new String[] { gr });					 
					 if(n)
						 log.info((new StringBuilder("Set ")).append(p.getName()).append(" to group ").append(gr).toString());
					 return true;
				 }
			 } else
			 {
				 if(permissions_system.equalsIgnoreCase("bPerms"))
				 {
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), (new StringBuilder("world ")).append(p.getWorld().getName()).toString());
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), (new StringBuilder("user ")).append(p.getName()).toString());
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), (new StringBuilder("user setgroup ")).append(gr).toString());
					 if(n)
						 log.info((new StringBuilder("Set ")).append(p.getName()).append(" to group ").append(gr).toString());
					 return true;
				 }
				 if(permissions_system.equalsIgnoreCase("GroupManager"))
				 {
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "manuadd " + p.getName() + " " + gr);
					 if(n)
						 log.info((new StringBuilder("Setting ")).append(p.getName()).append(" to group ").append(gr).toString());
					 return true;
				 }
				 if(permissions_system.equalsIgnoreCase("PermsBukkit"))
				 {				 
					 cmd = "permissions player setgroup " + p.getName() + " " + gr;
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
					 if(n)
						 log.info((new StringBuilder("Set ")).append(p.getName()).append(" to group ").append(gr).toString());
					 return true;
				 }
			 }
		 } catch (Error e) {
			 log.severe(e.getMessage());
		 }
		 
		 return false;
	}
	

	public static boolean addGroup(int groupId, Player p, boolean n) {		
		 try{
			 String gr = config.getString((new StringBuilder("groups.")).append(groupId).toString());
			 if(permissions_system.equalsIgnoreCase("PEX"))
			 {
				 if(!PermissionsEx.getUser(p).getGroupsNames()[0].equalsIgnoreCase(gr))
				 {
					 PermissionsEx.getUser(p).addGroup(gr);					 
					 if(n)
						 log.info((new StringBuilder("Added ")).append(p.getName()).append(" to group ").append(gr).toString());
					 return true;
				 }
			 } else
			 {
				 if(permissions_system.equalsIgnoreCase("bPerms"))
				 {
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), (new StringBuilder("world ")).append(p.getWorld().getName()).toString());
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), (new StringBuilder("user ")).append(p.getName()).toString());
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), (new StringBuilder("user addgroup ")).append(gr).toString());
					 if(n)
						 log.info((new StringBuilder("Added ")).append(p.getName()).append(" to group ").append(gr).toString());
					 return true;
				 }
				 if(permissions_system.equalsIgnoreCase("GroupManager"))
				 {
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "manuadd " + p.getName() + " " + gr);
					 if(n)
						 log.info((new StringBuilder("Adding ")).append(p.getName()).append(" to group ").append(gr).toString());
					 return true;
				 }
				 if(permissions_system.equalsIgnoreCase("PermsBukkit"))
				 {			 
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + p.getName() + " " + gr);
					 if(n)
						 log.info((new StringBuilder("Adding ")).append(p.getName()).append(" to group ").append(gr).toString());
					 return true;
				 }
			 }
		 } catch (Error e) {
			 log.severe(e.getMessage());
		 }
		 
		 return false;

	}

	public static int getGroup(String id) {
		for (int i = 0; i < groups.length; i++) {
			if (groups[i].toLowerCase().contains(id.toLowerCase())) {
				return i + 1;
			}
		}
		return 0;
	}

	public static void syncAll() {
		log.info("Running Auto Sync");		
		for (Player play : Bukkit.getOnlinePlayers()) {			
			SyncPlayer(play, false);
		}
	}
  
  public static void RemindPlayer(Player p)
  {		
    int id = getUserId(p.getName());
    if (id == 0)
    {
      if (kick_unregistered)
      {
        p.kickPlayer(unregistered_message);
      } 
      else
      {			
        p.sendMessage(ChatColor.RED + unregistered_messagereminder);
        log.info(p.getName() + " issued unregistered reminder notice");					
      }
    }						
  }
	
  public static void remindUnregistered()
  {
    log.info("Running Auto UnRegistered Auto Reminder");
    
    for (Player play : Bukkit.getOnlinePlayers())
    {
      RemindPlayer(play);
    }
	
  }

	public static void SyncPlayer(Player p, boolean firstsync){		
		try {
			int id = getUserId(p.getName());
			if (id > 0) {					
				ResultSet res = sql.sqlQuery("SELECT * FROM " + users_table + " WHERE " + user_id_field + " = '" + id + "'");
				if (res.next()) {
					int group = res.getInt(groups_id_field);
					
					if (use_banned) {
						boolean banned = res.getBoolean(is_banned_field);

						if (banned)
							p.kickPlayer("You have been banned from the site.");
						
					} else {
						boolean banned = res.getInt(groups_id_field) == banned_users_group ? true : false;
						if (banned)							
							p.kickPlayer("You have been banned from the site.");
					}
          
					boolean requirements_met = true;
					
					if(require_minposts){
						if(!checkMinPosts(id, p))
							requirements_met = false;
					}
					
					if(require_avatar){
						if(!checkAvatar(id, p))
							requirements_met = false;
					}
					
					if(requirements_met){
						setGroup(group, p, firstsync);
						
						if(secondary_groups){
							String extra_groups = res.getString(secondary_groups_id_field);
							if(extra_groups.length()>0){
								for(String g: extra_groups.split(",")){
									if(!g.isEmpty())								
										addGroup(Integer.parseInt(g), p, firstsync);
								}
							}
						}
					}else{
						setGroup(Main.default_group, p, firstsync);
						group = Main.default_group;
					}
					
					if(firstsync){
						if (basic_tracking)
							Main.LoadTrackingStats(id, p);						
						
						if(show_primary_group){
							p.sendMessage(ChatColor.YELLOW + "Registered " + Main.config.getString("groups." + group) + " Account.");
						}else{
							p.sendMessage(ChatColor.YELLOW + registered_message);
						}
						
						Main.log.info(p.getName() + " linked to Community User #"+ id + ", Group: " + Main.config.getString("groups." + group));	
					
					}else if(basic_tracking){								
						UpdateTrackingStats(id, p);
					}
				}
			} else {
				if(kick_unregistered){
					p.kickPlayer(unregistered_message);
				}else{
					setGroup(Main.default_group, p, true);				
					if(firstsync){
						p.sendMessage(ChatColor.RED + unregistered_message);
						log.info(p.getName() + "'s name not set or not registered on community site");
					}else{
						p.sendMessage(ChatColor.RED + unregistered_messagereminder);
						log.info(p.getName() + " issued unregistered reminder notice");
					}
				}
			}
		} catch (MalformedURLException e) {
			log.severe("Database Error, Disabling Plugin.");
			disablePlugin();
		} catch (InstantiationException e) {
			log.severe("Database Error, Disabling Plugin.");
			disablePlugin();
		} catch (IllegalAccessException e) {
			log.severe("Database Error, Disabling Plugin.");
			disablePlugin();
		} catch (SQLException e) {
			log.severe("Sync User SQL Query Broken, check your config.yml;" + e.getMessage());			
			disablePlugin();
		}				
	}
	
	public static void checkDBSanity(int u, String keyval){
		ResultSet res;
		try {
			res = sql.sqlQuery("SELECT 1 FROM " + multi_table + " WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + keyval + "'");			
			if(!res.next())
				sql.insertQuery("INSERT INTO " + multi_table + " (`"+multi_table_user_id_field+"`, `"+multi_table_key_field+"`, `"+multi_table_value_field+"`) VALUES ('" + u + "', '" + keyval + "', 0)");
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			log.severe("Database SQL Error with " + keyval);
			disablePlugin();
		}			
	}
	
	public static boolean checkAvatar(int u, Player p){
		ResultSet res;
		
		try {
			res = sql.sqlQuery("SELECT " + avatar_field + " FROM " + avatar_table + " WHERE " + avatar_user_field + " = '" + u + "'");
			if(res == null)
				return false;
			
			if(res.next())
				if(!res.getString(avatar_field).isEmpty())
					return true;
			
		} catch (MalformedURLException e) {
			disablePlugin();	
		} catch (InstantiationException e) {
			disablePlugin();	
		} catch (IllegalAccessException e) {						
			disablePlugin();	
		} catch (SQLException e) {
			log.severe("Broken Avatar Check SQL Query, check your config.yml");			
			disablePlugin();	
		}
		
		p.sendMessage(ChatColor.YELLOW + avatar_message);
		log.info((new StringBuilder("Notice Issued to ")).append(p.getName()).append(" for not having profile avatar").toString());
		return false;
	}
	
	public static boolean checkMinPosts(int u, Player p){
		ResultSet res;
		
		try {
			res = sql.sqlQuery("SELECT " + minposts_field + " FROM " + minposts_table + " WHERE " + minposts_user_field + " = '" + u + "'");
			if(res == null)
				return false;
			
			if(res.next())
				if(res.getInt(minposts_field) >= minposts_required)
					return true;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			log.severe("Broken Post Count SQL Query, check your config.yml");
			e.printStackTrace();
			disablePlugin();
		}		
		p.sendMessage(ChatColor.YELLOW + minposts_message);
		log.info((new StringBuilder("Notice Issued to ")).append(p.getName()).append(" for having less than ").append(minposts_required).append(" posts").toString());
		 
		return false;
	}
	
	public static void LoadTrackingStats(int u, Player p){
		
		int t = (int) (System.currentTimeMillis() / 1000L);
		
		try {			
			ResultSet res;
			if (multi_tables && multi_tables_use_key){					
				// Check for each custom field in the database.								
				if(onlinestatus_enabled){
					checkDBSanity(u, onlinestatus_key_value);
					sql.updateQuery("UPDATE "+Main.multi_table+" SET " + multi_table_value_field +" = '" + onlinestatus_valueonline + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + onlinestatus_key_value + "'");
				}

				if(lastonline_enabled){
					checkDBSanity(u, lastonline_key_value);
					sql.updateQuery("UPDATE "+Main.multi_table+" SET " + multi_table_value_field +" = '" + t + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + lastonline_key_value + "'");
				}

				if(currentxp_enabled){			
					checkDBSanity(u, currentxp_key_value);					
					res = sql.sqlQuery("SELECT " + multi_table_value_field + " FROM " + multi_table + " WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '"+currentxp_key_value+"'");
					if(res.next())
						p.setExp(res.getInt(multi_table_value_field));
				}
				
				if(totalxp_enabled){		
					checkDBSanity(u, totalxp_key_value);					
					res = sql.sqlQuery("SELECT " + multi_table_value_field + " FROM " + multi_table + " WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '"+totalxp_key_value+"'");
					if(res.next())
						p.setTotalExperience(res.getInt(multi_table_value_field));
				}
				
				if(lifeticks_enabled){
					checkDBSanity(u, lifeticks_key_value);					
					res = sql.sqlQuery("SELECT " + multi_table_value_field + " FROM " + multi_table + " WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '"+lifeticks_key_value+"'");
					if(res.next())
						if(res.getInt(multi_table_value_field) > 0)
							p.setTicksLived(res.getInt(multi_table_value_field));
				}
									
				if(level_enabled){
					checkDBSanity(u, level_key_value);					
					res = sql.sqlQuery("SELECT " + multi_table_value_field + " FROM " + multi_table + " WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '"+level_key_value+"'");
					if(res.next())
						p.setLevel(res.getInt(multi_table_value_field));
				}
				
				if(health_enabled){
					checkDBSanity(u, health_key_value);					
					res = sql.sqlQuery("SELECT " + multi_table_value_field + " FROM " + multi_table + " WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '"+health_key_value+"'");
					if(res.next())
						if(res.getInt(multi_table_value_field)>0)
							p.setHealth(res.getInt(multi_table_value_field));
				}
								
			} else {
				if(multi_tables){
					res = sql.sqlQuery("SELECT * FROM " + multi_table + " WHERE " + multi_table_value_field + " = '" + p.getName() + "'");
					
					if(onlinestatus_enabled)
						sql.updateQuery("UPDATE " + multi_table + " SET " + onlinestatus_field + " = '" + onlinestatus_valueonline + "' WHERE " + multi_table_user_id_field + " = '" + u + "'");
					
					if(lastonline_enabled)
						sql.updateQuery("UPDATE " + multi_table + " SET " + lastonline_field + " = " + t + " WHERE " + multi_table_user_id_field + " = '" + u + "'");
										
				}else{
					res = sql.sqlQuery("SELECT * FROM " + users_table + " WHERE " + user_name_field + " = '" + p.getName() + "'");
					
					if(onlinestatus_enabled)
						sql.updateQuery("UPDATE " + users_table + " SET " + onlinestatus_field + " = '" + onlinestatus_valueonline + "' WHERE " + user_id_field + " = '" + u + "'");
					
					if(lastonline_enabled)
						sql.updateQuery("UPDATE " + users_table + " SET " + lastonline_field + " = " + t +" WHERE " + user_id_field + " = '" + u + "'");
					
					if(!res.next())
						return;
				}
				if(res.next()){
					if(currentxp_enabled)				
						p.setExp(res.getInt(currentxp_field));
					
					if(totalxp_enabled)				
						p.setTotalExperience(res.getInt(totalxp_field));
					
					if(lifeticks_enabled){
						if(res.getInt(lifeticks_field)>0)
							p.setTicksLived(res.getInt(lifeticks_field));
					}
										
					if(level_enabled)
						p.setLevel(res.getInt(level_field));
					
					if(health_enabled){
						if(res.getInt(health_field) > 0)
							p.setHealth(res.getInt(health_field));
					}
				}
			}		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			log.severe("Broken Stat Tracking SQL Query, check your config.yml");
			e.printStackTrace();
			disablePlugin();
		}		
	}
	
	public static void UpdateTrackingStats(int u, Player p){
		
		float currentxp = p.getExp();
		int t = (int) (System.currentTimeMillis() / 1000L);		
		int lifeticks = p.getTicksLived();
		int totalxp = p.getTotalExperience();
		int level = p.getLevel();
		int health = p.getHealth();
		int lastonline = 0;
		int gametime = 0;
		
		ResultSet res;
		
		try {			
			if (multi_tables && multi_tables_use_key){
				if(lastonline_enabled){
					res = sql.sqlQuery("SELECT " + multi_table_value_field + " FROM " + multi_table + " WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + lastonline_key_value + "'");
					if(res.next())
						lastonline = res.getInt(multi_table_value_field);
				}
				
				if(lastonline_enabled && gametime_enabled){
					res = sql.sqlQuery("SELECT " + multi_table_value_field + " FROM " + multi_table + " WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + gametime_key_value + "'");
					if(res.next()){
						gametime = res.getInt(multi_table_value_field);
						if(lastonline > 0)
							gametime = gametime + (t - lastonline);
					}
				}
								
				if(onlinestatus_enabled)
					sql.updateQuery("UPDATE " + multi_table + " SET " + multi_table_value_field + " = '" + onlinestatus_valueonline + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + onlinestatus_key_value + "'");
										
				if(totalxp_enabled)
					sql.updateQuery("UPDATE " + multi_table + " SET " + multi_table_value_field + " = '" + totalxp + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + totalxp_key_value + "'");					
								
				if(currentxp_enabled)
					sql.updateQuery("UPDATE " + multi_table + " SET " + multi_table_value_field + " = '" + currentxp + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + currentxp_key_value + "'");						
					
				if(level_enabled)
					sql.updateQuery("UPDATE " + multi_table + " SET " + multi_table_value_field + " = '" + level + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + level_key_value + "'");

				if(health_enabled)
					sql.updateQuery("UPDATE " + multi_table + " SET " + multi_table_value_field + " = '" + health + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + health_key_value + "'");

				if(lifeticks_enabled)
					sql.updateQuery("UPDATE " + multi_table + " SET " + multi_table_value_field + " = '" + lifeticks + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + lifeticks_key_value + "'");						

				if(gametime_enabled)
					sql.updateQuery("UPDATE " + multi_table + " SET " + multi_table_value_field + " = '" + t + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + lastonline_key_value + "'");
									
				if(lastonline_enabled && gametime_enabled)
					sql.updateQuery("UPDATE " + multi_table + " SET " + multi_table_value_field + " = '" + gametime + "' WHERE " + multi_table_user_id_field + " = '" + u + "' and " + multi_table_key_field +" = '" + gametime_key_value + "'");
				
			}else{	
				
				if(multi_tables){
					res = Main.sql.sqlQuery("SELECT * FROM  "+ multi_table +" WHERE " + multi_table_user_id_field + " = '" + u + "'");
				} else {
					res = Main.sql.sqlQuery("SELECT * FROM " + users_table + " WHERE " + user_id_field + " = '" + u + "'");
				}
				
				if(res.next()){
					if(lastonline_enabled)
						lastonline = res.getInt(lastonline_field);

					if(lastonline_enabled && gametime_enabled){
						gametime = res.getInt(gametime_field);
						if(lastonline > 0)
							gametime = gametime + (t - lastonline);
					}
				}				
				
				LinkedList <String> SQLParts = new LinkedList<String>();
				if(onlinestatus_enabled)
					SQLParts.add(Main.onlinestatus_field + " = '" + onlinestatus_valueonline + "'");
	
				if(totalxp_enabled)
					SQLParts.add(Main.totalxp_field + " = '" + totalxp + "'");
					
				if(currentxp_enabled)
					SQLParts.add(Main.currentxp_field + " = '" + currentxp + "'");
															
				if(level_enabled)
					SQLParts.add(Main.level_field + " = '" + level + "'");
					
				if(health_enabled)
					SQLParts.add(Main.health_field + " = '" + health + "'");
				
				if(lifeticks_enabled)
					SQLParts.add(Main.lifeticks_field + " = '" + lifeticks + "'");
				
				if(gametime_enabled)
					SQLParts.add(Main.gametime_field + " = '" + gametime + "'");
				
				if(lastonline_enabled)
					SQLParts.add(Main.lastonline_field + " = '" + t + "'");
			
				
				StringBuilder SQLUpdates = new StringBuilder();
				for(String s: SQLParts){
					if(!SQLUpdates.toString().isEmpty())
						SQLUpdates.append(", ");
					SQLUpdates.append(s);
				}			
				
				if(multi_tables){
					sql.updateQuery("UPDATE " + multi_table + " SET " + SQLUpdates + " WHERE " + multi_table_user_id_field + " = '" + u + "'");
				}else{
					sql.updateQuery("UPDATE " + users_table + " SET " + SQLUpdates + " WHERE " + user_id_field + " = '" + u + "'");						
				}
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log.severe("Broken Save Stats SQL Query, check your config.yml");
			e.printStackTrace();
			disablePlugin();
		}			
	}	
}