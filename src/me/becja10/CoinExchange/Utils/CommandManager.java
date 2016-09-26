package me.becja10.CoinExchange.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.becja10.CoinExchange.CoinExchange;

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
		 * pages:
		 *   page-<page number>: 
		 *     slot-<1-18>:
		 *       command: <some command to run>
		 *       price: <in coins>
		 *       displayItem: <id of material to display in chest>
		 *       displayText: <the text to show>
		 *       showPermission: 
		 *       usePermToHide:
		 *       closeWhenDone:
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
		reloadCommands();
		String header = "You can use these tokens in your commands.\n";
		header += "{player} : The player who selected the command to run\n";

		header += "\n";
		
		header += "By default, the command will be run by the console. \n"
				+ "To have the player run it with elevated permissions,\n"
				+ "add a ^ in front of the command.\n\n";
		
		header += "Example setup:"
				+ "pages:"
				+ "  page-1:"
				+ "    slot-1:"
				+ "      command: eco give {player} 250"
				+ "      price: 10"
				+ "      displayItem: 371"
				+ "      displayText: convert to $250"
				+ "      permission: <optional>"
				+ "      usePermToHide: false\n"
				+ "      closeWhenDone: <optional>";
		
		config.options().header(header);
		config.options().copyHeader(true);
		
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
		String str = "pages.page-" + page + ".slot-" + (slot + 1);
		if(config.contains(str))
			ret = new CommandObject(config.getString(str + ".command"),
									config.getInt(str + ".price"),
									config.getBoolean(str + ".closeWhenDone", false));
		return ret;
	}
	
	@SuppressWarnings("deprecation")
	public static Inventory viewPage(int page, Player p)
	{
		Inventory inv = null;
		String str = "pages.page-" + page;
		if(config.contains(str))
		{
			inv = Bukkit.createInventory(null, 27, "Page " + page);
			for(String slot : config.getConfigurationSection(str).getKeys(false))
			{
				String key = str + "." + slot;
				int slotNum = Integer.parseInt(slot.substring(5)) - 1;
				if(slotNum > 18) continue;
				
				String permission = config.getString(key + ".permission", "");
				if(permission != ""){
					int idx = -1;
					if(permission.contains("*")){
						idx = permission.lastIndexOf("*");
						if(idx >= 0)
							permission = permission.substring(0, idx);
					}
					boolean hide = config.getBoolean(key + ".usePermToHide", false);

					boolean hasPermission = false;
					for(PermissionAttachmentInfo perm : p.getEffectivePermissions()){
						String node = perm.getPermission();
						if(idx >= 0){
							if(idx < node.length())
								node = node.substring(0, idx);
							else
								continue;
						}
						if(node.equalsIgnoreCase(permission)){
							hasPermission = true;
							break;
						}								
					}
					//if hide and hasPermission
					//if not hide and not hasPermission
					if(hide == hasPermission)
						continue;					
				}
				
				String mat = config.getString(key + ".displayItem");				
				ItemStack item = ItemManager.getItem(mat);
				
				if(item == null){
					
					boolean isNum = false;
					int matNum = 0;
					try{
						matNum = Integer.parseInt(mat);
						isNum = true;
					}catch(NumberFormatException e){
						isNum = false;
					}					
					
					Material material = (isNum) ? Material.getMaterial(matNum) : Material.getMaterial(mat);
					if(material == null)
						System.out.println("Bad displayItem for page " + page + " slot " + slot);
					item = (material == null) ? new ItemStack(Material.GOLD_NUGGET) : new ItemStack(material);
				}
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(config.getString(key + ".displayText", "A thing"));
				meta.setLore(Arrays.asList("Price: " + config.getString(key + ".price", "$$") + " coins"));
				item.setItemMeta(meta);		
				
				inv.setItem(slotNum, item);
			}
			if(page > 1)
				inv.setItem(18, back);
			if(page < config.getConfigurationSection("pages").getKeys(false).size())
				inv.setItem(19, forward);
			inv.setItem(26, close);
		}
		
		return inv;		
	}

	public static int getPage(Inventory inv) {
		String titlePage = inv.getTitle().substring(5);
		int page = 1;
		try{
			page = Integer.parseInt(titlePage);
		}
		catch(NumberFormatException ex){
			page = 1;
		}
		
		return page;
	}
}
