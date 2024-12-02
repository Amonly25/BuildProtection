package com.ar.askgaming.buildprotection.FlagsFromListeners;

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
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Interact implements Listener {

    private BuildProtection plugin;
    public Interact(BuildProtection main){
        plugin = main;
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
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
                    player.sendMessage(plugin.getDataHandler().getLang(langKey, player));
                    event.setCancelled(true);
                }
            }
        }
        return false;
    }
    @EventHandler()
    public void onBlockInteract(PlayerInteractEntityEvent event){
        Player p = event.getPlayer();

        Location l = event.getRightClicked().getLocation();
        
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, l)){
            p.sendMessage(plugin.getDataHandler().getLang("flags.interact", p));
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Player p = event.getPlayer();
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, event.getRightClicked().getLocation())) {
            p.sendMessage(plugin.getDataHandler().getLang("flags.interact", p));
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, e.getBlock().getLocation())) {
            p.sendMessage(plugin.getDataHandler().getLang("flags.interact", p));
            e.setCancelled(true);
        }
    }
    @EventHandler()
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, e.getBlock().getLocation())) {
            p.sendMessage(plugin.getDataHandler().getLang("flags.interact", p));
            e.setCancelled(true);
        }
    }
    @EventHandler()
    public void onSgn(SignChangeEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getProtectionFlags().hasPermission(FlagType.INTERACT, p, e.getBlock().getLocation())) {
            p.sendMessage(plugin.getDataHandler().getLang("flags.interact", p));
            e.setCancelled(true);
        }
    }
}
