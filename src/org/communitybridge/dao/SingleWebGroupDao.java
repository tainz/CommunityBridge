package org.communitybridge.dao;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.communitybridge.dao.WebGroupDao.EMPTY_LIST;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;

public class SingleWebGroupDao extends WebGroupDao
{
	public static final String EXCEPTION_MESSAGE_GETSECONDARY = "Exception during SingleMethodWebGroupDao.getSecondaryGroups(): ";
	public static final String EXCEPTION_MESSAGE_GETPRIMARY_USERIDS = "Exception during SingleMethodWebGroupDao.getPrimaryGroupUserIDs(): ";
	public static final String EXCEPTION_MESSAGE_GETSECONDARY_USERIDS = "Exception during SingleMethodWebGroupDao.getSecondaryGroupUserIDs(): ";
	public SingleWebGroupDao(Configuration configuration, SQL sql, Log log)
	{
		super(configuration, sql, log);
	}

	@Override
	public List<String> getSecondaryGroups(String userID)
	{
		if (!configuration.webappSecondaryGroupEnabled)
		{
			return EMPTY_LIST;
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
				return convertDelimitedIDString(result.getString(configuration.webappSecondaryGroupGroupIDColumn));
			}
			return EMPTY_LIST;
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
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
