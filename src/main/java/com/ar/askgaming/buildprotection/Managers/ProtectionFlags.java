package com.ar.askgaming.buildprotection.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Area;
import com.ar.askgaming.buildprotection.BuildProtection;

public class ProtectionFlags {
    
    private File file;
    private FileConfiguration config;
    
    private BuildProtection plugin;
    public ProtectionFlags(BuildProtection main){
        plugin = main;

        file = new File(plugin.getDataFolder(), "flags.yml");
        if (!file.exists()) {
            plugin.saveResource("flags.yml", false);
        }
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        useFlagsInteracts = config.getStringList("flags.use");
        containerFlagsInteracts = config.getStringList("flags.container");
        teleportFlagsInteracts = config.getStringList("flags.teleport");
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
        CONTAINER,
        USE
    }
        //#region setDefautsFlags
    public void setDefaultsFlags(Area area){
        HashMap<FlagType, Boolean> flagsMap = area.getFlagsMap();
        flagsMap.put(FlagType.PLACE, config.getBoolean("default_flags.place",false));
        flagsMap.put(FlagType.BREAK, config.getBoolean("default_flags.break",false));
        flagsMap.put(FlagType.IGNITE, config.getBoolean("default_flags.ignite",false));
        flagsMap.put(FlagType.INTERACT, config.getBoolean("default_flags.interact",false));
        flagsMap.put(FlagType.FISH, config.getBoolean("default_flags.fish",false));
        flagsMap.put(FlagType.RIDE, config.getBoolean("default_flags.ride",false));
        flagsMap.put(FlagType.TELEPORT, config.getBoolean("default_flags.teleport",false));
        flagsMap.put(FlagType.PVP, config.getBoolean("default_flags.pvp",true));
        flagsMap.put(FlagType.MOVE, config.getBoolean("default_flags.move",true));
        flagsMap.put(FlagType.PISTON, config.getBoolean("default_flags.piston",false));
        flagsMap.put(FlagType.ENTITY_DAMAGE, config.getBoolean("default_flags.entity_damage",false));
        flagsMap.put(FlagType.FLOW, config.getBoolean("default_flags.flow",true));
        flagsMap.put(FlagType.ANIMALS, config.getBoolean("default_flags.animals",false));
        flagsMap.put(FlagType.MONSTERS, config.getBoolean("default_flags.monsters",true));
        flagsMap.put(FlagType.EXPLODE, config.getBoolean("default_flags.explode",false));
        flagsMap.put(FlagType.CONTAINER, config.getBoolean("default_flags.container",false));
        flagsMap.put(FlagType.USE, config.getBoolean("default_flags.use",false));

        area.setFlagsMap(flagsMap);
    }

    public boolean hasPermission(FlagType type, Player player, Location location){

        Area area = plugin.getProtectionsManager().getAreaByLocation(location);
        if (area == null){
            return true;
        }

        if (plugin.getProtectionsManager().hasAdminPermission(area, player)){
            return true;
        }
        if (area.getRentedOwner().equals(player.getUniqueId())){
            return true;
        }
        if (area.getPlayers().contains(player.getUniqueId())){
            return true;
        }
        else {
            return area.getFlagsMap().get(type);
        } 

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
    private List<String> useFlagsInteracts = new ArrayList<>();
    private List<String> containerFlagsInteracts = new ArrayList<>();
    private List<String> teleportFlagsInteracts = new ArrayList<>();
    public List<String> getTeleportFlagsInteracts() {
        return teleportFlagsInteracts;
    }
    public List<String> getUseFlagsInteracts() {
        return useFlagsInteracts;
    }
    public List<String> getContainerFlagsInteracts() {
        return containerFlagsInteracts;
    }
    public LinkedHashMap<FlagType, Boolean> getSortedFlags(Area area){
        List<Map.Entry<FlagType, Boolean>> entryList = new ArrayList<>(area.getFlagsMap().entrySet());

        // Ordenar la lista: los valores `true` primero y luego los `false`
        entryList.sort((entry1, entry2) -> Boolean.compare(!entry1.getValue(), !entry2.getValue()));

        // Crear un nuevo LinkedHashMap para mantener el orden
        LinkedHashMap<FlagType, Boolean> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<FlagType, Boolean> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
