package com.ar.askgaming.buildprotection.Managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Area;
import com.ar.askgaming.buildprotection.Main;

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
        ENTITY_DAMAGE,
        IGNITE,
        PISTON,
        RIDE,
        TELEPORT,
        FISH,
        FLOW,
        MOVE,
        ANIMALS,
        MONSTERS,
        EXPLODE,
        CONTAINER
    }

    public boolean hasPermission(FlagType type, Player player, Location location){

        if (player.hasPermission("buildprotection.admin")) {
            return true;
        }

        Area area = plugin.getProtectionsManager().getAreaByLocation(location);
        if (area != null){
            if (area.getParentProtection().getOwner().equals(player.getUniqueId())) {
                return true;
            } else if (area.getPlayers().contains(player.getUniqueId())){
                return true;
            }
            else {
                return area.getFlagsMap().get(type);
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
        Area area = plugin.getProtectionsManager().getAreaByLocation(location);
        if (area != null){
            return area.getFlagsMap().get(type);
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
