package org.communitybridge.dao;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;

public abstract class WebGroupDao
{
	public static final List<String> EMPTY_LIST = new ArrayList<String>();
	protected Configuration configuration;
	protected SQL sql;
	protected Log log;
	protected ResultSet result;
	
	WebGroupDao(Configuration configuration, SQL sql, Log log)
	{
		this.configuration = configuration;
		this.sql = sql;
		this.log = log;
	}
	
	abstract public void addGroup(String userID, String groupID, int currentGroupCount) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException;

	abstract public void removeGroup(String userID, String groupID) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException;

	abstract public List<String> getUserSecondaryGroupIDs(String userID) throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException;
	abstract public List<String> getGroupUserIDs(String groupID);
	abstract public List<String> getGroupUserIDsPrimary(String groupID);
	abstract public List<String> getGroupUserIDsSecondary(String groupID);

	public String getUserPrimaryGroupID(String userID) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		if (!configuration.webappPrimaryGroupEnabled)
		{
			return "";
		}
		String query = determinePrimaryGroupQuery(userID);

		result = sql.sqlQuery(query);

		if (result.next())
		{
			return result.getString(configuration.webappPrimaryGroupGroupIDColumn);
		}
		else
		{
			return "";
		}
	}
	
	private String determinePrimaryGroupQuery(String userID)
	{
		if (configuration.webappPrimaryGroupUsesKey)
		{
			return "SELECT `" + configuration.webappPrimaryGroupGroupIDColumn + "` "
						+ "FROM `" + configuration.webappPrimaryGroupTable + "` "
						+ "WHERE `" + configuration.webappPrimaryGroupUserIDColumn + "` = '" + userID + "' "
						+ "AND `" + configuration.webappPrimaryGroupKeyColumn + "` = '" + configuration.webappPrimaryGroupKeyName + "' ";
		}
		else
		{
			return "SELECT `" + configuration.webappPrimaryGroupGroupIDColumn + "` "
						+ "FROM `" + configuration.webappPrimaryGroupTable + "` "
						+ "WHERE `" + configuration.webappPrimaryGroupUserIDColumn + "` = '" + userID + "'";
		}
	}

	protected void addCleanID(String id, List<String> idList)
	{
		if (id != null && !id.isEmpty())
		{
			id = id.trim();
			if (!id.isEmpty())
			{
				idList.add(id);
			}
		}
	}

	protected List<String> convertDelimitedIDString(String ids)
	{
		List<String> idList = new ArrayList<String>();
		if (ids != null)
		{
			for (String id : ids.split(configuration.webappSecondaryGroupGroupIDDelimiter))
			{
				addCleanID(id, idList);
			}
		}
		return idList;
	}
}
