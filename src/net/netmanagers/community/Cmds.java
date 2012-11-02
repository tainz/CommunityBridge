package net.netmanagers.community;

import java.net.MalformedURLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmds implements CommandExecutor
{
	/**
	 * Handles all 'commands' entered via console or chat interface.
	 * 
	 * @param sender      CommandSender object, either the player or console
	 * @param command     Command object for the selected command
	 * @param label       String, the actual alias that was typed
	 * @param arguments   String array of command arguments
	 * @return false if the fail message needs to be shown.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
	                         String[] arguments)
	{
		// This models how I think each command should be handled. Note that if
		// you're looking for permissions, that is handled entirely by the
		// Bukkit via settings in plugin.yml
		if (label.equalsIgnoreCase("cbrank"))
		{
			if (arguments.length == 2)
			{
				rankPlayer(sender, arguments[0], arguments[1]);
			}
			else
			{
				sendOrLog(sender,
								  "Insufficient arguments. Usage: /cbrank <playername> <group>",
									ChatColor.RED, false);
			}
			
			return true;
		}
		
		Player p = null;

		if (sender instanceof Player) {
			p = (Player) sender;

			if (label.equalsIgnoreCase("cbban")) {
				if (p.hasPermission("communitybridge.cbban")) {
					if (arguments.length == 1) {
            banPlayer(p, arguments[0]);
					} else {
						p.sendMessage(ChatColor.RED + "Incorrect usage: /cbban <username>");
					}
				} else {
					p.sendMessage(ChatColor.RED + "You do not have access to that command.");
				}
			} else if (label.equalsIgnoreCase("cbunban")) {
				if (p.hasPermission("communitybridge.cbunban")) {
					if (arguments.length == 1) {
            unbanPlayer(p, arguments[0]);
					} else {
						p.sendMessage(ChatColor.RED + "Incorrect usage: /cbunban <username>");
					}
				} else {
					p.sendMessage(ChatColor.RED + "You do not have access to that command.");
				}
			} else if (label.equalsIgnoreCase("cbsyncall")) {
				if (p.hasPermission("communitybridge.cbsyncall")) {
					if (arguments.length == 0) {
						Main.syncAll();
						p.sendMessage(ChatColor.GREEN + "Everyone has been synced");
					} else {
						p.sendMessage(ChatColor.RED + "Incorrect usage: /cbsyncall");
					}
				} else {
					p.sendMessage(ChatColor.RED + "You do not have access to that command.");
				}
			} else if (label.equalsIgnoreCase("cbsync")) {
				if (arguments.length == 0) {
					Main.SyncPlayer(p, false);					
				} else {
					p.sendMessage(ChatColor.RED + "Incorrect usage: /cbsync");
				}
			}
		} else {
			if (label.equalsIgnoreCase("cbban")) {
				if (arguments.length == 1) {
					try {
						if (Bukkit.getPlayer(arguments[0]) != null) {
							Player pl = Bukkit.getPlayer(arguments[0]);
							Main.sql.updateQuery("UPDATE "+Main.users_table+" SET "+Main.is_banned_field+"='1' WHERE "+Main.user_id_field+"='" + Main.getUserId(pl.getName()) + "'");
							pl.kickPlayer("You have been banned from the site.");
							Main.log.info("Banning " + pl.getName() + " from the site");
						} else {
							OfflinePlayer pl = Bukkit.getOfflinePlayer(arguments[0]);
							Main.sql.updateQuery("UPDATE "+Main.users_table+" SET "+Main.is_banned_field+"='1' WHERE "+Main.user_id_field+"='" + Main.getUserId(pl.getName()) + "'");
							Main.log.info("Banning " + pl.getName() + " from the site");
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					Main.log.info("Incorrect usage: /cbban <username>");
				}
			}
			if (label.equalsIgnoreCase("cbunban")) {
				if (arguments.length == 1) {
					try {
						OfflinePlayer pl = Bukkit.getOfflinePlayer(arguments[0]);
						Main.sql.updateQuery("UPDATE "+Main.users_table+" SET "+Main.is_banned_field+"='0' WHERE "+Main.user_id_field+"='" + Main.getUserId(pl.getName()) + "'");						
						Main.log.info("Unbanning " + pl.getName() + " from the site");
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					Main.log.info("Incorrect usage: /cbunban <username>");
				}
			}
			if (label.equalsIgnoreCase("cbsyncall")) {
				if (arguments.length == 0) {
					Main.syncAll();
					Main.log.info("Everyone has been synced");
				} else {
					Main.log.info("Incorrect usage: /cbsyncall");
				}
			}
		}
		return true;
	}

  private void banPlayer(Player staff, String victimString)
  {
    String query;
    try
    {
      if (Bukkit.getPlayer(victimString) == null)
      {
        OfflinePlayer victim = Bukkit.getOfflinePlayer(victimString);
        query = "UPDATE " + Main.users_table +
                " SET " + Main.is_banned_field + "='1'" +
                " WHERE " + Main.user_id_field + "='" +
                Main.getUserId(victim.getName()) + "'";
        Main.log.finer(query);
        Main.sql.updateQuery(query);
				sendAndLog(staff, "banned " + victim.getName() + " from the site.",
								  ChatColor.RED);
      }
      else
      {
        Player victim = Bukkit.getPlayer(victimString);
        query = "UPDATE " + Main.users_table +
                " SET " + Main.is_banned_field +"='1'" +
                " WHERE " + Main.user_id_field + "='" +
                Main.getUserId(victim.getName()) + "'";
        Main.log.finer(query);
        Main.sql.updateQuery(query);
        victim.kickPlayer("You have been banned from the site.");
				sendAndLog(staff, "banned " + victim.getName() + " from the site",
								   ChatColor.RED);
      }
    }
		catch (MalformedURLException e)
		{
      Main.log.severe("Error in banPlayer(): " + e.getMessage());
    }
		catch (InstantiationException e)
		{
      Main.log.severe("Error in banPlayer(): " + e.getMessage());
    }
		catch (IllegalAccessException e)
		{
      Main.log.severe("Error in banPlayer(): " + e.getMessage());
    }
  }

  private void unbanPlayer(Player staff, String victimString)
  {
    try
    {
      OfflinePlayer victim = Bukkit.getOfflinePlayer(victimString);
			String query = "UPDATE " + Main.users_table
							     + " SET " + Main.is_banned_field + "='0'"
							     + " WHERE " + Main.user_id_field + "='"
							     + Main.getUserId(victim.getName()) + "'";
			Main.log.finer(query);
			Main.sql.updateQuery(query);
							sendAndLog(staff, "unbanned " + victim.getName() + " from the site",
							   ChatColor.RED);
    }
		catch (MalformedURLException e)
		{
      Main.log.severe("Error in unbanPlayer(): " + e.getMessage());
    }
		catch (InstantiationException e)
		{
      Main.log.severe("Error in unbanPlayer(): " + e.getMessage());
    }
		catch (IllegalAccessException e)
		{
      Main.log.severe("Error in unbanPlayer(): " + e.getMessage());
    }
  }
	
	/**
	 * Change a player's 'rank', that is, group.
	 * 
	 * @param sender         CommandSender object of the command initiator
	 * @param playerArgument String containing the target player's name
	 * @param groupArgument  String containing the destination group
	 */
	private void rankPlayer(CommandSender sender, String playerArgument,
					                String groupArgument)
	{
		String groupName = Main.getGroupNameFull(groupArgument);
		if (groupName == null)
		{
			sendOrLog(sender, "Could not find the group mapping for '" + groupArgument + "'.",
								ChatColor.RED, false);
			return;
		}
		
		String groupID = Main.getGroupID(groupName);

		String playerName;
		Player player = Bukkit.getPlayerExact(playerArgument);

		if (player == null)
		{
			playerName = Bukkit.getOfflinePlayer(playerArgument).getName();
		}
		else
		{
			playerName = player.getName();
		}

		if (Main.permissionHandler.isMemberOfGroup(playerName, groupName))
		{
			sendOrLog(sender, "'" + playerName + "' is already member of group '"
							        + groupArgument + "'.",
								ChatColor.RED, false);
			return;
		}

		if (player == null)
		{}
		else
		{
			if (Main.setGroup(groupName, player, false))
			{
				player.sendMessage(ChatColor.GREEN + "You have been set to a "
												 + groupName + ".");
				sendAndLog(sender, "Set " + player.getName() + " to rank "
													 + groupName + ".",
									 ChatColor.GREEN, true);
			}
			else
			{
				sendOrLog(sender, "Permissions set group for '" + player.getName()
												+ "' failed in rankPlayer().",
									ChatColor.RED, false);
			}
		}

		try
		{
			String query = "UPDATE " + Main.users_table
									 + " SET " + Main.groups_id_field + "='" + groupID
									 + "' WHERE " + Main.user_id_field + "='"
									 + Main.getUserId(playerName) + "'";
			Main.log.finer(query);
			Main.sql.updateQuery(query);
			sendAndLog(sender, "Changed group for " + playerName
											 + " to '" + groupArgument
											 + "' in the application database.",
								 ChatColor.GREEN, true);
		}
		catch (MalformedURLException e)
		{
			Main.log.severe("Error in rankPlayer(): " + e.getMessage());
		} 
		catch (InstantiationException e)
		{
			Main.log.severe("Error in rankPlayer(): " + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			Main.log.severe("Error in rankPlayer(): " + e.getMessage());
		}
	}

	/** 
  * Send response message to player (if possible) AND to the console/log.
  * 
	* @param staff Target Player object or null
	* @param message Message to send
	* @param color ChatColor for message text, not used for console/log
  */
	private void sendAndLog(Player staff, String message, ChatColor color)
  {
		String name;
		if (staff == null)
		{
			name = "Console";
		}
		else
		{
			staff.sendMessage(color + message);
			name = staff.getName();
		}
		
		Main.log.info(name + " " + message);
  }
	
	/** 
  * Send response message to player (if possible) or to the console/log.
  * 
	* @param staff Target Player object or null
	* @param message Message to send
	* @param color ChatColor for message text, not used for console/log
  */
  private void sendOrLog(Player staff, String message, ChatColor color)
  {
    if (staff == null)
    {
      Main.log.info("Console " + message);
		}
    else
    {
      staff.sendMessage(color + message);
		}
	}

/** 
  * Send response message to player (if possible) AND to the console/log.
  * 
	* @param sender  CommandSender object for recipient
	* @param message String containing the message to send
	* @param color   ChatColor for message text, not used for console/log
  */
	private void sendAndLog(CommandSender sender, String message, ChatColor color,
					                boolean who)
  {
		if (sender instanceof Player)
		{
			sender.sendMessage(color + message);
		}
		
		if (who)
		{
			message = "(" + sender.getName() + ") " + message;
		}

		Main.log.info(message);
  }
	
	/** 
  * Send response message to player (if possible) or to the console/log.
  * 
	* @param sender  CommandSender object representing the recipient
	* @param message String containing the message to send
	* @param color   ChatColor for message text, not used for console/log
	* @param who     True to include sender's name  in the console/log output
  */
	private void sendOrLog(CommandSender sender, String message, ChatColor color,
					               boolean who)
  {
    if (sender instanceof Player)
    {
      sender.sendMessage(color + message);
		}
		else
		{
			if (who)
			{
				message = "(" + sender.getName() + ") " + message;
			}
      Main.log.info(message);
		}
	}
}