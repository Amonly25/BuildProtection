package com.ar.askgaming.buildprotection.Misc;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.Area;

public class RandomTeleport {

    private BuildProtection plugin;
	private File file;
	private FileConfiguration config;

	private HashMap<UUID, Long> cooldowns = new HashMap<>();
	private HashMap<UUID, Location> lastLocation = new HashMap<>();

    public RandomTeleport(BuildProtection main){
        plugin = main;

		file = new File(plugin.getDataFolder(), "rtp_cooldowns.yml");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
	}
	public void loadCooldowns() {
		Set<String> keys = config.getKeys(false);
		if (keys.isEmpty()) {
			return;
		}
		for (String key : config.getKeys(false)) {
			UUID uuid = UUID.fromString(key);
			long cooldown = config.getLong(key + ".cooldown");
			Location loc = (Location) config.get(key + ".last");
			cooldowns.put(uuid, cooldown);
			lastLocation.put(uuid, loc);
		}
	}

	public void saveCooldown(Player player) {
		cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
		lastLocation.put(player.getUniqueId(), player.getLocation());
		config.set(player.getUniqueId().toString()+".cooldown", System.currentTimeMillis());
		config.set(player.getUniqueId().toString() + ".last", player.getLocation());


		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		} 
			
    }

    public boolean canTeleport(Player player) {

		if (player.hasPermission("buildprotection.rtp.cooldown.bypass")){
			return true;
		}

		long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
		long current = System.currentTimeMillis();
		long diff = current - last;
		long cooldown = plugin.getConfig().getInt("random_teleport.cooldown",1440) * 1000 * 60;
		long remaining = cooldown - diff;
		if (remaining > 0) {
			player.sendMessage(plugin.getLangManager().getLang("rtp.cooldown", player).replace("%time%", String.valueOf(remaining / 1000/60)));
			if (plugin.getConfig().getBoolean("random_teleport.last_location_on_cooldown", true)) {
				if (lastLocation.containsKey(player.getUniqueId())) {
					player.sendMessage(plugin.getLangManager().getLang("rtp.last_location", player));
				}
			}
			return false;
		}
		return true;			
	}
	public void sendToLastLocation(Player player) {
		if (!plugin.getConfig().getBoolean("random_teleport.last_location_on_cooldown", true)) {
			player.sendMessage(plugin.getLangManager().getLang("rtp.disabled", player));
			return;
		}
		if (lastLocation.containsKey(player.getUniqueId())) {
			Location loc = lastLocation.get(player.getUniqueId());
			teleport(player, loc);
		} else {
			sendToLastLocation(player);
		}
	}

	public void sendToRandomLocation(Player player) {
		if (!canTeleport(player)) {
			return;
		}
		player.sendMessage(plugin.getLangManager().getLang("rtp.searching", player));
        
		Location loc = null;

        for (int i = 0; i < 10; i++) {
            loc = searchLocation(player);
            if (loc != null) {
                teleport(player, loc);
                return;
            }
        }
    
        player.sendMessage(plugin.getLangManager().getLang("rtp.failed", player));
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
		p.sendMessage(plugin.getLangManager().getLang("rtp.teleport", p));
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
	            	p.sendMessage(plugin.getLangManager().getLang("rtp.cancel", p));
	            	cancel();
	            	return;
	            }
	            count--;  
	    	}
		}.runTaskTimer(plugin, 0L, 20L); 
	}

}
