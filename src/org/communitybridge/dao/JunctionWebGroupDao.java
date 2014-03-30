package org.communitybridge.dao;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;

public class JunctionWebGroupDao extends WebGroupDao
{
	public static final String EXCEPTION_MESSAGE_GETSECONDARY = "Error during WebApplication.getUserGroupIDsJunction(): ";

	public JunctionWebGroupDao(Configuration configuration, SQL sql, Log log)
	{
		super(configuration, sql, log);
	}

	@Override
	public void addGroup(String userID, String groupID, int currentGroupCount) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		String columns = "(`" + configuration.webappSecondaryGroupUserIDColumn + "`, `" + configuration.webappSecondaryGroupGroupIDColumn;
		String values = "VALUES ('" + userID + "', '" + groupID;

		for (Entry entry : configuration.webappSecondaryAdditionalColumns.entrySet())
		{
			columns = columns + "`, `" + entry.getKey();
			values = values + "', '" + entry.getValue();
		}

		columns = columns + "`) ";
		values = values + "')";
		
		String query = "INSERT INTO `" + configuration.webappSecondaryGroupTable + "` " + columns + values;
		sql.insertQuery(query);
	}

	@Override
	public void removeGroup(String userID, String groupID) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		String query = "DELETE FROM `" + configuration.webappSecondaryGroupTable + "` "
								 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
								 + "AND `" + configuration.webappSecondaryGroupGroupIDColumn + "` = '" + groupID + "' ";
		sql.deleteQuery(query);
	}

	@Override
	public List<String> getUserSecondaryGroupIDs(String userID) throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		List<String> groupIDs = new ArrayList<String>();
		String query = "SELECT `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + configuration.webappSecondaryGroupTable + "` "
					+ "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' ";

		result = sql.sqlQuery(query);

		while (result.next())
		{
			addCleanID(result.getString(configuration.webappSecondaryGroupGroupIDColumn), groupIDs);
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
