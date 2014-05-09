package org.communitybridge.linker;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.communitybridge.main.WebApplication;

public class UserIDDao
{
	protected	static final String EXCEPTION_MESSAGE_GETUSERID = "Exception during UserIDDao.getUserID: ";

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
}
