package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.ProtectionFlags.FlagType;

public class Move implements Listener{

    private Main plugin;
    public Move(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent event){

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (!plugin.getProtectionFlags().hasPermission(FlagType.MOVE, event.getPlayer(), event.getTo())){
            event.setCancelled(true);
            event.getPlayer().sendMessage("No puedes moverte en esta zona");
        }
    }

}
