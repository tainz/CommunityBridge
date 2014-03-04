package org.communitybridge.main;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQL
{
	private String host;
	private DatabaseHandler manageDB;
	private String username;
	private String password;
	private String database;
	private String localAddress;

	public SQL(String host, String database, String username, String password, String localAddress)
	{
		this.database = database;
		this.host = host;
		this.username = username;
		this.password = password;
		this.localAddress = localAddress;
	}

	public boolean initialize()
	{
		this.manageDB = new DatabaseHandler(host, database, username, password, localAddress);
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