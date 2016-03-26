package me.becja10.CoinExchange.Commands;


import java.util.UUID;

import me.becja10.CoinExchange.Utils.Messages;
import me.becja10.CoinExchange.Utils.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class CoinCmdHandler {
	
	public static boolean AddCoins(CommandSender sender, String[] args)
	{
		if(args.length < 2) return false;

		if(sender instanceof Player && !sender.hasPermission("coinexchange.addcoins"))
			sender.sendMessage(Messages.noPermission());
		else
		{
			String playername = args[0];
			String amount = args[1];
			
			Player player = Bukkit.getPlayer(playername);
			if(player != null)
			{
				UUID id = player.getUniqueId();
				int coins = PlayerManager.getCoinsFor(id);
				int toAdd = 0;
				try {
					toAdd = Integer.parseInt(amount);
				} catch(NumberFormatException ex){
					return false;
				}
				toAdd = Math.abs(toAdd);
				player.sendMessage(Messages.coinsAdded(toAdd));
				PlayerManager.updateCoins(id, coins + toAdd);
			}
			else
				sender.sendMessage(Messages.playerNotFound());
		}
		return true;
	}
	
	public static boolean RemoveCoins(CommandSender sender, String[] args)
	{
		if(args.length < 2) return false;

		if(sender instanceof Player && !sender.hasPermission("coinexchange.removecoins"))
			sender.sendMessage(Messages.noPermission());
		else
		{
			String playername = args[0];
			String amount = args[1];
			
			Player player = Bukkit.getPlayer(playername);
			if(player != null)
			{
				UUID id = player.getUniqueId();
				int coins = PlayerManager.getCoinsFor(id);
				int toRemove = 0;
				try {
					toRemove = Integer.parseInt(amount);
				} catch(NumberFormatException ex){
					return false;
				}
				toRemove = Math.abs(toRemove);
				player.sendMessage(Messages.coinsRemoved(toRemove));
				PlayerManager.updateCoins(id, coins - toRemove);
			}
			else
				sender.sendMessage(Messages.playerNotFound());
		}
		return true;
	}
	
	public static boolean ViewCoins(CommandSender sender, String[] args)
	{
		if(sender instanceof Player && (!sender.hasPermission("coinexchange.viewcoins") || 
				(args.length > 0 && !sender.hasPermission("coinexchange.viewcoins.other"))))
			sender.sendMessage(Messages.noPermission());
		else
		{
			UUID id = null;
			if(args.length > 0)
			{
				id = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
			}
			else{
				if(sender instanceof Player){
					id = ((Player) sender).getUniqueId();
				}
				else{
					sender.sendMessage(Messages.playersOnly());
				}
			}
			if(id != null){
				int coins = PlayerManager.getCoinsFor(id);
				String msg = (args.length == 0) ? Messages.myCoins(coins) : Messages.otherCoins(((Player) sender).getName(), coins);
				sender.sendMessage(msg);
			}
			else
				sender.sendMessage(Messages.playerNotFound());
		}
		return true;
	}

}
