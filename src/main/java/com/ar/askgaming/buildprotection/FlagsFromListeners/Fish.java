package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags.FlagType;

public class Fish implements Listener{

    private BuildProtection plugin;   
    public Fish(BuildProtection main){
        plugin = main;
    }

    @EventHandler()
    public void onFish(PlayerFishEvent event){
        Player p = event.getPlayer();

        if (event.getCaught() == null) return;

        Location l = event.getHook().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.FISH, p, l)){
            p.sendMessage(plugin.getLangManager().get("flags.fish", p));
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onBucketFish(PlayerBucketEntityEvent event){
        Player p = event.getPlayer();

        Location l = event.getEntity().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.FISH, p, l)){
            p.sendMessage(plugin.getLangManager().get("flags.fish", p));
            event.setCancelled(true);
        }
    }

}
