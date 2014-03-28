package org.communitybridge.dao;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
		String query = "INSERT INTO `" + configuration.webappSecondaryGroupTable + "` "
						 + "(`" + configuration.webappSecondaryGroupUserIDColumn + "`, `" + configuration.webappSecondaryGroupGroupIDColumn + "`) "
						 + "VALUES ('" + userID + "', '" + groupID +"')";
		sql.insertQuery(query);
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
