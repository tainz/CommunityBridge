package org.communitybridge.main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.communitybridge.utility.Log;

public class CBCommandExecutor implements CommandExecutor
{
	private Configuration configuration;
	private Log log;

	private BukkitWrapper bukkit;

	public CBCommandExecutor(Environment environment)
	{
		this.configuration = environment.getConfiguration();
		this.log = environment.getLog();
		this.bukkit = new BukkitWrapper();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments)
	{
		label = label.toLowerCase();
		if (label.equals("cbreload"))
		{
			commandReload(arguments, sender);
			return true;
		}

		if (CommunityBridge.isActive() == false)
		{
			sendOrLog(sender, configuration.messages.get("communitybridge-inactive"), ChatColor.RED, false);
			return true;
		}

		if (label.equals("cbsync"))
		{
			if (arguments.length == 1 && (!(sender instanceof Player) || ((Player)sender).hasPermission("communitybridge.cbsynctarget")))
			{
				commandSyncTarget(sender, arguments[0]);
			}
			else
			{
				commandSync(sender);
			}
			return true;
		}

		if (label.equals("cbsyncall"))
		{
			commandSyncAll(sender);
			return true;
		}

		return true;
	}

	private void sendAndLog(CommandSender sender, String message, ChatColor color, boolean who)
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

	private void sendOrLog(CommandSender sender, String message, ChatColor color, boolean who)
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

	private void commandReload(String[] arguments, CommandSender sender)
	{
		if (arguments.length > 1)
		{
			sendOrLog(sender, configuration.messages.get("cbreload-too-many-arguments"), ChatColor.RED, false);
		}

		sendAndLog(sender, configuration.messages.get("cbreload"), ChatColor.GREEN, true);

		String error;
		String filename;
		if (arguments.length == 1)
		{
			filename = arguments[0];
		}
		else
		{
			filename = "config.yml";
		}

		error = configuration.reload(filename);

		if (error == null)
		{
			sendOrLog(sender, configuration.messages.get("cbreload-success").replace("~FILENAME~", filename), ChatColor.GREEN, false);
			if (CommunityBridge.isActive())
			{
				configuration.report();
			}
		}
		else
		{
			sendOrLog(sender, error, ChatColor.RED, false);
		}
	}

	private void commandSync(CommandSender sender)
	{
		if (sender instanceof Player)
		{
			sendOrLog(sender, configuration.messages.get("cbsync"), ChatColor.GREEN, false);
			CommunityBridge.webapp.runSynchronizePlayer((Player) sender, true);
		}
		else
		{
			sendOrLog(sender, configuration.messages.get("cbsync-ingame"), ChatColor.RED, false);
		}
	}

	private void commandSyncAll(CommandSender sender)
	{
		sendAndLog(sender, configuration.messages.get("cbsyncall"), ChatColor.GREEN, true);
		CommunityBridge.webapp.runSynchronizeAll();
	}

	private void commandSyncTarget(CommandSender sender, String playerName)
	{
		Player player  = bukkit.getServer().getPlayerExact(playerName);
		if (player == null)
		{
			String message = configuration.messages.get("cbsync-target-not-found").replace("~PLAYERNAME~", playerName);
			sendOrLog(sender, message, ChatColor.RED, false);
		}
		else
		{
			String message = configuration.messages.get("cbsync-target").replace("~PLAYERNAME~", player.getName());
			sendAndLog(sender, message, ChatColor.GREEN, true);
			CommunityBridge.webapp.runSynchronizePlayer(player, true);
		}
	}
}
