package org.ruhlendavis.mc.communitybridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold a player's group membership state
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class PlayerGroupState
{
	private String playerName;

	public PlayerGroupState(String playerNameIn)
	{
		playerName = playerNameIn;
	}

	public List identifyAdditions(PlayerGroupState newState)
	{
		return new ArrayList();
	}

	public List identifyRemovals(PlayerGroupState newState)
	{
		return new ArrayList();
	}

	public boolean generate()
	{
		return true;
	}

	public boolean load()
	{
		return true;
	}
}
