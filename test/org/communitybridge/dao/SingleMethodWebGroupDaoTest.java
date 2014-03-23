package org.communitybridge.dao;

import org.communitybridge.dao.WebGroupDao;
import org.communitybridge.dao.SingleMethodWebGroupDao;
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

public class SingleMethodWebGroupDaoTest
{
	private static final String EXCEPTION_MESSAGE = "test message";
	private final String USER_ID = RandomStringUtils.randomNumeric(2);
	private String groups;
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
		webGroupDao = new SingleMethodWebGroupDao(configuration,sql,log);
		
		result = mock(ResultSet.class);
		configuration.webappSecondaryGroupEnabled = true;
		configuration.webappSecondaryGroupGroupIDDelimiter = ",";
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(Boolean.TRUE);
	}
	
	@Test
	public void getSecondaryGroupsWithEmptyStringReturnsEmptyList() throws SQLException
	{
		groups = "";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroups(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithWhitespaceStringReturnsEmptyList() throws SQLException
	{
		groups = "          ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroups(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsReturnsOneGroupID() throws SQLException
	{
		groups = RandomStringUtils.randomNumeric(2);
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroups(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertEquals(groups, secondaryGroups.get(0));
	}
	
	@Test
	public void getSecondaryGroupsReturnsTwoGroupIDs() throws SQLException
	{
		String group1 = RandomStringUtils.randomNumeric(2);
		String group2 = RandomStringUtils.randomNumeric(2);
		groups = group1 + "," + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroups(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertEquals(group1, secondaryGroups.get(0));
		assertEquals(group2, secondaryGroups.get(1));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoCleanGroupIDs() throws SQLException
	{
		String group1 = RandomStringUtils.randomNumeric(2);
		String group2 = RandomStringUtils.randomNumeric(2);
		groups = group1 + " , " + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroups(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertEquals(group1, secondaryGroups.get(0));
		assertEquals(group2, secondaryGroups.get(1));
	}

	@Test
	public void getSecondaryGroupsWhenSecondaryDisableReturnsEmptyList()
	{
		configuration.webappSecondaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getSecondaryGroups("").size());
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
		when(sql.sqlQuery(anyString())).thenThrow(exception);
		assertEquals(0, webGroupDao.getSecondaryGroups(USER_ID).size());
		verify(log).severe(SingleMethodWebGroupDao.EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
	}

}
