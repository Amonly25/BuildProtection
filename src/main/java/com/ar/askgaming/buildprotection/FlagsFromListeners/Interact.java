package com.ar.askgaming.buildprotection.FlagsFromListeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.EnchantingTable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Interact implements Listener {

    private Main plugin;
    public Interact(Main main){
        plugin = main;
    }

    @EventHandler()
    public void onBlockInteract(PlayerInteractEvent event){
        Player p = event.getPlayer();

        Block b = event.getClickedBlock();
        if (b == null) return;

        Location l = b.getLocation();

        BlockData bd = b.getBlockData();
        if (bd instanceof Powerable || bd instanceof Bed || bd instanceof EnchantingTable){
            if (!plugin.getProtectionFlags().hasPermission(FlagType.USE, p, l)){
                p.sendMessage(plugin.getDataHandler().getLang("flags.use", p));
                event.setCancelled(true);
            }
            return;
        }
       
        if (b.getState() instanceof Container) {
            if (!plugin.getProtectionFlags().hasPermission(FlagType.CONTAINER, p, l)){
                p.sendMessage(plugin.getDataHandler().getLang("flags.container", p));
                event.setCancelled(true);
            }
            return;
        }
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, l)){

            event.setCancelled(true);
            sendMessage(p);
        }
    }
    @EventHandler()
    public void onBlockInteract(PlayerInteractEntityEvent event){
        Player p = event.getPlayer();

        Location l = event.getRightClicked().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, l)){
            sendMessage(p);
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Player p = event.getPlayer();
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, event.getRightClicked().getLocation())) {
            sendMessage(p);
            event.setCancelled(true);
        }
    }

    private HashMap<Player, Long> lastMessage = new HashMap<Player, Long>();
    private void sendMessage(Player p) {

        // Añadir un mensaje de cooldown
        long lastHitTime = lastMessage.getOrDefault(p, 0L);
        long currentTime = System.currentTimeMillis();
        // Calcular el tiempo transcurrido desde el último golpe
        long timeSinceLastHit = System.currentTimeMillis() - lastHitTime;

        if (timeSinceLastHit > 15000) {
            // Si el tiempo transcurrido es menor que el cooldown, enviar el mensaje y actualizar el último golpe
            p.sendMessage(plugin.getDataHandler().getLang("flags.interact", p));
            lastMessage.put(p, currentTime);
        }
    }
}
