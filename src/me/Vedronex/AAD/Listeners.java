package me.Vedronex.AAD;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import me.Vedronex.AAD.AutoAirDrop;

public class Listeners implements Listener{

	public AutoAirDrop plugin;
	public Listeners(AutoAirDrop plugin){this.plugin = plugin;}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent e){
		Block b = e.getBlock();
		if(b.getType() == Material.CHEST){
			String x = Integer.toString(b.getX()) + "-" + Integer.toString(b.getY()) + "-" + Integer.toString(b.getZ());
			if(plugin.LocList.contains(x)){plugin.LocList.remove(x);return;}	
	}}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent e){
		if(e.getInventory().getHolder() instanceof Chest){
			Chest c = (Chest)e.getInventory().getHolder();
			Block b = c.getBlock();
			String loc = Integer.toString(b.getX()) + "-" + Integer.toString(b.getY()) + "-" + Integer.toString(b.getZ());
			if(plugin.LocList.contains(loc)){
				ItemStack items[] = c.getInventory().getContents();
				int x = 0;
				for (ItemStack item: items){if(item != null){x = 1;}}
				if(x == 0){plugin.LocList.remove(loc);if(plugin.DEL){b.setType(Material.AIR);}
	}}}}
}