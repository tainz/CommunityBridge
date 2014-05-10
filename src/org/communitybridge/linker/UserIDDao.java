package org.communitybridge.linker;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;

public class UserIDDao
{
	protected	static final String EXCEPTION_MESSAGE_GETUSERID = "Exception during UserIDDao.getUserID: ";
	protected	static final String EXCEPTION_MESSAGE_GETUUID = "Exception during UserIDDao.getUUID: ";

	private Environment environment;
	private Configuration configuration;
	private ResultSet result;

	public UserIDDao(Environment environment)
	{
		this.environment = environment;
		this.configuration = environment.getConfiguration();
	}

	public String getUserID(String identifier)
	{
		String query = "SELECT `" + configuration.linkingTableName + "`.`" + configuration.linkingUserIDColumn + "` " + "FROM `" + configuration.linkingTableName + "` ";
		if (configuration.linkingUsesKey)
		{
			query = query + "WHERE `" + configuration.linkingKeyColumn + "` = '" + configuration.linkingKeyName + "' " + "AND `" + configuration.linkingValueColumn + "` = '" + identifier + "' ";
		}
		else
		{
			query = query + "WHERE LOWER(`" + configuration.linkingIdentifierColumn + "`) = LOWER('" + identifier + "') ";
		}
		query = query + "ORDER BY `" + configuration.linkingUserIDColumn + "` DESC";
		try
		{
			result = environment.getSql().sqlQuery(query);
			if (result != null && result.next())
			{
				return result.getString(configuration.linkingUserIDColumn);
			}
		}
		catch (IllegalAccessException exception)
		{
			environment.getLog().severe(EXCEPTION_MESSAGE_GETUSERID + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			environment.getLog().severe(EXCEPTION_MESSAGE_GETUSERID + exception.getMessage());
		}
		catch (MalformedURLException exception)
		{
			environment.getLog().severe(EXCEPTION_MESSAGE_GETUSERID + exception.getMessage());
		}
		catch (SQLException exception)
		{
			environment.getLog().severe(EXCEPTION_MESSAGE_GETUSERID + exception.getMessage());
		}
		return "";
	}

	public String getUUID(String userID)
	{
		String query;
		String uuid = "";
		if (configuration.linkingUsesKey)
		{
			query = "SELECT `" + configuration.linkingValueColumn + "` "
						+ "FROM `" + configuration.linkingTableName + "` "
						+ "WHERE `" + configuration.linkingKeyColumn + "` = '" + configuration.linkingKeyName + "' "
						+ "AND `" + configuration.linkingUserIDColumn + "` = '" + userID + "'";
		}
		else
		{
			query = "SELECT `" + configuration.linkingIdentifierColumn + "` "
						+ "FROM `" + configuration.linkingTableName + "` "
						+ "WHERE `" + configuration.linkingUserIDColumn + "` = '" + userID + "'";
		}
		try
		{
			result = environment.getSql().sqlQuery(query);
			if (result != null && result.next())
			{
				if (configuration.linkingUsesKey)
				{
					uuid = result.getString(configuration.linkingValueColumn);
				}
				else
				{
					uuid = result.getString(configuration.linkingIdentifierColumn);
				}
			}
		}
		catch (MalformedURLException exception)
		{
			environment.getLog().severe(EXCEPTION_MESSAGE_GETUUID + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			environment.getLog().severe(EXCEPTION_MESSAGE_GETUUID + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			environment.getLog().severe(EXCEPTION_MESSAGE_GETUUID + exception.getMessage());
		}
		catch (SQLException exception)
		{
			environment.getLog().severe(EXCEPTION_MESSAGE_GETUUID + exception.getMessage());
		}
		return uuid;
	}
}
