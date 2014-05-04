package org.communitybridge.main;

import org.communitybridge.utility.Log;

public class Environment
{
	private Configuration configuration;
	private Log log;
	private SQL sql;

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
}
