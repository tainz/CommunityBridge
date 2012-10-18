package net.netmanagers.community;

import java.net.MalformedURLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] split) {
		Player p = null;

		if (sender instanceof Player) {
			p = (Player) sender;

			if (label.equalsIgnoreCase("cbban")) {
				if (p.hasPermission("communitybridge.cbban")) {
					if (split.length == 1) {
            banPlayer(p, split[0]);
					} else {
						p.sendMessage(ChatColor.RED + "Incorrect usage: /cbban <username>");
					}
				} else {
					p.sendMessage(ChatColor.RED + "You do not have access to that command.");
				}
			} else if (label.equalsIgnoreCase("cbunban")) {
				if (p.hasPermission("communitybridge.cbunban")) {
					if (split.length == 1) {
            unbanPlayer(p, split[0]);
					} else {
						p.sendMessage(ChatColor.RED + "Incorrect usage: /cbunban <username>");
					}
				} else {
					p.sendMessage(ChatColor.RED + "You do not have access to that command.");
				}
			} else if (label.equalsIgnoreCase("cbrank")) {
				if (p.hasPermission("communitybridge.cbrank")) {
					if (split.length == 2) {
						try {
							if (Main.getGroup(split[1]) != 0) {
								Player pl = Bukkit.getPlayer(split[0]);
								
								Main.sql.updateQuery("UPDATE "+Main.users_table+" SET "+Main.groups_id_field+"='"+ Main.getGroup(split[1]) +"' WHERE "+Main.user_id_field+"='" + Main.getUserId(pl.getName()) + "'");
								if (Main.setGroup(Main.getGroup(split[1]), pl, true)) {
									pl.sendMessage(ChatColor.GREEN + "You have been set to a " + Main.config.getString("groups." + Main.getGroup(split[1])));
									p.sendMessage(ChatColor.GREEN + "Set " + pl.getName() + " to rank " + Main.groups[Main.getGroup(split[1]) - 1]);
								}
							} else {
								p.sendMessage(ChatColor.RED + "Could not find the group " + split[1]);
							}
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					} else {
						p.sendMessage(ChatColor.RED + "Incorrect usage: /cbrank <username> <group>");
					}
				} else {
					p.sendMessage(ChatColor.RED + "You do not have access to that command.");
				}
			} else if (label.equalsIgnoreCase("cbsyncall")) {
				if (p.hasPermission("communitybridge.cbsyncall")) {
					if (split.length == 0) {
						Main.syncAll();
						p.sendMessage(ChatColor.GREEN + "Everyone has been synced");
					} else {
						p.sendMessage(ChatColor.RED + "Incorrect usage: /cbsyncall");
					}
				} else {
					p.sendMessage(ChatColor.RED + "You do not have access to that command.");
				}
			} else if (label.equalsIgnoreCase("cbsync")) {
				if (split.length == 0) {
					Main.SyncPlayer(p, false);					
				} else {
					p.sendMessage(ChatColor.RED + "Incorrect usage: /cbsync");
				}
			}
		} else {
			if (label.equalsIgnoreCase("cbban")) {
				if (split.length == 1) {
					try {
						if (Bukkit.getPlayer(split[0]) != null) {
							Player pl = Bukkit.getPlayer(split[0]);
							Main.sql.updateQuery("UPDATE "+Main.users_table+" SET "+Main.is_banned_field+"='1' WHERE "+Main.user_id_field+"='" + Main.getUserId(pl.getName()) + "'");
							pl.kickPlayer("You have been banned from the site.");
							Main.log.info("Banning " + pl.getName() + " from the site");
						} else {
							OfflinePlayer pl = Bukkit.getOfflinePlayer(split[0]);
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
				if (split.length == 1) {
					try {
						OfflinePlayer pl = Bukkit.getOfflinePlayer(split[0]);
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
				if (split.length == 0) {
					Main.syncAll();
					Main.log.info("Everyone has been synced");
				} else {
					Main.log.info("Incorrect usage: /cbsyncall");
				}
			}
			if (label.equalsIgnoreCase("cbrank")) {
				if (split.length == 2) {
					try {
						if (Main.getGroup(split[1]) != 0) {
							Player pl = Bukkit.getPlayer(split[0]);
							Main.sql.updateQuery("UPDATE "+Main.users_table+" SET "+Main.groups_id_field+"='"+ Main.getGroup(split[1]) +"' WHERE "+Main.user_id_field+"='" + Main.getUserId(pl.getName()) + "'");
							if (Main.setGroup(Main.getGroup(split[1]), pl, true)) {
								pl.sendMessage(ChatColor.GREEN + "You have been set to a " + Main.config.getString("groups." + Main.getGroup(split[1])));
							}
						} else {
							Main.log.info("Could not find the group " + split[1]);
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					Main.log.info("Incorrect usage: /cbrank <username> <group>");
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
        Main.log.info(query);
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
        Main.log.info(query);
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
      Main.sql.updateQuery("UPDATE " + Main.users_table
							           + " SET " + Main.is_banned_field + "='0'"
							           + " WHERE " + Main.user_id_field + "='"
							           + Main.getUserId(victim.getName()) + "'");
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
}