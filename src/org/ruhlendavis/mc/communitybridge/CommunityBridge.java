package org.ruhlendavis.mc.communitybridge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import net.netmanagers.api.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.ruhlendavis.mc.utility.Log;
import org.ruhlendavis.utility.StringUtilities;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Main plugin class
 *
 * During a normal startup, first CraftBukkit calls the onEnable method,
 * which in turn calls activate(). If, however, the configuration has a
 * problem, instead of disabling the plugin, which would disable the
 * configuration reload command, we "deactivate" instead. This leaves the
 * plugin "enabled" in the eyes of CraftBukkit so that the reload can be
 * used, but "disabled" in reality to prevent things from going wrong when
 * the configuration is broken. Correspondingly, during a configuration
 * reload, first deactivate() is called if necessary, the new configuration
 * is loaded, and then activate() is called.
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */

public final class CommunityBridge extends JavaPlugin
{
	private static boolean active;
	public static Configuration config;
	@SuppressWarnings("NonConstantLogger")
	public static Log log;
	public static SQL sql;
	public static Economy economy;
	private static CommunityBridge instance = null;
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
		config = new Configuration(this, log);

		getCommand("cbban").setExecutor(new CBCommandExecutor(config, log));
		getCommand("cbunban").setExecutor(new CBCommandExecutor(config, log));
		getCommand("cbrank").setExecutor(new CBCommandExecutor(config, log));
		getCommand("cbreload").setExecutor(new CBCommandExecutor(config, log));
		getCommand("cbsync").setExecutor(new CBCommandExecutor(config, log));
		getCommand("cbsyncall").setExecutor(new CBCommandExecutor(config, log));

		if (config.statisticsEnabled && config.walletEnabled)
		{
	    if (getServer().getPluginManager().getPlugin("Vault") == null)
			{
				log.warning("Wallet statistics tracker requires Vault. Temporarily disabling Wallet tracker");
				config.walletEnabled = false;
			}
			else
			{
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
				{
					log.warning("Failure getting economy service registration. Temporarily disabling Wallet tracker");
					config.walletEnabled = false;
	      }
				else
				{
	        economy = rsp.getProvider();
					if (economy == null)
					{
						log.warning("Failure getting economy provider. Temporarily disabling Wallet tracker");
						config.walletEnabled = false;
					}
				}
			}
		}
		activate();

		if (CommunityBridge.isActive())
		{
			log.info("CommunityBridge is now active.");
		}
	}

	/**
	 * Handles all the setup to be done during activation.
	 */
	public void activate()
	{
		if (config.databaseUsername.equals("username")
		 && config.databasePassword.equals("password"))
		{
			log.severe("You need to set configuration options in the config.yml.");
			deactivate();
			return;
		}

		if (enableSQL(false) == false)
		{
			deactivate();
			return;
		}

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

		webapp = new WebApplication(this, config, log, sql);
		webapp.loadOnlineUserIDsFromDatabase();
		getServer().getPluginManager().registerEvents(new PlayerListener(log, config, webapp), this);

		if (config.linkingAutoRemind)
		{
			reminderStart();
		}

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
					log.severe("Unknown permissions system in config.yml. CommunityBridge deactivated.");
					deactivate();
					return;
				}
			}
			catch (IllegalStateException e)
			{
				log.severe(e.getMessage());
				log.severe("Deactivating CommunityBridge.");
				deactivate();
				return;
			}
		}

		// *** OLD boundary

//		if (config.statisticsEnabled && config.onlineStatusEnabled)
//		{
//			resetOnlineStatus();
//		}
//
//		syncAll();
//
//		if (config.autoSync)
//		{
//			startSyncing();
//		}

		active = true;
		log.finest("CommunityBridge activated.");
	}

	/**
	 * Handles any clean up that needs done when the plugin is disabled.
	 */
	@Override
	public void onDisable()
  {
		deactivate();

		getCommand("cbban").setExecutor(null);
		getCommand("cbunban").setExecutor(null);
		getCommand("cbrank").setExecutor(null);
		getCommand("cbreload").setExecutor(null);
		getCommand("cbsync").setExecutor(null);
		getCommand("cbsyncall").setExecutor(null);

		config = null;

		log.config("Disabled...");
		log = null;
		instance = null;
	}

	/**
	 * Handles any clean up that needs to be done when the plugin is deactivated.
	 */
	public void deactivate()
	{
		active = false;

		// Cancel the tasks: autoRemind and autoSync
		Bukkit.getServer().getScheduler().cancelTasks(this);

		permissionHandler = null;

		// Drop all of our listeners
		HandlerList.unregisterAll(this);

		webapp = null;

		if (metrics != null)
		{
			metrics = null;
		}

		if (sql != null)
    {
			sql.close();
			sql = null;
		}
		log.finest("CommunityBridge deactivated.");
	}

	/**
	 * Returns true if the plugin is active.
	 */
	public static boolean isActive()
	{
		return active;
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
					res = CommunityBridge.sql.sqlQuery("SELECT * FROM " + config.multi_table + " WHERE " + config.multi_table_key_field + " = '" + config.multi_table_key_value + "' AND " + config.multi_table_value_field + " = '" + username + "'");
				}
        else
        {
					res = CommunityBridge.sql.sqlQuery("SELECT * FROM  "+ config.multi_table +" WHERE " + config.multi_table_value_field + " = '" + username + "'");
				}
			}
      else
      {
				res = CommunityBridge.sql.sqlQuery("SELECT * FROM " + config.users_table + " WHERE " + config.user_name_field + " = '" + username + "'");
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
						if (config.statisticsEnabled)
            {
              //CommunityBridge.loadStatistics(id, p);
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
          else if (config.statisticsEnabled)
          {
//						updateStatistics(id, p);
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

	public static boolean isOkayToSetPrimaryGroup(String groupID)
	{
		return config.groupSyncPrimaryEnabled
			  && (groupID == null || !config.primaryGroupIDsToIgnore.contains(groupID));
	}

	/**
	 * (Re)Starts up the SQL connection to the web application.
	 *
	 * @return boolean False if the connection fails for any reason.
	 */
	public boolean enableSQL(boolean reload)
	{
		if (reload)
		{
			sql.close();
		}

		sql = new SQL(config.databaseHost + ":" + config.databasePort,
									config.databaseName + "",
									config.databaseUsername + "",
									config.databasePassword + "");
		sql.initialize();

		if (sql.checkConnection() == false)
		{
			return false;
		}

		if (reload)
		{
			webapp.setSQL(sql);
		}

		if (config.analyzeConfiguration(sql) == false)
		{
			return false;
		}

		return true;
	}
}
