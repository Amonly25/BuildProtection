package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags.FlagType;

public class Ride implements Listener{

    private BuildProtection plugin;
    public Ride(BuildProtection main){
        plugin = main;
    }

    //Interact overides this event

    @EventHandler()
    public void onRide(EntityMountEvent event){

        if (event.getEntity() instanceof TextDisplay){
            return;
        }
        
        if (event.getEntity() instanceof Player){
            
            Player p = (Player) event.getEntity();
            Location l = p.getLocation();
            if (!plugin.getProtectionFlags().hasPermission(FlagType.RIDE, p,l)){
                p.sendMessage(plugin.getLangManager().getLang("flags.ride", p));
                event.setCancelled(true);
            }
        } else if (!(event.getMount() instanceof Player)){

            Location l = event.getMount().getLocation();
            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.RIDE,l)){
                event.setCancelled(true);
            }
        }
    }   
}
