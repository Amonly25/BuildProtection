package com.ar.askgaming.buildprotection.FlagsFromListeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Break implements Listener {
    
    private Main plugin;
    public Break(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onBlockBreak(BlockBreakEvent event){
        Player p = event.getPlayer();
        Location l = event.getBlock().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.BREAK, p, l)){
            sendMessage(p);
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onHangingItemBreak(EntityDamageEvent event){
        if (event.getEntity() instanceof Hanging){
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
