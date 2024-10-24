package com.ar.askgaming.buildprotection.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ar.askgaming.buildprotection.Main;

public class PlayerQuitListener implements Listener{

    private Main plugin;
    public PlayerQuitListener(Main main){
        plugin = main;
    }

    @EventHandler()
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (plugin.playersInEditMode.containsKey(p)){
            plugin.playersInEditMode.get(p).cancel();
            plugin.playersInEditMode.remove(p);
        }
    }
}
