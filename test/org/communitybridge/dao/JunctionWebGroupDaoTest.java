package org.communitybridge.dao;

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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JunctionWebGroupDaoTest
{
	private static final String EXCEPTION_MESSAGE = "test message";
	private final String USER_ID = RandomStringUtils.randomNumeric(2);
	private String group1 = RandomStringUtils.randomNumeric(2);
	private String group2 = RandomStringUtils.randomNumeric(2);
	private WebGroupDao webGroupDao;
	private Configuration configuration;
	private Log log;
	private SQL sql;
	private ResultSet result;

	@Before
	public void setup() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration = mock(Configuration.class);
		log = mock(Log.class);
		sql = mock(SQL.class);
		webGroupDao = new JunctionWebGroupDao(configuration,sql,log);

		result = mock(ResultSet.class);
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappSecondaryGroupEnabled = true;
		configuration.webappSecondaryGroupGroupIDDelimiter = ",";
		configuration.webappPrimaryGroupUserIDColumn = "primaryUserID";
		configuration.webappSecondaryGroupUserIDColumn = "secondaryUserID";
		configuration.webappPrimaryGroupGroupIDColumn = "primaryGroupIDs";
		configuration.webappSecondaryGroupGroupIDColumn = "secondaryGroupIDs";
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(true, false);
		when(result.getString(configuration.webappPrimaryGroupUserIDColumn)).thenReturn(USER_ID);
	}

	@Test
	public void getSecondaryGroupsShouldNeverReturnNull()
	{
		assertNotNull(webGroupDao.getUserSecondaryGroupIDs(USER_ID));
	}

	@Test
	public void getSecondaryGroupsWhenSecondaryDisableReturnsEmptyList()
	{
		configuration.webappSecondaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getUserSecondaryGroupIDs("").size());
	}

	@Test
	public void getSecondaryGroupsWithEmptyStringReturnsEmptyList() throws SQLException
	{
		group1 = "";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithWhitespaceStringReturnsEmptyList() throws SQLException
	{
		group1 = "          ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithNullReturnsEmptyList() throws SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(null);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}
	
	@Test
	public void getSecondaryGroupsReturnsOneGroupID() throws SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoGroupIDs() throws SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1, group2);
		when(result.next()).thenReturn(true, true, false);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoCleanGroupIDs() throws SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1 + "  ", group2);
		when(result.next()).thenReturn(true, true, false);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupsReturnsOnlyGroupIDs() throws SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn("  ", group2);
		when(result.next()).thenReturn(true, true, false);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group2));
	}
		@Test
	public void getSecondaryHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testSecondaryGroupsException(exception);
	}
	
	@Test
	public void getSecondaryHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testSecondaryGroupsException(exception);
	}
		
	@Test
	public void getSecondaryHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testSecondaryGroupsException(exception);
	}
	
	@Test
	public void getSecondaryHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testSecondaryGroupsException(exception);
	}
	
	private void testSecondaryGroupsException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		when(sql.sqlQuery(anyString())).thenThrow(exception);
		assertEquals(0, webGroupDao.getUserSecondaryGroupIDs(USER_ID).size());
		verify(log).severe(JunctionWebGroupDao.EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
	}
}