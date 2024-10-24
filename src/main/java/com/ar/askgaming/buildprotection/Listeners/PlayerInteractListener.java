package com.ar.askgaming.buildprotection.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.buildprotection.Main;

public class PlayerInteractListener implements Listener{

    private Main plugin;
    public PlayerInteractListener(Main main){
        plugin = main;
    }

    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent event){
        
        Player p = (Player ) event.getPlayer();

        if (!plugin.playersInEditMode.containsKey(p)){return;}
        if (event.getClickedBlock() == null){return;}

        Block block = event.getClickedBlock();

        if (p.getInventory().getItemInMainHand().equals(new ItemStack(Material.STICK))){
            
            if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
               plugin.playersInEditMode.get(p).setLoc1(block.getLocation());
               p.sendMessage("Has establecido la esquina 1");
               event.setCancelled(true);

            }
            if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                plugin.playersInEditMode.get(p).setLoc2(block.getLocation().clone().add(1, 1, 1));
                p.sendMessage("Has establecido la esquina 2");
                event.setCancelled(true);
            }
        }
    }
}
