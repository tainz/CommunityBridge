package org.communitybridge.dao;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;

public class SingleMethodWebGroupDao extends WebGroupDao
{
	public static final String EXCEPTION_MESSAGE_GETSECONDARY = "Error during WebApplication.getUserGroupIDsSingleColumn(): ";
	public SingleMethodWebGroupDao(Configuration configuration, SQL sql, Log log)
	{
		super(configuration, sql, log);
	}

	@Override
	public List<String> getSecondaryGroups(String userID)
	{
		List<String> groupIDs = new ArrayList<String>();
		
		if (!configuration.webappSecondaryGroupEnabled)
		{
			return groupIDs;
		}
		String query =
						"SELECT `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + configuration.webappSecondaryGroupTable + "` "
					+ "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' ";

		try
		{
			result = sql.sqlQuery(query);

			if (result.next())
			{
				String groupsFromDB = result.getString(configuration.webappSecondaryGroupGroupIDColumn).trim();
				if (!groupsFromDB.isEmpty())
				{
					for (String id : Arrays.asList(groupsFromDB.split(configuration.webappSecondaryGroupGroupIDDelimiter)))
					{
						groupIDs.add(id.trim());
					}				
				}
			}
			return groupIDs;
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return groupIDs;
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return groupIDs;
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return groupIDs;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return groupIDs;
		}
	}
}
