package org.communitybridge.configuration;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

public class MoneyConfigurationTest
{
	MoneyConfiguration moneyConfiguration = new MoneyConfiguration();

	@Test
	public void getConfigurationStringShouldNeverReturnNull() {
		Assert.assertNotNull(moneyConfiguration.getConfigurationString());
	}

	@Test
	public void getConfigurationStringWhenInactiveShouldReturnEmptyString() {
		Assert.assertEquals("", moneyConfiguration.getConfigurationString());
	}

	@Test
	public void getConfigurationStringWhenUsesKeyFalseShouldReturnConfiguration() {
		String tableName = RandomStringUtils.randomAlphabetic(13);
		String userIdColumn = RandomStringUtils.randomAlphabetic(6);
		String moneyColumn = RandomStringUtils.randomAlphabetic(7);
		String expected = tableName + "-" + userIdColumn + "-" + moneyColumn;
		moneyConfiguration.setEnabled(true);
		moneyConfiguration.setUsesKey(false);
		moneyConfiguration.setTableName(tableName);
		moneyConfiguration.setUserIdColumn(userIdColumn);
		moneyConfiguration.setColumnOrKey(moneyColumn);

		Assert.assertEquals(expected, moneyConfiguration.getConfigurationString());
	}

	@Test
	public void getConfigurationStringWhenUsesKeyTrueShouldReturnConfiguration() {
		String tableName = RandomStringUtils.randomAlphabetic(13);
		String userIdColumn = RandomStringUtils.randomAlphabetic(6);
		String key = RandomStringUtils.randomAlphabetic(7);
		String keyColumn = RandomStringUtils.randomAlphabetic(8);
		String valueColumn = RandomStringUtils.randomAlphabetic(9);

		String expected = tableName + "-" + userIdColumn + "-" + key + "-" + keyColumn + "-" + valueColumn;
		moneyConfiguration.setEnabled(true);
		moneyConfiguration.setUsesKey(true);
		moneyConfiguration.setTableName(tableName);
		moneyConfiguration.setUserIdColumn(userIdColumn);
		moneyConfiguration.setColumnOrKey(key);
		moneyConfiguration.setKeyColumn(keyColumn);
		moneyConfiguration.setValueColumn(valueColumn);

		Assert.assertEquals(expected, moneyConfiguration.getConfigurationString());
	}
}
