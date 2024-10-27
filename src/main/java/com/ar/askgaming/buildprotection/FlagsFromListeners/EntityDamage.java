package com.ar.askgaming.buildprotection.FlagsFromListeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.ProtectionFlags.FlagType;

public class EntityDamage implements Listener{
    
    private Main plugin;
    public EntityDamage(Main main){
        plugin = main;
    }
    @EventHandler()
    public void onEntityAttack(EntityDamageByEntityEvent event){

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            Player p = (Player) event.getEntity();
            Player d = (Player) event.getDamager();

            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.PVP, d.getLocation())){
                event.setCancelled(true);
                d.sendMessage("No puedes atacar a esta zona");
                return;
            }
        }
        if (event.getEntity() instanceof Animals){

            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.ANIMALS, event.getEntity().getLocation())){
                event.setCancelled(true);
                return;
            }
        }
        if (event.getEntity() instanceof Monster){

            if (!plugin.getProtectionFlags().isFlagEnabled(FlagType.MONSTERS, event.getEntity().getLocation())){
                event.setCancelled(true);
            }
        }
    }

}
