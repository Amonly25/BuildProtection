package com.ar.askgaming.buildprotection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.t;

import java.util.Map.Entry;

public class ProtectionFlags {
    
    private Main plugin;
    public ProtectionFlags(Main main){
        plugin = main;
    }

    public static enum FlagType {
        BREAK,
        PLACE,
        INTERACT,
        PVP,
        DAMAGE,
        IGNITE,
        PISTON,
        RIDE,
        TELEPORT,
        FISH,
        FLOW,
        MOVE,
        ANIMALS,
        MONSTERS,
    }

    public boolean hasPermission(FlagType type, Player player, Location location){

        if (player.hasPermission("buildprotection.admin")) {
            return true;
        }

        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(location);
        if (prote != null){
            if (prote.getOwner().equals(player.getName())) {
                return true;
            } else if (prote.getPlayers().contains(player.getName())){
                return true;
            }
            else {
                return prote.getFlagsMap().get(type);
            }
        } 
        
        return true;
    }
    public boolean isFlagEnabled(FlagType type, Location location){
        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(location);
        if (prote != null){
            return prote.getFlagsMap().get(type);
        } 
        return true;
    }
    public boolean isValid(String string) {
        for (FlagType flag : FlagType.values()) {
            if (flag.name().equalsIgnoreCase(string)) {
            return true;
            }
        }
        return false;
    }
}
