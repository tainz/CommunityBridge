package org.communitybridge.groupsynchronizer;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.communitybridge.groupsynchronizer.SingleWebGroupDao.EXCEPTION_MESSAGE_GET_USERIDS;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;

public abstract class WebGroupDao
{
	protected static final List<String> EMPTY_LIST = new ArrayList<String>();

	protected Configuration configuration;
	protected SQL sql;
	protected Log log;
	protected ResultSet result;

	WebGroupDao(Environment environment)
	{
		this.configuration = environment.getConfiguration();
		this.sql = environment.getSql();
		this.log = environment.getLog();
	}

	abstract public void addUserToGroup(String userID, String groupID, int currentGroupCount) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException;

	abstract public void removeUserFromGroup(String userID, String groupID) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException;

	abstract public List<String> getSecondaryGroupIDs(String userID) throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException;
	abstract public List<String> getSecondaryGroupUserIDs(String groupID) throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException;

	public String getPrimaryGroupID(String userID) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
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

	public List<String> getGroupUserIDs(String groupID)
	{
		List<String> userIDs = new ArrayList<String>();
		try
		{
			userIDs.addAll(getUserIDsFromPrimaryGroup(groupID));
			userIDs.addAll(getSecondaryGroupUserIDs(groupID));

			return userIDs;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GET_USERIDS + exception.getMessage());
			return userIDs;
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GET_USERIDS + exception.getMessage());
			return userIDs;
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GET_USERIDS + exception.getMessage());
			return userIDs;
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GET_USERIDS + exception.getMessage());
			return userIDs;
		}
	}

	protected List<String> getUserIDsFromPrimaryGroup(String groupID) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
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
		result = sql.sqlQuery(query);
		while(result.next())
		{
			userIDs.add(result.getString(configuration.webappPrimaryGroupUserIDColumn));
		}
		return userIDs;
	}
}
