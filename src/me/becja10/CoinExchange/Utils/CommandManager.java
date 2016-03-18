package me.becja10.CoinExchange.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import me.becja10.CoinExchange.CoinExchange;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
public class CommandManager
{
	private static FileConfiguration config = null;
	private static File commands = null;
	private static String path = CoinExchange.instance.getDataFolder().getAbsolutePath() 
			+ File.separator + "commands.yml";
	
	private static ItemStack forward = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
	private static ItemStack back = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6);
	private static ItemStack close = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

	public static FileConfiguration getCommands() {
		/*
		 * "You can use these tokens in your commands":
		 *   {players}: blah
		 * 
		 * pages:
		 *   page-<page number>: 
		 *     slot-<1-18>:
		 *       command: <some command to run>
		 *       price: <in coins>
		 *       displayItem: <id of material to display in chest>
		 *       displayText: <the text to show>
		 */
		if (config == null)
			reloadCommands();
		return config;
	}

	public static void reloadCommands() {
		if (commands == null)
			commands = new File(path);
		config = YamlConfiguration.loadConfiguration(commands);
	}
	
	public static void saveCommands() {
		if ((config == null) || (commands == null))
			return;
		try {
			getCommands().save(commands);
		} catch (IOException ex) {
			CoinExchange.logger.warning("Unable to write to the command file at \"" + path + "\"");
		}
	}
	
	public static void setUpManager(){
		String str = "pages.page-1.slot-1";
		String help = "You can use these tokens in your commands";
		config.set(help + ".{player}", "The player who selected the command to run.");
		if(!config.contains(str)){
			config.set(str + ".command", "eco give {player} 1000");
			config.set(str + ".price", 10);
			config.set(str + ".displayItem", 371);
			config.set(str + ".displayText", "Convert Coins to Economy Money");
		}
		saveCommands();
		ItemMeta meta = forward.getItemMeta();
		meta.setDisplayName("Next Page");
		forward.setItemMeta(meta);
		meta = back.getItemMeta();
		meta.setDisplayName("Prev Page");
		back.setItemMeta(meta);
		meta = close.getItemMeta();
		meta.setDisplayName("Close");
		close.setItemMeta(meta);
	}
	
	public static CommandObject getObj(int page, int slot){
		CommandObject ret = null;
		String str = "pages.page-" + page + ".slot-" + slot;
		if(config.contains(str))
			ret = new CommandObject(config.getString(str + ".command"),
									config.getInt(str + ".price"));
		return ret;
	}
	
	public static Inventory viewPage(int page, int coins)
	{
		Inventory inv = null;
		String str = "pages.page-" + page;
		if(config.contains(str))
		{
			inv = Bukkit.createInventory(null, 27, "CoinExchange purchases: Page " + page + ". You have " + coins + " coins" );
			for(String slot : config.getConfigurationSection(str).getKeys(false))
			{
				int slotNum = Integer.parseInt(slot.substring(5));
				if(slotNum > 18) continue;
				int matId = config.getInt(slot + ".displayItem", -1);
				Material mat = Material.getMaterial(matId);
				ItemStack item = (mat == null) ? new ItemStack(Material.GOLD_NUGGET) : new ItemStack(mat);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(config.getString(slot + ".displayText", "A thing"));
				meta.setLore(Arrays.asList("Price: " + config.getString(slot + ".price"), "$$"));
				item.setItemMeta(meta);		
				
				inv.setItem(slotNum, item);
			}
			if(page > 1)
				inv.setItem(19, back);
			if(page < config.getConfigurationSection("pages").getKeys(false).size())
				inv.setItem(20, forward);
			inv.setItem(27, close);
		}
		
		return inv;		
	}

	public static int getPage(Inventory inv) {
		String title = inv.getTitle().split(".")[0];
		title = title.replace("CoinExchange purchases: Page ", "");
		int page = 1;
		try{
			page = Integer.parseInt(title);
		}
		catch(NumberFormatException ex){
			page = 1;
		}
		
		return page;
	}

//	public static void saveDefaultCommands() {
//		if (commands == null)
//			commands = new File(path);
//		if (!commands.exists())
//			CoinExchange.instance.saveResource("commands.yml", false);
//	}
}
