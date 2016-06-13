package org.communitybridge.main;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.communitybridge.utility.Log;

public class DatabaseHandler
{
	private Log log;
	private Connection connection;
	private String dblocation;
	private String username;
	private String password;
	private String database;
	private String localAddress;

	public DatabaseHandler(Log log, String dbLocation, String database, String username, String password, String localAddress)
	{
		this.log = log;
		this.dblocation = dbLocation;
		this.database = database;
		this.username = username;
		this.password = password;
		this.localAddress = localAddress;
	}

	private void openConnection() throws MalformedURLException, InstantiationException, IllegalAccessException
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Properties properties = new Properties();
			properties.setProperty("user", username);
			properties.setProperty("password", password);
			if (!localAddress.isEmpty())
			{
				properties.setProperty("localSocketAddress", localAddress);
			}
			connection = DriverManager.getConnection("jdbc:mysql://" + dblocation + "/" + database, properties);
		}
		catch (ClassNotFoundException exception)
		{
			log.severe("No MySQL Driver Found:" + exception.getMessage());
			connection = null;
		}
		catch (SQLException exception)
		{
			log.severe("Could not connect to MySQL Server:" + exception.getMessage());
			connection = null;
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
				log.severe("MalformedURLException! " + exception.getMessage());
			}
			catch (InstantiationException exception)
			{
				log.severe("InstantiationExceptioon! " + exception.getMessage());
			}
			catch (IllegalAccessException exception)
			{
				log.severe("IllegalAccessException! " + exception.getMessage());
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
			log.warning("Failed to close database connection! " + e.getMessage());
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
					log.warning("Database Connection Exception: " + exception2.getMessage());
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
				log.warning("Exception at SQL UPDATE Query: " + exception);
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
				log.warning("Exception at SQL DELETE Query: " + exception);
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

			log.warning("Exception at SQL Query: " + exception.getMessage());
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
			log.warning("Exception at SQL Query: " + exception.getMessage());
		}
		return false;
	}
}
