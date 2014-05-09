package org.communitybridge.main;

import java.net.MalformedURLException;
import java.sql.SQLException;
import org.apache.commons.lang.RandomStringUtils;
import org.communitybridge.groupsynchronizer.WebGroupDao;
import org.communitybridge.utility.Log;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

public class WebApplicationTest
{
	private static final String EXCEPTION_MESSAGE = "test message";
	private static final String PLAYER_NAME = RandomStringUtils.randomAlphabetic(7);
	private static final String USER_ID = RandomStringUtils.randomNumeric(2);
	private static final String GROUP_NAME = RandomStringUtils.randomAlphabetic(10);
	private static final String GROUP_ID = RandomStringUtils.randomAlphabetic(2);
	private static final int COUNT = 0;

	private TestableWebApplication  webApplication;

	private Environment environment = new Environment();
	private Configuration configuration = mock(Configuration.class);
	private Log log = mock(Log.class);
	private SQL sql = mock(SQL.class);

	private WebGroupDao webGroupDao = mock(WebGroupDao.class);

	public class TestableWebApplication extends WebApplication
	{
		public TestableWebApplication(Environment environment, WebGroupDao webGroupDao)
		{
			super(environment, webGroupDao);
		}

		public TestableWebApplication(CommunityBridge plugin, Environment environment)
		{
			super(plugin, environment);
		}

		@Override
		public String getUserID(String playerName)
		{
			return USER_ID;
		}
	}

	@Before
	public void setup()
	{
		environment.setConfiguration(configuration);
		environment.setLog(log);
		environment.setSql(sql);
		webApplication = new TestableWebApplication(environment, webGroupDao);
	}

	@Test
	public void addGroupWorks() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		doNothing().when(webGroupDao).addUserToGroup(USER_ID, GROUP_NAME, COUNT);
		webApplication.addGroup(USER_ID, GROUP_NAME, COUNT);
	}

	@Test
	public void addGroupHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testAddGroupException(exception);
	}

	@Test
	public void addGroupHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testAddGroupException(exception);
	}

	@Test
	public void addGroupHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testAddGroupException(exception);
	}

	@Test
	public void addGroupHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testAddGroupException(exception);
	}

	private void testAddGroupException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		doThrow(exception).when(webGroupDao).addUserToGroup(USER_ID, GROUP_NAME, COUNT);
		webApplication.addGroup(USER_ID, GROUP_NAME, COUNT);
		verify(log).severe(WebApplication.EXCEPTION_MESSAGE_ADDGROUP + exception.getMessage());
	}

	@Test
	public void removeGroupWorks() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		doNothing().when(webGroupDao).removeUserFromGroup(USER_ID, GROUP_NAME);
		webApplication.removeGroup(USER_ID, GROUP_NAME);
	}

	@Test
	public void removeGroupHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testRemoveGroupException(exception);
	}

	@Test
	public void removeGroupHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testRemoveGroupException(exception);
	}

	@Test
	public void removeGroupHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testRemoveGroupException(exception);
	}

	@Test
	public void removeGroupHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testRemoveGroupException(exception);
	}

	private void testRemoveGroupException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		when(configuration.getWebappGroupIDbyGroupName(GROUP_NAME)).thenReturn(GROUP_ID);
		doThrow(exception).when(webGroupDao).removeUserFromGroup(USER_ID, GROUP_ID);
		webApplication.removeGroup(USER_ID, GROUP_NAME);
		verify(log).severe(WebApplication.EXCEPTION_MESSAGE_REMOVEGROUP + exception.getMessage());
	}

	@Test
	public void getUserPrimaryGroupIDShouldNeverReturnNull()throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(webGroupDao.getPrimaryGroupID(anyString())).thenReturn("");
		assertNotNull(webApplication.getUserPrimaryGroupID(""));
	}

	@Test
	public void getUserPrimaryGroupIDHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testUserPrimaryGroupIDGroupsException(exception);
	}

	@Test
	public void getUserPrimaryGroupIDHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testUserPrimaryGroupIDGroupsException(exception);
	}

	@Test
	public void getUserPrimaryGroupIDHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testUserPrimaryGroupIDGroupsException(exception);
	}

	@Test
	public void getUserPrimaryGroupIDHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testUserPrimaryGroupIDGroupsException(exception);
	}

	private void testUserPrimaryGroupIDGroupsException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		when(webGroupDao.getPrimaryGroupID(anyString())).thenThrow(exception);
		webApplication.getUserPrimaryGroupID(PLAYER_NAME);
		verify(log).severe(WebApplication.EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
	}

	@Test
	public void getUserSecondaryGroupIDsShouldNeverReturnNull()throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(webGroupDao.getSecondaryGroupIDs(anyString())).thenReturn(WebApplication.EMPTY_LIST);
		assertNotNull(webApplication.getUserSecondaryGroupIDs(""));
	}

	@Test
	public void getUserSecondaryGroupIDsHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testSecondaryGroupUserIDsGroupsException(exception);
	}

	@Test
	public void getUserSecondaryGroupIDsHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testSecondaryGroupUserIDsGroupsException(exception);
	}

	@Test
	public void getUserSecondaryGroupIDsHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testSecondaryGroupUserIDsGroupsException(exception);
	}

	@Test
	public void getUserSecondaryGroupIDsHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testSecondaryGroupUserIDsGroupsException(exception);
	}

	private void testSecondaryGroupUserIDsGroupsException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		when(webGroupDao.getSecondaryGroupIDs(anyString())).thenThrow(exception);
		assertEquals(0, webApplication.getUserSecondaryGroupIDs(PLAYER_NAME).size());
		verify(log).severe(WebApplication.EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
	}
}
