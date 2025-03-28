package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags.FlagType;

public class Ignite implements Listener{

    private BuildProtection plugin;
    public Ignite(BuildProtection main){
        plugin = main;
    }
    @EventHandler()
    public void onBlockIgnite(BlockIgniteEvent event){

        Location l = event.getBlock().getLocation();
        
        if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.IGNITE, l)){
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onBlockBurn(BlockBurnEvent event){

        Location l = event.getBlock().getLocation();
        
        if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.IGNITE, l)){
            event.setCancelled(true);
        }
    }
}
