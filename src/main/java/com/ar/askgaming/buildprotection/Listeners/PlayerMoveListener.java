package com.ar.askgaming.buildprotection.Listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.ar.askgaming.buildprotection.Area;
import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerMoveListener implements Listener {
    
    private BuildProtection plugin;
    public PlayerMoveListener(BuildProtection main){
        plugin = main;
    }

    private HashMap <Player, Area> lastLocation = new HashMap<>();

    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent event){
        
        Player p = event.getPlayer();
        //Revisar el mejor metodo
        //No mondar de forma constante el mensaje
        if (plugin.getConfig().getBoolean("options.no_enter_message_using_elytra", true)){
            if (p.isGliding()){
                return;
            }
        }
        
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Location l = p.getLocation();

        Area area = plugin.getProtectionsManager().getAreaByLocation(l);

        if (area != null){
            if (lastLocation.containsKey(p)){
                if (lastLocation.get(p) != area){
                    lastLocation.put(p, area);
                    sendEnterExitMessage(p, area, "enter");
                }
            } else {
                lastLocation.put(p, area);
                sendEnterExitMessage(p, area, "enter");
            }
            
        } else {
            if (lastLocation.containsKey(p)){
                Area lastProte = lastLocation.get(p);
                lastLocation.remove(p);
                
                sendEnterExitMessage(p, lastProte, "exit");
            }
        }
    }
    private void sendEnterExitMessage(Player p, Area prote, String type) {
        String message;
        if ("enter".equals(type)) {
            message = prote.getEnterMessage();
            if (prote.isRentable() && !prote.isRented()) {
                double cost = prote.getRentCost();
                message = plugin.getDataHandler().getLang("rent.available", p).replace("%cost%", cost + "");
            } else if (prote.isRentable() && prote.isRented()) {
                message = plugin.getDataHandler().getLang("rent.rented", p).replace("%player%", prote.getRentedOwnerName());
            }
        } else {
            message = prote.getExitMessage();
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        String messageType = plugin.getConfig().getString("protection.enter_message_type");
        switch (messageType) {
            case "action":
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                break;
            case "message":
                p.sendMessage(message);
                break;
            default:
                break;
        }
    }
}
