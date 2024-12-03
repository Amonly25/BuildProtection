package com.ar.askgaming.buildprotection.FlagsFromListeners;

import java.util.HashMap;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class EntityDamage implements Listener{
    
    private BuildProtection plugin;
    public EntityDamage(BuildProtection main){
        plugin = main;
    }
    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            handlePlayerDamage(event);
        } else if (entity instanceof Animals) {
            handleAnimalDamage(event);
        } else if (entity instanceof Enemy) {
            handleMonsterDamage(event);
        }
        else {
            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.ENTITY_DAMAGE, entity.getLocation())) {
                event.setCancelled(true);
            }
        }
    }
    
    private void handlePlayerDamage(EntityDamageByEntityEvent event) {
        Player damaged = (Player) event.getEntity();
        Player damager = null;
    
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }
    
        if (damager != null && !plugin.getProtectionFlags().isFlagEnabled(FlagType.PVP, damaged.getLocation())) {
            event.setCancelled(true);
            sendMessage(damager);
        }
    }
    
    private void handleAnimalDamage(EntityDamageByEntityEvent event) {
        if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.ANIMALS, event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }
    
    private void handleMonsterDamage(EntityDamageByEntityEvent event) {
        if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.MONSTERS, event.getEntity().getLocation())) {
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
            p.sendMessage(plugin.getDataHandler().getLang("flags.pvp", p));
            lastMessage.put(p, currentTime);
        }
    }
}
