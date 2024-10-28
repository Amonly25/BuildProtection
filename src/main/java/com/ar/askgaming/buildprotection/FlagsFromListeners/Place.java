package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.ProtectionFlags.FlagType;

public class Place implements Listener {
    
    private Main plugin;
    public Place(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onBlockPlace(BlockPlaceEvent event){
        Player p = event.getPlayer();
        Location l = event.getBlock().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.PLACE, p, l)){
            p.sendMessage("No tienes permiso para colocar bloques en este lugar.");
            event.setCancelled(true);
        }
    }
}
