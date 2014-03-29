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

public class SingleWebGroupDaoTest
{
	private static final String EXCEPTION_MESSAGE = "test message";
	private final String USER_ID = RandomStringUtils.randomNumeric(2);
	private final String GROUP_ID = RandomStringUtils.randomNumeric(2);
	private String group1 = RandomStringUtils.randomNumeric(2);
  private String group2 = RandomStringUtils.randomNumeric(2);
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
		webGroupDao = new SingleWebGroupDao(configuration,sql,log);
		
		result = mock(ResultSet.class);

		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappSecondaryGroupEnabled = true;
		configuration.webappPrimaryGroupUserIDColumn = "primaryUserID";
		configuration.webappSecondaryGroupUserIDColumn = "secondaryUserID";
		configuration.webappPrimaryGroupGroupIDColumn = "primaryGroupIDs";
		configuration.webappSecondaryGroupGroupIDColumn = "secondaryGroupIDs";
		configuration.webappSecondaryGroupGroupIDDelimiter = ",";

		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(true, false);
		when(result.getString(configuration.webappPrimaryGroupUserIDColumn)).thenReturn(USER_ID);
	}
	
	@Test
	public void addGroupWithZeroCountUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getAddGroupUpdateQuery("");
		doNothing().when(sql).updateQuery(query);
		webGroupDao.addGroup(USER_ID, GROUP_ID, 0);
		verify(sql).updateQuery(query);
	}

	@Test
	public void addGroupWithOneCountUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getAddGroupUpdateQuery("");
		doNothing().when(sql).updateQuery(query);
		webGroupDao.addGroup(USER_ID, GROUP_ID, 1);
		verify(sql).updateQuery(query);
	}

	@Test
	public void addGroupWithTwoCountUsesCorrectQuery() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getAddGroupUpdateQuery(",");
		doNothing().when(sql).updateQuery(query);
		webGroupDao.addGroup(USER_ID, GROUP_ID, 2);
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
		webGroupDao.removeGroup(USER_ID, GROUP_ID);
		verify(sql).sqlQuery(query);
	}
	
	@Test
	public void removeGroupHandlesNoResultOnRead() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String query = getRemoveGroupReadQuery();
		when(sql.sqlQuery(query)).thenReturn(result);
		when(result.next()).thenReturn(false);
		webGroupDao.removeGroup(USER_ID, GROUP_ID);
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
		webGroupDao.removeGroup(USER_ID, group1);
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
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(0, secondaryGroups.size());
	}
	
	@Test
	public void getSecondaryGroupsShouldHandleNoResult() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		when(result.next()).thenReturn(false);
		assertNotNull(webGroupDao.getUserSecondaryGroupIDs(USER_ID));
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
		assertEquals(group1, secondaryGroups.get(0));
		assertEquals(group2, secondaryGroups.get(1));
	}

	@Test
	public void getSecondaryGroupsReturnsTwoCleanGroupIDs() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		groups = group1 + " , " + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getUserSecondaryGroupIDs(USER_ID);
		assertEquals(2, secondaryGroups.size());
		assertEquals(group1, secondaryGroups.get(0));
		assertEquals(group2, secondaryGroups.get(1));
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
	
	@Test
	public void getSecondaryGroupsWhenSecondaryDisableReturnsEmptyList() throws IllegalAccessException, InstantiationException,MalformedURLException, SQLException
	{
		configuration.webappSecondaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getUserSecondaryGroupIDs("").size());
	}
	
	@Test
	public void getPrimaryGroupUserIDsNeverReturnNull()
	{
		assertNotNull(webGroupDao.getGroupUserIDsPrimary(GROUP_ID));
	}

	@Test
	public void getPrimaryGroupUserIDsWhenPrimaryDisabledReturnsEmptyList()
	{
		configuration.webappPrimaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getGroupUserIDsPrimary(GROUP_ID).size());
	}
	
	@Test
	public void getPrimaryGroupUserIDsWhenNoMembersReturnsEmptyList() throws SQLException
	{
		when(result.next()).thenReturn(false);
		assertEquals(0, webGroupDao.getGroupUserIDsPrimary(GROUP_ID).size());
	}

	@Test
	public void getPrimaryGroupUserIDsReturnUserIDForMemberOFPrimaryGroup() throws SQLException
	{
		List<String> groupMembers = webGroupDao.getGroupUserIDsPrimary(GROUP_ID);
		assertEquals(1, groupMembers.size());
		assertEquals(USER_ID, groupMembers.get(0));
	}
	
	@Test
	public void getPrimaryGroupUserIDsReturnUserIDsForMemberOFPrimaryGroup() throws SQLException
	{
		String userID2 = RandomStringUtils.randomNumeric(2);
		when(result.next()).thenReturn(true, true, false);
		when(result.getString(configuration.webappPrimaryGroupUserIDColumn)).thenReturn(USER_ID, userID2);

		List<String> groupMembers = webGroupDao.getGroupUserIDsPrimary(GROUP_ID);
		assertEquals(2, groupMembers.size());
		assertEquals(USER_ID, groupMembers.get(0));
		assertEquals(userID2, groupMembers.get(1));
	}
	
	@Test
	public void getPrimaryGroupUserIDsHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testPrimaryGroupUserIDsGroupsException(exception);
	}
	
	@Test
	public void getPrimaryGroupUserIDsHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testPrimaryGroupUserIDsGroupsException(exception);
	}
		
	@Test
	public void getPrimaryGroupUserIDsHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testPrimaryGroupUserIDsGroupsException(exception);
	}
	
	@Test
	public void getPrimaryGroupUserIDsHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testPrimaryGroupUserIDsGroupsException(exception);
	}
	
	private void testPrimaryGroupUserIDsGroupsException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		when(sql.sqlQuery(anyString())).thenThrow(exception);
		assertEquals(0, webGroupDao.getGroupUserIDsPrimary(USER_ID).size());
		verify(log).severe(SingleWebGroupDao.EXCEPTION_MESSAGE_GETPRIMARY_USERIDS + exception.getMessage());
	}
	
	@Test
	public void getSecondaryGroupUserIDsNeverReturnNull()
	{
		assertNotNull(webGroupDao.getGroupUserIDsSecondary(GROUP_ID));
	}

	@Test
	public void getSecondaryGroupUserIDsWhenSecondaryDisabledReturnsEmptyList()
	{
		configuration.webappSecondaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getGroupUserIDsSecondary(GROUP_ID).size());
	}
	
	@Test
	public void getSecondaryGroupUserIDsWhenNoQueryResultsReturnsEmptyList() throws SQLException
	{
		when(result.next()).thenReturn(false);
		assertEquals(0, webGroupDao.getGroupUserIDsSecondary(GROUP_ID).size());
	}
	
	@Test
	public void getSecondaryGroupUserIDsWhenNoGroupsResultsReturnsEmptyList() throws SQLException
	{
		groups = "";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getGroupUserIDsSecondary(GROUP_ID);
		assertEquals(0, secondaryGroups.size());
	}
	
	@Test
	public void getSecondaryGroupUserIDsWhenWhitespaceResultsReturnsEmptyList() throws SQLException
	{
		groups = "              ";
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		List<String> secondaryGroups = webGroupDao.getGroupUserIDsSecondary(GROUP_ID);
		assertEquals(0, secondaryGroups.size());
	}
	
	@Test
	public void getSecondaryGroupUserIDsReturnsOneUserID() throws SQLException
	{
		groups = RandomStringUtils.randomNumeric(2);
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		when(result.getString(configuration.webappSecondaryGroupUserIDColumn)).thenReturn(USER_ID);
		List<String> secondaryGroups = webGroupDao.getGroupUserIDsSecondary(groups);
		assertEquals(1, secondaryGroups.size());
		assertEquals(USER_ID, secondaryGroups.get(0));
	}
	
	@Test
	public void getSecondaryGroupsReturnsTwoUserIDs() throws SQLException
	{
		String userID2 = RandomStringUtils.randomNumeric(2);
		groups = group1 + "," + group2;
		when(result.next()).thenReturn(true, true, false);
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		when(result.getString(configuration.webappSecondaryGroupUserIDColumn)).thenReturn(USER_ID, userID2);
		List<String> secondaryGroups = webGroupDao.getGroupUserIDsSecondary(group1);
		assertEquals(2, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(USER_ID));
		assertTrue(secondaryGroups.contains(userID2));
	}
	
	@Test
	public void getSecondaryGroupUserIDsHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testSecondaryGroupUserIDsGroupsException(exception);
	}
	
	@Test
	public void getSecondaryGroupUserIDsHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testSecondaryGroupUserIDsGroupsException(exception);
	}
		
	@Test
	public void getSecondaryGroupUserIDsHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testSecondaryGroupUserIDsGroupsException(exception);
	}
	
	@Test
	public void getSecondaryGroupUserIDsHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testSecondaryGroupUserIDsGroupsException(exception);
	}
	
	private void testSecondaryGroupUserIDsGroupsException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		when(sql.sqlQuery(anyString())).thenThrow(exception);
		assertEquals(0, webGroupDao.getGroupUserIDsSecondary(USER_ID).size());
		verify(log).severe(SingleWebGroupDao.EXCEPTION_MESSAGE_GETSECONDARY_USERIDS + exception.getMessage());
	}
	
	@Test
	public void getGroupUserIDs() throws SQLException
	{
		String userID2 = RandomStringUtils.randomNumeric(2);
		String primaryID1 = RandomStringUtils.randomNumeric(2);
		String primaryID2 = RandomStringUtils.randomNumeric(2);
		when(result.next()).thenReturn(true, true, false, true, true, false);
		when(result.getString(configuration.webappPrimaryGroupUserIDColumn)).thenReturn(primaryID1, primaryID2);

		groups = group1 + "," + group2;
		when(result.getString(configuration.webappSecondaryGroupGroupIDColumn)).thenReturn(groups);
		when(result.getString(configuration.webappSecondaryGroupUserIDColumn)).thenReturn(USER_ID, userID2);
		List<String> secondaryGroups = webGroupDao.getGroupUserIDs(group1);
		assertEquals(4, secondaryGroups.size());
		assertTrue(secondaryGroups.contains(primaryID1));
		assertTrue(secondaryGroups.contains(primaryID2));
		assertTrue(secondaryGroups.contains(USER_ID));
		assertTrue(secondaryGroups.contains(userID2));
	}
}
