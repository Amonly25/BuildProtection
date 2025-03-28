package com.ar.askgaming.buildprotection.Misc;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.Area;

public class RandomTeleport {

    private final BuildProtection plugin;
	private final File file;
	private FileConfiguration config;

	private boolean lastLocation;
	private long cooldown;
	private double minXZ, maxXZ;

    public RandomTeleport(BuildProtection main){
        plugin = main;

		file = new File(plugin.getDataFolder(), "rtp_cooldowns.yml");

		load();
	}
	public void load(){
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);

		lastLocation = plugin.getConfig().getBoolean("random_teleport.last_location_on_cooldown", true);
		cooldown = plugin.getConfig().getInt("random_teleport.cooldown", 1440);
		minXZ = plugin.getConfig().getDouble("random_teleport.min_x_z", 1000);
		maxXZ = plugin.getConfig().getDouble("random_teleport.max_x_z", 1000);
	}

	private String getLang(String path, Player player) {
		return plugin.getLangManager().get(path, player);
	}

	public void saveCooldown(Player player) {
		String key = player.getUniqueId().toString();

		config.set(key + ".cooldown", System.currentTimeMillis());
		config.set(key + ".last", player.getLocation());

		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		} 
			
    }
	//#region CanTelepor
    public boolean canTeleport(Player player) {

		if (player.hasPermission("buildprotection.rtp.cooldown.bypass")){
			return true;
		}

		long last = config.getLong(player.getUniqueId().toString() + ".cooldown", 0);
		long current = System.currentTimeMillis();
		long diff = current - last;
		long remaining = (this.cooldown * 1000 * 60) - diff;

		if (remaining > 0) {
			player.sendMessage(getLang("rtp.cooldown", player).replace("%time%", String.valueOf(remaining / 1000/60)));
			if (lastLocation) {
				if (config.getLocation(player.getUniqueId().toString() + ".last") != null) {
					player.sendMessage(getLang("rtp.last_location", player));
				}
			}
			return false;
		}
		return true;			
	}
	//#region sendToLast
	public void sendToLastLocation(Player player) {
		if (!lastLocation) {
			player.sendMessage(getLang("rtp.disabled", player));
			return;
		}
		Location loc = config.getLocation(player.getUniqueId().toString() + ".last");
		if (loc != null) teleport(player, loc);
	}
	//#region send
	public void sendToRandomLocation(Player player) {
		if (!canTeleport(player)) {
			return;
		}
		player.sendMessage(getLang("rtp.searching", player));
        
		Location loc = null;

        for (int i = 0; i < 10; i++) {
            loc = searchLocation(player);
            if (loc != null) {
                teleport(player, loc);
                return;
            }
        }
    
        player.sendMessage(getLang("rtp.failed", player));
	}
	//#region search
	private Location searchLocation(Player p) {
				
		double randX = Math.floor(Math.random() * (maxXZ - minXZ + 1)) + minXZ, 
			   randZ = Math.floor(Math.random() * (maxXZ - minXZ + 1)) + minXZ,
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
	//#region teleport
	private void teleport(Player p, Location loc) {
		
		final int playerZ = p.getLocation().getBlockZ(), playerX = p.getLocation().getBlockX();
		p.sendMessage(getLang("rtp.teleport", p));
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
	            	p.sendMessage(getLang("rtp.cancel", p));
	            	cancel();
	            	return;
	            }
	            count--;  
	    	}
		}.runTaskTimer(plugin, 0L, 20L); 
	}

}
