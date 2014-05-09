package org.communitybridge.groupsynchronizer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.communitybridge.main.Configuration;

public class DaoTestsHelper
{
	public static void setupConfiguration(Configuration configuration)
	{
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappPrimaryGroupTable = "primaryGroupTable";
		configuration.webappPrimaryGroupUserIDColumn = "primaryUserID";
		configuration.webappPrimaryGroupGroupIDColumn = "primaryGroupIDs";
		configuration.webappPrimaryGroupUsesKey = true;
		configuration.webappPrimaryGroupKeyName = "keyName";
		configuration.webappPrimaryGroupKeyColumn = "keyColumn";
		configuration.webappSecondaryGroupEnabled = true;
		configuration.webappSecondaryGroupUserIDColumn = "secondaryUserID";
		configuration.webappSecondaryGroupGroupIDColumn = "secondaryGroupIDs";
		configuration.webappSecondaryGroupGroupIDDelimiter = ",";
		configuration.webappSecondaryGroupTable = "secondaryGroupTable";
		configuration.webappSecondaryGroupKeyName = "secondaryGroupKeyName";
		configuration.webappSecondaryGroupKeyColumn = "secondaryGroupKeyColumn";
		configuration.webappSecondaryGroupStorageMethod = "single";
		configuration.webappSecondaryAdditionalColumns = new LinkedHashMap<String, Object>();
	}
}
