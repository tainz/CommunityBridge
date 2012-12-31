package net.netmanagers.community;

import org.ruhlendavis.mc.communitybridge.PlayerListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import net.netmanagers.api.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.ruhlendavis.mc.communitybridge.PermissionHandler;
import org.ruhlendavis.mc.communitybridge.PermissionHandlerBPermissions;
import org.ruhlendavis.mc.communitybridge.PermissionHandlerGroupManager;
import org.ruhlendavis.mc.communitybridge.PermissionHandlerPermissionsBukkit;
import org.ruhlendavis.mc.communitybridge.PermissionHandlerPermissionsEx;
import org.ruhlendavis.mc.communitybridge.WebApplication;
import org.ruhlendavis.mc.utility.Log;
import org.ruhlendavis.utility.StringUtilities;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public final class Main extends JavaPlugin
{
	@SuppressWarnings("NonConstantLogger")

	public static String thisPluginName = "CommunityBridge";
	public static org.ruhlendavis.mc.communitybridge.Configuration config;
	public static Log log;
	public static SQL sql;
	private static Main instance = null;
	private static Metrics metrics = null;
	public static WebApplication webapp = null;
	public static PermissionHandler permissionHandler;

	/**
	 * Handles all the set up for the plugin.
	 *
	 */
	@Override
	public void onEnable()
  {
		instance = this;
		log = new Log(this.getLogger(), Level.CONFIG);
		config = new org.ruhlendavis.mc.communitybridge.Configuration(this, log);

		if (config.databaseUsername.equals("username")
		 && config.databasePassword.equals("password"))
		{
			log.severe("You need to set configuration options in the config.yml.");
			disablePlugin();
			return;
		}

		sql = new SQL(config.databaseHost + ":" + config.databasePort,
									config.databaseName + "",
									config.databaseUsername + "",
									config.databasePassword + "");
		sql.initialize();

		if (sql.checkConnection() == false)
		{
			disablePlugin();
			return;
		}

		if (config.analyzeConfiguration(sql) == false)
		{
			disablePlugin();
			return;
		}

		webapp = new WebApplication(config, sql, log);

		getServer().getPluginManager().registerEvents(new PlayerListener(log, config, webapp), this);

		if (config.usePluginMetrics)
		{
			try
			{
				metrics = new Metrics(this);
				metrics.start();
				log.fine("Plugin Metrics activated.");
			}
			catch (IOException e)
			{
				log.warning("Plugin Metrics submission failed.");
			}
		}

		if (config.linkingAutoRemind)
		{
			reminderStart();
		}

		// *** OLD boundary
		getCommand("cbban").setExecutor(new Cmds());
		getCommand("cbunban").setExecutor(new Cmds());
		getCommand("cbrank").setExecutor(new Cmds());
		getCommand("cbsync").setExecutor(new Cmds());
		getCommand("cbsyncall").setExecutor(new Cmds());

		// If a feature requires a permissions system we load it up here.
		if (config.permissionsSystemRequired)
		{
			try
			{
				if (config.permissionsSystem.equalsIgnoreCase("PEX"))
				{
					permissionHandler = new PermissionHandlerPermissionsEx();
					log.config("Permissions System: PermissionsEx (PEX)");
				}
				else if (config.permissionsSystem.equalsIgnoreCase("bPerms"))
				{
					permissionHandler = new PermissionHandlerBPermissions();
					log.config("Permissions System: bPermissions (bPerms)");
				}
				else if (config.permissionsSystem.equalsIgnoreCase("GroupManager"))
				{
					permissionHandler = new PermissionHandlerGroupManager();
					log.config("Permissions System: GroupManager");
				}
				else if (config.permissionsSystem.equalsIgnoreCase("PermsBukkit"))
				{
					permissionHandler = new PermissionHandlerPermissionsBukkit();
					log.config("Permissions System: PermissionsBukkit (PermsBukkit)");
				}
				else
				{
					log.severe("Unknown permissions system in config.yml. CommunityBridge disabled.");
					disablePlugin();
					return;
				}
			}
			catch (IllegalStateException e)
			{
				log.severe(e.getMessage());
				log.severe("Disabling CommunityBridge.");
				disablePlugin();
				return;
			}
		}

		if (config.statisticsTrackingEnabled && config.onlinestatusEnabled)
		{
			resetOnlineStatus();
		}

		syncAll();

		if (config.autoSync)
		{
			startSyncing();
		}

		log.config("Enabled!");
	}

	/**
	 * Handles any clean up that needs done when the plugin is disabled.
	 *
	 */
	@Override
	public void onDisable()
  {
		// Toss the metrics object.
		metrics = null;

		// Cancel the tasks we'll restart them later
		Bukkit.getServer().getScheduler().cancelTasks(this);

		// Drop all of our listeners
		HandlerList.unregisterAll(this);

		if (sql != null)
    {
			sql.close();
		}

		permissionHandler = null;
		webapp = null;
		log.config("Disabled...");
		log = null;
		instance = null;
	}

	/**
	 * Disables the plugin.
	 *
	 */
	private static void disablePlugin()
  {
		Bukkit.getServer().getPluginManager().disablePlugin(instance);
	}

	/**
	 * Called by onEnable() if the auto reminder to register is turned on, this
	 * method starts up the reminder task.
	 *
	 */
	// EXPIRABLE: When we remove the deprecated code, this can go away as well.
	@SuppressWarnings("deprecation")
	private void reminderStart()
  {
    long every = config.linkingAutoEvery; // Effectively defaulting to ticks.

    if (config.autoEveryUnit.toLowerCase().startsWith("second"))
    {
      every = config.linkingAutoEvery * 20; // 20 ticks per second.
    }
    else if (config.autoEveryUnit.toLowerCase().startsWith("minute"))
    {
      every = config.linkingAutoEvery * 1200; // 20 ticks per second, 60 sec/minute
    }
    else if (config.autoEveryUnit.toLowerCase().startsWith("hour"))
    {
      every = config.linkingAutoEvery * 72000; // 20 ticks/s 60s/m, 60m/h
    }

		// EXPIRABLE: ST2012-Dec-21: The else block and the if statement itself. The true block should stay
		if (StringUtilities.compareVersion(Bukkit.getBukkitVersion().replace("R", ""), "1.4.5.1.0") > -1)
		{
			// As of MC 1.4.5.1.0, running tasks have changed.
			Bukkit.getScheduler().runTaskTimerAsynchronously(this,
																											new Runnable()
																											{
																												@Override
																												public void run()
																												{
																													remindUnregisteredPlayers();
																												}
																											},
																											every, every);
		}
		else
		{
			Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
			{
				@Override
				public void run()
				{
					remindUnregisteredPlayers();
				}
			}, every, every);
		}
		log.fine("Auto reminder started.");
  }

	/**
	 * Reminds a single player to register if they are not registered.
	 * If linking-kick-unregistered is turned on, an unregistered player will
	 * be kicked.
	 *
	 * @param Player The player to remind.
	 */
  private void remindPlayer(Player player)
  {
		String playerName = player.getName();
		String id = webapp.getUserID(playerName);
    if (id == null || id.isEmpty())
    {
      if (config.linkingKickUnregistered)
      {
        player.kickPlayer(config.messages.get("link-unregistered-player"));
				log.info(playerName + " kicked because they are not registered.");
      }
      else
      {
        player.sendMessage(ChatColor.RED + config.messages.get("link-unregistered-reminder"));
        log.fine(playerName + " issued unregistered reminder notice");
      }
    }
  }

	/**
	 * Calls remindPlayer() for all connected players. Called by the reminder
	 * task.
	 */
  private void remindUnregisteredPlayers()
  {
    log.fine("Running Auto UnRegistered Auto Reminder");

    for (Player player : Bukkit.getOnlinePlayers())
    {
      remindPlayer(player);
    }

  }

	// EXPIRABLE: ST2012-12-21: When we remove the deprecated code, this can go away as well.
	@SuppressWarnings("deprecation")
	private void startSyncing()
  {
    long every = config.autoSyncEvery; // Effectively defaulting to ticks.
    String unit = "ticks";

    if (config.autoEveryUnit.toLowerCase().startsWith("second"))
    {
      every = config.autoSyncEvery * 20; // 20 ticks per second.
      unit = "seconds";
    }
    else if (config.autoEveryUnit.toLowerCase().startsWith("minute"))
    {
      every = config.autoSyncEvery * 1200; // 20 ticks per second, 60 sec per minute
      unit = "minutes";
    }
    else if (config.autoEveryUnit.toLowerCase().startsWith("hour"))
    {
      every = config.autoSyncEvery * 72000; // 20 ticks/s 60s/m, 60m/h
      unit = "hours";
    }

		log.config(String.format("Auto Sync Every: %d %s.", config.autoSyncEvery, unit));
		// EXPIRABLE: ST2012-Dec-21: The else block and the if statement itself. The true block should stay
		if (StringUtilities.compareVersion(Bukkit.getBukkitVersion().replace("R", ""), "1.4.5.1.0") > -1)
		{
			// As of MC 1.4.5.1.0, running tasks have changed.
			Bukkit.getScheduler().runTaskTimerAsynchronously(this,
																											new Runnable()
																											{
																												@Override
																												public void run()
																												{
																													syncAll();
																												}
																											},
																											every, every);
		}
		else
		{
			Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
			{
				@Override
				public void run()
				{
					syncAll();
				}
			}, every, every);
		}
	}

	private void resetOnlineStatus()
  {
		try {
			if (config.multiTables)
      {
				if (config.multiTablesUseKey)
        {
					sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.multi_table_value_field + " = '" + config.onlinestatusValueOffline + "' WHERE " + config.multi_table_key_field + " = '" + config.onlinestatusKeyValue + "'");
				}
        else
        {
					sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.onlinestatusColumn + " = '" + config.onlinestatusValueOffline + "' WHERE " + config.onlinestatusColumn + " = '" + config.onlinestatusValueOnline + "'");
				}
			}
      else
      {
				sql.updateQuery("UPDATE " + config.users_table + " SET " + config.onlinestatusColumn + " = '" + config.onlinestatusValueOffline + "'  WHERE " + config.onlinestatusColumn + " = '" + config.onlinestatusValueOnline + "'");
			}
		}
		catch (MalformedURLException e)
		{
			log.severe("Error in ResetOnlineStatus: " + e.getMessage());
			disablePlugin();
		} catch (InstantiationException e) {
			log.severe("Error in ResetOnlineStatus: " + e.getMessage());
			disablePlugin();
		} catch (IllegalAccessException e) {
			log.severe("Error in ResetOnlineStatus: " + e.getMessage());
			disablePlugin();
		}
	}

	public static int getUserId(String username)
	{
		return webapp.getUserIDint(username);
	}

	public static ResultSet getOnlinePlayerInfo(String username) {
		try {
			ResultSet res;
			if (config.multiTables)
      {
				if (config.multiTablesUseKey)
        {
					res = Main.sql.sqlQuery("SELECT * FROM " + config.multi_table + " WHERE " + config.multi_table_key_field + " = '" + config.multi_table_key_value + "' AND " + config.multi_table_value_field + " = '" + username + "'");
				}
        else
        {
					res = Main.sql.sqlQuery("SELECT * FROM  "+ config.multi_table +" WHERE " + config.multi_table_value_field + " = '" + username + "'");
				}
			}
      else
      {
				res = Main.sql.sqlQuery("SELECT * FROM " + config.users_table + " WHERE " + config.user_name_field + " = '" + username + "'");
			}

			if (res.next())
      {
				return res;
			}
		} catch (MalformedURLException e) {

		} catch (InstantiationException e) {

		} catch (IllegalAccessException e) {

		}
		catch (SQLException e)
		{
			log.severe("Error in getOnlinePlayerInfo(): " + e.getMessage());
			log.severe("Broken Get Online Player Info SQL Query, check your config.yml");
			disablePlugin();
		}
		return null;
	}

	/**
	 * A case insensitive check to see if a given group is in the group mapping.
	 *
	 * @param groupName String containing the group name to search for.
	 * @return true if the group is in the mapping.
	 */
	public static boolean inGroupMap(String groupName)
	{
		for (Object value : config.groups.values())
		{
			if (((String)value).equalsIgnoreCase(groupName))
			{
				return true;
			}
		}

		return false;
	}

	public static boolean setGroup(String groupName, Player player, boolean n)
  {
    try
    {
      if (config.permissionsSystem.equalsIgnoreCase("PEX"))
      {
        if (permissionHandler.isMemberOfGroup(player.getName(), groupName))
        {}
				else
				{
          PermissionsEx.getUser(player).setGroups(new String[] { groupName });
          if (n)
          {
            log.fine((new StringBuilder("Set ")).append(player.getName()).append(" to group ").append(groupName).toString());
          }
          return true;
        }
      }
      else if (config.permissionsSystem.equalsIgnoreCase("bPerms"))
			{
				String command = "world " + player.getWorld().getName();
				log.finest("setGroup(): " + command);
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
				command = "user " + player.getName();
				log.finest("setGroup(): " + command);
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
				command = "user setgroup " + groupName;
				log.finest("setGroup(): " + command);
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);

				if (n)
        {
          log.fine((new StringBuilder("Set ")).append(player.getName()).append(" to group ").append(groupName).toString());
        }
        return true;
      }
      else if (config.permissionsSystem.equalsIgnoreCase("GroupManager"))
      {
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "manuadd " + player.getName() + " " + groupName);
        if (n)
        {
          log.fine((new StringBuilder("Setting ")).append(player.getName()).append(" to group ").append(groupName).toString());
        }
        return true;
      }
      else if (config.permissionsSystem.equalsIgnoreCase("PermsBukkit"))
      {
        String cmd = "permissions player setgroup " + player.getName() + " " + groupName;
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);

        if (n)
        {
          log.fine((new StringBuilder("Set ")).append(player.getName()).append(" to group ").append(groupName).toString());
        }
        return true;
      }
    } catch (Error e) {
      log.severe(e.getMessage());
    }

    return false;
	}

	public static boolean addGroup(String groupName, Player player, boolean n)
	{
		 try
		 {
			 if (config.permissionsSystem.equalsIgnoreCase("PEX"))
			 {
	       if (permissionHandler.isMemberOfGroup(player.getName(), groupName))
		     {}
				 else
				 {
					 PermissionsEx.getUser(player).addGroup(groupName);

					 if (n)
           {
             log.fine((new StringBuilder("Added ")).append(player.getName()).append(" to group ").append(groupName).toString());
           }
					 return true;
				 }
			 }
       else
			 {
				 if (config.permissionsSystem.equalsIgnoreCase("bPerms"))
				 {
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), (new StringBuilder("world ")).append(player.getWorld().getName()).toString());
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), (new StringBuilder("user ")).append(player.getName()).toString());
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), (new StringBuilder("user addgroup ")).append(groupName).toString());
					 if (n)
           {
             log.fine((new StringBuilder("Added ")).append(player.getName()).append(" to group ").append(groupName).toString());
           }
					 return true;
				 }
				 if (config.permissionsSystem.equalsIgnoreCase("GroupManager"))
				 {
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "manuadd " + player.getName() + " " + groupName);
					 if (n)
           {
             log.fine((new StringBuilder("Adding ")).append(player.getName()).append(" to group ").append(groupName).toString());
           }
					 return true;
				 }
				 if (config.permissionsSystem.equalsIgnoreCase("PermsBukkit"))
				 {
					 Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + player.getName() + " " + groupName);
					 if (n)
           {
             log.fine((new StringBuilder("Adding ")).append(player.getName()).append(" to group ").append(groupName).toString());
           }
					 return true;
				 }
			 }
		 }
		 catch (Error e)
		 {
			 log.severe(e.getMessage());
		 }

		 return false;
	}

	/**
	 * Returns a database group ID from a permissions group name
	 *
	 * @param groupName String containing group name to search for.
	 * @return String containing database group ID or null if not found.
	 */
	public static String getGroupID(String groupName)
	{
		for (Map.Entry<String, Object> entry : config.groups.entrySet())
		{
			if (((String)entry.getValue()).equalsIgnoreCase(groupName))
			{
				return (String)entry.getKey();
			}
		}

		return null;
	}
	/**
	 * Returns a permissions group name from a database group ID
	 *
	 * @param groupID  String containing group ID to search for.
	 * @return String containing permissions group name or null if not found.
	 */
	public static String getGroupName(String groupID)
  {
		for (Map.Entry<String, Object> entry : config.groups.entrySet())
		{
			if (entry.getKey().equalsIgnoreCase(groupID))
			{
				return (String)entry.getValue();
			}
		}

		return null;
	}

	/**
	 * Returns exact permissions group name from (presumably user entered)
	 * permissions group name
	 *
	 * @param groupName  String containing group name to search for.
	 * @return String containing group name or null if not found.
	 */
	public static String getGroupNameFull(String groupName)
	{
		for (Object value : config.groups.values())
		{
			if (((String)value).equalsIgnoreCase(groupName))
			{
				return (String)value;
			}
		}

		return null;
	}

	public static void syncAll() {
		log.fine("Running Auto Sync");
		for (Player play : Bukkit.getOnlinePlayers())
    {
			syncPlayer(play, false);
		}
	}

	public static void syncPlayer(Player p, boolean firstsync)
	{
		String groupID = "";
		String groupName = "";

		try
		{
			int id = getUserId(p.getName());
			if (id > 0)
      {
				ResultSet res = sql.sqlQuery("SELECT * FROM " + config.users_table
								                   + " WHERE " + config.user_id_field + " = '" + id + "'");
				if (res.next())
        {
					if (config.useBanned)
          {
						boolean banned = res.getBoolean(config.is_banned_field);

						if (banned)
            {
              p.kickPlayer("You have been banned from the site.");
            }
					}

					boolean requirements_met = true;

					if (config.require_minposts)
          {
						if (!checkMinPosts(id, p))
            {
              requirements_met = false;
            }
					}

					if (config.require_avatar) {
						if (!checkAvatar(id, p))
            {
              requirements_met = false;
            }
					}

					if (config.groupSyncPrimaryEnabled)
					{
						// Note: groups is a map <String, Object> so we need the cast.
						groupID = res.getString(config.groups_id_field);
						groupName = (String)config.groups.get(groupID);

						if (config.banlistTableEnabled)
						{
							if (res.getString(config.groups_id_field).equalsIgnoreCase(config.banned_users_group))
							{
								p.kickPlayer("You have been banned from the site.");
							}
						}

						if (requirements_met)
						{
							if (isOkayToSetPrimaryGroup(groupID))
							{
								setGroup(groupName, p, firstsync);
							}
							else
							{
								log.finer(p.getName()
												+ "'s primary group not synchronized due to config.");
							}
						}
						else
						{
							if (isOkayToSetPrimaryGroup(groupID))
							{
								setGroup(config.defaultGroup, p, firstsync);
							}
							else
							{
								log.finer(p.getName()
												+ "'s  primary group not synchronized due to config.");
							}
						}
					}

					if (config.secondary_groups)
					{
						String extra_groups = res.getString(config.secondary_groups_id_field);
						if (extra_groups.length() > 0)
						{
							for(String g: extra_groups.split(","))
							{
								if (!g.isEmpty())
								{
									addGroup((String)config.groups.get(g), p, firstsync);
								}
							}
						}
					}

					if (firstsync)
          {
						if (config.statisticsTrackingEnabled)
            {
              Main.loadStatistics(id, p);
            }

						if (config.linkingNotifyRegistered)
						{
							if (isOkayToSetPrimaryGroup(groupID))
							{
								String message = config.messages.get("link-registered-player-group");
								message = message.replace("~GROUPNAME~", groupName);
								p.sendMessage(ChatColor.YELLOW + message);
							}
						}
					}
          else if (config.statisticsTrackingEnabled)
          {
						updateStatistics(id, p);
					}
				}
			}
      else
      {
				if (firstsync)
				{
					if (config.linkingNotifyUnregistered)
					{
						p.sendMessage(ChatColor.RED + config.messages.get("link-unregistered-player"));
					}
					log.fine(p.getName() + "'s name not set or not registered on community site");
				}
				else
				{
					p.sendMessage(ChatColor.RED + config.messages.get("link-unregistered-reminder"));
					log.fine(p.getName() + " issued unregistered reminder notice");
				}

				if (isOkayToSetPrimaryGroup(null))
				{
					setGroup(config.defaultGroup, p, true);
				}
				else
				{
					log.finer(p.getName() + "'s  primary group not set to default due to config.");
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

	public static void checkDBSanity(int u, String keyval)
  {
		ResultSet res;
		try
    {
			res = sql.sqlQuery("SELECT 1 FROM " + config.multi_table + " WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '" + keyval + "'");
			if (!res.next())
      {
        sql.insertQuery("INSERT INTO " + config.multi_table + " (`"+config.multi_table_user_id_field+"`, `"+config.multi_table_key_field+"`, `"+config.multi_table_value_field+"`) VALUES ('" + u + "', '" + keyval + "', 0)");
      }

		}
		catch (MalformedURLException e)
		{
			log.severe("Error in checkDBSanity(): " + e.getMessage());
		}
		catch (InstantiationException e)
		{
			log.severe("Error in checkDBSanity(): " + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			log.severe("Error in checkDBSanity(): " + e.getMessage());
		}
		catch (SQLException e)
		{
			log.severe("Error in checkDBSanity(): " + e.getMessage());
			log.severe("Database SQL Error with " + keyval);
			disablePlugin();
		}
	}

	public static boolean checkAvatar(int u, Player p){
		ResultSet res;

		try {
			res = sql.sqlQuery("SELECT " + config.avatar_field + " FROM " + config.avatar_table + " WHERE " + config.avatar_user_field + " = '" + u + "'");
			if (res == null)
      {
        return false;
      }

			if (res.next())
      {
        if (!res.getString(config.avatar_field).isEmpty())
        {
          return true;
        }
      }

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

		p.sendMessage(ChatColor.YELLOW + config.avatar_message);
		log.fine((new StringBuilder("Notice Issued to ")).append(p.getName()).append(" for not having profile avatar").toString());
		return false;
	}

	public static boolean checkMinPosts(int u, Player p){
		ResultSet res;

		try {
			res = sql.sqlQuery("SELECT " + config.minposts_field + " FROM " + config.minposts_table + " WHERE " + config.minposts_user_field + " = '" + u + "'");
			if (res == null)
      {
        return false;
      }

			if (res.next())
      {
        if (res.getInt(config.minposts_field) >= config.minposts_required)
        {
          return true;
        }
      }

		}
		catch (MalformedURLException e)
		{
			log.severe("Error in checkMinPosts():" + e.getMessage());
		}
		catch (InstantiationException e)
		{
			log.severe("Error in checkMinPosts():" + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			log.severe("Error in checkMinPosts():" + e.getMessage());
		}
		catch (SQLException e)
		{
			log.severe("Error in checkMinPosts():" + e.getMessage());
			log.severe("Broken Post Count SQL Query, check your config.yml");
			disablePlugin();
		}
		p.sendMessage(ChatColor.YELLOW + config.minposts_message);
		log.fine((new StringBuilder("Notice Issued to ")).append(p.getName()).append(" for having less than ").append(config.minposts_required).append(" posts").toString());

		return false;
	}

	public static void loadStatistics(int u, Player p){

		int t = (int) (System.currentTimeMillis() / 1000L);

		try {
			ResultSet res;
			if (config.multiTables && config.multiTablesUseKey)
      {
				// Check for each custom field in the database.
				if (config.onlinestatusEnabled)
        {
					checkDBSanity(u, config.onlinestatusKeyValue);
					sql.updateQuery("UPDATE "+config.multi_table+" SET " + config.multi_table_value_field +" = '" + config.onlinestatusValueOnline + "' WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '" + config.onlinestatusKeyValue + "'");
				}

				if (config.lastonlineEnabled)
        {
					checkDBSanity(u, config.lastonlineKeyValue);
					sql.updateQuery("UPDATE " + config.multi_table
									       + " SET " + config.multi_table_value_field + " = '" + t
									       + "' WHERE " + config.multi_table_user_id_field + " = '" + u
									       + "' AND " + config.multi_table_key_field + " = '"
									       + config.lastonlineKeyValue + "'");
				}

				if (config.currentxpEnabled)
        {
					checkDBSanity(u, config.currentxpKeyValue);
					res = sql.sqlQuery("SELECT " + config.multi_table_value_field + " FROM " + config.multi_table + " WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '"+ config.currentxpKeyValue + "'");
					if (res.next())
          {
            p.setExp(res.getInt(config.multi_table_value_field));
          }
				}

				if (config.totalxpEnabled)
        {
					checkDBSanity(u, config.totalxpKeyValue);
					res = sql.sqlQuery("SELECT " + config.multi_table_value_field + " FROM " + config.multi_table + " WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '"+ config.totalxpKeyValue + "'");
					if (res.next())
          {
            p.setTotalExperience(res.getInt(config.multi_table_value_field));
          }
				}

				if (config.lifeticksEnabled)
        {
					checkDBSanity(u, config.lifeticksKeyValue);
					res = sql.sqlQuery("SELECT " + config.multi_table_value_field + " FROM " + config.multi_table + " WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '"+ config.lifeticksKeyValue + "'");
					if (res.next())
          {
            if (res.getInt(config.multi_table_value_field) > 0)
            {
              p.setTicksLived(res.getInt(config.multi_table_value_field));
            }
          }
				}

				if (config.levelEnabled)
        {
					checkDBSanity(u, config.levelKeyValue);
					res = sql.sqlQuery("SELECT " + config.multi_table_value_field + " FROM " + config.multi_table + " WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '"+ config.levelKeyValue+"'");
					if (res.next())
          {
            p.setLevel(res.getInt(config.multi_table_value_field));
          }
				}

				if (config.healthEnabled)
        {
					checkDBSanity(u, config.healthKeyValue);
					res = sql.sqlQuery("SELECT " + config.multi_table_value_field + " FROM " + config.multi_table + " WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '"+config.healthKeyValue+"'");
					if (res.next())
          {
            if (res.getInt(config.multi_table_value_field)>0)
            {
              p.setHealth(res.getInt(config.multi_table_value_field));
            }
          }
				}

			}
      else
      {
				if (config.multiTables)
        {
					res = sql.sqlQuery("SELECT * FROM " + config.multi_table + " WHERE " + config.multi_table_value_field + " = '" + p.getName() + "'");

					if (config.onlinestatusEnabled)
          {
            sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.onlinestatusColumn + " = '" + config.onlinestatusValueOnline + "' WHERE " + config.multi_table_user_id_field + " = '" + u + "'");
          }

					if (config.lastonlineEnabled)
          {
            sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.lastonlineColumn + " = " + t + " WHERE " + config.multi_table_user_id_field + " = '" + u + "'");
          }
				}
        else
        {
					res = sql.sqlQuery("SELECT * FROM " + config.users_table + " WHERE " + config.user_name_field + " = '" + p.getName() + "'");

					if (config.onlinestatusEnabled)
          {
            sql.updateQuery("UPDATE " + config.users_table + " SET " + config.onlinestatusColumn + " = '" + config.onlinestatusValueOnline + "' WHERE " + config.user_id_field + " = '" + u + "'");
          }

					if (config.lastonlineEnabled)
          {
            sql.updateQuery("UPDATE " + config.users_table + " SET " + config.lastonlineColumn + " = " + t +" WHERE " + config.user_id_field + " = '" + u + "'");
          }

					if (!res.next())
          {
            return;
          }
				}

				if (res.next())
        {
					if (config.currentxpEnabled)
          {
            p.setExp(res.getInt(config.currentxpColumn));
          }

					if (config.totalxpEnabled)
          {
            p.setTotalExperience(res.getInt(config.totalxpColumn));
          }

					if (config.lifeticksEnabled)
          {
						if (res.getInt(config.lifeticksColumn) > 0)
            {
              p.setTicksLived(res.getInt(config.lifeticksColumn));
            }
					}

					if (config.levelEnabled)
          {
            p.setLevel(res.getInt(config.levelColumn));
          }

					if (config.healthEnabled)
          {
						if (res.getInt(config.healthColumn) > 0)
            {
              p.setHealth(res.getInt(config.healthColumn));
            }
					}
				}
			}
		}
		catch (MalformedURLException e)
		{
			log.severe("Error in LoadTrackingStats(): " + e.getMessage());
		}
		catch (InstantiationException e)
		{
			log.severe("Error in LoadTrackingStats(): " + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			log.severe("Error in LoadTrackingStats(): " + e.getMessage());
		}
		catch (SQLException e)
		{
			log.severe("Error in LoadTrackingStats(): " + e.getMessage());
			log.severe("Broken Stat Tracking SQL Query, check your config.yml");
			disablePlugin();
		}
	}

  public static String timeElapsedtoString(int time)
  {
    if (time == 0)
    {
      return "0 seconds";
    }

    String elapsed = "";

    if (time >= 86400)
    {
      elapsed = elapsed
              + time / 86400 + " day"
              + (time >= 172800 ? "s" : "");
      time = time - 86400 * (time / 86400); // Ah, the joys of integer math.
    }

    if (time >= 3600)
    {
      elapsed = elapsed
              + (!elapsed.isEmpty() ? ", " : "")
              + time / 3600 + " hour"
              + (time >= 7200 ? "s" : "");
      time = time - 3600 * (time / 3600);
    }

    if (time >= 60)
    {
      elapsed = elapsed
              + (!elapsed.isEmpty() ? ", " : "")
              + time / 60 + " minute"
              + (time >= 120 ? "s" : "");
      time = time - 60 * (time / 60);
    }

    if (time > 0)
    {
      elapsed = elapsed
              + (!elapsed.isEmpty() ? ", " : "")
              + time + " second"
              + (time >= 1 ? "s" : "");
    }

		if (elapsed.length() >= 60)
		{
			elapsed = elapsed.substring(0, 60);
		}

    return elapsed;
  }


	public static void updateStatistics(int u, Player p)
  {
		float currentxp = p.getExp();
		String currentxp_formatted = ((int)(currentxp * 100)) + "%";
		int t = (int) (System.currentTimeMillis() / 1000L);
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss a");
		int lifeticks = p.getTicksLived();
		String lifeticks_formatted = timeElapsedtoString((int)(lifeticks / 20));
		int totalxp = p.getTotalExperience();
		int level = p.getLevel();
		int health = p.getHealth();
		int lastonline = 0;
		int gametime = 0;
    String timeElapsed = "";

		ResultSet res;

		try {
			if (config.multiTables && config.multiTablesUseKey)
      {
				if (config.lastonlineEnabled)
        {
					res = sql.sqlQuery("SELECT " + config.multi_table_value_field + " FROM " + config.multi_table + " WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '" + config.lastonlineKeyValue + "'");
					if (res.next())
          {
            lastonline = res.getInt(config.multi_table_value_field);
          }
				}

				if (config.lastonlineEnabled && config.gametimeEnabled)
        {
					res = sql.sqlQuery("SELECT " + config.multi_table_value_field + " FROM " + config.multi_table + " WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '" + config.gametimeKeyValue + "'");

          if (res.next())
          {
						gametime = res.getInt(config.multi_table_value_field);

						if (lastonline > 0)
            {
              gametime = gametime + (t - lastonline);
              timeElapsed = timeElapsedtoString(gametime);
            }
					}
				}

				if (config.onlinestatusEnabled)
        {
          sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.multi_table_value_field + " = '" + config.onlinestatusValueOnline + "' WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '" + config.onlinestatusKeyValue + "'");
        }

				if (config.totalxpEnabled)
        {
          sql.updateQuery("UPDATE " + config.multi_table + " SET " +config.multi_table_value_field + " = '" + totalxp + "' WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '" + config.totalxpKeyValue + "'");
        }

				if (config.currentxpEnabled)
        {
          sql.updateQuery("UPDATE " + config.multi_table
									       + " SET " + config.multi_table_value_field + " = '" + currentxp
									       + "' WHERE " + config.multi_table_user_id_field + " = '" + u
									       + "' AND " + config.multi_table_key_field + " = '"
									       + config.currentxpKeyValue + "'");

					if (!config.currentxpFormattedKeyValue.isEmpty())
					{
						sql.updateQuery("UPDATE " + config.multi_table
													 + " SET " + config.multi_table_value_field + " = '"
													 + currentxp_formatted
													 + "' WHERE " + config.multi_table_user_id_field + " = '" + u
													 + "' AND " + config.multi_table_key_field + " = '"
													 + config.currentxpFormattedKeyValue + "'");
					}
        }

				if (config.levelEnabled)
        {
          sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.multi_table_value_field + " = '" + level + "' WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '" + config.levelKeyValue + "'");
        }

				if (config.healthEnabled)
        {
          sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.multi_table_value_field + " = '" + health + "' WHERE " + config.multi_table_user_id_field + " = '" + u + "' and " + config.multi_table_key_field +" = '" + config.healthKeyValue + "'");
        }

				if (config.lifeticksEnabled)
        {
          sql.updateQuery("UPDATE " + config.multi_table
									       + " SET " + config.multi_table_value_field + " = '"
									       + lifeticks
									       + "' WHERE " + config.multi_table_user_id_field + " = '" + u
									       + "' AND " + config.multi_table_key_field + " = '"
									       + config.lifeticksKeyValue + "'");
					if (!config.lifeticksFormattedKeyValue.isEmpty())
					{
						sql.updateQuery("UPDATE " + config.multi_table
													 + " SET " + config.multi_table_value_field + " = '"
													 + lifeticks_formatted
													 + "' WHERE " + config.multi_table_user_id_field + " = '" + u
													 + "' AND " + config.multi_table_key_field + " = '"
													 + config.lifeticksFormattedKeyValue + "'");
					}
        }

				if (config.lastonlineEnabled)
        {
					sql.updateQuery("UPDATE " + config.multi_table
                         + " SET " + config.multi_table_value_field + " = '" + t
                         + "' WHERE " + config.multi_table_user_id_field + " = '" + u
                         + "' AND " + config.multi_table_key_field +" = '"
                         + config.lastonlineKeyValue + "'");

          if (!config.lastonlineFormattedKeyValue.isEmpty())
          {
            sql.updateQuery("UPDATE " + config.multi_table
                           + " SET " + config.multi_table_value_field + " = '"
                           + format.format(date)
                           + "' WHERE " + config.multi_table_user_id_field + " = '" + u
                           + "' AND " + config.multi_table_key_field + " = '"
                           + config.lastonlineFormattedKeyValue + "'");
          }
        }

				if (config.lastonlineEnabled && config.gametimeEnabled)
        {
					sql.updateQuery("UPDATE " + config.multi_table
                         + " SET " + config.multi_table_value_field + " = '" + gametime
                         + "' WHERE " + config.multi_table_user_id_field + " = '" + u
                         + "' AND " + config.multi_table_key_field + " = '"
                         + config.gametimeKeyValue + "'");

          if (!config.gametimeFormattedKeyValue.isEmpty())
          {
            sql.updateQuery("UPDATE " + config.multi_table
                           + " SET " + config.multi_table_value_field + " = '"
                           + timeElapsed + "'"
                           + " WHERE " + config.multi_table_user_id_field + " = '" + u
                           + "' AND " + config.multi_table_key_field + " = '"
                           + config.gametimeFormattedKeyValue + "'");
          }
        }
			}
      else
      {
				if (config.multiTables)
        {
					res = Main.sql.sqlQuery("SELECT * FROM  "+ config.multi_table +" WHERE " + config.multi_table_user_id_field + " = '" + u + "'");
				}
        else
        {
					res = Main.sql.sqlQuery("SELECT * FROM " + config.users_table + " WHERE " + config.user_id_field + " = '" + u + "'");
				}

				if (res.next())
        {
					if (config.lastonlineEnabled)
          {
            lastonline = res.getInt(config.lastonlineColumn);
          }

					if (config.lastonlineEnabled && config.gametimeEnabled)
          {
						gametime = res.getInt(config.gametimeColumn);
						if (lastonline > 0)
            {
              gametime = gametime + (t - lastonline);
              timeElapsed = timeElapsedtoString(gametime);
            }
					}
				}

				LinkedList <String> SQLParts = new LinkedList<String>();
				if (config.onlinestatusEnabled)
        {
          SQLParts.add(config.onlinestatusColumn + " = '" + config.onlinestatusValueOnline + "'");
        }

				if (config.totalxpEnabled)
        {
          SQLParts.add(config.totalxpColumn + " = '" + totalxp + "'");
        }

				if (config.currentxpEnabled)
        {
          SQLParts.add(config.currentxpColumn + " = '" + currentxp + "'");

					if (!config.currentxpFormattedColumn.isEmpty())
					{
						SQLParts.add(config.currentxpFormattedColumn + " = '"
										    + currentxp_formatted + "'");
					}
        }

				if (config.levelEnabled)
        {
          SQLParts.add(config.levelColumn + " = '" + level + "'");
        }

				if (config.healthEnabled)
        {
          SQLParts.add(config.healthColumn + " = '" + health + "'");
        }

				if (config.lifeticksEnabled)
        {
          SQLParts.add(config.lifeticksColumn + " = '" + lifeticks + "'");

					if (!config.lifeticksFormattedColumn.isEmpty())
					{
						SQLParts.add(config.lifeticksFormattedColumn
										    + " = '" + lifeticks_formatted + "'");
					}
        }

				if (config.gametimeEnabled)
        {
					SQLParts.add(config.gametimeColumn + " = '" + gametime + "'");

          if (!config.gametimeFormattedColumn.isEmpty())
          {
            SQLParts.add(config.gametimeFormattedColumn + " = '"
                        + timeElapsed
                        + "'");
          }
        }

				if (config.lastonlineEnabled)
        {
					SQLParts.add(config.lastonlineColumn + " = '" + t + "'");

          if (!config.lastonlineFormattedColumn.isEmpty())
          {
            SQLParts.add(config.lastonlineFormattedColumn + " = '"
                        + format.format(date)
                        + "'");
          }
        }

				StringBuilder SQLUpdates = new StringBuilder();
				for(String s: SQLParts)
        {
					if (!SQLUpdates.toString().isEmpty())
          {
            SQLUpdates.append(", ");
          }
					SQLUpdates.append(s);
				}

				if (config.multiTables)
        {
					sql.updateQuery("UPDATE " + config.multi_table + " SET " + SQLUpdates + " WHERE " + config.multi_table_user_id_field + " = '" + u + "'");
				}
        else
        {
					sql.updateQuery("UPDATE " + config.users_table + " SET " + SQLUpdates + " WHERE " + config.user_id_field + " = '" + u + "'");
				}
			}
		}
		catch (SQLException e)
		{
			log.severe("Error in UpdateTrackingStats(): " + e.getMessage());
		}
		catch (MalformedURLException e)
		{
			log.severe("Error in UpdateTrackingStats(): " + e.getMessage());
		}
		catch (InstantiationException e)
		{
			log.severe("Error in UpdateTrackingStats(): " + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			log.severe("Error in UpdateTrackingStats(): " + e.getMessage());
			log.severe("Broken Save Stats SQL Query, check your config.yml");
			disablePlugin();
		}
	}

	public static boolean isOkayToSetPrimaryGroup(String groupID)
	{
		return config.groupSyncPrimaryEnabled
			  && (groupID == null || !config.primaryGroupIDsToIgnore.contains(groupID));
	}
}
