package com.ar.askgaming.buildprotection.Misc;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.buildprotection.Area;
import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Commands;

import net.md_5.bungee.api.ChatColor;

public class RandomTeleport {

    private BuildProtection plugin;
    public RandomTeleport(BuildProtection main){
        plugin = main;
    }

    public void send(Player player) {
				
		player.sendMessage(plugin.getDataHandler().getLang("rtp.searching", player));
        
		Location loc = null;

        for (int i = 0; i < 10; i++) {
            loc = searchLocation(player);
            if (loc != null) {
                teleport(player, loc);
                return;
            }
        }
    
        player.sendMessage(plugin.getDataHandler().getLang("rtp.failed", player));
		
	}
		
	private Location searchLocation(Player p) {
		
		double max = plugin.getConfig().getDouble("random_teleport.max_x_z"), min = plugin.getConfig().getDouble("random_teleport.min_x_z");
		
		double randX = Math.floor(Math.random() * (max - min + 1)) + min, 
			   randZ = Math.floor(Math.random() * (max - min + 1)) + min,
			   randY = 100;

		World world = p.getWorld();
		
		Location loc = new Location(world, randX, randY, randZ);
		Location modified = new Location(world, randX, world.getHighestBlockYAt(loc)+1, randZ);
				
		Area area = plugin.getProtectionsManager().getAreaByLocation(modified);
        
        if (area == null) {
        	return modified;
        }
        return null;
	}
	
	private void teleport(Player p, Location loc) {
		
		final int playerZ = p.getLocation().getBlockZ(), playerX = p.getLocation().getBlockX();
		p.sendMessage(plugin.getDataHandler().getLang("rtp.teleport", p));
		loc.getChunk().load();
		new BukkitRunnable() {
			int count = 5;
			
	    	@Override
	    	public void run() {	      
	    		if (count == 0) {
					p.teleport(loc);
	                cancel(); 
	                return;
	    		}	    	    	                                    	    	                        
	            if (playerZ != p.getLocation().getBlockZ() || playerX != p.getLocation().getBlockX()){
	            	p.sendMessage(plugin.getDataHandler().getLang("rtp.cancel", p));
	            	cancel();
	            	return;
	            }
	            count--;  
	    	}
		}.runTaskTimer(plugin, 0L, 20L); 
	}

}
