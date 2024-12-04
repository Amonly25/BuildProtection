package com.ar.askgaming.buildprotection.FlagsFromListeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Break implements Listener {
    
    private BuildProtection plugin;
    public Break(BuildProtection main){
        plugin = main;
    }
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        Player p = event.getPlayer();
        Location l = event.getBlock().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.BREAK, p, l)){
            sendMessage(p);
            event.setCancelled(true);
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onHangingItemBreak(HangingBreakByEntityEvent event){
        if (event.getRemover() instanceof Player){
            Player p = (Player) event.getRemover();
            Hanging h = event.getEntity();
            Location l = h.getLocation();
            
            if (!plugin.getProtectionFlags().hasPermission(FlagType.BREAK, p, l)){
                sendMessage(p);
                event.setCancelled(true);
            }
        } else {
            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.BREAK, event.getEntity().getLocation())){
                event.setCancelled(true);
            }
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
            p.sendMessage(plugin.getDataHandler().getLang("flags.break", p));
            lastMessage.put(p, currentTime);
        }
    }
}
