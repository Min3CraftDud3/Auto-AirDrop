package me.Vedronex.AAD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoAirDrop extends JavaPlugin{

	File locFile;
	FileConfiguration locYml = new YamlConfiguration();
	
	public boolean PluginOn, ALLOW, ModArea, ModLoc, DEL;
	public List<String> LocList;
	public String World;
	public int Delay;
	public ArrayList<String> ADL = new ArrayList<String>();
	
	public void onEnable()
	{
 		locFile = new File(getDataFolder(), "locations.yml");
 		if(!locFile.exists()){locFile.getParentFile().mkdirs();copy(getResource("locations.yml"), locFile);}
 		try{locYml.load(this.locFile);}catch (Exception e){e.printStackTrace();}
		if(!new File(File.separator + getDataFolder(), "config.yml").exists()){saveDefaultConfig();}
		loadConfig();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new Listeners(this), this);
		getCommand("ad").setExecutor(new Commands(this));
		runTask();
	}	
	public void onDisable() {
 		if(!locFile.exists()){locFile.getParentFile().mkdirs();copy(getResource("locations.yml"), locFile);}
 		locYml.set("Locations", LocList);
 		try {locYml.save(locFile);} catch (IOException e) {e.printStackTrace();}
 	}
 	
	private void copy(InputStream in, File file) {
 		try
 	    {
 	      OutputStream out = new FileOutputStream(file);
 	      byte[] buf = new byte[1024];
 	      int len;
 	      while ((len = in.read(buf)) > 0)
 	      {
 	        out.write(buf, 0, len);
 	      }
 	      out.close();
 	      in.close();
 	    }
 	    catch (Exception e)
 	    {
 	      e.printStackTrace();
 	    }
		
	}
 	
 	public class AutoAD extends BukkitRunnable
	{
		public AutoAD() {}
		public void run(){if(PluginOn && ALLOW){AutoAirDrop.this.run();}}
	}	
 	public void runTask(){
 		if(ALLOW && PluginOn){new AutoAD().runTaskTimer(this, Delay, Delay);}
 	}
 	
	public void loadConfig()
	{
		FileConfiguration c = getConfig();
		PluginOn = c.getBoolean("Enabled");
		World = c.getString("World");
		Delay = c.getInt("Delay");
		DEL = c.getBoolean("Auto Delete");
		ModArea = c.getBoolean("AirDrop Mode.Areas.Enabled");
		ModLoc = c.getBoolean("AirDrop Mode.Locations.Enabled");
		LocList = locYml.getStringList("Locations");
		
		Server s = getServer();
		ConsoleCommandSender console = s.getConsoleSender();
		String error = ChatColor.RED + "============ Error in " + ChatColor.AQUA + "Auto AirDrop " + ChatColor.RED + "config.yml File ============";
		String st = ChatColor.YELLOW + " ";
		String fi = ChatColor.RED + "===============================================================";
		ALLOW = true;
		if(!getServer().getWorlds().contains(getServer().getWorld(World))){ALLOW = false;PluginOn = false;console.sendMessage(error);console.sendMessage(st + "World not found.");console.sendMessage(st + "Auto AirDrop Disabled.");console.sendMessage(fi);}
		if(ALLOW && Delay < 1 || ALLOW && Delay != (int)Delay){ALLOW = false;PluginOn = false;console.sendMessage(error);console.sendMessage(st + "Wrong Delay.");console.sendMessage(st + "Auto AirDrop Disabled.");console.sendMessage(fi);}
		if(ALLOW && ModArea == ModLoc && ModArea){ALLOW = false;PluginOn = false;console.sendMessage(error);console.sendMessage(st + "Both AirDrop Mode are Enabled. Disable One");console.sendMessage(st + "Auto AirDrop Disabled.");console.sendMessage(fi);}
		if(ALLOW && ModArea == ModLoc && !ModArea){ALLOW = false;PluginOn = false;console.sendMessage(error);console.sendMessage(st + "Both AirDrop Mode are Disabled. Enable One");console.sendMessage(st + "Auto AirDrop Disabled.");console.sendMessage(fi);}
		if(ALLOW){Delay *= 1200;}
	}
	
	@SuppressWarnings("deprecation")
	public void run(){

		FileConfiguration c = getConfig();
		Server s = getServer();
		World world = s.getWorld(World);
		ConsoleCommandSender console = s.getConsoleSender();
		String error = ChatColor.RED + "============ Error in " + ChatColor.AQUA + "Auto AirDrop " + ChatColor.RED + "config.yml File ============";
		String st = ChatColor.YELLOW + " ";
		String fi = ChatColor.RED + "================================================================";

		Random r = new Random();
		int X = 0;
		int Z = 0;
		if(ModArea){
			ADL.clear();
			int i = 1;
			while (i != 0){
				String name = "Area " + Integer.toString(i);
				String A = "AirDrop Mode.Areas." + name;
				if(c.contains(A) && c.contains(A + ".Enabled")){
					Boolean B = c.getBoolean(A + ".Enabled");
					if(B){ADL.add(name);}i++;
				}else{i = 0;}
			}if(ADL.isEmpty()){ALLOW = false;PluginOn = false;
				console.sendMessage(error);
				console.sendMessage(st + "No AirDrop Areas found. Enable or add Areas");
				console.sendMessage(st + "Auto AirDrop Disabled.");
				console.sendMessage(fi);return;}
			
			int index = r.nextInt(ADL.size());
			String name = ADL.get(index);
			String S = "AirDrop Mode.Areas." + name;
			
			if(!c.contains(S + ".Maximum X") || !c.contains(S + ".Minimum X") || !c.contains(S + ".Maximum Z") || !c.contains(S + ".Minimum Z")){
				ALLOW = false;PluginOn = false;
				console.sendMessage(error);
				console.sendMessage(st + "Required settings for " + name);
				console.sendMessage(st + "Auto AirDrop Disabled.");
				console.sendMessage(fi);return;}
			Integer MaxX, MinX, MaxZ, MinZ;
			MaxX = c.getInt(S + ".Maximum X");
			MinX = c.getInt(S + ".Minimum X");
			MaxZ = c.getInt(S + ".Maximum Z");
			MinZ = c.getInt(S + ".Minimum Z");
			if(MaxX < MinX || MaxX == MinX){
				ALLOW = false;PluginOn = false;
				console.sendMessage(error);
				console.sendMessage(ChatColor.AQUA + " [" + name + "]" + st + "Maximum X can't be lower or equal to Minimum X");
				console.sendMessage(st + "Auto AirDrop Disabled.");
				console.sendMessage(fi);return;}
			if(MaxZ < MinZ || MaxZ == MinZ){
				ALLOW = false;PluginOn = false;
				console.sendMessage(error);
				console.sendMessage(ChatColor.AQUA + " [" + name + "]" + st + "Maximum Z can't be lower or equal to Minimum Z");
				console.sendMessage(st + "Auto AirDrop Disabled.");
				console.sendMessage(fi);return;}
			X = r.nextInt(MaxX-MinX) + MinX;
			Z = r.nextInt(MaxZ-MinZ) + MinZ;
		}else if(ModLoc){
			ADL.clear();
			int i = 1;
			while (i != 0){
				String name = "Location " + Integer.toString(i);
				String S = "AirDrop Mode.Locations." + name;
				if(c.contains(S) && c.contains(S + ".Enabled")){
					Boolean B = c.getBoolean(S + ".Enabled");
					if(B){ADL.add(name);}i++;
				}else{i = 0;}
			}if(ADL.isEmpty()){ALLOW = false;PluginOn = false;
			console.sendMessage(error);
			console.sendMessage(st + "No AirDrop Locations found. Enable or add Locations");
			console.sendMessage(st + "Auto AirDrop Disabled.");
			console.sendMessage(fi);return;}
			int index = r.nextInt(ADL.size());
			String name = ADL.get(index);
			String S = "AirDrop Mode.Locations." + name + ".Coordinates";

			if(!c.contains(S)){ALLOW = false;PluginOn = false;
				console.sendMessage(error);
				console.sendMessage(st + "Required settings for " + ChatColor.AQUA + name);
				console.sendMessage(st + "Auto AirDrop Disabled.");
				console.sendMessage(fi);return;}
			String Loc = c.getString(S);
			if(Loc == null){ALLOW = false;PluginOn = false;
				console.sendMessage(error);
				console.sendMessage(st + "Required settings for " + ChatColor.AQUA + name);
				console.sendMessage(st + "Auto AirDrop Disabled.");
				console.sendMessage(fi);return;}
			String[] co = Loc.split(", ");
			X = Integer.valueOf(co[0]).intValue();
			Z =	Integer.valueOf(co[1]).intValue();
			
		}else{return;}
		ADL.clear();
		int i = 1;
		while (i != 0){
			String name = "PKG " + Integer.toString(i);
			String S = "Packages." + name;
			
			if(c.contains(S) && c.contains(S + ".Enabled")){
				Boolean B = c.getBoolean(S + ".Enabled");
				if(B){ADL.add(name);}i++;
			}else{i = 0;}
		}if(ADL.isEmpty()){ALLOW = false;PluginOn = false;
			console.sendMessage(error);
			console.sendMessage(st + "No Items Packages found. Enable or add Packages");
			console.sendMessage(st + "Auto AirDrop Disabled.");
			console.sendMessage(fi);return;}
		int index = r.nextInt(ADL.size());
		String name = ADL.get(index);
		String S = "Packages." + name + ".Items";
		
		if(!c.contains(S)){ALLOW = false;PluginOn = false;
			console.sendMessage(error);
			console.sendMessage(st + "Required settings for " + ChatColor.AQUA + name);
			console.sendMessage(st + "Auto AirDrop Disabled.");
			console.sendMessage(fi);return;}
		String items = c.getString(S);
		if(items.contains(":")){ALLOW = false;PluginOn = false;
			console.sendMessage(error);
			console.sendMessage(ChatColor.AQUA + " [" + name + "]" + st + "ItemID with [:] is not allowed.");
			console.sendMessage(st + "Auto AirDrop Disabled.");
			console.sendMessage(fi);return;}

		int Y = world.getHighestBlockYAt(X, Z);

		Y += 20;
		Location l = new Location(world, X, Y, Z);
		FallingBlock block = world.spawnFallingBlock(l, Material.CHEST, (byte)0);
		Y -= 20;
		Location NL = new Location(world, X, Y, Z);
		Block NB = world.getBlockAt(NL);
		block.remove();
		NB.setType(Material.CHEST);
		if(items != null){
		Chest chest = (Chest)NL.getBlock().getState();
		Y = chest.getY();
		String[] ia;
		ItemStack is;
		String[] il = items.split(",");
		for (String string1 : il){
			ia = string1.split("-");
			is = new ItemStack(Integer.valueOf(ia[0]).intValue(), Integer.valueOf(ia[1]).intValue());
			chest.getBlockInventory().addItem(new ItemStack[]{is});
		}}
		
		String addloc = Integer.toString(X) + "-" + Integer.toString(Y) + "-" + Integer.toString(Z);LocList.add(addloc);
		s.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + World + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + "AirDrop dropped at: " + ChatColor.AQUA + "X: " + ChatColor.RED + X + ChatColor.AQUA +" Z: " + ChatColor.RED + Z);
	}
	
}
