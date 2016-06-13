/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.communitybridge.achievement;

import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class AchievementSectionPostCount extends AchievementPostCount
{
	private String sectionID;

	public AchievementSectionPostCount(Environment environment)
	{
		super(environment);
	}
	
	@Override
	public boolean playerQualifies(Player player, PlayerAchievementState state)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public String getSectionID()
	{
		return sectionID;
	}

	public void setSectionID(String sectionName)
	{
		this.sectionID = sectionName;
	}

}
