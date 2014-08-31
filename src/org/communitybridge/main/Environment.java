package org.communitybridge.main;

import org.communitybridge.configuration.Configuration;
import net.milkbowl.vault.economy.Economy;
import org.communitybridge.linker.UserPlayerLinker;
import org.communitybridge.permissionhandlers.PermissionHandler;
import org.communitybridge.utility.Log;

public class Environment
{
	private BukkitWrapper bukkit;
	private CommunityBridge plugin;
	private Configuration configuration;
	private Economy economy;
	private Log log;
	private PermissionHandler permissionHandler;
	private SQL sql;
	private UserPlayerLinker userPlayerLinker;
	private WebApplication webApplication;

	public Configuration getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
	}

	public Economy getEconomy()
	{
		return economy;
	}

	public void setEconomy(Economy economy)
	{
		this.economy = economy;
	}

	public SQL getSql()
	{
		return sql;
	}

	public void setSql(SQL sql)
	{
		this.sql = sql;
	}

	public Log getLog()
	{
		return log;
	}

	public void setLog(Log log)
	{
		this.log = log;
	}

	public PermissionHandler getPermissionHandler()
	{
		return permissionHandler;
	}

	public void setPermissionHandler(PermissionHandler permissionHandler)
	{
		this.permissionHandler = permissionHandler;
	}

	public CommunityBridge getPlugin()
	{
		return plugin;
	}

	public void setPlugin(CommunityBridge plugin)
	{
		this.plugin = plugin;
	}

	public UserPlayerLinker getUserPlayerLinker()
	{
		return userPlayerLinker;
	}

	public void setUserPlayerLinker(UserPlayerLinker userPlayerLinker)
	{
		this.userPlayerLinker = userPlayerLinker;
	}

	public WebApplication getWebApplication()
	{
		return webApplication;
	}

	public void setWebApplication(WebApplication webApplication)
	{
		this.webApplication = webApplication;
	}


	public BukkitWrapper getBukkit()
	{
		return bukkit;
	}

	public void setBukkit(BukkitWrapper bukkit)
	{
		this.bukkit = bukkit;
	}
}
