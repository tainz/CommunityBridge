package org.communitybridge.synchronization.group;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.communitybridge.synchronization.group.WebGroupDao.EMPTY_LIST;
import org.communitybridge.main.Environment;
import org.communitybridge.utility.StringUtilities;

public class SingleWebGroupDao extends WebGroupDao
{
	public static final String EXCEPTION_MESSAGE_GETSECONDARY = "Exception during SingleMethodWebGroupDao.getSecondaryGroups(): ";
	public static final String EXCEPTION_MESSAGE_GET_USERIDS = "Exception during SingleMethodWebGroupDao.getGroupUserIDs(): ";
	public static final String EXCEPTION_MESSAGE_GETSECONDARY_USERIDS = "Exception during SingleMethodWebGroupDao.getSecondaryGroupUserIDs(): ";
	public SingleWebGroupDao(Environment environment)
	{
		super(environment);
	}

	@Override
	public void addUserToGroup(String userID, String groupID, int currentGroupCount) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		if (currentGroupCount >= 1)
		{
			groupID = configuration.webappSecondaryGroupGroupIDDelimiter + groupID;
		}
		String query = "UPDATE `" + configuration.webappSecondaryGroupTable + "` "
								 + "SET `" + configuration.webappSecondaryGroupGroupIDColumn + "` = CONCAT(`" + configuration.webappSecondaryGroupGroupIDColumn + "`, '" + groupID + "') "
								 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "'";
		sql.updateQuery(query);
	}

	@Override
	public void removeUserFromGroup(String userID, String groupID) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		String query = "SELECT `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
								 + "FROM `" + configuration.webappSecondaryGroupTable + "` "
								 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "'";
		result = sql.sqlQuery(query);

		if (result.next())
		{
			String groupIDs = result.getString(configuration.webappSecondaryGroupGroupIDColumn);
			List<String> groupIDsAsList = new ArrayList<String>(Arrays.asList(groupIDs.split(configuration.webappSecondaryGroupGroupIDDelimiter)));
			groupIDsAsList.remove(groupID);
			groupIDs = StringUtilities.joinStrings(groupIDsAsList, configuration.webappSecondaryGroupGroupIDDelimiter);
			query = "UPDATE `" + configuration.webappSecondaryGroupTable + "` "
						+ "SET `" + configuration.webappSecondaryGroupGroupIDColumn + "` = '" + groupIDs + "' "
						+ "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "'";
			sql.updateQuery(query);
		}
	}

	@Override
	public List<String> getSecondaryGroupIDs(String userID) throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		if (!configuration.webappSecondaryGroupEnabled)
		{
			return EMPTY_LIST;
		}
		String query =
						"SELECT `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + configuration.webappSecondaryGroupTable + "` "
					+ "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' ";

			result = sql.sqlQuery(query);

			if (result.next())
			{
				return convertDelimitedIDString(result.getString(configuration.webappSecondaryGroupGroupIDColumn));
			}
			return EMPTY_LIST;
	}

	@Override
	public List<String> getSecondaryGroupUserIDs(String groupID) throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		List<String> userIDs = new ArrayList<String>();

		if (!configuration.webappSecondaryGroupEnabled)
		{
			return userIDs;
		}

		String query =
						"SELECT `" + configuration.webappSecondaryGroupUserIDColumn + "`, `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
						+ "FROM `" + configuration.webappSecondaryGroupTable + "` ";
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
}
