package com.ar.askgaming.buildprotection.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.Protection;

public class PlayerInteractListener implements Listener{

    private BuildProtection plugin;
    public PlayerInteractListener(BuildProtection main){
        plugin = main;
    }

    private long lastClickTime = 0;

    @EventHandler()
    public void onSelectByTool(PlayerInteractEvent event){
        
        Player p = (Player ) event.getPlayer();

        if (!plugin.getProtectionsManager().getPlayersInEditMode().containsKey(p)){return;}
        if (event.getClickedBlock() == null){return;}

        //hacerlo por jugador - es global asi que si dos jugadores estan editando al mismo tiempo se jode todo

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 100) { // 100 ms de espera
            return; // Ignorar si el clic fue demasiado rápido
        }
        lastClickTime = currentTime;
        Block block = event.getClickedBlock();
        Location l = block.getLocation();

        if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK)){
            event.setCancelled(true);

            Protection prote = plugin.getProtectionsManager().getProtectionByLocation(l);

            if (prote != null){
                if (!prote.getOwner().equals(p.getUniqueId())){
                    p.sendMessage(plugin.getLangManager().get("select.not_owner", p));
                    return;
                }
                p.sendMessage(plugin.getLangManager().get("select.inside_own", p));
            }
            

            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
               p.sendMessage(plugin.getLangManager().get("select.p1", p));
               plugin.getProtectionsManager().getPlayersInEditMode().get(p).setLoc1(block.getLocation());

            } else if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                p.sendMessage(plugin.getLangManager().get("select.p2", p));
                plugin.getProtectionsManager().getPlayersInEditMode().get(p).setLoc2(block.getLocation());
            }
        }
    }
}
