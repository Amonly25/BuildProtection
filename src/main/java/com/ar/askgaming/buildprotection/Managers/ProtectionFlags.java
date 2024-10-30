package com.ar.askgaming.buildprotection.Managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Protection;

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
        EXPLODE
    }

    public boolean hasPermission(FlagType type, Player player, Location location){

        if (player.hasPermission("buildprotection.admin")) {
            return true;
        }

        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(location);
        if (prote != null){
            if (prote.getOwner().equals(player.getUniqueId())) {
                return true;
            } else if (prote.getPlayers().contains(player.getUniqueId())){
                return true;
            }
            else {
                return prote.getFlagsMap().get(type);
            }
        } 
        
        return true;
    }
    public boolean hasFlagPermission(String flag, Player player){

        if (!player.hasPermission("buildprotection.flags." + flag)) {
            return false;
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
