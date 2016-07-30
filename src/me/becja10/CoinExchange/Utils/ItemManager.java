package me.becja10.CoinExchange.Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemManager {

	private static Logger logger;
	private static FileConfiguration config = null;
	private static File Items = null;
	private static String path;
	
	public static FileConfiguration getItems() {
		/*
		 *     name: ItemStack
		 */
		if (config == null)
			reloadItems();
		return config;
	}

	public static void reloadItems() {
		if (Items == null)
			Items = new File(path);
		config = YamlConfiguration.loadConfiguration(Items);
	}
	
	public static void saveItems() {
		if ((config == null) || (Items == null))
			return;
		try {
			getItems().save(Items);
		} catch (IOException ex) {
			logger.warning("Unable to write to the file \"" + path + "\"");
		}
	}
	
	public static void setUpManager(JavaPlugin plugin, Logger log){
		path = plugin.getDataFolder().getAbsolutePath()	+ File.separator + "Items.yml".toLowerCase();
		reloadItems();
		
	}
	
	public static boolean addCustomItem(String name, ItemStack item){
		if(config.contains(name))
			return false;
		
		config.set(name, item);
		saveItems();
		
		return true;
	}
	
	public static ItemStack getItem(String itemKey){
		return config.getItemStack(itemKey, null);
	}
}
