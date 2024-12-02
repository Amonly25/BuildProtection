package com.ar.askgaming.buildprotection.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ar.askgaming.buildprotection.BuildProtection;

public class PlayerQuitListener implements Listener{

    private BuildProtection plugin;
    public PlayerQuitListener(BuildProtection main){
        plugin = main;
    }

    @EventHandler()
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (plugin.getProtectionsManager().getPlayersInEditMode().containsKey(p)){
            plugin.getProtectionsManager().getPlayersInEditMode().remove(p);
        }
    }
}
