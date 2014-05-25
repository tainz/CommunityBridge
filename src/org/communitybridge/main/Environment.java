package org.communitybridge.main;

import org.communitybridge.linker.UserPlayerLinker;
import org.communitybridge.permissionhandlers.PermissionHandler;
import org.communitybridge.utility.Log;

public class Environment
{
	private Configuration configuration;
	private Log log;
	private PermissionHandler permissionHandler;
	private CommunityBridge plugin;
	private SQL sql;
	private UserPlayerLinker userPlayerLinker;

	public Configuration getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
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
}
