package org.communitybridge.main;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.communitybridge.utility.Log;

public abstract class WebGroupDao
{
	public static final String EXCEPTION_MESSAGE_GETPRIMARY = "Exception during WebGroupDao.getPrimaryGroup: ";
	private Configuration configuration;
	private SQL sql;
	private Log log;
	private ResultSet result;
	
	WebGroupDao(Configuration configuration, SQL sql, Log log)
	{
		this.configuration = configuration;
		this.sql = sql;
		this.log = log;
	}
	
	public String getPrimary(String userID)
	{
		if (!configuration.webappPrimaryGroupEnabled)
		{
			return "";
		}
		String query = determinePrimaryGroupQuery(userID);

		try
		{
			result = sql.sqlQuery(query);

			if (result.next())
			{
				return result.getString(configuration.webappPrimaryGroupGroupIDColumn);
			}
			else
			{
				return "";
			}
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
			return "";
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
			return "";
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
			return "";
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
			return "";
		}
	}
	
	abstract public List<String> getSecondaryGroups();

	private String determinePrimaryGroupQuery(String userID)
	{
		if (configuration.webappPrimaryGroupUsesKey)
		{
			return "SELECT `" + configuration.webappPrimaryGroupGroupIDColumn + "` "
						+ "FROM `" + configuration.webappPrimaryGroupTable + "` "
						+ "WHERE `" + configuration.webappPrimaryGroupUserIDColumn + "` = '" + userID + "' "
						+ "AND `" + configuration.webappPrimaryGroupKeyColumn + "` = '" + configuration.webappPrimaryGroupKeyName + "' ";
		}
		else
		{
			return "SELECT `" + configuration.webappPrimaryGroupGroupIDColumn + "` "
						+ "FROM `" + configuration.webappPrimaryGroupTable + "` "
						+ "WHERE `" + configuration.webappPrimaryGroupUserIDColumn + "` = '" + userID + "'";
		}
	}
}
