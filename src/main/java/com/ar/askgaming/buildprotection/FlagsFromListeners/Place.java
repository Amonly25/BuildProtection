package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags.FlagType;

public class Place implements Listener {
    
    private BuildProtection plugin;
    public Place(BuildProtection main){
        plugin = main;
    }
    @EventHandler()
    public void onBlockPlace(BlockPlaceEvent event){
        Player p = event.getPlayer();
        Location l = event.getBlock().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.PLACE, p, l)){
            p.sendMessage(plugin.getLangManager().get("flags.place", p));
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onHangingPlace(HangingPlaceEvent event){
        Player p = event.getPlayer();
        Location l = event.getBlock().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.PLACE, p, l)){
            p.sendMessage(plugin.getLangManager().get("flags.place", p));
            event.setCancelled(true);
        }
    }
}
