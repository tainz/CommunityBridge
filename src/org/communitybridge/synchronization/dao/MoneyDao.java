package org.communitybridge.synchronization.dao;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;

public class MoneyDao
{
	public Double getBalance(Environment environment, String userId) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		String query;
		String column;
		ResultSet result;
		Configuration configuration = environment.getConfiguration();
		if (configuration.walletUsesKey)
		{
			query = "SELECT `" + configuration.walletValueColumn + "` "
						+ "FROM `" + configuration.walletTableName + "` "
						+ "WHERE `" + configuration.walletUserIDColumn + "` = '" + userId + "' "
						+ "AND " + configuration.walletKeyColumn + "` = '" + configuration.walletColumnOrKey + "'";
			result = environment.getSql().sqlQuery(query);
			column = configuration.walletValueColumn;
		}
		else
		{
			query = "SELECT `" + configuration.walletColumnOrKey + "` "
						+ "FROM `" + configuration.walletTableName + "` "
						+ "WHERE `" + configuration.walletUserIDColumn + "` = '" + userId + "'";
			result = environment.getSql().sqlQuery(query);
			column = configuration.walletColumnOrKey;
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
		Configuration configuration = environment.getConfiguration();
		if (configuration.walletUsesKey)
		{
			query = "UPDATE `" + configuration.walletTableName + "` "
			      + "SET `" + configuration.walletValueColumn + "` = '" + balance.toString() + "' "
			  		+ "WHERE `" + configuration.walletUserIDColumn + "` = '" + userId + "'"
						+ "AND " + configuration.walletKeyColumn + "` = '" + configuration.walletColumnOrKey + "'";
		}
		else
		{
			query = "UPDATE `" + configuration.walletTableName + "` "
						+ "SET `" + configuration.walletColumnOrKey + "` = '" + balance.toString() + "' "
						+ "WHERE `" + configuration.walletUserIDColumn + "` = '" + userId + "'";
		}
		environment.getSql().updateQuery(query);
	}
}
