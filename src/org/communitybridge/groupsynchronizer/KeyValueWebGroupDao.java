package org.communitybridge.groupsynchronizer;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;
import org.communitybridge.utility.StringUtilities;

public class KeyValueWebGroupDao extends WebGroupDao
{
	public static final String EXCEPTION_MESSAGE_GETSECONDARY = "Exception during KeyValueWebGroupDao.getSecondaryGroups(): ";

	public KeyValueWebGroupDao(Configuration configuration, SQL sql, Log log)
	{
		super(configuration, sql, log);
	}

	@Override
	public void addUserToGroup(String userID, String groupID, int currentGroupCount) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		result = sql.sqlQuery(getSecondaryGroupReadQuery(userID));

		if (result.next())
		{
			List<String> groupIDs = getGroupIDsFromResult();
			groupIDs.add(groupID);
			sql.updateQuery(getGroupIDsUpdateQuery(groupIDs, userID));
		}
		else
		{
			sql.insertQuery(getGroupIDInsertQuery(userID, groupID));
		}
	}

	@Override
	public void removeUserFromGroup(String userID, String groupID) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		result = sql.sqlQuery(getSecondaryGroupReadQuery(userID));

		if (result.next())
		{
			List<String> groupIDs  = getGroupIDsFromResult();
			groupIDs.remove(groupID);
			sql.updateQuery(getGroupIDsUpdateQuery(groupIDs, userID));
		}
	}

	protected List<String> getGroupIDsFromResult() throws SQLException
	{
		List<String> groupIDs = new ArrayList();
		String groupIDString = result.getString(configuration.webappSecondaryGroupGroupIDColumn);

		if (groupIDString == null)
		{
			return groupIDs;
		}

		groupIDString = groupIDString.trim();

		if (groupIDString.isEmpty())
		{
			return groupIDs;
		}

		groupIDs.addAll(Arrays.asList(groupIDString.split(configuration.webappSecondaryGroupGroupIDDelimiter)));
		return groupIDs;
	}

	protected String getSecondaryGroupReadQuery(String userID)
	{
		return "SELECT `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
				 + "FROM `" + configuration.webappSecondaryGroupTable + "` "
				 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
				 + "AND `" + configuration.webappSecondaryGroupKeyColumn + "` = '" + configuration.webappSecondaryGroupKeyName + "' ";
	}

	protected String getGroupIDsUpdateQuery(List<String> groupIDs, String userID)
	{
		String groupIDString = StringUtilities.joinStrings(groupIDs, configuration.webappSecondaryGroupGroupIDDelimiter);
		return "UPDATE `" + configuration.webappSecondaryGroupTable + "` "
				 + "SET `" + configuration.webappSecondaryGroupGroupIDColumn + "` = '" + groupIDString + "' "
				 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
				 + "AND `" + configuration.webappSecondaryGroupKeyColumn + "` = '" + configuration.webappSecondaryGroupKeyName + "'";
	}

	protected String getGroupIDInsertQuery(String userID, String groupID)
	{
		String query = "INSERT INTO `" + configuration.webappSecondaryGroupTable + "` "
								 + "(`"
								 + configuration.webappSecondaryGroupUserIDColumn + "`, `"
								 + configuration.webappSecondaryGroupKeyColumn + "`, `"
								 + configuration.webappSecondaryGroupGroupIDColumn + "`) "
								 + "VALUES ('"
								 + userID + "', '"
								 + configuration.webappSecondaryGroupKeyName + "', '"
								 + groupID + "')";
		return query;
	}

	@Override
	public List<String> getSecondaryGroupIDs(String userID) throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		List<String> groupIDs = new ArrayList<String>();

		result = sql.sqlQuery(getSecondaryGroupReadQuery(userID));

		if (result.next())
		{
			return convertDelimitedIDString(result.getString(configuration.webappSecondaryGroupGroupIDColumn));
		}
		return groupIDs;
	}

	@Override
	public List<String> getSecondaryGroupUserIDs(String groupID) throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		List<String> userIDs = new ArrayList<String>();
		result = sql.sqlQuery(getSecondaryGroupUserIDsReadQuery());
		while(result.next())
		{
			List<String> groupIDs = convertDelimitedIDString(result.getString(configuration.webappSecondaryGroupGroupIDColumn));
			if (groupIDs.contains(groupID))
			{
				userIDs.add(result.getString(configuration.webappSecondaryGroupUserIDColumn));
			}
		}

		return userIDs;
	}

	protected String getSecondaryGroupUserIDsReadQuery()
	{
		return "SELECT `" + configuration.webappSecondaryGroupUserIDColumn + "`, `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
				 + "FROM `" + configuration.webappSecondaryGroupTable + "` "
				 + "WHERE `" + configuration.webappSecondaryGroupKeyColumn + "` = '" + configuration.webappSecondaryGroupKeyName + "' ";
	}
}
