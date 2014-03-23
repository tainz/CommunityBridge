package org.communitybridge.dao;

import org.communitybridge.dao.WebGroupDao;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebGroupDaoTest
{
	private static final String EXCEPTION_MESSAGE = "test message";
	private static final String USER_ID = RandomStringUtils.randomNumeric(2);
	private static final String GROUP_ID = RandomStringUtils.randomNumeric(2);
	private WebGroupDao webGroupDao;
	private Configuration configuration;
	private Log log;
	private SQL sql;
	private ResultSet result;
	
	@Before
	public void setup()
	{
		configuration = mock(Configuration.class);
		log = mock(Log.class);
		sql = mock(SQL.class);
		webGroupDao = new TestableWebGroupDao(configuration,sql,log);
		
		result = mock(ResultSet.class);
	}
	
	@Test
	public void getPrimaryGroupNeverReturnsNull()
	{
		assertNotNull(webGroupDao.getPrimary(""));
	}

	@Test
	public void getPrimaryGroupReturnsBlankWithPrimaryDisabled()
	{
		configuration.webappPrimaryGroupEnabled = false;
		assertEquals("", webGroupDao.getPrimary(USER_ID));
	}
	
	@Test
	public void getPrimaryGroupKeyedWithUnknownIDReturnsBlank() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappPrimaryGroupUsesKey = true;
		when(sql.sqlQuery(anyString())).thenReturn(result);
		assertEquals("", webGroupDao.getPrimary(USER_ID));
	}
	
	@Test
	public void getPrimaryGroupKeylessWithUnknownIDReturnsBlank() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappPrimaryGroupUsesKey = false;
		when(sql.sqlQuery(anyString())).thenReturn(result);
		assertEquals("", webGroupDao.getPrimary(USER_ID));
	}
	
	@Test
	public void getPrimaryGroupWithValidIDReturnsGroup() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappPrimaryGroupUsesKey = true;
		configuration.webappPrimaryGroupGroupIDColumn = "group_id";
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(Boolean.TRUE);
		when(result.getString(configuration.webappPrimaryGroupGroupIDColumn)).thenReturn(GROUP_ID);
		assertEquals(GROUP_ID, webGroupDao.getPrimary(USER_ID));
	}
	
	@Test
	public void getPrimaryHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testException(exception);
	}
	
	@Test
	public void getPrimaryHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testException(exception);
	}
		
	@Test
	public void getPrimaryHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testException(exception);
	}
	
	@Test
	public void getPrimaryHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testException(exception);
	}
	
	private void testException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappPrimaryGroupUsesKey = true;
		configuration.webappPrimaryGroupGroupIDColumn = "group_id";
		when(sql.sqlQuery(anyString())).thenThrow(exception);
		assertEquals("", webGroupDao.getPrimary(USER_ID));
		verify(log).severe(WebGroupDao.EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
	}

	public class TestableWebGroupDao extends WebGroupDao
	{
		public TestableWebGroupDao(Configuration configuration, SQL sql, Log log)
		{
			super(configuration, sql, log);
		}

		@Override
		public List<String> getSecondaryGroups(String userID)
		{
			return null;
		}
	}
}