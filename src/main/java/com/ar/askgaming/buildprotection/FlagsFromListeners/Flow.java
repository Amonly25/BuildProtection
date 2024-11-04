package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Flow implements Listener{

    private Main plugin;
    public Flow(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onBlockFromTo(BlockFromToEvent event){
        try {
            Location l = event.getBlock().getLocation();
            
            if (event.getBlock().isLiquid()) {
                if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.FLOW, l)){
                    event.setCancelled(true);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
