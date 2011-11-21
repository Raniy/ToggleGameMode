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
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings("deprecation")
public class TGM extends JavaPlugin {
	// Imported stuff
	private Logger myLog=Logger.getLogger("Minecraft");
	public PluginDescriptionFile myDesc = null;
	public Configuration myConfig = null;
	private boolean inVerbose = false;
	
	// Constants
	private final GameMode creativeMode = GameMode.CREATIVE;
	private final GameMode survivalMode = GameMode.SURVIVAL;
	
	//permissions
	private String toggleGameModePermission = "ToggleGameMode.TGM";
	private String toggleOthersGameModePermission = "ToggleGameMode.TGM.Other";
	private String toggleAdminPermission = "ToggleGameMode.*";
	
	
	// commands
	private String toggleGameMode = "togglegamemode";
	private String toggleGameModeAlias = "tgm";
	
	// Notifications.
	private String notPermissioned = "I'm sorry Dave. I can't let you do that.";
	private String noSuchPlayer = "I couldn't find anyone with that name.";
	private String switchBy = " by: ";
	private String switchTo = ChatColor.WHITE + "Gamemode switched to:";
	private String switchTo1 = ChatColor.GREEN + " Creative Mode" + ChatColor.WHITE;
	private String switchTo0 = ChatColor.GREEN + " Survival Mode" + ChatColor.WHITE;
	

	@Override
	public void onDisable() {
		this.doLog("Disabled.");
	}

	@Override
	public void onEnable() {
		// Load my ymls
    	this.getMyYMLs();
		
		if (this.getServer().getPluginManager().isPluginEnabled("PermissionsEx")){
			this.doLog("I see PermissionsEx. I will use it.");
		}
		this.doLog(this.myDesc.getVersion() + " enabled.");
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
    		//We need to see which version of the command we are doing IE self or other.
    		//See if they are toggling themselves or trying to toggle someone else.
    		if (args.length > 0)
    		{
    			//	They gave an argument IE they are trying to toggle someone else
        		// See if they are allowed. 
    			if(this.hasPermissions(player, this.toggleOthersGameModePermission) | this.hasPermissions(player, this.toggleAdminPermission))
    			{
    				// They are allowed to flip other people. Lets see who they are trying to flip.
        			String theName = args[0];
        			Player targetPlayer = null;
        			// Idiot Checking: See if target is valid.	
    				targetPlayer = this.getServer().getPlayerExact(theName);
    			 
    				if (targetPlayer != null)
    				{
    					//The target is valid
    					// Is targetPlayer sender? :)
    					if(player == targetPlayer)
    					{
    						// LOL. OK bit wordier then just typing the command but why not...
    						flipPlayersGameMode(player);
    						return true;
    					} else {
    						// Toggle the target
    						this.flipPlayersGameMode(targetPlayer,player);
    						return true;
    					}
    				} else {
    					// Inform them the target could not be found.
    					player.sendMessage(this.noSuchPlayer);
    					return true;
    				}
    			} else {
    				//They dont have permission to toggle others.
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
    		} else {
    			// 	They are toggling themselves.
    			// See if they are allowed
    			if(this.hasPermissions(player, this.toggleGameModePermission) | this.hasPermissions(player, this.toggleAdminPermission))
    			{
    				// Allowed
    				this.flipPlayersGameMode(player);
    				return true;
    			} else {
    				// Not allowed
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
    	}
    	// Not our command, we dont care.
    	// Tell Bukkit we didnt process this command
    	return false;
	}
	
	private boolean flipPlayersGameMode(Player targetPlayer, Player callingPlayer){
		String verboseModeString = "";
		
		// Determine game mode and flip it.
		switch(targetPlayer.getGameMode().getValue()){
			case(0):
				// Switch to 1
				targetPlayer.setGameMode(this.creativeMode);
				if (this.isInVerbose())
				{
					verboseModeString = targetPlayer.getName() + ": " + this.switchTo + this.switchTo1 + this.getVerboseModeLocationString(targetPlayer) + this.switchBy + callingPlayer.getName();
					this.doLog(verboseModeString);
					Player onlinePlayers[] = this.getServer().getOnlinePlayers();
					for(int cnt = 0; cnt < onlinePlayers.length; cnt++){
						if (onlinePlayers[cnt].isOp()){
							onlinePlayers[cnt].sendMessage(verboseModeString);
						}
					}
				}
				// Notify player of switch to 1
				targetPlayer.sendMessage(switchTo + switchTo1);
				return true;
			case(1):
				// Switch to 0
				targetPlayer.setGameMode(this.survivalMode);
				if (this.isInVerbose()){
					verboseModeString = targetPlayer.getName() + ": " + this.switchTo + this.switchTo0 + this.getVerboseModeLocationString(targetPlayer) + this.switchBy + callingPlayer.getName();
					this.doLog(verboseModeString);
					Player onlinePlayers[] = this.getServer().getOnlinePlayers();
					
					for(int cnt = 0; cnt < onlinePlayers.length; cnt++){
						if (onlinePlayers[cnt].isOp()){
							onlinePlayers[cnt].sendMessage(verboseModeString);
						}
					}
				}
				// Notify player of switch to 0
				targetPlayer.sendMessage(switchTo + switchTo0);
				return true;
		}
		return false;
	}
	private boolean flipPlayersGameMode(Player targetPlayer){
		String verboseModeString = "";
			
		// Determine game mode and flip it.
		switch(targetPlayer.getGameMode().getValue()){
			case(0):
				// Switch to 1
				targetPlayer.setGameMode(this.creativeMode);
				if (this.isInVerbose())
				{
					verboseModeString = targetPlayer.getName() + ": " + this.switchTo + this.switchTo1 + this.getVerboseModeLocationString(targetPlayer);
					this.doLog(verboseModeString);
					Player onlinePlayers[] = this.getServer().getOnlinePlayers();
					for(int cnt = 0; cnt < onlinePlayers.length; cnt++){
						if (onlinePlayers[cnt].isOp()){
							onlinePlayers[cnt].sendMessage(verboseModeString);
						}
					}
				}
				// Notify player of switch to 1
				targetPlayer.sendMessage(switchTo + switchTo1);
				return true;
			case(1):
				// Switch to 0
				targetPlayer.setGameMode(this.survivalMode);
				if (this.isInVerbose()){
					verboseModeString = targetPlayer.getName() + ": " + this.switchTo + this.switchTo0 + this.getVerboseModeLocationString(targetPlayer);
					this.doLog(verboseModeString);
					Player onlinePlayers[] = this.getServer().getOnlinePlayers();
					
					for(int cnt = 0; cnt < onlinePlayers.length; cnt++){
						if (onlinePlayers[cnt].isOp()){
							onlinePlayers[cnt].sendMessage(verboseModeString);
						}
					}
				}
				// Notify player of switch to 0
				targetPlayer.sendMessage(switchTo + switchTo0);
				return true;
		}
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
    	this.toggleGameModePermission = this.myConfig.getString("TGM.Permissions.SelfNode",this.toggleGameModePermission);
    	this.toggleOthersGameModePermission = this.myConfig.getString("TGM.Permissions.OthersNode",this.toggleOthersGameModePermission);
    	this.toggleAdminPermission = this.myConfig.getString("TGM.Permissions.AdminNode",this.toggleAdminPermission);
    	
    	// Message Strings
    	this.switchTo = this.myConfig.getString("TGM.ToggledMessage.Base",this.switchTo);
    	this.switchTo0 = this.myConfig.getString("TGM.ToggledMessage.Survival",this.switchTo0);
    	this.switchTo1 = this.myConfig.getString("TGM.ToggledMessage.Creative",this.switchTo1);
    	this.notPermissioned = this.myConfig.getString("TGM.NoPermissionsMessage", this.notPermissioned);
    	this.noSuchPlayer = this.myConfig.getString("TGM.NoSuchPlayerMessage", this.noSuchPlayer);
    	this.switchBy = this.myConfig.getString("TGM.SwitchedBy", this.switchBy);
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
	
	 private boolean hasPermissions(Player player, String thePermissionToCheckFor) {

            if(player.hasPermission(thePermissionToCheckFor) || player.isOp()) {
                return true;
            }
            // PermissionsEx check
            else if (this.getServer().getPluginManager().isPluginEnabled("PermissionsEx")){
                PermissionManager PExInterface = PermissionsEx.getPermissionManager();

                if (PExInterface.has(player, thePermissionToCheckFor))
                    return true;
            }

        return false;
   }
	 
 }
