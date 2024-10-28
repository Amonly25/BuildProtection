package com.ar.askgaming.buildprotection.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Protection;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerMoveListener implements Listener {
    
    private Main plugin;
    public PlayerMoveListener(Main main){
        plugin = main;
    }

    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent event){
        
        Player p = event.getPlayer();
        //Revisar el mejor metodo
        //No mondar de forma constante el mensaje

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Location l = p.getLocation();
        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(l);
        if (prote != null){
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(prote.getMessage()));
        }
    }
}
