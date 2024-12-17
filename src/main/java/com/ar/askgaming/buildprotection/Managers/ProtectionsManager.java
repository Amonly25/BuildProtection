package com.ar.askgaming.buildprotection.Managers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Area;
import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection;
import com.ar.askgaming.buildprotection.Selection;

public class ProtectionsManager {

    private HashMap<String, HashMap<String, Protection>> mapList = new HashMap<>();

    public HashMap<String, HashMap<String, Protection>> getAllProtections() {
        return mapList;
    }

    public HashMap<String, Protection> getProtectionsByWorld(World world) {
        return mapList.get(world.getName());
    }

    private BuildProtection plugin;
    public ProtectionsManager(BuildProtection main){
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

    private HashMap<Player,Selection> playersInEditMode = new HashMap<>();
    
    public HashMap<Player, Selection> getPlayersInEditMode() {
        return playersInEditMode;
    }
    public Protection getProtectionByLocation(Location l){

        World world = l.getWorld();
        for (Entry<String, Protection> entry : plugin.getProtectionsManager().getProtectionsByWorld(world).entrySet()) {
            Protection prote = entry.getValue(); 
            for (Area area : prote.getAreas().values()) {
                if (isInside(area,l)){
                    return prote;
                }
            } 
        }
        return null;
    }
    public Area getAreaByLocation(Location l){

        // Iterar sobre el HashMap usando un bucle for tradicional
        Protection prote = getProtectionByLocation(l);
        if (prote != null){
            int highestPriority = 0;
            Area highestPriorityAreaObj = null;
            for (Area area : prote.getAreas().values()) {
                if (isInside(area,l) && area.getPriority() > highestPriority) {
                    highestPriority = area.getPriority();
                    highestPriorityAreaObj = area;
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
    public void deleteProtection(Player p, Protection prote) {
        String wName = prote.getLoc1().getWorld().getName();
        FileConfiguration cfg = plugin.getDataHandler().getWorldConfig(wName);
        if (cfg != null){
            double d = calculateM3(prote.getLoc1(), prote.getLoc2());  
            if (plugin.getEconomy() != null){
                
                if (!p.hasPermission("buildprotection.admin")){
                    double price = d * plugin.getConfig().getDouble("protection.cost_sell_per_block"); 
                    plugin.getEconomy().depositPlayer(p, price);
                    p.sendMessage("You have been refunded " + price + " for the protection " + prote.getName());
                } else {
                    p.sendMessage("No refund was given because you are an admin");
                }
            }
            p.sendMessage("You have deleted the protection " + prote.getName());
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
    //#region adminPerm
    public boolean hasAdminPermission(Area area, Player player){
        UUID rentOwner = area.getRentedOwner();
        if (rentOwner != null && rentOwner.equals(player.getUniqueId())){
            return true;
        } 
        return hasAdminPermission(area.getParentProtection(), player);
    }
    public boolean hasAdminPermission(Protection prote, Player player){
        if (prote.getOwner().equals(player.getUniqueId())){
            return true;
        }
        if (player.hasPermission("buildprotection.admin")){
            return true;
        }
        return false;
    }
    public boolean isInside(Area area, Location check){

        double x1 = Math.min(area.getLoc1().getX(), area.getLoc2().getX());
        double x2 = Math.max(area.getLoc1().getX(), area.getLoc2().getX());
        double y1 = Math.min(area.getLoc1().getY(), area.getLoc2().getY());
        double y2 = Math.max(area.getLoc1().getY(), area.getLoc2().getY());
        double z1 = Math.min(area.getLoc1().getZ(), area.getLoc2().getZ());
        double z2 = Math.max(area.getLoc1().getZ(), area.getLoc2().getZ());

        double checkX = check.getX();
        double checkY = check.getY();
        double checkZ = check.getZ();

        if (checkX >= x1 && checkX <= x2 && checkY >= y1 && checkY <= y2 && checkZ >= z1 && checkZ <= z2) {
            return true;
        }

        return false;
    }
    //#region rent
    public void rent(Area area, Player p) {
        double d = area.getRentCost();

        if (plugin.getEconomy() == null){
            plugin.getLogger().warning("No economy plugin found, cant rent protection.");
            p.sendMessage(plugin.getDataHandler().getLang("misc.no_economy", p));
            return;
        }

        if (plugin.getEconomy().getBalance(p) >= d){
            area.setRented(true);
            area.setRentedOwner(p.getUniqueId());
            area.setRentedSince(System.currentTimeMillis());
            p.sendMessage(plugin.getDataHandler().getLang("rent.rented", p));
            save(area.getParentProtection());
            if (plugin.getRealisticEconomy() != null){
                plugin.getRealisticEconomy().getServerBank().depositFromPlayerToServer(p.getUniqueId(), d);
            } else {
                plugin.getEconomy().withdrawPlayer(p, d);

            }
        } else{
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_money", p));
            return;
        }
    }
    public void autoRent(Area area, OfflinePlayer player) {
        if (player.isOnline()){
            rent(area, (Player) player);
            return;
        }
        if (plugin.getEconomy() == null){
            return;
        }
        
        double d = area.getRentCost();

        if (plugin.getEconomy().getBalance(player) >= d){
            area.setRentedSince(System.currentTimeMillis());

            if (plugin.getRealisticEconomy() != null){
                plugin.getRealisticEconomy().getServerBank().depositFromPlayerToServer(player.getUniqueId(), d);
            } else {
                plugin.getEconomy().withdrawPlayer(player, d);
            }
        } else {
            area.setRented(false);
            area.setRentedOwner(null);
            area.setRentedSince(0);
        }
        save(area.getParentProtection());
    }
    public void save(Protection prote){
        FileConfiguration cfg = plugin.getDataHandler().getWorldConfig(prote.getLoc1().getWorld().getName());
        cfg.set(prote.getName(), prote);
        plugin.getDataHandler().saveWorldConfig(cfg, prote.getLoc1().getWorld().getName());
    }
    //#region unrent
    public void unrent(Area area, Player p) {
        area.setRented(false);
        area.setRentedOwner(null);
        area.setRentedSince(0);
        p.sendMessage(plugin.getDataHandler().getLang("rent.unrented", p));
        save(area.getParentProtection());
    }
}
