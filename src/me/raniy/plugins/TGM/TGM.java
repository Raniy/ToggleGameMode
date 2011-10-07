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
	private boolean inVerbose = true;
	
	// Constants
	private final GameMode creativeMode = GameMode.CREATIVE;
	private final GameMode survivalMode = GameMode.SURVIVAL;
	
	//permissions
	private String toggleGameModePermission = "ToggleGameMode.TGM";
	
	// commands
	private String toggleGameMode = "togglegamemode";
	private String toggleGameModeAlias = "tgm";
	
	// Notifications.
	private String notPermissioned = "I'm sorry Dave. I can't let you do that.";
	private String switchTo = ChatColor.WHITE + "GameMode switched to:";
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
    	
    	// Do our command, whatever it might actually be called
    	if (((command.getName().equalsIgnoreCase(toggleGameMode)) || (command.getName().equalsIgnoreCase(toggleGameModeAlias))) && (!(player == null)))
    	{
    		if(player.hasPermission(toggleGameModePermission) || player.isOp()){
    			String verboseModeString = "";
    			   			
    			// Determine game mode and flip it.
    			switch(player.getGameMode().getValue()){
    				case(0):
    					// Switch to 1
    					player.setGameMode(this.creativeMode);
    					if (this.isInVerbose()){
    						verboseModeString = player.getName() + ": " + this.switchTo + this.switchTo1 + this.getVerboseModeLocationString(player);
    						this.doLog(verboseModeString);
    						Player onlinePlayers[] = this.getServer().getOnlinePlayers();
    						
    						for(int cnt = 0; cnt < onlinePlayers.length; cnt++){
    							if (onlinePlayers[cnt].isOp()){
    								onlinePlayers[cnt].sendMessage(verboseModeString);
    							}
    						}
    					}
    					// Notify player of switch to 1
    					player.sendMessage(switchTo + switchTo1);
    					break;
    				case(1):
    					// Switch to 0
    					player.setGameMode(this.survivalMode);
    					if (this.isInVerbose()){
    						verboseModeString = player.getName() + ": " + this.switchTo + this.switchTo0 + this.getVerboseModeLocationString(player);
    						this.doLog(verboseModeString);
    						Player onlinePlayers[] = this.getServer().getOnlinePlayers();
    						
    						for(int cnt = 0; cnt < onlinePlayers.length; cnt++){
    							if (onlinePlayers[cnt].isOp()){
    								onlinePlayers[cnt].sendMessage(verboseModeString);
    							}
    						}
    					}
    					// Notify player of switch to 0
    					player.sendMessage(switchTo + switchTo0);
    				break;
    			}
    			// Tell bukkit we handled the command
    			
    			return true;
    		} else {
    			// This is our command, but they cant use it. Naughty.
    			// If we have a error message configured then send it and tell bukkit we handled the command.
    			if(this.notPermissioned != "" || this.isInVerbose()){
        			// If in Verbose mode then tell the console it happened. 
        			if (this.isInVerbose()){
        				this.doLog(player.getName() + " failed to switch game modes." + this.getVerboseModeLocationString(player));
        			}
    				player.sendMessage(notPermissioned);
    				//Tell bukkit we process the command.
    				return true;
    			}
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
    	// Load our startup variables, set defaults if no config was present
    	// Permission
    	this.toggleGameModePermission = this.myConfig.getString("TGM.PermissionNode",toggleGameModePermission);
    	// Message Strings
    	this.switchTo = this.myConfig.getString("TGM.ToggledMessage.Base",this.switchTo);
    	this.switchTo0 = this.myConfig.getString("TGM.ToggledMessage.Survival",this.switchTo0);
    	this.switchTo1 = this.myConfig.getString("TGM.ToggledMessage.Creative",this.switchTo1);
    	this.notPermissioned = this.myConfig.getString("TGM.NoPermissionsMessage", this.notPermissioned);
    	
    	// Verbose Mode
    	this.setInVerbose(this.myConfig.getBoolean("TGM.VerboseMode", this.isInVerbose()));

    	// Command and Alias
    	// Until I figure a way to add our commands dynamically to the plugin.yml this is going to have to be disabled.
    	//this.toggleGameMode = this.myConfig.getString("TGM.Command",this.toggleGameMode);
    	//this.toggleGameModeAlias = this.myConfig.getString("TGM.CommandAlias",this.toggleGameModeAlias);
    	
    	//Make sure the config file is created.
    	this.myConfig.save();
	}

	public boolean isInVerbose() {
		return inVerbose;
	}

	public boolean setInVerbose(boolean inVerbose) {
		this.inVerbose = inVerbose;
		return inVerbose;
	}
	private String getVerboseModeLocationString(Player player){
		String retval = "";
		retval = " in world: " + player.getWorld().getName() + " at X: " + player.getLocation().getBlockX() + " Y: " + player.getLocation().getBlockY() + " Z: " + player.getLocation().getBlockZ() ;
		return retval;
	}
}
