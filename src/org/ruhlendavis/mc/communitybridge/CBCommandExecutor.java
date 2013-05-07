package org.ruhlendavis.mc.communitybridge;

import java.net.MalformedURLException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ruhlendavis.mc.utility.Log;

/**
 * Class which handles command events.
 *
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public class CBCommandExecutor implements CommandExecutor
{
	private Configuration config;
	private Log log;

	/**
	 * Constructor
	 * @param Configuration The configuration object, passed in by onEnable().
	 * @param Log The log object, passed in by onEnable().
	 */
	public CBCommandExecutor(Configuration config, Log log)
	{
		this.config = config;
		this.log = log;
	}

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
		label = label.toLowerCase();
		if (label.equals("cbreload"))
		{
			if (arguments.length > 1)
			{
				sendOrLog(sender, "Too many arguments. Usage: /cbreload [filename]", ChatColor.RED, false);
			}

			sendAndLog(sender, "Reloading CommunityBridge configuration.", ChatColor.GREEN, true);

			String error;
			String filename;
			if (arguments.length == 1)
			{
				// reload using the specified file.
				error = config.reload(arguments[0]);
				filename = arguments[0];
			}
			else
			{
				// reload using the config.yml file.
				error = config.reload(null);
				filename = "config.yml";
			}

			if (error == null)
			{
				sendOrLog(sender, "Reload succeeded. Loaded from: " + filename, ChatColor.GREEN, false);
				config.report();
			}
			else
			{
				sendOrLog(sender, error, ChatColor.RED, false);
			}
			return true;
		}

		if (CommunityBridge.isActive() == false)
		{
			sendOrLog(sender, "CommunityBridge is NOT active. Only the cbreload command is available.", ChatColor.RED, false);
			return true;
		}
		else if (label.equals("cbsync"))
		{
			if (sender instanceof Player)
			{
				CommunityBridge.webapp.runSynchronizePlayer((Player) sender, true);
			}
			else
			{
				sendOrLog(sender, "cbsync can only be used while in the game. You can use cbsyncall to force a synchronization for all connected players.", ChatColor.RED, false);
			}
			return true;
		}
		else if (label.equals("cbysncall"))
		{
			CommunityBridge.webapp.runSynchronizeAll();
			return true;
		}

		return true;
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

		log.info(message);
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
      log.info(message);
		}
	}
}