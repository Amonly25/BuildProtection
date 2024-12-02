package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Move implements Listener{

    private BuildProtection plugin;
    public Move(BuildProtection main){
        plugin = main;
    }
    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent event){

        Player p = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (!plugin.getProtectionFlags().hasPermission(FlagType.MOVE, p, event.getTo())){
            event.setCancelled(true);
            p.sendMessage(plugin.getDataHandler().getLang("flags.move", p));
        }
    }

}
