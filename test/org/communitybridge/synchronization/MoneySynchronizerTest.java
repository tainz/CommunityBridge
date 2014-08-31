package org.communitybridge.synchronization;

import java.net.MalformedURLException;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.entity.Player;
import org.communitybridge.configuration.MoneyConfiguration;
import org.communitybridge.configuration.Configuration;
import org.communitybridge.main.Environment;
import org.communitybridge.main.WebApplication;
import org.communitybridge.synchronization.dao.MoneyDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MoneySynchronizerTest
{
	private String USER_ID = RandomStringUtils.randomAlphabetic(13);
	private static final double DOUBLE_DELTA = 0.001;
	private Configuration configuration = Mockito.mock(Configuration.class);
	private Economy economy = Mockito.mock(Economy.class);
	private WebApplication webApplication = Mockito.mock(WebApplication.class);

	private Environment environment = new Environment();

	private Player player = Mockito.mock(Player.class);
	private PlayerState previous = Mockito.mock(PlayerState.class);
	private PlayerState current = Mockito.mock(PlayerState.class);
	private PlayerState result = Mockito.mock(PlayerState.class);
	private MoneyConfiguration moneyConfiguration = new MoneyConfiguration();
	private MoneyDao moneyDao = Mockito.mock(MoneyDao.class);

	@InjectMocks
	private MoneySynchronizer synchronizer = new MoneySynchronizer(environment);

	@Before
	public void beforeEach()
	{
		environment.setConfiguration(configuration);
		environment.setEconomy(economy);
		environment.setWebApplication(webApplication);
		Mockito.when(configuration.getMoney()).thenReturn(moneyConfiguration);
	}

	@Test
	public void isActiveReturnsTrue()
	{
		moneyConfiguration.setEnabled(true);
		Assert.assertTrue(synchronizer.isActive(environment));
	}

	@Test
	public void isActiveReturnsFalse()
	{
		moneyConfiguration.setEnabled(false);
		Assert.assertFalse(synchronizer.isActive(environment));
	}

	@Test
	public void synchronizeShouldNeverReturnNull()
	{
		PlayerState newState = synchronizer.synchronize(environment, player, USER_ID, previous, current, result);
		Assert.assertNotNull(newState);
	}

	@Test
	public void synchronizeWithNoChangeShouldReturnSameState()
	{
		PlayerState newState = synchronizer.synchronize(environment, player, USER_ID, previous, current, result);
		Assert.assertEquals(result, newState);
	}

	@Test
	public void synchronizeWithPositiveWebChangeShouldDeposit()
	{
		double webPrevious = RandomUtils.nextDouble();
		double webCurrent = webPrevious * 2;
		double expected = webCurrent - webPrevious;

		Mockito.when(previous.getWebApplicationWallet()).thenReturn(webPrevious);
		Mockito.when(current.getWebApplicationWallet()).thenReturn(webCurrent);

		synchronizer.synchronize(environment, player, USER_ID, previous, current, result);

		Mockito.verify(economy).depositPlayer(player, expected);
	}

	@Test
	public void synchronizeWithNegativeWebChangeShouldWithdraw()
	{
		double webCurrent = RandomUtils.nextDouble();
		double webPrevious = webCurrent * 2;
		double expected = Math.abs(webCurrent - webPrevious);

		Mockito.when(previous.getWebApplicationWallet()).thenReturn(webPrevious);
		Mockito.when(current.getWebApplicationWallet()).thenReturn(webCurrent);

		synchronizer.synchronize(environment, player, USER_ID, previous, current, result);

		Mockito.verify(economy).withdrawPlayer(player, expected);
	}

	@Test
	public void synchronizeWithPositiveWebChangeShouldUpdateResult()
	{
		double mcCurrent = RandomUtils.nextDouble();
		double webPrevious = RandomUtils.nextDouble();
		double webCurrent = webPrevious * 2;
		double expected = mcCurrent + webCurrent - webPrevious;

		Mockito.when(previous.getWebApplicationWallet()).thenReturn(webPrevious);
		Mockito.when(current.getWebApplicationWallet()).thenReturn(webCurrent);
		Mockito.when(current.getMinecraftWallet()).thenReturn(mcCurrent);

		synchronizer.synchronize(environment, player, USER_ID, previous, current, result);

		Mockito.verify(result).setMinecraftWallet(AdditionalMatchers.eq(expected, DOUBLE_DELTA));
	}

	@Test
	public void synchronizeWithNegativeWebChangeShouldUpdateResult()
	{
		double mcCurrent = RandomUtils.nextDouble();
		double webCurrent = RandomUtils.nextDouble();
		double webPrevious = webCurrent * 2;
		double expected = mcCurrent + webCurrent - webPrevious;

		Mockito.when(previous.getWebApplicationWallet()).thenReturn(webPrevious);
		Mockito.when(current.getWebApplicationWallet()).thenReturn(webCurrent);
		Mockito.when(current.getMinecraftWallet()).thenReturn(mcCurrent);

		synchronizer.synchronize(environment, player, USER_ID, previous, current, result);

		Mockito.verify(result).setMinecraftWallet(AdditionalMatchers.eq(expected, DOUBLE_DELTA));
	}

	@Test
	public void synchronizeWithNoChangeShouldNotChangePlayersBalance()
	{
		double webCurrent = RandomUtils.nextDouble();

		Mockito.when(previous.getWebApplicationWallet()).thenReturn(webCurrent);
		Mockito.when(current.getWebApplicationWallet()).thenReturn(webCurrent);

		synchronizer.synchronize(environment, player, USER_ID, previous, current, result);

		Mockito.verify(economy, Mockito.never()).depositPlayer(Mockito.eq(player), Mockito.anyDouble());
		Mockito.verify(economy, Mockito.never()).withdrawPlayer(Mockito.eq(player), Mockito.anyDouble());
	}

	@Test
	public void synchronizeWithPositiveGameChangeShouldUpdateWeb() throws IllegalAccessException, InstantiationException, MalformedURLException
	{
		double mcPrevious = RandomUtils.nextDouble();
		double mcCurrent = mcPrevious * 2;
		double webCurrent = RandomUtils.nextDouble();
		double expected = webCurrent + mcCurrent - mcPrevious;

		Mockito.when(previous.getMinecraftWallet()).thenReturn(mcPrevious);
		Mockito.when(current.getMinecraftWallet()).thenReturn(mcCurrent);
		Mockito.when(current.getWebApplicationWallet()).thenReturn(webCurrent);

		synchronizer.synchronize(environment, player, USER_ID, previous, current, result);

		Mockito.verify(moneyDao).setBalance(Mockito.eq(environment), Mockito.eq(USER_ID),
																							AdditionalMatchers.eq(expected, DOUBLE_DELTA));
	}

	@Test
	public void synchronizeWithNegativeGameChangeShouldUpdateWeb() throws IllegalAccessException, InstantiationException, MalformedURLException
	{
		double mcCurrent = RandomUtils.nextDouble();
		double mcPrevious = mcCurrent * 2;
		double webCurrent = RandomUtils.nextDouble();
		double expected = webCurrent + mcCurrent - mcPrevious;

		Mockito.when(previous.getMinecraftWallet()).thenReturn(mcPrevious);
		Mockito.when(current.getMinecraftWallet()).thenReturn(mcCurrent);
		Mockito.when(current.getWebApplicationWallet()).thenReturn(webCurrent);

		synchronizer.synchronize(environment, player, USER_ID, previous, current, result);

		Mockito.verify(moneyDao).setBalance(Mockito.eq(environment), Mockito.eq(USER_ID),
																							AdditionalMatchers.eq(expected, DOUBLE_DELTA));
	}

	@Test
	public void synchronizeWithGameChangeShouldUpdateResult()
	{
		double mcPrevious = RandomUtils.nextDouble();
		double mcCurrent = mcPrevious * 2;
		double webCurrent = RandomUtils.nextDouble();
		double expected = webCurrent + mcCurrent - mcPrevious;

		Mockito.when(previous.getMinecraftWallet()).thenReturn(mcPrevious);
		Mockito.when(current.getMinecraftWallet()).thenReturn(mcCurrent);
		Mockito.when(current.getWebApplicationWallet()).thenReturn(webCurrent);

		synchronizer.synchronize(environment, player, USER_ID, previous, current, result);

		Mockito.verify(result).setWebApplicationWallet(AdditionalMatchers.eq(expected, DOUBLE_DELTA));
	}

	@Test
	public void synchronizeWithNoGameChangeShouldNotUpdateWeb()
	{
		double mcCurrent = RandomUtils.nextDouble();
		double webCurrent = RandomUtils.nextDouble();

		Mockito.when(previous.getMinecraftWallet()).thenReturn(mcCurrent);
		Mockito.when(current.getMinecraftWallet()).thenReturn(mcCurrent);
		Mockito.when(current.getWebApplicationWallet()).thenReturn(webCurrent);

		synchronizer.synchronize(environment, player, USER_ID, previous, current, result);

		Mockito.verify(webApplication, Mockito.never()).setBalance(Mockito.eq(USER_ID),	Mockito.anyDouble());
	}
}