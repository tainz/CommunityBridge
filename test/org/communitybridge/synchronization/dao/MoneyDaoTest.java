package org.communitybridge.synchronization.dao;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.communitybridge.configuration.SynchronizationConfiguration;
import org.communitybridge.configuration.Configuration;
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
	private final SynchronizationConfiguration moneyConfiguration = new SynchronizationConfiguration();
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
		configuration.setMoney(moneyConfiguration);
		moneyConfiguration.setValueColumn(RandomStringUtils.randomAlphabetic(13));
		moneyConfiguration.setTableName(RandomStringUtils.randomAlphabetic(10));
		moneyConfiguration.setKeyColumn(RandomStringUtils.randomAlphabetic(9));
		moneyConfiguration.setUserIdColumn(RandomStringUtils.randomAlphabetic(8));
		moneyConfiguration.setColumnOrKey(RandomStringUtils.randomAlphabetic(7));

		environment.setConfiguration(configuration);
		environment.setSql(sql);
		KEYED_QUERY = "SELECT `" + moneyConfiguration.getValueColumn() + "` "
								 + "FROM `" + moneyConfiguration.getTableName() + "` "
								 + "WHERE `" + moneyConfiguration.getUserIdColumn() + "` = '" + USER_ID + "' "
								 + "AND " + moneyConfiguration.getKeyColumn() + "` = '" + moneyConfiguration.getColumnOrKey() + "'";
		KEYLESS_QUERY = "SELECT `" + moneyConfiguration.getColumnOrKey() + "` "
									+ "FROM `" + moneyConfiguration.getTableName() + "` "
									+ "WHERE `" + moneyConfiguration.getUserIdColumn() + "` = '" + USER_ID + "'";

		when(configuration.getMoney()).thenReturn(moneyConfiguration);
		when(sql.sqlQuery(KEYED_QUERY)).thenReturn(result);
		when(sql.sqlQuery(KEYLESS_QUERY)).thenReturn(result);
		when(result.next()).thenReturn(true);
		when(result.getDouble(moneyConfiguration.getValueColumn())).thenReturn(KEYED_BALANCE);
		when(result.getDouble(moneyConfiguration.getColumnOrKey())).thenReturn(KEYLESS_BALANCE);
	}

	@Test
	public void getBalanceNeverReturnsNull() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		Assert.assertNotNull(dao.getBalance(environment, USER_ID));
	}

	@Test
	public void getBalanceKeylessWhenEmptyResultSetReturnsZero() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		moneyConfiguration.setUsesKey(false);
		when(result.next()).thenReturn(false);
		Assert.assertEquals(new Double(0.0), dao.getBalance(environment, USER_ID), 0.0);
	}

	@Test
	public void getBalanceKeylessReturnsBalance() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		moneyConfiguration.setUsesKey(false);
		Assert.assertEquals(KEYLESS_BALANCE, dao.getBalance(environment, USER_ID), 0.0);
	}

	@Test
	public void getBalanceUsesKeyNeverReturnsNull() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		moneyConfiguration.setUsesKey(true);
		Assert.assertNotNull(dao.getBalance(environment, USER_ID));
	}

	@Test
	public void getBalanceKeyedWhenEmptyResultSetReturnsZero() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		moneyConfiguration.setUsesKey(true);
		when(result.next()).thenReturn(false);
		Assert.assertEquals(new Double(0.0), dao.getBalance(environment, USER_ID), 0.0);
	}

	@Test
	public void getBalanceUsesKeyReturnsBalance() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		moneyConfiguration.setUsesKey(true);
		Assert.assertEquals(KEYED_BALANCE, dao.getBalance(environment, USER_ID), 0.0);
	}

	@Test
	public void setBalanceKeylessSetsBalance() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		Double balance = RandomUtils.nextDouble();
		String query = "UPDATE `" + moneyConfiguration.getTableName() + "` "
						     + "SET `" + moneyConfiguration.getColumnOrKey() + "` = '" + balance.toString() + "' "
								 + "WHERE `" + moneyConfiguration.getUserIdColumn() + "` = '" + USER_ID + "'";
		moneyConfiguration.setUsesKey(false);
		dao.setBalance(environment, USER_ID, balance);
		Mockito.verify(sql).updateQuery(query);
	}

	@Test
	public void setBalanceKeyedSetsBalance() throws IllegalAccessException, InstantiationException, MalformedURLException, SQLException
	{
		Double balance = RandomUtils.nextDouble();
		String query = "UPDATE `" + moneyConfiguration.getTableName() + "` "
						     + "SET `" + moneyConfiguration.getValueColumn() + "` = '" + balance.toString() + "' "
								 + "WHERE `" + moneyConfiguration.getUserIdColumn() + "` = '" + USER_ID + "'"
								 + "AND " + moneyConfiguration.getKeyColumn() + "` = '" + moneyConfiguration.getColumnOrKey() + "'";
		moneyConfiguration.setUsesKey(true);
		dao.setBalance(environment, USER_ID, balance);
		Mockito.verify(sql).updateQuery(query);
	}
}