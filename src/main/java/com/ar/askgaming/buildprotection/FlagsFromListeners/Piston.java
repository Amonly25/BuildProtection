package com.ar.askgaming.buildprotection.FlagsFromListeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags.FlagType;

public class Piston implements Listener{

    private BuildProtection plugin;
    public Piston(BuildProtection main){
        plugin = main;
    }
    @EventHandler()
    public void onPistonExtend(BlockPistonExtendEvent event){

        List<Block> blocks = event.getBlocks();
        
        for (Block b : blocks){
            Location l = b.getLocation();
            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.PISTON, l)){
                event.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler()
    public void onPistonExtend(BlockPistonRetractEvent event){

        List<Block> blocks = event.getBlocks();
        
        for (Block b : blocks){
            Location l = b.getLocation();
            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.PISTON, l)){
                event.setCancelled(true);
                return;
            }
        }
    }
}
