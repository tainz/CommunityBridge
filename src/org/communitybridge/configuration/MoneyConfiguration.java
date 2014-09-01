package org.communitybridge.configuration;

import org.communitybridge.utility.StringUtilities;

public class MoneyConfiguration
{
	private boolean enabled;
	private boolean usesKey;
	private String	tableName;
	private String	userIdColumn;
	private String  columnOrKey;
	private String  keyColumn;
	private String  valueColumn;

	public String getConfigurationState()
	{
		if (enabled)
		{
			return usesKey ? getKeyedConfigurationString() : getKeylessConfigurationString();
		}
		return "";
	}

	private String getKeyedConfigurationString()
	{
		return StringUtilities.rot13(tableName + "-" + userIdColumn + "-" + columnOrKey + "-" + keyColumn + "-" + valueColumn);
	}

	private String getKeylessConfigurationString()
	{
		return StringUtilities.rot13(tableName + "-" + userIdColumn + "-" + columnOrKey);
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isUsesKey()
	{
		return usesKey;
	}

	public void setUsesKey(boolean usesKey)
	{
		this.usesKey = usesKey;
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public String getUserIdColumn()
	{
		return userIdColumn;
	}

	public void setUserIdColumn(String userIDColumn)
	{
		this.userIdColumn = userIDColumn;
	}

	public String getColumnOrKey()
	{
		return columnOrKey;
	}

	public void setColumnOrKey(String columnOrKey)
	{
		this.columnOrKey = columnOrKey;
	}

	public String getKeyColumn()
	{
		return keyColumn;
	}

	public void setKeyColumn(String keyColumn)
	{
		this.keyColumn = keyColumn;
	}

	public String getValueColumn()
	{
		return valueColumn;
	}

	public void setValueColumn(String valueColumn)
	{
		this.valueColumn = valueColumn;
	}
}
