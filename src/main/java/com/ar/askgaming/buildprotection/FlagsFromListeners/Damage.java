package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Damage implements Listener{

    private Main plugin;
    public Damage(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onEntityDamage(EntityDamageEvent event){

        if (event.getEntity() instanceof Monster){
            return;
        }

        Location l = event.getEntity().getLocation();
        
        
        if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.DAMAGE, l)){
            event.setCancelled(true);
 
        }
    }
}
