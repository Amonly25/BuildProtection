package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags.FlagType;

public class EntitySpawn implements Listener{

    private BuildProtection plugin;
    public EntitySpawn(BuildProtection main){
        plugin = main;
    }
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        
        Entity entity = event.getEntity();
        
        if (entity instanceof Player) {

        } else if (entity instanceof Animals) {

        } else if (entity instanceof Enemy) {
            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.ENEMY_SPAWN, entity.getLocation())) {
                event.setCancelled(true);
            }
        }
        // else {
        //     if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.ENTITY_SPAWN, entity.getLocation())) {
        //         event.setCancelled(true);
        //     }
        // }
    }

}
