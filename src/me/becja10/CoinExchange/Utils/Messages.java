package me.becja10.CoinExchange.Utils;

import org.bukkit.ChatColor;

public class Messages {
	
	private static String prefix = ChatColor.AQUA + "[" + ChatColor.GREEN + "Coins" + ChatColor.AQUA + "] ";
	
	private static Message noPermission = new Message(ChatColor.DARK_RED + "You do not have permission for this command");
	private static Message playerNotFound = new Message(ChatColor.DARK_RED + "Could not find player by that name");	
	private static Message coinsRemoved = new Message(prefix + ChatColor.YELLOW + "{0} coins have been removed");
	private static Message coinsAdded = new Message(prefix + ChatColor.YELLOW + "{0} coins have been added");
	private static Message playersOnly = new Message("This command must be run by a player");
	private static Message myCoins = new Message(prefix + ChatColor.YELLOW + "You have {0} coins");
	private static Message otherCoins = new Message(prefix + ChatColor.YELLOW + "{0} has {1} coins");

	
	public static String noPermission(){ return noPermission.getMsg();}
	public static String playerNotFound(){return playerNotFound.getMsg();}
	public static String coinsRemoved(int amount){return coinsRemoved.format(amount + "");}
	public static String coinsAdded(int amount){return coinsAdded.format(amount + "");}
	public static String playersOnly(){ return playersOnly.getMsg();}
	public static String myCoins(int amount){return myCoins.format(amount + "");}
	public static String otherCoins(String player, int amount){return otherCoins.format(player, amount + "");}

	
	
	private static class Message{
		String msg;
		
		Message(String str)
		{
			msg = str;
		}
		
		String format(String... args){
			String ret = msg;
			for(int i = 0; i < args.length; i++)
			{
				ret.replace("{" + i + "}", args[i]);
			}
			return ret;
		}
		
		String getMsg()
		{
			return msg;
		}
	}

}
