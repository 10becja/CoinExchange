package me.becja10.CoinExchange.Utils;

public class CommandObject {
	
	public String command;
	public int price;
	public boolean closeWhenDone;
	
	public CommandObject(String cmd, int prc, boolean close){
		command = cmd;
		price = prc;
		closeWhenDone = close;
		
	}
}
