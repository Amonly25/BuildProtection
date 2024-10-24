package com.ar.askgaming.buildprotection.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.ar.askgaming.buildprotection.Main;

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

        plugin.getAllProtections.forEach(prote -> {
            if (prote.isInsideArea(event.getTo())){
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Bienvenido a " + prote.getName()));          
            }
        });
    }
}
