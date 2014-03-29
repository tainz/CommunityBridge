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

public class WebGroupDaoTest
{
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
		DaoTestsHelper.setupConfiguration(configuration);
		log = mock(Log.class);
		sql = mock(SQL.class);
		webGroupDao = new TestableWebGroupDao(configuration, sql, log);
		
		result = mock(ResultSet.class);
	}
	
	@Test
	public void getPrimaryGroupNeverReturnsNull() throws IllegalAccessException, SQLException, MalformedURLException, InstantiationException
	{
		configuration.webappPrimaryGroupEnabled = false;
		assertNotNull(webGroupDao.getUserPrimaryGroupID(""));
	}

	@Test
	public void getPrimaryGroupReturnsBlankWithPrimaryDisabled() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = false;
		assertEquals("", webGroupDao.getUserPrimaryGroupID(USER_ID));
	}
	
	@Test
	public void getPrimaryGroupKeyedWithUnknownIDReturnsBlank() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(result);
		assertEquals("", webGroupDao.getUserPrimaryGroupID(USER_ID));
	}
	
	@Test
	public void getPrimaryGroupKeylessWithUnknownIDReturnsBlank() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappPrimaryGroupUsesKey = false;
		when(sql.sqlQuery(anyString())).thenReturn(result);
		assertEquals("", webGroupDao.getUserPrimaryGroupID(USER_ID));
	}
	
	@Test
	public void getPrimaryGroupWithValidIDReturnsGroup() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
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

		@Override
		public void addGroup(String userID, String groupID, int currentGroupCount) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
		{}

		@Override
		public void removeGroup(String userID, String groupName) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
		{}
	}
}
