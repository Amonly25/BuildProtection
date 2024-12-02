package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Teleport implements Listener {
    
    private BuildProtection plugin;
    public Teleport(BuildProtection main){
        plugin = main;
    }
    @EventHandler()
    public void onTeleport(PlayerTeleportEvent event){
        Player p = event.getPlayer();
        Location l = event.getTo();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.TELEPORT, p, l)){
            TeleportCause cause = event.getCause();
            for (String s : plugin.getProtectionFlags().getTeleportFlagsInteracts()){
                if (cause.toString().equals(s)){
                    if (plugin.getProtectionFlags().hasPermission(FlagType.TELEPORT, p, l)){
                        return;
                    }
                    p.sendMessage(plugin.getDataHandler().getLang("flags.teleport", p));
                    event.setCancelled(true);
                    
                }
            }
        }
    } 
}
