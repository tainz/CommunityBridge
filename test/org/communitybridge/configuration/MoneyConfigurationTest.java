package org.communitybridge.configuration;

import org.apache.commons.lang3.RandomStringUtils;
import org.communitybridge.utility.StringUtilities;
import org.junit.Assert;
import org.junit.Test;

public class MoneyConfigurationTest
{
	SynchronizationConfiguration moneyConfiguration = new SynchronizationConfiguration();

	@Test
	public void getConfigurationStringShouldNeverReturnNull() {
		Assert.assertNotNull(moneyConfiguration.getConfigurationState());
	}

	@Test
	public void getConfigurationStringWhenInactiveShouldReturnEmptyString() {
		Assert.assertEquals("", moneyConfiguration.getConfigurationState());
	}

	@Test
	public void getConfigurationStringWhenUsesKeyFalseShouldReturnConfiguration() {
		String tableName = RandomStringUtils.randomAlphabetic(13);
		String userIdColumn = RandomStringUtils.randomAlphabetic(6);
		String moneyColumn = RandomStringUtils.randomAlphabetic(7);
		String expected = StringUtilities.rot13(tableName + "-" + userIdColumn + "-" + moneyColumn);
		moneyConfiguration.setEnabled(true);
		moneyConfiguration.setUsesKey(false);
		moneyConfiguration.setTableName(tableName);
		moneyConfiguration.setUserIdColumn(userIdColumn);
		moneyConfiguration.setColumnOrKey(moneyColumn);

		Assert.assertEquals(expected, moneyConfiguration.getConfigurationState());
	}

	@Test
	public void getConfigurationStringWhenUsesKeyTrueShouldReturnConfiguration() {
		String tableName = RandomStringUtils.randomAlphabetic(13);
		String userIdColumn = RandomStringUtils.randomAlphabetic(6);
		String key = RandomStringUtils.randomAlphabetic(7);
		String keyColumn = RandomStringUtils.randomAlphabetic(8);
		String valueColumn = RandomStringUtils.randomAlphabetic(9);

		String expected = StringUtilities.rot13(tableName + "-" + userIdColumn + "-" + key + "-" + keyColumn + "-" + valueColumn);
		moneyConfiguration.setEnabled(true);
		moneyConfiguration.setUsesKey(true);
		moneyConfiguration.setTableName(tableName);
		moneyConfiguration.setUserIdColumn(userIdColumn);
		moneyConfiguration.setColumnOrKey(key);
		moneyConfiguration.setKeyColumn(keyColumn);
		moneyConfiguration.setValueColumn(valueColumn);

		Assert.assertEquals(expected, moneyConfiguration.getConfigurationState());
	}
}
