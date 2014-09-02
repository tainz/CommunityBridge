package org.communitybridge.synchronization.dao;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.communitybridge.configuration.SynchronizationConfiguration;
import org.communitybridge.main.Environment;

public class MoneyDao
{
	public Double getBalance(Environment environment, String userId) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		String query;
		String column;
		ResultSet result;
		SynchronizationConfiguration configuration = environment.getConfiguration().getMoney();
		if (configuration.isUsesKey())
		{
			query = "SELECT `" + configuration.getValueColumn() + "` "
						+ "FROM `" + configuration.getTableName() + "` "
						+ "WHERE `" + configuration.getUserIdColumn() + "` = '" + userId + "' "
						+ "AND " + configuration.getKeyColumn() + "` = '" + configuration.getColumnOrKey() + "'";
			result = environment.getSql().sqlQuery(query);
			column = configuration.getValueColumn();
		}
		else
		{
			query = "SELECT `" + configuration.getColumnOrKey() + "` "
						+ "FROM `" + configuration.getTableName() + "` "
						+ "WHERE `" + configuration.getUserIdColumn() + "` = '" + userId + "'";
			result = environment.getSql().sqlQuery(query);
			column = configuration.getColumnOrKey();
		}
		if (result.next())
		{
			return result.getDouble(column);
		}
		else
		{
			return new Double(0.0);
		}
	}

	public void setBalance(Environment environment, String userId, Double balance) throws IllegalAccessException, InstantiationException, MalformedURLException
	{
		String query;
		SynchronizationConfiguration configuration = environment.getConfiguration().getMoney();
		if (configuration.isUsesKey())
		{
			query = "UPDATE `" + configuration.getTableName() + "` "
			      + "SET `" + configuration.getValueColumn() + "` = '" + balance.toString() + "' "
			  		+ "WHERE `" + configuration.getUserIdColumn() + "` = '" + userId + "'"
						+ "AND " + configuration.getKeyColumn() + "` = '" + configuration.getColumnOrKey() + "'";
		}
		else
		{
			query = "UPDATE `" + configuration.getTableName() + "` "
						+ "SET `" + configuration.getColumnOrKey() + "` = '" + balance.toString() + "' "
						+ "WHERE `" + configuration.getUserIdColumn() + "` = '" + userId + "'";
		}
		environment.getSql().updateQuery(query);
	}
}
