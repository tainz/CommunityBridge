package net.netmanagers.api;

import net.netmanagers.api.DatabaseHandler;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;

public class SQL {
	public String host;
	private DatabaseHandler manageDB;
	public String username;
	public String password;
	public String database;

	public SQL(String host, String database, String username, String password) {
		this.database = database;
		this.host = host;
		this.username = username;
		this.password = password;
	}

	public Boolean initialize() {
		this.manageDB = new DatabaseHandler(this, this.host, this.database, this.username, this.password);
		return Boolean.valueOf(false);
	}

	public ResultSet sqlQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		return this.manageDB.sqlQuery(query);
	}

	public void insertQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		this.manageDB.insertQuery(query);
	}

	public void updateQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		this.manageDB.updateQuery(query);
	}

	public void deleteQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		this.manageDB.deleteQuery(query);
	}

	public Boolean checkTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		return this.manageDB.checkTable(table);
	}

	public Connection getConnection() throws MalformedURLException, InstantiationException, IllegalAccessException {
		return this.manageDB.getConnection();
	}

	public void close() {
		this.manageDB.closeConnection();
	}

	public Boolean checkConnection() {
		return this.manageDB.checkConnection();
	}
}