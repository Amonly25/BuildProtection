package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.ProtectionFlags.FlagType;

public class Interact implements Listener {

    private Main plugin;
    public Interact(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onBlockInteract(PlayerInteractEvent event){
        Player p = event.getPlayer();

        if (event.getClickedBlock() == null) return;

        Location l = event.getClickedBlock().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, l)){
            p.sendMessage(plugin.getDataHandler().getLang("flags.interact", p));
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onBlockInteract(PlayerInteractEntityEvent event){
        Player p = event.getPlayer();

        Location l = event.getRightClicked().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, l)){
            p.sendMessage(plugin.getDataHandler().getLang("flags.interact", p));
            event.setCancelled(true);
        }
    }

}
