package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Teleport implements Listener {
    
    private Main plugin;
    public Teleport(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onTeleport(PlayerTeleportEvent event){
        Player p = event.getPlayer();
        Location l = event.getTo();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.TELEPORT, p, l)){
            p.sendMessage(plugin.getDataHandler().getLang("flags.teleport", p));
            event.setCancelled(true);
        }
    } 
}
