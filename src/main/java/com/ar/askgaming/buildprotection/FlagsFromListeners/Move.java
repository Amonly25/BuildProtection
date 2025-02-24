package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.Area;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags.FlagType;

public class Move implements Listener{

    private BuildProtection plugin;
    public Move(BuildProtection main){
        plugin = main;
    }
    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent event){

        Player p = event.getPlayer();
        Location to = event.getTo();

        if (event.getFrom().getBlockX() == to.getBlockX() &&
            event.getFrom().getBlockY() == to.getBlockY() &&
            event.getFrom().getBlockZ() == to.getBlockZ()) {
            return;
        }

        if (!plugin.getProtectionFlags().hasPermission(FlagType.MOVE, p, to)){
            event.setCancelled(true);
            p.sendMessage(plugin.getLangManager().getLang("flags.move", p));
        }
        // if (p.isGliding() || p.isFlying()){
        //     return;
        // }
        // Area area = plugin.getProtectionsManager().getAreaByLocation(to);

        // if (area != null){
                
        //     if (to.getBlock().getType().isSolid() || to.getBlock().getRelative(0, 1, 0).getType().isSolid()) {
        //         event.setCancelled(true);

        //     }
        // }
    }
}
