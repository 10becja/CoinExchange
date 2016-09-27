package me.becja10.CoinExchange.Commands;

import java.util.HashMap;
import java.util.UUID;

import me.becja10.CoinExchange.CoinExchange;
import me.becja10.CoinExchange.Utils.CommandManager;
import me.becja10.CoinExchange.Utils.CommandObject;
import me.becja10.CoinExchange.Utils.Messages;
import me.becja10.CoinExchange.Utils.PlayerManager;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class SpendHandler implements Listener {
	
	private static HashMap<UUID, Inventory> openInventories = new HashMap<UUID,Inventory>();

	public static boolean runCommand(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player))
			sender.sendMessage(Messages.playersOnly());
		else if(!sender.hasPermission("coinexchange.spend"))
			sender.sendMessage(Messages.noPermission());
		else
		{
			int page = 1;
			if(args.length > 0){
				try{
					page = Integer.parseInt(args[0]);
				}
				catch(Exception e){
					page = 1;
				}
			}
			Player p = (Player) sender;
			Inventory inv = CommandManager.viewPage(page, p);
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
		boolean isOpen = p.getOpenInventory().getTopInventory().equals(openInventories.get(p.getUniqueId()));
		if(inv == null || !isOpen)
			return;
		if(!inv.equals(openInventories.get(p.getUniqueId())) || event.isShiftClick() || event.getCursor().getType() != Material.AIR)
		{
			event.setCancelled(true);
			return;
		}
		int page = CommandManager.getPage(inv);
		int slot = event.getSlot();
		if(inv.getItem(slot) == null || inv.getItem(slot).getType() == Material.AIR)
			return;
		switch(slot){
			case 18: //back
				Inventory back = CommandManager.viewPage(page - 1, p);
				closeAndOpenInventory(event, p, back);
				return;
			case 19: //forward
				Inventory forward = CommandManager.viewPage(page + 1, p);
				closeAndOpenInventory(event, p, forward);
				return;
			case 26: //close
				closeAndOpenInventory(event, p, null);
				return;
		}
		event.setCancelled(true);
		int coins = PlayerManager.getCoinsFor(p.getUniqueId());
		CommandObject obj = CommandManager.getObj(page, slot);
		if(coins < obj.price){
			p.sendMessage(Messages.notEnoughCoins());
		}			
		else{
			String cmd = obj.command.replace("{player}", p.getName());
			boolean op = false;
			boolean cmdSucceeded = false;
			try{
				if(cmd.startsWith("^")){
					cmd = cmd.substring(1);
					if(!p.isOp()){
						op = true;
						p.setOp(true);
					}					
					cmdSucceeded = p.performCommand(cmd);
				}else{
					cmdSucceeded = Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
				}
			} finally{
				if(cmdSucceeded){
					PlayerManager.updateCoins(p.getUniqueId(), coins - obj.price);
					p.sendMessage(Messages.spentCoins(obj.price, PlayerManager.getCoinsFor(p.getUniqueId())));
					if(obj.closeWhenDone)
						closeAndOpenInventory(event, p, null);
				} else{
					p.sendMessage(ChatColor.RED + "Command failed to run. Please contact staff.");
				}
				if(op)
					p.setOp(false);
			}	
			
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event){
		Inventory inv = event.getInventory();
		Player p = (Player) event.getPlayer();
		if(inv.equals(openInventories.get(p.getUniqueId())))
				openInventories.remove(p.getUniqueId());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryDrag(InventoryDragEvent event){
		Inventory inv = event.getInventory();
		Player p = (Player) event.getWhoClicked();
		if(inv.equals(openInventories.get(p.getUniqueId())))
			event.setCancelled(true);
	}
	
	private void closeAndOpenInventory(InventoryClickEvent event, final Player p, final Inventory toOpen) {
		event.setCancelled(true);
		Bukkit.getScheduler().runTask(CoinExchange.instance, new Runnable(){
			public void run(){
				p.closeInventory();
				if(toOpen != null){
					Bukkit.getScheduler().runTask(CoinExchange.instance, new Runnable(){
						public void run(){
							p.openInventory(toOpen);
							openInventories.put(p.getUniqueId(), toOpen);
						}
					});
				}
			}
		});
	}

}
