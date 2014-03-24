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
	public static final String EXCEPTION_MESSAGE_GETSECONDARY = "Exception during SingleMethodWebGroupDao.getSecondaryGroups(): ";
	public static final String EXCEPTION_MESSAGE_GETPRIMARY_USERIDS = "Exception during SingleMethodWebGroupDao.getPrimaryGroupUserIDs(): ";
	public static final String EXCEPTION_MESSAGE_GETSECONDARY_USERIDS = "Exception during SingleMethodWebGroupDao.getSecondaryGroupUserIDs(): ";
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

	@Override
	public List<String> getGroupUserIDs(String groupID)
	{
		List<String> userIDs = getGroupUserIDsPrimary(groupID);
		userIDs.addAll(getGroupUserIDsSecondary(groupID));
		
		return userIDs;
	}

	@Override
	public List<String> getGroupUserIDsPrimary(String groupID)
	{
		List<String> userIDs = new ArrayList<String>();
		
		if (!configuration.webappPrimaryGroupEnabled)
		{
			return userIDs;
		}
		
		String query =
						"SELECT `" + configuration.webappPrimaryGroupUserIDColumn + "` "
						+ "FROM `" + configuration.webappPrimaryGroupTable + "` "
						+ "WHERE `" + configuration.webappPrimaryGroupGroupIDColumn + "` = '" + groupID + "' ";
		try
		{
			result = sql.sqlQuery(query);
			while(result.next())
			{
				userIDs.add(result.getString(configuration.webappPrimaryGroupUserIDColumn));
			}
			return userIDs;
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY_USERIDS + exception.getMessage());
			return userIDs;
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY_USERIDS + exception.getMessage());
			return userIDs;
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY_USERIDS + exception.getMessage());
			return userIDs;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY_USERIDS + exception.getMessage());
			return userIDs;
		}
	}

	@Override
	public List<String> getGroupUserIDsSecondary(String groupID)
	{
		List<String> userIDs = new ArrayList<String>();
		
		if (!configuration.webappSecondaryGroupEnabled)
		{
			return userIDs;
		}
		
		String query =
						"SELECT `" + configuration.webappSecondaryGroupUserIDColumn + "`, `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
						+ "FROM `" + configuration.webappSecondaryGroupTable + "` ";
		try
		{
			result = sql.sqlQuery(query);
			while(result.next())
			{
				String groupIDs = result.getString(configuration.webappSecondaryGroupGroupIDColumn);
				if (groupIDs != null)
				{
					groupIDs = groupIDs.trim();
					if (!groupIDs.isEmpty())
					{
						for (String id : groupIDs.split(configuration.webappSecondaryGroupGroupIDDelimiter))
						{
							if (id.equals(groupID))
							{
								userIDs.add(result.getString(configuration.webappSecondaryGroupUserIDColumn));
							}
						}
					}
				}
			}
			return userIDs;
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY_USERIDS + exception.getMessage());
			return userIDs;
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY_USERIDS + exception.getMessage());
			return userIDs;
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY_USERIDS + exception.getMessage());
			return userIDs;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY_USERIDS + exception.getMessage());
			return userIDs;
		}
	}
}
