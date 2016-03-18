package me.becja10.CoinExchange.Utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import me.becja10.CoinExchange.CoinExchange;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerManager
{
	private static FileConfiguration config = null;
	private static File players = null;
	private static String path = CoinExchange.instance.getDataFolder().getAbsolutePath() 
			+ File.separator + "players.yml";

	public static FileConfiguration getPlayers() {
		/*
		 * <uuid>: coin count
		 */
		if (config == null)
			reloadPlayers();
		return config;
	}

	public static void reloadPlayers() {
		if (players == null)
			players = new File(path);
		config = YamlConfiguration.loadConfiguration(players);
	}
	
	public static void savePlayers() {
		if ((config == null) || (players == null))
			return;
		try {
			getPlayers().save(players);
		} catch (IOException ex) {
			CoinExchange.logger.warning("Unable to write to the player file at \"" + path + "\"");
		}
	}
	
	public static int getCoinsFor(UUID id)
	{
		return config.getInt(id.toString(), 0);
	}
	
	public static void updateCoins(UUID id, int coins)
	{
		config.set(id.toString(), coins);
		savePlayers();
	}

	/*
	 * Creates the default, empty player.yml file
	 */
//	public static void saveDefaultPlayers() {
//		if (players == null)
//			players = new File(path);
//		if (!players.exists())
//			CoinExchange.instance.saveResource("players.yml", false);
//	}
}
