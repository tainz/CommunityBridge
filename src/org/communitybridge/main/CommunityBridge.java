package org.communitybridge.main;

import java.io.IOException;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.communitybridge.linker.UserPlayerLinker;
import org.communitybridge.main.CBMetrics.Graph;
import org.communitybridge.utility.Log;
import org.communitybridge.permissionhandlers.*;
import org.communitybridge.utility.MinecraftUtilities;
import org.communitybridge.utility.StringUtilities;

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

public class CommunityBridge extends JavaPlugin
{
	private Environment environment = new Environment();

	private static boolean active;
	private static CBMetrics metrics;

	@Override
	public void onEnable()
  {
		setupEnvironment();
		javaVersionCheck();

		if (StringUtilities.compareVersion(MinecraftUtilities.getBukkitVersion(), "1.7.9") < 0)
		{
			environment.getLog().severe("This version of CommunityBridge requires Bukkit 1.7.9 or later.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		CBCommandExecutor command = new CBCommandExecutor(environment);
		getCommand("cbreload").setExecutor(command);
		getCommand("cbsync").setExecutor(command);
		getCommand("cbsyncall").setExecutor(command);

//		getCommand("cbban").setExecutor(new CBCommandExecutor(config, log));
//		getCommand("cbunban").setExecutor(new CBCommandExecutor(config, log));
//		getCommand("cbrank").setExecutor(new CBCommandExecutor(config, log));

		activate();

		if (CommunityBridge.isActive())
		{
			environment.getLog().info("CommunityBridge is now active.");
		}
	}

	private void setupEnvironment()
	{
		environment.setBukkit(new BukkitWrapper());
		environment.setPlugin(this);
		environment.setLog(new Log(this.getLogger(), Level.CONFIG));
		environment.setConfiguration(new Configuration(environment));

		// PermissionHandler set by Configuration initialization.

		environment.setUserPlayerLinker(new UserPlayerLinker(environment, Bukkit.getMaxPlayers() * 4));
	}

	public void activate()
	{
		if (environment.getConfiguration().databaseUsername.equals("username")
		 && environment.getConfiguration().databasePassword.equals("password"))
		{
			environment.getLog().severe("You need to set configuration options in the config.yml.");
			deactivate();
			return;
		}

		environment.setWebApplication(new WebApplication(environment));

		getServer().getPluginManager().registerEvents(new PlayerListener(environment), this);

		if (environment.getConfiguration().economyEnabled	|| environment.getConfiguration().walletEnabled)
		{
	    if (getServer().getPluginManager().getPlugin("Vault") == null)
			{
				environment.getLog().warning("Vault not present. Temporarily disabling economy based features.");
				disableEconomyBasedFeatures();
			}
			else
			{
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
				{
					environment.getLog().warning("Failure getting economy service registration. Is an economy plugin installed? Temporarily disabling economy based features.");
					disableEconomyBasedFeatures();
	      }
				else
				{
	        environment.setEconomy(rsp.getProvider());
					if (environment.getEconomy() == null)
					{
						environment.getLog().warning("Failure getting economy provider. Temporarily disabling economy based features.");
						disableEconomyBasedFeatures();
					}
				}
			}
		}

		activateMetrics();

		if (environment.getConfiguration().linkingAutoRemind)
		{
			reminderStart();
		}

		if (environment.getConfiguration().autoSync)
		{
			autosyncStart();
		}

		active = true;
		environment.getLog().finest("CommunityBridge activated.");
	}

	/**
	 * Handles any clean up that needs done when the plugin is disabled.
	 */
	@Override
	public void onDisable()
  {
		deactivate();

//		getCommand("cbban").setExecutor(null);
//		getCommand("cbunban").setExecutor(null);
//		getCommand("cbrank").setExecutor(null);
		getCommand("cbreload").setExecutor(null);
		getCommand("cbsync").setExecutor(null);
		getCommand("cbsyncall").setExecutor(null);

		environment.getLog().config("Disabled...");
		environment = null;
	}

	/**
	 * Handles any clean up that needs to be done when the plugin is deactivated.
	 */
	public void deactivate()
	{
		active = false;

		// Cancel the tasks: autoRemind and autoSync
		Bukkit.getServer().getScheduler().cancelTasks(this);

		// Drop all of our listeners
		HandlerList.unregisterAll(this);

		if (metrics != null)
		{
			try
			{
				metrics.cancelTask();
			}
			catch (NoSuchMethodError exception)
			{
				environment.getLog().warning("Metrics cancelTask() method unavailable: " + exception.getMessage());
			}
			metrics = null;
		}

		if (environment.getSql() != null)
    {
			environment.getSql().close();
		}

		if (environment.getEconomy() != null)
		{
			environment.setEconomy(null);
		}

		environment.getLog().finest("CommunityBridge deactivated.");
	}

	public static boolean isActive()
	{
		return active;
	}

	private void reminderStart()
  {
		MinecraftUtilities.startTaskTimer(this,
																			calculateTaskTicks(environment.getConfiguration().linkingAutoEvery),
																			new Runnable()
																			{
																				@Override
																				public void run()
																				{
																					remindUnregisteredPlayers();
																				}
																			}
																		 );
		environment.getLog().fine("Auto reminder started.");
  }

	/**
	 * Called by activate() if the auto sync is turned on, this starts up the
	 * auto synchronization task runner.
	 */
	private void autosyncStart()
  {
		MinecraftUtilities.startTaskTimer(this,
																			calculateTaskTicks(environment.getConfiguration().autoSyncEvery),
																			new Runnable()
																			{
																				@Override
																				public void run()
																				{
																					environment.getWebApplication().synchronizeAll();
																				}
																			}
																		 );
		environment.getLog().fine("Auto synchronization started.");
  }

	/**
	 * Reminds a single player to register if they are not registered.
	 * If linking-kick-unregistered is turned on, an unregistered player will
	 * be kicked.
	 */
  private void remindPlayer(Player player) {
		String userID = environment.getUserPlayerLinker().getUserID(player);
    if (userID == null || userID.isEmpty()) {
			String playerName = player.getName();
      if (environment.getConfiguration().linkingKickUnregistered) {
        player.kickPlayer(environment.getConfiguration().messages.get("link-unregistered-player"));
				environment.getLog().info(playerName + " kicked because they are not registered.");
      } else {
        player.sendMessage(ChatColor.RED + environment.getConfiguration().messages.get("link-unregistered-reminder"));
        environment.getLog().fine(playerName + " issued unregistered reminder notice");
      }
    }
  }

	/**
	 * Calls remindPlayer() for all connected players. Called by the reminder
	 * task.
	 */
  @SuppressWarnings("deprecation")
private void remindUnregisteredPlayers() {
	  environment.getLog().finest("Running unregistered auto reminder.");

	  for (Player player : Bukkit.getOnlinePlayers()) {
		  remindPlayer(player);
    }
    environment.getLog().finest("Unregistered auto reminder complete.");
  }

	private void activateMetrics()
	{
		if (environment.getConfiguration().usePluginMetrics)
		{
			try
			{
				metrics = new CBMetrics(this);
				Graph permsGraph = metrics.createGraph("Permissions Plugin Used");

				if (environment.getPermissionHandler() == null)
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("None")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (environment.getConfiguration().permissionsSystem.equalsIgnoreCase("bPerms"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("bPermissions")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (environment.getConfiguration().permissionsSystem.equalsIgnoreCase("GroupManager"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("GroupManager")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (environment.getConfiguration().permissionsSystem.equalsIgnoreCase("PermsBukkit"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("PermissionsBukkit")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (environment.getConfiguration().permissionsSystem.equalsIgnoreCase("PEX"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("PermissionsEx")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (environment.getConfiguration().permissionsSystem.equalsIgnoreCase("Vault"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("Vault")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				metrics.start();
				environment.getLog().fine("Plugin Metrics activated.");
			}
			catch (IOException e)
			{
				environment.getLog().warning("Plugin Metrics activation failed.");
			}
		}
	}

	private long calculateTaskTicks(final long every)
	{
		if (environment.getConfiguration().autoEveryUnit.startsWith("sec"))
		{
			return every * 20; // 20 ticks per second.
		}
		else if (environment.getConfiguration().autoEveryUnit.startsWith("min"))
		{
			return every * 1200; // 20 ticks per second, 60 sec/minute
		}
		else if (environment.getConfiguration().autoEveryUnit.startsWith("hou"))
		{
			return every * 72000; // 20 ticks/s 60s/m, 60m/h
		}
		else if (environment.getConfiguration().autoEveryUnit.startsWith("day"))
		{
			return every * 1728000; // 20 ticks/s 60s/m, 60m/h, 24h/day
		}
		else
		{
			// Effectively defaulting to ticks.
			return every;
		}
	}

	private void javaVersionCheck()
	{
		int javaVersion = Integer.parseInt(System.getProperty("java.version").split("\\.")[1]);

		if (javaVersion < 7)
		{
			environment.getLog().warning("Future versions of CommunityBridge may require Java 7 or later. It is recommended you upgrade your JRE.");
		}
	}

	private void disableEconomyBasedFeatures()
	{
		environment.getConfiguration().economyEnabled = false;
		environment.getConfiguration().walletEnabled = false;
	}
}
