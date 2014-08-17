package org.communitybridge.synchronization.dao;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.communitybridge.main.SQL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoneyDaoTest
{
	MoneyDao dao = new MoneyDao();
	private final Environment environment = new Environment();
	private final Configuration configuration = mock(Configuration.class);
	private final SQL sql = mock(SQL.class);
	private final ResultSet result = mock(ResultSet.class);
	private String KEYED_QUERY;
	private String KEYLESS_QUERY;
	private final String USER_ID = RandomStringUtils.randomAlphabetic(2);
	private	final double KEYLESS_BALANCE = RandomUtils.nextDouble();
	private final double KEYED_BALANCE = RandomUtils.nextDouble();

	@Before
	public void beforeEach() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.walletValueColumn = RandomStringUtils.randomAlphabetic(13);
		configuration.walletTableName = RandomStringUtils.randomAlphabetic(10);
		configuration.walletKeyColumn =  RandomStringUtils.randomAlphabetic(9);
		configuration.walletUserIDColumn =  RandomStringUtils.randomAlphabetic(8);
		configuration.walletColumnOrKey = RandomStringUtils.randomAlphabetic(7);

		environment.setConfiguration(configuration);
		environment.setSql(sql);
		KEYED_QUERY = "SELECT `" + configuration.walletValueColumn + "` "
								 + "FROM `" + configuration.walletTableName + "` "
								 + "WHERE `" + configuration.walletUserIDColumn + "` = '" + USER_ID + "' "
								 + "AND " + configuration.walletKeyColumn + "` = '" + configuration.walletColumnOrKey + "'";
		KEYLESS_QUERY = "SELECT `" + configuration.walletColumnOrKey + "` "
									+ "FROM `" + configuration.walletTableName + "` "
									+ "WHERE `" + configuration.walletUserIDColumn + "` = '" + USER_ID + "'";

		when(sql.sqlQuery(KEYED_QUERY)).thenReturn(result);
		when(sql.sqlQuery(KEYLESS_QUERY)).thenReturn(result);
		when(result.next()).thenReturn(true);
		when(result.getDouble(configuration.walletValueColumn)).thenReturn(KEYED_BALANCE);
		when(result.getDouble(configuration.walletColumnOrKey)).thenReturn(KEYLESS_BALANCE);
	}

	@Test
	public void getBalanceNeverReturnsNull() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		Assert.assertNotNull(dao.getBalance(environment, USER_ID));
	}

	@Test
	public void getBalanceKeylessWhenEmptyResultSetReturnsZero() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.walletUsesKey = false;
		when(result.next()).thenReturn(false);
		Assert.assertEquals(new Double(0.0), dao.getBalance(environment, USER_ID), 0.0);
	}

	@Test
	public void getBalanceKeylessReturnsBalance() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.walletUsesKey = false;
		Assert.assertEquals(KEYLESS_BALANCE, dao.getBalance(environment, USER_ID), 0.0);
	}

	@Test
	public void getBalanceUsesKeyNeverReturnsNull() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.walletUsesKey = true;
		Assert.assertNotNull(dao.getBalance(environment, USER_ID));
	}

	@Test
	public void getBalanceKeyedWhenEmptyResultSetReturnsZero() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.walletUsesKey = true;
		when(result.next()).thenReturn(false);
		Assert.assertEquals(new Double(0.0), dao.getBalance(environment, USER_ID), 0.0);
	}

	@Test
	public void getBalanceUsesKeyReturnsBalance() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		configuration.walletUsesKey = true;
		Assert.assertEquals(KEYED_BALANCE, dao.getBalance(environment, USER_ID), 0.0);
	}

	@Test
	public void setBalanceKeylessSetsBalance() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		Double balance = RandomUtils.nextDouble();
		String query = "UPDATE `" + configuration.walletTableName + "` "
						     + "SET `" + configuration.walletColumnOrKey + "` = '" + balance.toString() + "' "
								 + "WHERE `" + configuration.walletUserIDColumn + "` = '" + USER_ID + "'";
		configuration.walletUsesKey = false;
		dao.setBalance(environment, USER_ID, balance);
		Mockito.verify(sql).updateQuery(query);
	}

	@Test
	public void setBalanceKeyedSetsBalance() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		Double balance = RandomUtils.nextDouble();
		String query = "UPDATE `" + configuration.walletTableName + "` "
						     + "SET `" + configuration.walletValueColumn + "` = '" + balance.toString() + "' "
								 + "WHERE `" + configuration.walletUserIDColumn + "` = '" + USER_ID + "'"
								 + "AND " + configuration.walletKeyColumn + "` = '" + configuration.walletColumnOrKey + "'";
		configuration.walletUsesKey = true;
		dao.setBalance(environment, USER_ID, balance);
		Mockito.verify(sql).updateQuery(query);
	}
}