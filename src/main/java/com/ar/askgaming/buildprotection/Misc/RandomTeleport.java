package com.ar.askgaming.buildprotection.Misc;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.buildprotection.Area;
import com.ar.askgaming.buildprotection.BuildProtection;

public class RandomTeleport {

    private BuildProtection plugin;
	private File file;
	private FileConfiguration config;
    public RandomTeleport(BuildProtection main){
        plugin = main;

		file = new File(plugin.getDataFolder() + "/protections", "rtp_cooldowns.yml");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		config = new YamlConfiguration();

		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveCooldown(Player player) {
		config.set(player.getUniqueId().toString(), System.currentTimeMillis());

		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		} 
			
    }

    public void send(Player player) {

		if (config.contains(player.getUniqueId().toString()) && (!player.hasPermission("buildprotection.rtp.cooldown.bypass"))) {
			long last = config.getLong(player.getUniqueId().toString());
			long current = System.currentTimeMillis();
			long diff = current - last;
			long cooldown = plugin.getConfig().getInt("random_teleport.cooldown",1440) * 1000 * 60;
			long remaining = cooldown - diff;
			if (remaining > 0) {
				player.sendMessage(plugin.getDataHandler().getLang("rtp.cooldown", player).replace("%time%", String.valueOf(remaining / 1000/60)));
				return;
			}
		}
				
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
					saveCooldown(p);
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
