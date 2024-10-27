package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.ProtectionFlags.FlagType;

public class Break implements Listener {
    
    private Main plugin;
    public Break(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onBlockBreak(BlockBreakEvent event){
        Player p = event.getPlayer();
        Location l = event.getBlock().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.BREAK, p, l)){
            p.sendMessage("No tienes permiso para romper este bloque.");
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onHangingItemBreak(EntityDamageEvent event){
        if (event.getEntity() instanceof Hanging){
            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.BREAK, event.getEntity().getLocation())){
                event.setCancelled(true);
            }
        }

    }
}
