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

public class KeyValueWebGroupDaoTest
{
	private final String USER_ID = RandomStringUtils.randomNumeric(2);
	private String group1 = RandomStringUtils.randomNumeric(2);
	private String group2 = RandomStringUtils.randomNumeric(2);
	private String group3 = RandomStringUtils.randomNumeric(2);
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
		webGroupDao = new KeyValueWebGroupDao(configuration,sql,log);

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
	public void addGroupWithZeroCountUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = "UPDATE `" + configuration.webappSecondaryGroupTable + "` "
								 + "SET `" + configuration.webappSecondaryGroupGroupIDColumn + "` = CONCAT(`" + configuration.webappSecondaryGroupGroupIDColumn + "`, '" + group1 + "') "
								 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + USER_ID + "' "
								 + "AND `" + configuration.webappSecondaryGroupKeyColumn + "` = '" + configuration.webappSecondaryGroupKeyName + "' ";
		doNothing().when(sql).updateQuery(query);
		webGroupDao.addGroup(USER_ID, group1, 0);
		verify(sql).updateQuery(query);
	}

	@Test
	public void addGroupWithOneCountUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = "UPDATE `" + configuration.webappSecondaryGroupTable + "` "
								 + "SET `" + configuration.webappSecondaryGroupGroupIDColumn + "` = CONCAT(`" + configuration.webappSecondaryGroupGroupIDColumn + "`, '," + group1 + "') "
								 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + USER_ID + "' "
								 + "AND `" + configuration.webappSecondaryGroupKeyColumn + "` = '" + configuration.webappSecondaryGroupKeyName + "' ";
		doNothing().when(sql).updateQuery(query);
		webGroupDao.addGroup(USER_ID, group1, 1);
		verify(sql).updateQuery(query);
	}

	@Test
	public void removeGroupUsesCorrectReadQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getRemoveGroupReadQuery();
		when(sql.sqlQuery(query)).thenReturn(result);
		when(result.next()).thenReturn(false);
		webGroupDao.removeGroup(USER_ID, group1);
		verify(sql).sqlQuery(query);
	}
	
	@Test
	public void removeGroupHandlesNoResultOnRead() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getRemoveGroupReadQuery();
		when(sql.sqlQuery(query)).thenReturn(result);
		when(result.next()).thenReturn(false);
		webGroupDao.removeGroup(USER_ID, group1);
		verify(sql).sqlQuery(query);
		verifyNoMoreInteractions(sql);
	}
	
	@Test
	public void removeGroupUsesCorrectUpdateCallRemovingOneOfNone() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		testRemoveGroupUpdateQuery("", "");
	}
	
	@Test
	public void removeGroupUsesCorrectUpdateCallRemovingOneOfOne() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		testRemoveGroupUpdateQuery(group1, "");
	}

	@Test
	public void removeGroupUsesCorrectUpdateCallRemovingOneOfPair() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		testRemoveGroupUpdateQuery(group1 + "," + group2, group2);
	}

	@Test
	public void removeGroupUsesCorrectUpdateCallRemovingOneOfThree() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		testRemoveGroupUpdateQuery(group1 + "," + group2 + "," + group3, group2 + "," + group3);
	}

	private void testRemoveGroupUpdateQuery(String before, String after) throws SQLException, IllegalAccessException, MalformedURLException, InstantiationException
	{
		when(sql.sqlQuery(getRemoveGroupReadQuery())).thenReturn(result);
		when(result.next()).thenReturn(true);
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(before);
		String query = getRemoveGroupUpdateQuery(after);
		doNothing().when(sql).updateQuery(query);
		webGroupDao.removeGroup(USER_ID, group1);
		verify(sql).updateQuery(query);
	}

	private String getRemoveGroupReadQuery()
	{
		return "SELECT `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + configuration.webappSecondaryGroupTable + "` "
					+ "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + USER_ID + "' "
					+ "AND `" + configuration.webappSecondaryGroupKeyColumn + "` = '" + configuration.webappSecondaryGroupKeyName + "' ";
	}

	private String getRemoveGroupUpdateQuery(String after)
	{
		return "UPDATE `" + configuration.webappSecondaryGroupTable + "` "
				 + "SET `" + configuration.webappSecondaryGroupGroupIDColumn + "` = '" + after + "' "
				 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + USER_ID + "' "
				 + "AND `" + configuration.webappSecondaryGroupKeyColumn + "` = '" + configuration.webappSecondaryGroupKeyName + "'";
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
		groups = "";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithWhitespaceStringReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = "          ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithNullReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = "          ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(null);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsReturnsOneGroupID() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = RandomStringUtils.randomNumeric(2);
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertEquals(groups, secondaryGroups.get(0));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = group1 + "," + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoCleanGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = group1 + " , " + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group1));
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupsReturnsOnlyGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = " , " + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group2));
	}
}