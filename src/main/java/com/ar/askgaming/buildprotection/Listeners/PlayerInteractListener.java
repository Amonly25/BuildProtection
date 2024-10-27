package com.ar.askgaming.buildprotection.Listeners;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Protection;

public class PlayerInteractListener implements Listener{

    private Main plugin;
    public PlayerInteractListener(Main main){
        plugin = main;
    }

    private long lastClickTime = 0;

    @EventHandler()
    public void onSelectByTool(PlayerInteractEvent event){
        
        Player p = (Player ) event.getPlayer();

        if (!plugin.getProtectionsManager().getPlayersInEditMode().containsKey(p)){return;}
        if (event.getClickedBlock() == null){return;}

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
                p.sendMessage("No puedes establecer una proteccion dentro de otra.");
                return;
            }

            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
               plugin.getProtectionsManager().getPlayersInEditMode().get(p).setLoc1(block.getLocation());
               p.sendMessage("Has establecido la esquina 1");

            } else if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                plugin.getProtectionsManager().getPlayersInEditMode().get(p).setLoc2(block.getLocation().clone().add(1, 1, 1));
                p.sendMessage("Has establecido la esquina 2");
            }
        }
    }
}
