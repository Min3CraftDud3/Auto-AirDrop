package me.Vedronex.AAD;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.Vedronex.AAD.AutoAirDrop;

public class Commands implements CommandExecutor{

	public AutoAirDrop plugin;
	public Commands(AutoAirDrop plugin){this.plugin = plugin;}
	
	public boolean onCommand(CommandSender p, Command cmd, String label, String[] args){
		
		String perm = ChatColor.RED + "You don't have the permission to do this command";
		
		if(!p.hasPermission("ad.admin") && !p.hasPermission("ad.force")){p.sendMessage(perm);return true;}
		
		String use = ChatColor.RED + "Usage: "  + ChatColor.AQUA + "[" + ChatColor.DARK_GREEN +  "/ad <argument>" + ChatColor.AQUA + "]";
		String help = ChatColor.RED + "Type " + ChatColor.AQUA + "[" + ChatColor.DARK_GREEN +  "/ad help" + ChatColor.AQUA + "] " + ChatColor.RED +  "for help";
		String wrong = ChatColor.RED + "Something wrong in the Config.yml File";
		
		//No argument
		if (args.length == 0){p.sendMessage(use);p.sendMessage(help);return true;}

		//Plugin On
		else if (args[0].equalsIgnoreCase("on")){if(p.hasPermission("ad.admin")){if(plugin.ALLOW){if(!plugin.PluginOn)
		{plugin.PluginOn = true;p.sendMessage("Auto AirDrop now Enabled");}
		else{p.sendMessage("Auto AirDrop already Enabled");}}else{p.sendMessage(wrong);}}else{p.sendMessage(perm);}}

		//Plugin Off
		else if (args[0].equalsIgnoreCase("off")){if(p.hasPermission("ad.admin")){if(plugin.PluginOn)
		{plugin.PluginOn = false;p.sendMessage("Auto AirDrop now Disabled");}
		else{p.sendMessage("Auto AirDrop already Disabled");}}else{p.sendMessage(perm);}}

		//Reset
		else if (args[0].equalsIgnoreCase("reset")){if(p.hasPermission("ad.admin"))
		{plugin.saveDefaultConfig();plugin.loadConfig();p.sendMessage("Auto AirDrop config file reset");}else{p.sendMessage(perm);}}	

		//Force
		else if (args[0].equalsIgnoreCase("force") || args[0].equalsIgnoreCase("call")){if(p.hasPermission("ad.force")){if(plugin.ALLOW){plugin.run();
		}else{p.sendMessage(wrong);}}else{p.sendMessage(perm);}}

		//Help
		else if (args[0].equalsIgnoreCase("help")){
		p.sendMessage(ChatColor.RED + "=============== " + ChatColor.GREEN + "Auto AirDrop Help " + ChatColor.RED + "===============");
		
		if(p.hasPermission("ad.admin")){
			p.sendMessage(ChatColor.GREEN + "[/ad on]: " + ChatColor.BLUE + "Enable the Auto AirDrops");
			p.sendMessage(ChatColor.GREEN + "[/ad off]: " + ChatColor.BLUE + "Disable the Auto AirDrops");
			p.sendMessage(ChatColor.GREEN + "[/ad reset]: " + ChatColor.BLUE + "Reset the config.yml");
		}
		if(p.hasPermission("ad.force")){p.sendMessage(ChatColor.GREEN + "[/ad force]: " + ChatColor.BLUE + "Force an airdrop");}
		p.sendMessage(ChatColor.RED + "===============================================");
		}
		
		//No good arguments
		else{p.sendMessage(use);p.sendMessage(help);}return true;

	}
}
