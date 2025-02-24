package com.ar.askgaming.buildprotection.FlagsFromListeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags.FlagType;

public class Interact implements Listener {

    private BuildProtection plugin;
    public Interact(BuildProtection main){
        plugin = main;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if (b == null) return;

        Location l = b.getLocation();
        Material m = b.getType();
        ProtectionFlags flags = plugin.getProtectionFlags();

        if (checkInteraction(flags.getUseFlagsInteracts(), m, FlagType.USE, p, l, "flags.use", event)) return;
        if (checkInteraction(flags.getContainerFlagsInteracts(), m, FlagType.CONTAINER, p, l, "flags.container", event)) return;
    }

    private boolean checkInteraction(List<String> materials, Material blockMaterial, FlagType flagType, Player player, Location location, String langKey, PlayerInteractEvent event) {
        for (String mat : materials) {
            if (blockMaterial.toString().replace("_", "").contains(mat)) {
                if (!plugin.getProtectionFlags().hasPermission(flagType, player, location)) {
                    player.sendMessage(plugin.getLangManager().getLang(langKey, player));
                    event.setCancelled(true);
                }
            }
        }
        return false;
    }
    @EventHandler()
    public void onEntityInteract(PlayerInteractEntityEvent event){

        Player p = event.getPlayer();
        if (event.getRightClicked() instanceof Player){
            return;
        }
        Location l = event.getRightClicked().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, l)){
            sendMessage(p);
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
       
        Player p = event.getPlayer();
        if (event.getRightClicked() instanceof Player){
            return;
        }
        Location l = event.getRightClicked().getLocation();
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, l)) {
            sendMessage(p);
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, e.getBlock().getLocation())) {
            sendMessage(p);
            e.setCancelled(true);
        }
    }
    @EventHandler()
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, e.getBlock().getLocation())) {
            sendMessage(p);
            e.setCancelled(true);
        }
    }
    @EventHandler()
    public void onSgn(SignChangeEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, e.getBlock().getLocation())) {
            sendMessage(p);
            e.setCancelled(true);
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
            p.sendMessage(plugin.getLangManager().getLang("flags.interact", p));
            lastMessage.put(p, currentTime);
        }
    }

}
