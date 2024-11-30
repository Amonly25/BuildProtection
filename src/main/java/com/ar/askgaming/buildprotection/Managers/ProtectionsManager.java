package com.ar.askgaming.buildprotection.Managers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Area;
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

    //#region setDefautsFlags
    public void setDefaultsFlags(Area area){
        HashMap<FlagType, Boolean> flagsMap = area.getFlagsMap();
        flagsMap.put(FlagType.PLACE, false);
        flagsMap.put(FlagType.BREAK, false);
        flagsMap.put(FlagType.IGNITE, false);
        flagsMap.put(FlagType.INTERACT, true);
        flagsMap.put(FlagType.FISH, false);
        flagsMap.put(FlagType.RIDE, false);
        flagsMap.put(FlagType.TELEPORT, true);
        flagsMap.put(FlagType.PVP, true);
        flagsMap.put(FlagType.MOVE, true);
        flagsMap.put(FlagType.PISTON, false);
        flagsMap.put(FlagType.ENTITY_DAMAGE, false);
        flagsMap.put(FlagType.FLOW, true);
        flagsMap.put(FlagType.ANIMALS, false);
        flagsMap.put(FlagType.MONSTERS, true);
        flagsMap.put(FlagType.EXPLODE, false);
        flagsMap.put(FlagType.CONTAINER, false);
        flagsMap.put(FlagType.USE, false);

    }

    private HashMap<Player,Selection> playersInEditMode = new HashMap<>();
    
    public HashMap<Player, Selection> getPlayersInEditMode() {
        return playersInEditMode;
    }
    public Protection getProtectionByLocation(Location l){

        // Iterar sobre el HashMap usando un bucle for tradicional
        for (Entry<String, Protection> entry : plugin.getProtectionsManager().getProtectionsByWorld(l.getWorld()).entrySet()) {

            Protection prote = entry.getValue(); 
            if (prote.isInside(l)){
                return prote;
            }
        }

        return null;
    }
    public Area getAreaByLocation(Location l){

        // Iterar sobre el HashMap usando un bucle for tradicional
        Protection prote = getProtectionByLocation(l);
        if (prote != null){
            int highestPriorityArea = 0;
            Area highestPriorityAreaObj = null;
            for (Entry<String, Area> entry2 : prote.getAreas().entrySet()) {
                Area area = entry2.getValue(); 
                if (area.isInside(l)) {
                    if (area.getPriority() > highestPriorityArea) {
                        highestPriorityArea = area.getPriority();
                        highestPriorityAreaObj = area;
                    }
                }
            }
            return highestPriorityAreaObj;
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

    //#region deleteProtection
    public void deleteProtection(Protection prote) {
        String wName = prote.getLoc1().getWorld().getName();
        FileConfiguration cfg = plugin.getDataHandler().getWorldConfig(wName);
        if (cfg != null){
            double d = calculateM3(prote.getLoc1(), prote.getLoc2());  
            if (plugin.getEconomy() != null){
                OfflinePlayer owner = Bukkit.getOfflinePlayer(prote.getOwner());
                if (!owner.getPlayer().hasPermission("buildprotection.admin")){
                    double price = d * plugin.getConfig().getDouble("protection.cost_sell_per_block"); 
                    plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(prote.getOwner()), price);
                    Bukkit.getPlayer(prote.getOwner()).sendMessage("You have been refunded " + price + " for the protection " + prote.getName());
                } else {
                    Bukkit.getPlayer(prote.getOwner()).sendMessage("No refund was given because you are an admin");
                }
            }
            Bukkit.getPlayer(prote.getOwner()).sendMessage("You have deleted the protection " + prote.getName());
            cfg.set(prote.getName(), null);
            plugin.getDataHandler().saveWorldConfig(cfg, wName);
            mapList.get(wName).remove(prote.getName());
            prote = null;
        }
    }
    public Integer calculateM3(Location loc1, Location loc2){
        
        int x1 = loc1.getBlockX();
        int y1 = loc1.getBlockY();
        int z1 = loc1.getBlockZ();
        int x2 = loc2.getBlockX();
        int y2 = loc2.getBlockY();
        int z2 = loc2.getBlockZ();
        
        int m3 = (Math.abs(x1 - x2) + 1) * (Math.abs(y1 - y2) + 1) * (Math.abs(z1 - z2) + 1);
        return m3;

    }
}
