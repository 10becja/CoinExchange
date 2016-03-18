package me.becja10.CoinExchange.Commands;

import java.util.HashMap;
import java.util.UUID;

import me.becja10.CoinExchange.Utils.CommandManager;
import me.becja10.CoinExchange.Utils.CommandObject;
import me.becja10.CoinExchange.Utils.Messages;
import me.becja10.CoinExchange.Utils.PlayerManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class SpendHandler implements Listener {
	
	private static HashMap<UUID, Inventory> openInventories = new HashMap<UUID,Inventory>();

	public static boolean runCommand(CommandSender sender) {
		
		if(!(sender instanceof Player))
			sender.sendMessage(Messages.playersOnly());
		else if(!sender.hasPermission("coinexchange.spend"))
			sender.sendMessage(Messages.noPermission());
		else
		{
			Player p = (Player) sender;
			int coins = PlayerManager.getCoinsFor(p.getUniqueId());
			Inventory inv = CommandManager.viewPage(1, coins);
			p.openInventory(inv);
			openInventories.put(p.getUniqueId(), inv);
		}
		return true;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event)
	{
		Inventory inv = event.getClickedInventory();
		Player p = (Player) event.getWhoClicked();
		if(inv == null)
			return;
		if(!inv.equals(openInventories.get(p.getUniqueId())))
			return;
		String title = inv.getTitle();
		int page = CommandManager.getPage(inv);
		int slot = event.getSlot();
		switch(slot){
			case 19: //back
			
				return;
			case 20: //forward
				
				break;
			case 27: //cancel
				
				break;			
		}
			
		CommandObject obj = CommandManager.getObj(page, slot);
		String cmd = obj.command.replace("{player}", p.getName());
	}

}
