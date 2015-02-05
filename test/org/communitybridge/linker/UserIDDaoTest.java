package org.communitybridge.linker;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang.RandomStringUtils;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

public class UserIDDaoTest
{
	private static final String EXCEPTION_MESSAGE = "test message";
	private static final String IDENTIFIER = RandomStringUtils.randomAlphabetic(7);
	private static final String USER_ID = RandomStringUtils.randomNumeric(2);
	private static final String UUID = RandomStringUtils.randomAlphabetic(36);

	private Environment environment = new Environment();
	private UserIDDao userIDDao;
	private Configuration configuration = mock(Configuration.class);
	private Log log = mock(Log.class);
	private SQL sql = mock(SQL.class);
	private ResultSet result = mock(ResultSet.class);

	@Before
	public void setup()
	{
		environment.setConfiguration(configuration);
		environment.setLog(log);
		environment.setSql(sql);
		userIDDao = new UserIDDao(environment);
		configuration.linkingTableName = RandomStringUtils.randomAlphabetic(6);
		configuration.linkingUserIDColumn = RandomStringUtils.randomAlphabetic(9);
		configuration.linkingIdentifierColumn = RandomStringUtils.randomAlphabetic(4);
		configuration.linkingKeyColumn = RandomStringUtils.randomAlphabetic(7);
		configuration.linkingValueColumn = RandomStringUtils.randomAlphabetic(5);
		configuration.linkingKeyName = RandomStringUtils.randomAlphabetic(8);
	}

	@Test
	public void getUserIDNeverReturnsNull()
	{
		assertNotNull(userIDDao.getUserID(null));
	}

	@Test
	public void getUserIDUsesCorrectKeyedQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.linkingUsesKey = true;

		String query = "SELECT `" + configuration.linkingTableName + "`.`" + configuration.linkingUserIDColumn + "` "
								 + "FROM `" + configuration.linkingTableName + "` "
								 + "WHERE `" + configuration.linkingKeyColumn + "` = '" + configuration.linkingKeyName + "' "
								 + "AND `" + configuration.linkingValueColumn + "` = '" + IDENTIFIER + "' "
								 + "ORDER BY `" + configuration.linkingUserIDColumn + "` DESC";
		userIDDao.getUserID(IDENTIFIER);
		verify(sql).sqlQuery(query);
	}

	@Test
	public void getUserIDUsesCorrectKeylessQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.linkingUsesKey = false;

		String query = "SELECT `" + configuration.linkingTableName + "`.`" + configuration.linkingUserIDColumn + "` "
								 + "FROM `" + configuration.linkingTableName + "` "
								 + "WHERE LOWER(`" + configuration.linkingIdentifierColumn + "`) = LOWER('" + IDENTIFIER + "') "
								 + "ORDER BY `" + configuration.linkingUserIDColumn + "` DESC";
		userIDDao.getUserID(IDENTIFIER);
		verify(sql).sqlQuery(query);
	}

	@Test
	public void getUserIDWithNullResultReturnsEmptyString() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(null);
		String userID = userIDDao.getUserID(IDENTIFIER);
		assertEquals("", userID);
	}

	@Test
	public void getUserIDWithEmptyResultReturnsEmptyString() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(false);
		String userID = userIDDao.getUserID(IDENTIFIER);
		assertEquals("", userID);
	}

	@Test
	public void getUserIDReturnsUserID() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.getString(configuration.linkingUserIDColumn)).thenReturn(USER_ID);
		when(result.next()).thenReturn(true);
		assertEquals(USER_ID, userIDDao.getUserID(IDENTIFIER));
	}

	@Test
	public void getUserIDHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testGetUserIDFromDatabaseException(exception);
	}

	@Test
	public void getUserIDHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testGetUserIDFromDatabaseException(exception);
	}

	@Test
	public void getUserIDHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testGetUserIDFromDatabaseException(exception);
	}

	@Test
	public void getUserIDHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testGetUserIDFromDatabaseException(exception);
	}

	private void testGetUserIDFromDatabaseException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		when(sql.sqlQuery(anyString())).thenThrow(exception);
		assertEquals("", userIDDao.getUserID(IDENTIFIER));
		verify(log).severe(UserIDDao.EXCEPTION_MESSAGE_GETUSERID + exception.getMessage());
	}

	@Test
	public void getUUIDNeverReturnsNull()
	{
		assertNotNull(userIDDao.getUUID(null));
	}

	@Test
	public void getUUIDUsesCorrectKeyedQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.linkingUsesKey = true;

		String query = "SELECT `" + configuration.linkingValueColumn + "` "
						+ "FROM `" + configuration.linkingTableName + "` "
						+ "WHERE `" + configuration.linkingKeyColumn + "` = '" + configuration.linkingKeyName + "' "
						+ "AND `" + configuration.linkingUserIDColumn + "` = '" + USER_ID + "'";
		userIDDao.getUUID(USER_ID);
		verify(sql).sqlQuery(query);
	}

	@Test
	public void getUUIDUsesCorrectKeylessQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.linkingUsesKey = false;

		String query = "SELECT `" + configuration.linkingIdentifierColumn + "` "
						+ "FROM `" + configuration.linkingTableName + "` "
						+ "WHERE `" + configuration.linkingUserIDColumn + "` = '" + USER_ID + "'";
		userIDDao.getUUID(USER_ID);
		verify(sql).sqlQuery(query);
	}

	@Test
	public void getUUIDWithNullResultReturnsEmptyString() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(null);
		assertEquals("", userIDDao.getUUID(USER_ID));
	}

	@Test
	public void getUUIDWithEmptyResultReturnsEmptyString() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(false);
		assertEquals("", userIDDao.getUUID(USER_ID));
	}

	@Test
	public void getUUIDKeylessReturnsUUID() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.linkingUsesKey = false;
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.getString(configuration.linkingIdentifierColumn)).thenReturn(UUID);
		when(result.next()).thenReturn(true);
		assertEquals(UUID, userIDDao.getUUID(USER_ID));
	}

	@Test
	public void getUUIDKeyedReturnsUUID() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.linkingUsesKey = true;
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.getString(configuration.linkingValueColumn)).thenReturn(UUID);
		when(result.next()).thenReturn(true);
		assertEquals(UUID, userIDDao.getUUID(USER_ID));
	}

	@Test
	public void getUUIDHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testGetUUIDFromDatabaseException(exception);
	}

	@Test
	public void getUUIDHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testGetUUIDFromDatabaseException(exception);
	}

	@Test
	public void getUUIDHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testGetUUIDFromDatabaseException(exception);
	}

	@Test
	public void getUUIDHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testGetUUIDFromDatabaseException(exception);
	}

	private void testGetUUIDFromDatabaseException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		when(sql.sqlQuery(anyString())).thenThrow(exception);
		assertEquals("", userIDDao.getUUID(IDENTIFIER));
		verify(log).severe(UserIDDao.EXCEPTION_MESSAGE_GETUUID + exception.getMessage());
	}
}