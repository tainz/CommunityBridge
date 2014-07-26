package org.communitybridge.synchronization.group;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class WebGroupDaoTest
{
	private static final String EXCEPTION_MESSAGE = "test message";
	private static final String user_id1 = RandomStringUtils.randomNumeric(2);
	private static final String user_id2 = RandomStringUtils.randomNumeric(2);
	private static final String group1 = RandomStringUtils.randomNumeric(2);
	private static final String group2 = RandomStringUtils.randomNumeric(2);
	private WebGroupDao webGroupDao;
	private	Environment environment = new Environment();
	private Configuration configuration = mock(Configuration.class);
	private Log log = mock(Log.class);
	private SQL sql = mock(SQL.class);
	private ResultSet result = mock(ResultSet.class);

	@Before
	public void setup()
	{
		environment.setConfiguration(configuration);
		environment.setLog(log);
		environment.setSql(sql);
		DaoTestsHelper.setupConfiguration(configuration);
		webGroupDao = new TestableWebGroupDao(environment);
	}

	@Test
	public void getPrimaryGroupNeverReturnsNull() throws IllegalAccessException, SQLException, MalformedURLException, InstantiationException
	{
		configuration.webappPrimaryGroupEnabled = false;
		assertNotNull(webGroupDao.getPrimaryGroupID(""));
	}

	@Test
	public void getPrimaryGroupWithPrimaryDisabledReturnsBlank() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = false;
		assertEquals("", webGroupDao.getPrimaryGroupID(user_id1));
	}

	@Test
	public void getPrimaryGroupKeyedWithUnknownIDReturnsBlank() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(result);
		assertEquals("", webGroupDao.getPrimaryGroupID(user_id1));
	}

	@Test
	public void getPrimaryGroupKeylessWithUnknownIDReturnsBlank() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappPrimaryGroupUsesKey = false;
		when(sql.sqlQuery(anyString())).thenReturn(result);
		assertEquals("", webGroupDao.getPrimaryGroupID(user_id1));
	}

	@Test
	public void getPrimaryGroupKeyedWithValidIDReturnsGroup() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(Boolean.TRUE);
		when(result.getString(configuration.webappPrimaryGroupGroupIDColumn)).thenReturn(group1);
		assertEquals(group1, webGroupDao.getPrimaryGroupID(user_id1));
	}

	@Test
	public void getPrimaryGroupKeylessWithValidIDReturnsGroup() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		configuration.webappPrimaryGroupUsesKey = false;
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(Boolean.TRUE);
		when(result.getString(configuration.webappPrimaryGroupGroupIDColumn)).thenReturn(group1);
		assertEquals(group1, webGroupDao.getPrimaryGroupID(user_id1));
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
	public void convertDelimitedStringWithBlankStringReturnsEmpty()
	{
		assertEquals(0, webGroupDao.convertDelimitedIDString("").size());
	}

	@Test
	public void convertDelimitedStringWithWhitespaceStringReturnsEmpty()
	{
		assertEquals(0, webGroupDao.convertDelimitedIDString("  ").size());
	}

	@Test
	public void convertDelimitedStringWithNullReturnsEmpty()
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
	public void getPrimaryGroupUserIDsNeverReturnNull() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = false;
		assertNotNull(webGroupDao.getUserIDsFromPrimaryGroup(group1));
	}

	@Test
	public void getPrimaryGroupUserIDsWhenPrimaryDisabledReturnsEmptyList() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.webappPrimaryGroupEnabled = false;
		assertEquals(0, webGroupDao.getUserIDsFromPrimaryGroup(group1).size());
	}

	@Test
	public void getPrimaryGroupUserIDsWhenNoMembersReturnsEmptyList() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(false);
		assertEquals(0, webGroupDao.getUserIDsFromPrimaryGroup(group1).size());
	}

	@Test
	public void getPrimaryGroupUserIDsReturnsUserID() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(true, false);
		when(result.getString(configuration.webappPrimaryGroupUserIDColumn)).thenReturn(user_id1);
		List<String> groupMembers = webGroupDao.getUserIDsFromPrimaryGroup(group1);
		assertEquals(1, groupMembers.size());
		assertEquals(user_id1, groupMembers.get(0));
	}

	@Test
	public void getPrimaryGroupUserIDsWithMultipleUserIDsReturnUserIDs() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		String userID2 = RandomStringUtils.randomNumeric(2);
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(true, true, false);
		when(result.getString(configuration.webappPrimaryGroupUserIDColumn)).thenReturn(user_id1, userID2);

		List<String> groupMembers = webGroupDao.getUserIDsFromPrimaryGroup(group1);
		assertEquals(2, groupMembers.size());
		assertEquals(user_id1, groupMembers.get(0));
		assertEquals(userID2, groupMembers.get(1));
	}

	@Test
	public void getGroupUserIDsHandlesSQLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		SQLException exception = new SQLException(EXCEPTION_MESSAGE);
		testGroupUserIDsGroupsException(exception);
	}

	@Test
	public void getGroupUserIDsHandlesMalformedURLException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		MalformedURLException exception = new MalformedURLException(EXCEPTION_MESSAGE);
		testGroupUserIDsGroupsException(exception);
	}

	@Test
	public void getGroupUserIDsHandlesInstantiationException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		InstantiationException exception = new InstantiationException(EXCEPTION_MESSAGE);
		testGroupUserIDsGroupsException(exception);
	}

	@Test
	public void getGroupUserIDsHandlesIllegalAccessException() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		IllegalAccessException exception = new IllegalAccessException(EXCEPTION_MESSAGE);
		testGroupUserIDsGroupsException(exception);
	}

	private void testGroupUserIDsGroupsException(Exception exception) throws SQLException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		when(sql.sqlQuery(anyString())).thenThrow(exception);
		assertEquals(0, webGroupDao.getGroupUserIDs(group1).size());
		verify(log).severe(SingleWebGroupDao.EXCEPTION_MESSAGE_GET_USERIDS + exception.getMessage());
	}

	@Test
	public void getGroupUserIDs() throws MalformedURLException, InstantiationException, IllegalAccessException, SQLException
	{
		String primaryID1 = RandomStringUtils.randomNumeric(2);
		String primaryID2 = RandomStringUtils.randomNumeric(2);
		when(sql.sqlQuery(anyString())).thenReturn(result);
		when(result.next()).thenReturn(true, true, false);
		when(result.getString(configuration.webappPrimaryGroupUserIDColumn)).thenReturn(primaryID1, primaryID2);

		List<String> groups = webGroupDao.getGroupUserIDs(group1);
		assertEquals(4, groups.size());
		assertTrue(groups.contains(primaryID1));
		assertTrue(groups.contains(primaryID2));
		assertTrue(groups.contains(user_id1));
		assertTrue(groups.contains(user_id2));
	}

	public class TestableWebGroupDao extends WebGroupDao
	{
		public TestableWebGroupDao(Environment environment)
		{
			super(environment);
		}

		@Override
		public List<String> getSecondaryGroupIDs(String userID)
		{
			return null;
		}

		@Override
		public List<String> getSecondaryGroupUserIDs(String groupID)
		{
			return new ArrayList<String>(Arrays.asList(user_id1, user_id2));
		}

		@Override
		public void addUserToGroup(String userID, String groupID, int currentGroupCount) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
		{}

		@Override
		public void removeUserFromGroup(String userID, String groupName) throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
		{}
	}
}
