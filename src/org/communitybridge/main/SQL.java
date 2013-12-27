package org.communitybridge.main;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQL
{
	public String host;
	private DatabaseHandler manageDB;
	public String username;
	public String password;
	public String database;

	public SQL(String host, String database, String username, String password)
	{
		this.database = database;
		this.host = host;
		this.username = username;
		this.password = password;
	}

	public boolean initialize()
	{
		this.manageDB = new DatabaseHandler(this.host, this.database, this.username, this.password);
		return false;
	}

	public ResultSet sqlQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		CommunityBridge.log.finest(query);
		return this.manageDB.sqlQuery(query);
	}

	public void insertQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		CommunityBridge.log.finest(query);
		this.manageDB.insertQuery(query);
	}

	public void updateQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		CommunityBridge.log.finest(query);
		this.manageDB.updateQuery(query);
	}

	public void deleteQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		CommunityBridge.log.finest(query);
		this.manageDB.deleteQuery(query);
	}

	public Boolean checkTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		return this.manageDB.checkTable(table);
	}

	public Connection getConnection() throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		return this.manageDB.getConnection();
	}

	public void close()
	{
		this.manageDB.closeConnection();
	}

	public boolean checkConnection()
	{
		return this.manageDB.checkConnection();
	}
}