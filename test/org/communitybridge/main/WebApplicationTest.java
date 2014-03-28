package org.communitybridge.main;

import java.net.MalformedURLException;
import java.sql.SQLException;
import org.apache.commons.lang.RandomStringUtils;
import org.communitybridge.dao.WebGroupDao;
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
	TestableWebApplication  webApplication;
	WebGroupDao webGroupDao;
	Log log;
	
	public class TestableWebApplication extends WebApplication
	{
		public TestableWebApplication(WebGroupDao webGroupDao, Log log)
		{
			super(webGroupDao, log);
		}
		
		public TestableWebApplication(CommunityBridge plugin, Configuration config, Log log, SQL sql)
		{
			super(plugin, config, log, sql);
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
		webGroupDao = mock(WebGroupDao.class);
		log = mock(Log.class);
		webApplication= new TestableWebApplication(webGroupDao, log);
	}
	
	@Test
	public void getUserPrimaryGroupIDShouldNeverReturnNull()throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(webGroupDao.getUserPrimaryGroupID(anyString())).thenReturn("");
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
		when(webGroupDao.getUserPrimaryGroupID(anyString())).thenThrow(exception);
		webApplication.getUserPrimaryGroupID(PLAYER_NAME);
		verify(log).severe(WebApplication.EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
	}
	
	@Test
	public void getUserSecondaryGroupIDsShouldNeverReturnNull()throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(webGroupDao.getUserSecondaryGroupIDs(anyString())).thenReturn(WebApplication.EMPTY_LIST);
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
		when(webGroupDao.getUserSecondaryGroupIDs(anyString())).thenThrow(exception);
		assertEquals(0, webApplication.getUserSecondaryGroupIDs(PLAYER_NAME).size());
		verify(log).severe(WebApplication.EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
	}
}
