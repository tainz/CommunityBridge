package org.communitybridge.synchronization.group;

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

public class SingleWebGroupDaoTest
{
	private final String USER_ID = RandomStringUtils.randomNumeric(2);
	private final String GROUP_ID = RandomStringUtils.randomNumeric(2);
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

		webGroupDao = new SingleWebGroupDao(environment);

		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(true, false);
	}

	@Test
	public void addGroupWithZeroCountUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getAddGroupUpdateQuery("");
		doNothing().when(sql).updateQuery(query);
		webGroupDao.addUserToGroup(USER_ID, GROUP_ID, 0);
		verify(sql).updateQuery(query);
	}

	@Test
	public void addGroupWithOneCountUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getAddGroupUpdateQuery(",");
		doNothing().when(sql).updateQuery(query);
		webGroupDao.addUserToGroup(USER_ID, GROUP_ID, 1);
		verify(sql).updateQuery(query);
	}

	@Test
	public void addGroupWithTwoCountUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getAddGroupUpdateQuery(",");
		doNothing().when(sql).updateQuery(query);
		webGroupDao.addUserToGroup(USER_ID, GROUP_ID, 2);
		verify(sql).updateQuery(query);
	}


	private String getAddGroupUpdateQuery(String comma)
	{
		return "UPDATE `" + configuration.webappSecondaryGroupTable + "` "
				 + "SET `" + configuration.webappSecondaryGroupGroupIDColumn + "` = CONCAT(`" + configuration.webappSecondaryGroupGroupIDColumn + "`, '"+ comma + GROUP_ID + "') "
				 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + USER_ID + "'";
	}

	@Test
	public void removeGroupUsesCorrectReadQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getRemoveGroupReadQuery();
		when(sql.sqlQuery(query)).thenReturn(result);
		when(result.next()).thenReturn(false);
		webGroupDao.removeUserFromGroup(USER_ID, GROUP_ID);
		verify(sql).sqlQuery(query);
	}

	@Test
	public void removeGroupHandlesNoResultOnRead() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getRemoveGroupReadQuery();
		when(sql.sqlQuery(query)).thenReturn(result);
		when(result.next()).thenReturn(false);
		webGroupDao.removeUserFromGroup(USER_ID, GROUP_ID);
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
		testRemoveGroupUpdateQuery(group1 + "," + group2 + "," + GROUP_ID, group2 + "," + GROUP_ID);
	}

	private void testRemoveGroupUpdateQuery(String before, String after) throws SQLException, IllegalAccessException, MalformedURLException, InstantiationException
	{
		when(sql.sqlQuery(getRemoveGroupReadQuery())).thenReturn(result);
		when(result.next()).thenReturn(true);
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(before);
		String query = getRemoveGroupUpdateQuery(after);
		doNothing().when(sql).updateQuery(query);
		webGroupDao.removeUserFromGroup(USER_ID, group1);
		verify(sql).updateQuery(query);
	}

	private String getRemoveGroupReadQuery()
	{
		return "SELECT `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + configuration.webappSecondaryGroupTable + "` "
					+ "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + USER_ID + "'";
	}

	private String getRemoveGroupUpdateQuery(String after)
	{
		return "UPDATE `" + configuration.webappSecondaryGroupTable + "` "
				 + "SET `" + configuration.webappSecondaryGroupGroupIDColumn + "` = '" + after + "' "
				 + "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + USER_ID + "'";
	}

	@Test
	public void getSecondaryGroupsWithEmptyStringReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = "";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsShouldHandleNoResult() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.next()).thenReturn(false);
		assertNotNull(webGroupDao.getSecondaryGroupIDs(USER_ID));
	}

	@Test
	public void getSecondaryGroupsWithWhitespaceStringReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = "          ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsWithNullReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = "          ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(null);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupsReturnsOneGroupID() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = RandomStringUtils.randomNumeric(2);
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertEquals(groups, secondaryGroups.get(0));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = group1 + "," + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertEquals(group1, secondaryGroups.get(0));
		assertEquals(group2, secondaryGroups.get(1));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoCleanGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = group1 + " , " + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertEquals(group1, secondaryGroups.get(0));
		assertEquals(group2, secondaryGroups.get(1));
	}

	@Test
	public void getSecondaryGroupsReturnsOnlyGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = " , " + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupIDs(USER_ID);
		assertEquals(1, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(group2));
	}

	@Test
	public void getSecondaryGroupsWhenSecondaryDisableReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		configuration.webappSecondaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getSecondaryGroupIDs("").size());
	}

	@Test
	public void getSecondaryGroupUserIDsNeverReturnNull() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		assertNotNull(webGroupDao.getSecondaryGroupUserIDs(GROUP_ID));
	}

	@Test
	public void getSecondaryGroupUserIDsWhenSecondaryDisabledReturnsEmptyList() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappSecondaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getSecondaryGroupUserIDs(GROUP_ID).size());
	}

	@Test
	public void getSecondaryGroupUserIDsWhenNoQueryResultsReturnsEmptyList() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(result.next()).thenReturn(false);
		assertEquals(0, webGroupDao.getSecondaryGroupUserIDs(GROUP_ID).size());
	}

	@Test
	public void getSecondaryGroupUserIDsWhenNoGroupsResultsReturnsEmptyList() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		groups = "";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupUserIDs(GROUP_ID);
		assertEquals(0, secondaryGroups.size());
	}

	@Test
	public void getSecondaryGroupUserIDsWhenWhitespaceResultsReturnsEmptyList() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		groups = "              ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getSecondaryGroupUserIDs(GROUP_ID);
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
