package org.communitybridge.main;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserIDDao
{
	private Environment environment;

	public UserIDDao(Environment environment)
	{
		this.environment = environment;
	}

	public String getUserID(String identifier)
	{
		Configuration configuration = environment.getConfiguration();
		ResultSet result;
		String query = "SELECT `" + configuration.linkingTableName + "`.`" + configuration.linkingUserIDColumn + "` " + "FROM `" + configuration.linkingTableName + "` ";
		if (configuration.linkingUsesKey)
		{
			query = query + "WHERE `" + configuration.linkingKeyColumn + "` = '" + configuration.linkingKeyName + "' " + "AND `" + configuration.linkingValueColumn + "` = '" + identifier + "' ";
		}
		else
		{
			query = query + "WHERE LOWER(`" + configuration.linkingPlayerNameColumn + "`) = LOWER('" + identifier + "') ";
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
			environment.getLog().severe(WebApplication.EXCEPTION_MESSAGE_GETUSERID + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			environment.getLog().severe(WebApplication.EXCEPTION_MESSAGE_GETUSERID + exception.getMessage());
		}
		catch (MalformedURLException exception)
		{
			environment.getLog().severe(WebApplication.EXCEPTION_MESSAGE_GETUSERID + exception.getMessage());
		}
		catch (SQLException exception)
		{
			environment.getLog().severe(WebApplication.EXCEPTION_MESSAGE_GETUSERID + exception.getMessage());
		}
		return "";
	}
}
