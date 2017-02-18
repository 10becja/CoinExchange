package me.becja10.CoinExchange;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import me.becja10.CoinExchange.Commands.CoinCmdHandler;
import me.becja10.CoinExchange.Commands.SpendHandler;
import me.becja10.CoinExchange.Utils.CommandManager;
import me.becja10.CoinExchange.Utils.ItemManager;
import me.becja10.CoinExchange.Utils.Messages;
import me.becja10.CoinExchange.Utils.PlayerManager;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CoinExchange extends JavaPlugin implements Listener{
	
	public static CoinExchange instance;
	public final static Logger logger = Logger.getLogger("Minecraft");
	
	private String configPath;
	private FileConfiguration config;
	private FileConfiguration outConfig;
	
	public int coinValue; private String coinValueStr = "Value per coin";
	
	private void loadConfig(){
		configPath = this.getDataFolder().getAbsolutePath() + File.separator + "config.yml";
		config = YamlConfiguration.loadConfiguration(new File(configPath));
		outConfig = new YamlConfiguration();
		
		coinValue = config.getInt(coinValueStr, 0);
		
		outConfig.set(coinValueStr, coinValue);
		
		saveConfig(outConfig, configPath);
	}
	
	private void saveConfig(FileConfiguration config, String path)
	{
        try{config.save(path);}
        catch(IOException exception){logger.info("Unable to write to the configuration file at \"" + path + "\"");}
	}

	@Override
	public void onEnable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(pdfFile.getName() + "Version " + pdfFile.getVersion() + " has been enabled!");
		instance = this;
		
		PluginManager manager = getServer().getPluginManager();
		
		manager.registerEvents(new SpendHandler(), this);
		
		loadConfig();
		CommandManager.setUpManager();
		PlayerManager.setUpManager();
		ItemManager.setUpManager(this, logger);
		
	}
	
	@Override
	public void onDisable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(pdfFile.getName() + " Has Been Disabled!");
		saveConfig(outConfig, configPath);
		PlayerManager.savePlayers();
		CommandManager.saveCommands();
		ItemManager.saveItems();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		switch(cmd.getName().toLowerCase()){
			case "addcoins":
				return CoinCmdHandler.AddCoins(sender, args);
			case "removecoins":
				return CoinCmdHandler.RemoveCoins(sender, args);
			case "viewcoins":
				return CoinCmdHandler.ViewCoins(sender, args);
			case "spendcoins":
				return SpendHandler.runCommand(sender, args);
			case "cecustomitem":
				return addCustomItem(sender, args);
			case "reloadcoins":
				if(sender instanceof Player && !sender.hasPermission("coinexchange.admin"))
					sender.sendMessage(Messages.noPermission());
				else{
					PlayerManager.reloadPlayers();
					loadConfig();
					CommandManager.reloadCommands();
					ItemManager.reloadItems();
					sender.sendMessage(Messages.reloadSuccessful());
				}
				return true;
		}
		return true;
	}
	
	private boolean addCustomItem(CommandSender sender, String[] args){
		if(!(sender instanceof Player))
			sender.sendMessage(Messages.playersOnly());
		else if(!sender.hasPermission("coinexchange.customitem"))
			sender.sendMessage(Messages.noPermission());
		else if(args.length < 1)
			return false;
		else{
			Player player = (Player) sender;
			ItemStack inHand = player.getInventory().getItemInMainHand();
			if(ItemManager.addCustomItem(args[0].toLowerCase(), inHand))
				player.sendMessage(ChatColor.GREEN + "Item added with id: " + ChatColor.BLUE + args[0]);
			else
				player.sendMessage(ChatColor.RED + "This id is already taken.");			
		}
		
		return true;
	}
}
