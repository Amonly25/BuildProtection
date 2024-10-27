package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.ProtectionFlags.FlagType;

public class Ride implements Listener{

    private Main plugin;
    public Ride(Main main){
        plugin = main;
    }

    @EventHandler()
    public void onRide(EntityMountEvent event){

        if (!(event.getEntity() instanceof Player)) {
            
            Player p = (Player) event.getEntity();
            Location l = p.getLocation();
            if (!plugin.getProtectionFlags().hasPermission(FlagType.RIDE, p,l)){
                p.sendMessage("No tienes permiso para montar en este lugar.");
                event.setCancelled(true);
            }
        } else {

            Location l = event.getMount().getLocation();
            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.RIDE,l)){
                event.setCancelled(true);
            }
        }
    }   
}
