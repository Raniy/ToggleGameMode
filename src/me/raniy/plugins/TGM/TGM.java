/* ToggleGameMode is a plugin for CraftBukkit
 * it is designed to provide a user accessible
 * permission node controlled
 * method for easily switching gamemodes.
 * 
 * Created by Raniy, AKA christopher_lohman@yahoo.com
 */

package me.raniy.plugins.TGM;

import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.ChatColor;

public class TGM extends JavaPlugin {
	// Imported stuff
	private Logger myLog=Logger.getLogger("Minecraft");
	public PluginDescriptionFile myDesc = null;
	public Configuration myConfig = null;
	public java.util.Properties myProperties= null;
	
	// Constants
	private GameMode creativeMode = GameMode.CREATIVE;
	private GameMode survivalMode = GameMode.SURVIVAL;
	
	//permissions
	private String toggleGameModePermission = "ToggleGameMode.TGM";
	
	// commands
	private final String toggleGameMode = "togglegamemode";
	private final String toggleGameModeAlias = "tgm";
	
	// Notifications.
	private String switchTo = ChatColor.WHITE + "Switched to:";
	private String switchTo1 = ChatColor.GREEN + " Creative Mode" + ChatColor.WHITE;
	private String switchTo0 = ChatColor.GREEN + " Survival Mode" + ChatColor.WHITE;

	@Override
	public void onDisable() {
		// TODO 
		
		this.doLog("Disabled.");
	}

	@Override
	public void onEnable() {
		// Load my ymls
    	this.getMyYMLs();
		
		this.doLog("Enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Check sender / get Player
    	Player player = null;
    	if (sender instanceof Player) 
    	{
    		player = (Player) sender;
    	}
    	
    	// Do ToggleGameMode/tgm
    	if (((command.getName().equalsIgnoreCase(toggleGameMode)) || (command.getName().equalsIgnoreCase(toggleGameModeAlias))) && (!(player == null)))
    	{
    		if(player.hasPermission(toggleGameModePermission)){
    			// Check current game mode
    			// Assume survival
    			GameMode theCurrentGameMode = null;
    			int currentGameMode = 0;
    			
    			theCurrentGameMode = player.getGameMode();
    			currentGameMode = theCurrentGameMode.getValue();
    			// Determine game mode and flip it.
    			switch(currentGameMode){
    				case(0):
    					// Switch to 1
    					player.setGameMode(this.creativeMode);
    					// Notify player of switch to 1
    					player.sendMessage(switchTo + switchTo1);
    					break;
    				case(1):
    					// Switch to 0
    					player.setGameMode(this.survivalMode);
    					// Notify player of switch to 0
    					player.sendMessage(switchTo + switchTo0);
    				break;
    			}
    			// Tell bukkit we handled the command
    			return true;
    		} else {
    			// This is our command, but they cant use it. Naughty.
    			return false;
    		}
    	}
    	// Tell Bukkit we didnt process this command
    	return false;
	}

	public void doLog(String strLog)
	{
		//ToDo: Should probably check to make sure we are only logging events from us
		this.myLog.info("[" + this.myDesc.getName() + "] " + strLog);
	}

	private void getMyYMLs() {
    	this.myDesc = this.getDescription();
    	this.myConfig = this.getConfiguration();
    	this.myProperties = new java.util.Properties();
    	// Load our startup variables, set defaults if no config was present
    	this.toggleGameModePermission = this.myConfig.getString("ToggleGameModePermissionNode",toggleGameModePermission);
    	this.switchTo = this.myConfig.getString("ToggledMessage.Base",switchTo);
    	this.switchTo0 = this.myConfig.getString("ToggledMessage.Survival",switchTo0);
    	this.switchTo1 = this.myConfig.getString("ToggledMessage.Creative",switchTo1);
    	
    	//Make sure the config file is created.
    	this.myConfig.save();
	}

}
