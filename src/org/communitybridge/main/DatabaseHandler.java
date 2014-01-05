package org.communitybridge.main;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler
{
	private Connection connection;
	private String dblocation;
	private String username;
	private String password;
	private String database;

	public DatabaseHandler(String dbLocation, String database, String username, String password)
	{
		this.dblocation = dbLocation;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	private void openConnection() throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.dblocation + "/" + this.database, this.username, this.password);
		}
		catch (ClassNotFoundException exception)
		{
			CommunityBridge.log.severe("No MySQL Driver Found:" + exception.getMessage());
			this.connection = null;
		}
		catch (SQLException exception)
		{
			CommunityBridge.log.severe("Could not connect to MySQL Server:" + exception.getMessage());
			this.connection = null;
		}
	}

	public boolean checkConnection()
	{
		if (this.connection == null)
		{
			try
			{
				openConnection();
				if (this.connection == null)
				{
					return false;
				}
				return true;
			}
			catch (MalformedURLException exception)
			{
				CommunityBridge.log.severe("MalformedURLException! " + exception.getMessage());
			}
			catch (InstantiationException exception)
			{
				CommunityBridge.log.severe("InstantiationExceptioon! " + exception.getMessage());
			}
			catch (IllegalAccessException exception)
			{
				CommunityBridge.log.severe("IllegalAccessException! " + exception.getMessage());
			}
			return false;
		}
		
		try
		{
			return !connection.isClosed();
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	public void closeConnection() {
		try
		{
			if (this.connection != null)
			{
				this.connection.close();
			}
		}
		catch (Exception e)
		{
			CommunityBridge.log.warning("Failed to close database connection! " + e.getMessage());
		}
	}

	public Connection getConnection() throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		if (this.connection == null)
		{
			openConnection();
			return this.connection;
		}
		else
		{
			try
			{
				if (this.connection.isClosed())
				{
					openConnection();
				}

				Statement statement = connection.createStatement();
				statement.setQueryTimeout(5);
				ResultSet result = statement.executeQuery("SELECT 1");
				
				if (result.next())
				{
					return this.connection;
				}
				openConnection();
				return this.connection;
			}
			catch (SQLException exception)
			{
				try
				{
					openConnection();

					Statement statement = connection.createStatement();
					statement.setQueryTimeout(5);
					ResultSet result = statement.executeQuery("SELECT 1");
					if (result.next())
					{
						return this.connection;
					}
				}
				catch (SQLException exception2)
				{
					CommunityBridge.log.warning("Database Connection Error: " + exception2.getMessage());
				}
			}
		}
		return null;
	}

	public ResultSet sqlQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		Connection connectionLocal = getConnection();

		if (connectionLocal == null)
		{
			return null;
		}

		Statement statement = connectionLocal.createStatement();

		statement.setQueryTimeout(10);

		return statement.executeQuery(query);
	}

	public void insertQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		try
		{
			Connection connectionLocal = getConnection();
		
			if (connectionLocal != null)
			{
				Statement statement = connectionLocal.createStatement();
				statement.executeUpdate(query);
			}
		} 
		catch (SQLException exception)
		{
			if (!exception.toString().contains("not return ResultSet"))
			{
				throw exception;
			}
		}
	}

	public void updateQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		try
		{
			Connection connectionLocal = getConnection();
			
			if (connectionLocal != null)
			{
				Statement statement = connectionLocal.createStatement();
				statement.executeUpdate(query);
			}
		}
		catch (SQLException exception)
		{
			if (!exception.toString().contains("not return ResultSet"))
			{
				CommunityBridge.log.warning("Error at SQL UPDATE Query: " + exception);
			}
		}
	}

	public void deleteQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		try
		{
			Connection connectionLocal = getConnection();
			
			if (connectionLocal != null)
			{
				Statement statement = connectionLocal.createStatement();
				statement.executeUpdate(query);
			}
		}
		catch (SQLException exception)
		{
			if (!exception.toString().contains("not return ResultSet"))
			{
				CommunityBridge.log.warning("Error at SQL DELETE Query: " + exception);
			}
		}
	}

	public boolean checkTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		try
		{
			Connection connectionLocal = getConnection();
			
			if (connectionLocal == null)
			{
				return false;
			}
			Statement statement = connectionLocal.createStatement();

			ResultSet result = statement.executeQuery("SELECT * FROM " + table);

			if (result == null)
			{
				return false;
			}
			return true;
		}
		catch (SQLException exception)
		{
			if (exception.getMessage().contains("exist"))
			{
				return false;
			}
			
			CommunityBridge.log.warning("Error at SQL Query: " + exception.getMessage());
		}
		try
		{
			if (sqlQuery("SELECT * FROM " + table) == null)
			{
				return true;
			}
		}
		catch (SQLException exception)
		{
			CommunityBridge.log.warning("Error at SQL Query: " + exception.getMessage());
		}
		return false;
	}
}
