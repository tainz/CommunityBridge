package org.communitybridge.main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CBCommandExecutor implements CommandExecutor
{
	private Environment environment;

	private BukkitWrapper bukkit;

	public CBCommandExecutor(Environment environment)
	{
		this.environment = environment;
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
			sendOrLog(sender, environment.getConfiguration().messages.get("communitybridge-inactive"), ChatColor.RED, false);
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

		environment.getLog().info(message);
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
      environment.getLog().info(message);
		}
	}

	private void commandReload(String[] arguments, CommandSender sender)
	{
		if (arguments.length > 1)
		{
			sendOrLog(sender, environment.getConfiguration().messages.get("cbreload-too-many-arguments"), ChatColor.RED, false);
		}

		sendAndLog(sender, environment.getConfiguration().messages.get("cbreload"), ChatColor.GREEN, true);

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

		error = environment.getConfiguration().reload(filename);

		if (error == null)
		{
			sendOrLog(sender, environment.getConfiguration().messages.get("cbreload-success").replace("~FILENAME~", filename), ChatColor.GREEN, false);
			if (CommunityBridge.isActive())
			{
				environment.getConfiguration().report();
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
			sendOrLog(sender, environment.getConfiguration().messages.get("cbsync"), ChatColor.GREEN, false);
			environment.getWebApplication().runSynchronizePlayer(environment, (Player) sender, true);
		}
		else
		{
			sendOrLog(sender, environment.getConfiguration().messages.get("cbsync-ingame"), ChatColor.RED, false);
		}
	}

	private void commandSyncAll(CommandSender sender)
	{
		sendAndLog(sender, environment.getConfiguration().messages.get("cbsyncall"), ChatColor.GREEN, true);
		environment.getWebApplication().runSynchronizeAll();
	}

	private void commandSyncTarget(CommandSender sender, String playerName)
	{
		Player player  = bukkit.getServer().getPlayerExact(playerName);
		if (player == null)
		{
			String message = environment.getConfiguration().messages.get("cbsync-target-not-found").replace("~PLAYERNAME~", playerName);
			sendOrLog(sender, message, ChatColor.RED, false);
		}
		else
		{
			String message = environment.getConfiguration().messages.get("cbsync-target").replace("~PLAYERNAME~", player.getName());
			sendAndLog(sender, message, ChatColor.GREEN, true);
			environment.getWebApplication().runSynchronizePlayer(environment, player, true);
		}
	}
}
