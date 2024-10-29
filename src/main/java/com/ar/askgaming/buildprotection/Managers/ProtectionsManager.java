package com.ar.askgaming.buildprotection.Managers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Protection;
import com.ar.askgaming.buildprotection.Selection;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class ProtectionsManager {

    private HashMap<String, HashMap<String, Protection>> mapList = new HashMap<>();

    public HashMap<String, HashMap<String, Protection>> getAllProtections() {
        return mapList;
    }

    public HashMap<String, Protection> getProtectionsByWorld(World world) {
        return mapList.get(world.getName());
    }

    private Main plugin;
    public ProtectionsManager(Main main){
        plugin = main;

        // Cargar todas las protecciones guardadas en los archivos de configuración
        Bukkit.getWorlds().forEach(world -> {

            //Crear mapa que contiene las protecciones de cada mundo
            HashMap<String, Protection> worldMap = new HashMap<>();

            FileConfiguration config = plugin.getDataHandler().getWorldConfig(world.getName());

            // Obtener todas las claves del nivel raíz
            Set<String> protectionKeys = config.getKeys(false);
    
            // Iterar sobre todas las keys y cargar cada Protection
            for (String key : protectionKeys) {
                Object obj = config.get(key);
                if (obj instanceof Protection) {
                    Protection protection = (Protection) obj;
    
                    // Guardar cada Protection en el mapa con su clave
                    worldMap.put(key, protection);
                    protection.setName(key);
                }
            }
            mapList.put(world.getName(), worldMap);
        });
    }

    public void setDefaultsFlags(Protection prote){
        HashMap<FlagType, Boolean> flagsMap = prote.getFlagsMap();
        flagsMap.put(FlagType.PLACE, false);
        flagsMap.put(FlagType.BREAK, false);
        flagsMap.put(FlagType.IGNITE, false);
        flagsMap.put(FlagType.INTERACT, false);
        flagsMap.put(FlagType.FISH, false);
        flagsMap.put(FlagType.RIDE, false);
        flagsMap.put(FlagType.TELEPORT, true);
        flagsMap.put(FlagType.PVP, true);
        flagsMap.put(FlagType.MOVE, true);
        flagsMap.put(FlagType.PISTON, false);
        flagsMap.put(FlagType.DAMAGE, true);
        flagsMap.put(FlagType.FLOW, true);
        flagsMap.put(FlagType.ANIMALS, false);
        flagsMap.put(FlagType.MONSTERS, true);
        flagsMap.put(FlagType.EXPLODE, false);

    }

    private HashMap<Player,Selection> playersInEditMode = new HashMap<>();
    
    public HashMap<Player, Selection> getPlayersInEditMode() {
        return playersInEditMode;
    }
    public Protection getProtectionByLocation(Location l){

        // Iterar sobre el HashMap usando un bucle for tradicional
        for (Entry<String, Protection> entry : plugin.getProtectionsManager().getProtectionsByWorld(l.getWorld()).entrySet()) {

            Protection prote = entry.getValue(); 

            if (prote.isInsideArea(l)){
                return prote;
            }
        }

        return null;
    }
    public Protection getProtectionByName(String name, World world){

        // Iterar sobre el HashMap usando un bucle for tradicional
        for (Entry<String, Protection> entry : plugin.getProtectionsManager().getProtectionsByWorld(world).entrySet()) {
            String proteName = entry.getKey();
            Protection prote = entry.getValue(); 

            if (proteName.equalsIgnoreCase(name)){
                return prote;
            }
        }
        return null;
    }

    public void deleteProtection(Protection prote) {
        String wName = prote.getLoc1().getWorld().getName();
        FileConfiguration cfg = plugin.getDataHandler().getWorldConfig(wName);
        if (cfg != null){
            cfg.set(prote.getName(), null);
            plugin.getDataHandler().saveWorldConfig(cfg, wName);
            mapList.get(wName).remove(prote.getName());
            prote = null;
        }
    }
}
