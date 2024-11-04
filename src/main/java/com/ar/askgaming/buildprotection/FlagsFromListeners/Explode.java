package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Explode implements Listener{

    private Main plugin;
    public Explode(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onEntityExplode(BlockExplodeEvent event){
        if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.EXPLODE, event.getBlock().getLocation())){
            event.setCancelled(true);
  
        }
    }
    @EventHandler()
    public void onEntityExplode(EntityExplodeEvent event){

        for (Block b : event.blockList()){
            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.EXPLODE, b.getLocation())){
                event.setCancelled(true);
                event.getEntity().remove();
                return;
            }
        }
    }
    @EventHandler()
    public void onEntityExplode(EntityChangeBlockEvent event){
       try {
         if (event.getEntity() instanceof Player){
             return;
         }
         if (event.getEntity() instanceof Villager){
             return;
         }
         if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.EXPLODE, event.getBlock().getLocation())){
             event.setCancelled(true);
         }
       } catch (Exception e) {
              e.printStackTrace();
       }

    }
    
}
