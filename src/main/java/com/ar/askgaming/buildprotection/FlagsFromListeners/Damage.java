package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Damage implements Listener{

    private BuildProtection plugin;
    public Damage(BuildProtection main){
        plugin = main;
    }
    @EventHandler()
    public void onEntityDamage(EntityDamageEvent event){

        if (event.getEntity() instanceof Enemy){
            return;
        }

        Location l = event.getEntity().getLocation();
        
        if (event.getEntity() instanceof Player){
            if (plugin.getProtectionFlags().isFlagEnabled(FlagType.PVP, l)){
                return;
            }

        }
        
        if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.ENTITY_DAMAGE, l)){
            event.setCancelled(true);
 
        }
    }
}
