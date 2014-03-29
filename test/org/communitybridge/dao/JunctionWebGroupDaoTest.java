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
import static org.mockito.Mockito.*;

public class JunctionWebGroupDaoTest
{
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
		DaoTestsHelper.setupConfiguration(configuration);
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(true, false);
		when(result.getString(configuration.webappPrimaryGroupUserIDColumn)).thenReturn(USER_ID);
	}

	@Test
	public void addGroupUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = "INSERT INTO `" + configuration.webappSecondaryGroupTable + "` "
						 + "(`" + configuration.webappSecondaryGroupUserIDColumn + "`, `" + configuration.webappSecondaryGroupGroupIDColumn + "`) "
						 + "VALUES ('" + USER_ID + "', '" + group1 +"')";
		doNothing().when(sql).insertQuery(query);
		webGroupDao.addGroup(USER_ID, group1, 0);
		verify(sql).insertQuery(query);
	}

	@Test
	public void removeGroupUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = "DELETE FROM `" + configuration.webappSecondaryGroupTable + "` "
								 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + USER_ID + "' "
								 + "AND `" + configuration.webappSecondaryGroupGroupIDColumn + "` = '" + group1 + "' ";
		
		doNothing().when(sql).deleteQuery(query);
		webGroupDao.removeGroup(USER_ID, group1);
		verify(sql).deleteQuery(query);
	}

	@Test
	public void getSecondaryGroupsShouldNeverReturnNull() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		assertNotNull(webGroupDao.getUserSecondaryGroupIDs(USER_ID));
	}
	
	@Test
	public void getSecondaryGroupsShouldHandleNoResult() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.next()).thenReturn(false);
		assertNotNull(webGroupDao.getUserSecondaryGroupIDs(USER_ID));
	}
	
	@Test
	public void getSecondaryGroupsWhenSecondaryDisableReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		configuration.webappSecondaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getUserSecondaryGroupIDs("").size());
	}

	@Test
	public void getSecondaryGroupsWithEmptyStringReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		group1 = "";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithWhitespaceStringReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		group1 = "          ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithNullReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(null);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}
	
	@Test
	public void getSecondaryGroupsReturnsOneGroupID() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1, group2);
		when(result.next()).thenReturn(true, true, false);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoCleanGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1 + "  ", group2);
		when(result.next()).thenReturn(true, true, false);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupsReturnsOnlyGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn("  ", group2);
		when(result.next()).thenReturn(true, true, false);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group2));
	}
}