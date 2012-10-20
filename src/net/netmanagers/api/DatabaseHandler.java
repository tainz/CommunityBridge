package net.netmanagers.api;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.netmanagers.community.Main;

public class DatabaseHandler {
	private SQL core;
	private Connection connection;
	private String dblocation;
	private String username;
	private String password;
	private String database;

	public DatabaseHandler(SQL core, String dbLocation, String database, String username, String password) {
		this.core = core;
		this.dblocation = dbLocation;
		this.database = database;
		this.username = username;
		this.password = password;		
	}

	private void openConnection() throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.dblocation + "/" + this.database, this.username, this.password);
		} catch (ClassNotFoundException e) {
			Main.log.severe("No MySQL Driver Found");
			this.connection = null;
		} catch (SQLException e) {
			Main.log.severe("Could not connect to MySQL Server:" + e.getMessage());
			this.connection = null;
		}
	}

	public boolean checkConnection() {
		if (this.connection == null) {
			try {
				openConnection();
				if(this.connection == null)
					return Boolean.valueOf(false);
				
				return Boolean.valueOf(true);
			} catch (MalformedURLException ex) {
				Main.log.severe("MalformedURLException! " + ex.getMessage());
			} catch (InstantiationException ex) {
				Main.log.severe("InstantiationExceptioon! " + ex.getMessage());
			} catch (IllegalAccessException ex) {
				Main.log.severe("IllegalAccessException! " + ex.getMessage());
			}	
			return Boolean.valueOf(false);
		}
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	public void closeConnection() {
		try {
			if (this.connection != null)
				this.connection.close();
		} catch (Exception e) {
			Main.log.warning("Failed to close database connection! " + e.getMessage());
		}
	}
	
	public Connection getConnection() throws MalformedURLException, InstantiationException, IllegalAccessException {
		if (this.connection == null) {
			openConnection();
			return this.connection;
		}else{
			try {
				if(this.connection.isClosed())
					openConnection();
								
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(5);
				ResultSet result = statement.executeQuery("SELECT 1");
				if (result.next()) 
					return this.connection;
				
				openConnection();
				return this.connection;								
			} catch (SQLException e) {				
				try {
					openConnection();
					
					Statement statement = connection.createStatement();
					statement.setQueryTimeout(5);
					ResultSet result = statement.executeQuery("SELECT 1");
					if (result.next()) 
						return this.connection;
					
				} catch (SQLException e1) {					
					Main.log.warning("Database Connection Error: " + e.getMessage());					
				}					
			}
		}		
		return null;
	}
	
	public ResultSet sqlQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			if(connection == null)
				return null;
			
			Statement statement = connection.createStatement();

			statement.setQueryTimeout(10);

			ResultSet result = statement.executeQuery(query);

			return result;
		} catch (SQLException ex) {
			Main.log.warning("Error at SQL Query: " + ex.getMessage());
		}
		return null;
	}

	public void insertQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			if(connection != null){
				Statement statement = connection.createStatement();
				statement.executeUpdate(query);
			}
		} catch (SQLException ex) {
			if (!ex.toString().contains("not return ResultSet"))
				Main.log.warning("Error at SQL INSERT Query: " + ex);
		}
	}

	public void updateQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			if(connection != null){
				Statement statement = connection.createStatement();
				statement.executeUpdate(query);
			}
		} catch (SQLException ex) {
			if (!ex.toString().contains("not return ResultSet"))
				Main.log.warning("Error at SQL UPDATE Query: " + ex);						
		}
	}

	public void deleteQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			if(connection != null){
				Statement statement = connection.createStatement();
				statement.executeUpdate(query);
			}
		} catch (SQLException ex) {
			if (!ex.toString().contains("not return ResultSet"))
				Main.log.warning("Error at SQL DELETE Query: " + ex);
		}
	}

	public Boolean checkTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			if(connection == null)
				Boolean.valueOf(false);
			
			Statement statement = connection.createStatement();

			ResultSet result = statement.executeQuery("SELECT * FROM " + table);

			if (result == null)
				return Boolean.valueOf(false);
			if (result != null)
				return Boolean.valueOf(true);
		} catch (SQLException ex) {
			if (ex.getMessage().contains("exist")) {
				return Boolean.valueOf(false);
			}
			Main.log.warning("Error at SQL Query: " + ex.getMessage());
		}

		if (sqlQuery("SELECT * FROM " + table) == null)
			return Boolean.valueOf(true);
		return Boolean.valueOf(false);
	}
}