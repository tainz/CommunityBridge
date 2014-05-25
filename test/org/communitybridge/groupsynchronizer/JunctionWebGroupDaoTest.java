package org.communitybridge.groupsynchronizer;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
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
	private String groups;
	private WebGroupDao webGroupDao;
	private	Environment environment = new Environment();
	private Configuration configuration = mock(Configuration.class);
	private Log log = mock(Log.class);
	private SQL sql = mock(SQL.class);
	private ResultSet result = mock(ResultSet.class);

	@Before
	public void setup() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		environment.setConfiguration(configuration);
		environment.setLog(log);
		environment.setSql(sql);
		DaoTestsHelper.setupConfiguration(configuration);
		webGroupDao = new JunctionWebGroupDao(environment);

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
		webGroupDao.addUserToGroup(USER_ID, group1, 0);
		verify(sql).insertQuery(query);
	}

	@Test
	public void addGroupUsesCorrectQueryWithOneAdditionalColumn() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String additionalColumn = RandomStringUtils.randomAlphabetic(7);
		String additionalValue = RandomStringUtils.randomAlphanumeric(7);
		configuration.webappSecondaryAdditionalColumns.put(additionalColumn, additionalValue);
		String query = "INSERT INTO `" + configuration.webappSecondaryGroupTable + "` "
						 + "(`" + configuration.webappSecondaryGroupUserIDColumn + "`, `" + configuration.webappSecondaryGroupGroupIDColumn + "`, `" + additionalColumn + "`) "
						 + "VALUES ('" + USER_ID + "', '" + group1 + "', '" + additionalValue + "')";
		doNothing().when(sql).insertQuery(query);
		webGroupDao.addUserToGroup(USER_ID, group1, 0);
		verify(sql).insertQuery(query);
	}

	@Test
	public void addGroupUsesCorrectQueryWithTwoAdditionalColumns() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String additionalColumn = RandomStringUtils.randomAlphabetic(3);
		String additionalValue = RandomStringUtils.randomAlphanumeric(7);
		String additionalColumn2 = RandomStringUtils.randomAlphabetic(4);
		String additionalValue2 = RandomStringUtils.randomAlphanumeric(9);
		configuration.webappSecondaryAdditionalColumns.put(additionalColumn, additionalValue);
		configuration.webappSecondaryAdditionalColumns.put(additionalColumn2, additionalValue2);
		String query = "INSERT INTO `" + configuration.webappSecondaryGroupTable + "` "
						 + "(`" + configuration.webappSecondaryGroupUserIDColumn + "`, `" + configuration.webappSecondaryGroupGroupIDColumn + "`, `" + additionalColumn + "`, `" + additionalColumn2 + "`) "
						 + "VALUES ('" + USER_ID + "', '" + group1 + "', '" + additionalValue + "', '" + additionalValue2 + "')";
		doNothing().when(sql).insertQuery(query);
		webGroupDao.addUserToGroup(USER_ID, group1, 0);
		verify(sql).insertQuery(query);
	}

	@Test
	public void removeGroupUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = "DELETE FROM `" + configuration.webappSecondaryGroupTable + "` "
								 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + USER_ID + "' "
								 + "AND `" + configuration.webappSecondaryGroupGroupIDColumn + "` = '" + group1 + "' ";

		doNothing().when(sql).deleteQuery(query);
		webGroupDao.removeUserFromGroup(USER_ID, group1);
		verify(sql).deleteQuery(query);
	}

	@Test
	public void getSecondaryGroupsShouldNeverReturnNull() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		assertNotNull(webGroupDao.getSecondaryGroupIDs(USER_ID));
	}

	@Test
	public void getSecondaryGroupsShouldHandleNoResult() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.next()).thenReturn(false);
		assertNotNull(webGroupDao.getSecondaryGroupIDs(USER_ID));
	}

	@Test
	public void getSecondaryGroupsWhenSecondaryDisableReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		configuration.webappSecondaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getSecondaryGroupIDs("").size());
	}

	@Test
	public void getSecondaryGroupsWithEmptyStringReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		group1 = "";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithWhitespaceStringReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		group1 = "          ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithNullReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(null);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsReturnsOneGroupID() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1, group2);
		when(result.next()).thenReturn(true, true, false);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoCleanGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(group1 + "  ", group2);
		when(result.next()).thenReturn(true, true, false);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupsReturnsOnlyGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn("  ", group2);
		when(result.next()).thenReturn(true, true, false);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupUserIDsNeverReturnNull() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		assertNotNull(webGroupDao.getSecondaryGroupUserIDs(group1));
	}

	@Test
	public void getSecondaryGroupUserIDsWhenSecondaryDisabledReturnsEmptyList() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappSecondaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getSecondaryGroupUserIDs(group1).size());
	}

	@Test
	public void getSecondaryGroupUserIDsWhenNoQueryResultsReturnsEmptyList() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(result.next()).thenReturn(false);
		assertEquals(0, webGroupDao.getSecondaryGroupUserIDs(group1).size());
	}

	@Test
	public void getSecondaryGroupUserIDsWhenNoGroupsResultsReturnsEmptyList() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		groups = "";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupUserIDs(group1);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupUserIDsWhenWhitespaceResultsReturnsEmptyList() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		groups = "              ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupUserIDs(group1);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupUserIDsReturnsOneUserID() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		groups = RandomStringUtils.randomNumeric(2);
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		when(result.getString(configuration.webappSecondaryGroupUserIDColumn)).thenReturn(USER_ID);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupUserIDs(groups);
		assertEquals(1, secondaryGroups.size());
		assertEquals(USER_ID, secondaryGroups.get(0));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoUserIDs() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String userID2 = RandomStringUtils.randomNumeric(2);
		groups = group1 + "," + group2;
		when(result.next()).thenReturn(true, true, false);
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		when(result.getString(configuration.webappSecondaryGroupUserIDColumn)).thenReturn(USER_ID, userID2);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupUserIDs(group1);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(USER_ID));
		assertTrue(secondaryGroups.contains(userID2));
	}
}