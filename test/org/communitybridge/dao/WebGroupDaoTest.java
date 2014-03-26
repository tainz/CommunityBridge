package org.communitybridge.dao;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
	private static final String group1 = RandomStringUtils.randomNumeric(2);
	private static final String group2 = RandomStringUtils.randomNumeric(2);
	private WebGroupDao webGroupDao;
	private Configuration configuration;
	private Log log;
	private SQL sql;
	private ResultSet result;
	
	@Before
	public void setup()
	{
		configuration = mock(Configuration.class);
		configuration.webappSecondaryGroupGroupIDDelimiter = ",";
		log = mock(Log.class);
		sql = mock(SQL.class);
		webGroupDao = new TestableWebGroupDao(configuration,sql,log);
		
		result = mock(ResultSet.class);
	}
	
	@Test
	public void getPrimaryGroupNeverReturnsNull()
	{
		assertNotNull(webGroupDao.getUserPrimaryGroupID(""));
	}

	@Test
	public void getPrimaryGroupReturnsBlankWithPrimaryDisabled()
	{
		configuration.webappPrimaryGroupEnabled = false;
		assertEquals("", webGroupDao.getUserPrimaryGroupID(USER_ID));
	}
	
	@Test
	public void getPrimaryGroupKeyedWithUnknownIDReturnsBlank() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappPrimaryGroupUsesKey = true;
		when(sql.sqlQuery(anyString())).thenReturn(result);
		assertEquals("", webGroupDao.getUserPrimaryGroupID(USER_ID));
	}
	
	@Test
	public void getPrimaryGroupKeylessWithUnknownIDReturnsBlank() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappPrimaryGroupUsesKey = false;
		when(sql.sqlQuery(anyString())).thenReturn(result);
		assertEquals("", webGroupDao.getUserPrimaryGroupID(USER_ID));
	}
	
	@Test
	public void getPrimaryGroupWithValidIDReturnsGroup() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappPrimaryGroupUsesKey = true;
		configuration.webappPrimaryGroupGroupIDColumn = "group_id";
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(Boolean.TRUE);
		when(result.getString(configuration.webappPrimaryGroupGroupIDColumn)).thenReturn(group1);
		assertEquals(group1, webGroupDao.getUserPrimaryGroupID(USER_ID));
	}
	
	@Test
	public void addCleanIDAddsID()
	{
		List<String> list = new ArrayList<String>();
		
		webGroupDao.addCleanID(group1, list);
		assertEquals(1, list.size());
		assertTrue(list.contains(group1));
	}

	@Test
	public void addCleanIDDoesNotAddEmptyString()
	{
		List<String> list = new ArrayList<String>();
		
		webGroupDao.addCleanID("", list);
		assertEquals(0, list.size());
	}
	
	@Test
	public void addCleanIDDoesNotAddNull()
	{
		List<String> list = new ArrayList<String>();
		
		webGroupDao.addCleanID(null, list);
		assertEquals(0, list.size());
	}
	
	@Test
	public void addCleanIDDoesNotAddBlankString()
	{
		List<String> list = new ArrayList<String>();
		
		webGroupDao.addCleanID("   ", list);
		assertEquals(0, list.size());
	}
	
	@Test
	public void addCleanIDDoesCleansWhitespace()
	{
		List<String> list = new ArrayList<String>();
		
		webGroupDao.addCleanID(" 1  ", list);
		assertEquals(1, list.size());
		assertTrue(list.contains("1"));
	}

	@Test
	public void convertDelimitedStringNeverReturnsNull()
	{
		assertNotNull(webGroupDao.convertDelimitedIDString(""));
	}
	
	@Test
	public void convertDelimitedStringReturnsEmptyWithBlankString()
	{
		assertEquals(0, webGroupDao.convertDelimitedIDString("").size());
	}

	@Test
	public void convertDelimitedStringReturnsEmptyWithWhitespaceString()
	{
		assertEquals(0, webGroupDao.convertDelimitedIDString("  ").size());
	}

	@Test
	public void convertDelimitedStringReturnsEmptyWithNull()
	{
		assertEquals(0, webGroupDao.convertDelimitedIDString(null).size());
	}

	@Test
	public void convertDelimitedStringWithOneItemReturnsOneItem()
	{
		List<String> idList = webGroupDao.convertDelimitedIDString(group1);
		assertEquals(1, idList.size());
		assertTrue(idList.contains(group1));
	}

	@Test
	public void convertDelimitedStringWithTwoItemsReturnsTwoItems()
	{
		List<String> idList = webGroupDao.convertDelimitedIDString(group1 + "," + group2);
		assertEquals(2, idList.size());
		assertTrue(idList.contains(group1));
		assertTrue(idList.contains(group2));
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
		assertEquals("", webGroupDao.getUserPrimaryGroupID(USER_ID));
		verify(log).severe(WebGroupDao.EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
	}

	public class TestableWebGroupDao extends WebGroupDao
	{
		public TestableWebGroupDao(Configuration configuration, SQL sql, Log log)
		{
			super(configuration, sql, log);
		}

		@Override
		public List<String> getUserSecondaryGroupIDs(String userID)
		{
			return null;
		}

		@Override
		public List<String> getGroupUserIDs(String groupID)
		{
			return null;
		}

		@Override
		public List<String> getGroupUserIDsPrimary(String groupID)
		{
			return null;
		}

		@Override
		public List<String> getGroupUserIDsSecondary(String groupID)
		{
			return null;
		}
	}
}