package org.communitybridge.dao;

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
	public void addGroup(String userID, String groupID, int currentGroupCount) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
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
	public void removeGroup(String userID, String groupID) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
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
	public List<String> getUserSecondaryGroupIDs(String userID) throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		List<String> groupIDs = new ArrayList<String>();
		String query =
						"SELECT `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + configuration.webappSecondaryGroupTable + "` "
					+ "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
					+ "AND `" + configuration.webappSecondaryGroupKeyColumn + "` = '" + configuration.webappSecondaryGroupKeyName + "' ";

		result = sql.sqlQuery(query);

		if (result.next())
		{
			return convertDelimitedIDString(result.getString(configuration.webappSecondaryGroupGroupIDColumn));
		}
		return groupIDs;
	}

	@Override
	public List<String> getGroupUserIDs(String groupID)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<String> getGroupUserIDsPrimary(String groupID)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<String> getGroupUserIDsSecondary(String groupID)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
