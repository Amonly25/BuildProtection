package com.ar.askgaming.buildprotection.Protection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Selection.Selection;

public class ProtectionsManager {

    private final HashMap<String, HashMap<String, Protection>> mapList = new HashMap<>();
    private final HashMap<Player,Selection> playersInEditMode = new HashMap<>();

    private final BuildProtection plugin;
    public ProtectionsManager(BuildProtection main){
        plugin = main;

        Bukkit.getWorlds().forEach(world -> {
            getProtectionDataFromWorld(world);
        });
    }
    //#region loadProtections
    private void getProtectionDataFromWorld(World world){
        HashMap<String, Protection> worldMap = new HashMap<>();

        FileConfiguration config = plugin.getProtectionsData().getWorldConfig(world.getName());

        Set<String> protectionKeys = config.getKeys(false);
        if (!protectionKeys.isEmpty()) {

            for (String key : protectionKeys) {
                if (key.isBlank()) continue;
                Object obj = config.get(key);
                if (obj == null) continue;
                if (obj instanceof Protection) {
                    Protection protection = (Protection) obj;

                    worldMap.put(key, protection);
                    protection.setName(key);
                }
            }
        }
        mapList.put(world.getName(), worldMap);
    }    

    //#region deleteProtection
    public void deleteProtection(Player p, Protection prote) {
        String wName = prote.getLoc1().getWorld().getName();
        FileConfiguration cfg = plugin.getProtectionsData().getWorldConfig(wName);

        double d = calculateM3(prote.getLoc1(), prote.getLoc2());  
        if (plugin.getEconomy() != null){
            
            if (!p.hasPermission("buildprotection.admin")){
                double price = d * plugin.getConfig().getDouble("protection.cost_sell_per_block"); 
                plugin.getEconomy().depositPlayer(p, price);
                p.sendMessage(plugin.getLangManager().get("prote.refund", p).replace("%cost%", price + ""));
            } else {
                p.sendMessage("No refund was given because you are an admin");
            }
        }
        p.sendMessage(plugin.getLangManager().get("prote.deleted", p).replace("%name%", prote.getName()));
        cfg.set(prote.getName(), null);
        plugin.getProtectionsData().saveWorldConfig(cfg, wName);
        mapList.get(wName).remove(prote.getName());
        prote = null;
        
    }
    //#region calculateM3
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
    //#region isInside
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
            p.sendMessage(plugin.getLangManager().get("misc.no_economy", p));
            return;
        }

        if (plugin.getEconomy().getBalance(p) >= d){
            area.setRented(true);
            area.setRentedOwner(p.getUniqueId());
            area.setRentedSince(System.currentTimeMillis());
            p.sendMessage(plugin.getLangManager().get("rent.rented", p));
            save(area.getParentProtection());
            if (plugin.getRealisticEconomy() != null){
                plugin.getRealisticEconomy().getServerBank().depositFromPlayerToServer(p.getUniqueId(), d);
            } else {
                plugin.getEconomy().withdrawPlayer(p, d);

            }
        } else{
            p.sendMessage(plugin.getLangManager().get("prote.no_money", p));
            return;
        }
    }
    //#region autoRent
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

    //#region unrent
    public void unrent(Area area, Player p) {
        area.setRented(false);
        area.setRentedOwner(null);
        area.setRentedSince(0);
        p.sendMessage(plugin.getLangManager().get("rent.unrented", p));
        save(area.getParentProtection());
    }


    public void save(Protection prote){
        String wName = prote.getLoc1().getWorld().getName();
        
        FileConfiguration cfg = plugin.getProtectionsData().getWorldConfig(wName);
        cfg.set(prote.getName(), prote);
        plugin.getProtectionsData().saveWorldConfig(cfg, wName);
    }

     //#region getters
    public HashMap<String, HashMap<String, Protection>> getAllProtections() {
        return mapList;
    }
    public HashMap<String, Protection> getProtectionsByWorld(World world) {
        HashMap<String, Protection> worldMap = mapList.get(world.getName());
        if (worldMap == null) {
            getProtectionDataFromWorld(world);
            worldMap = mapList.get(world.getName());
        }
        return worldMap;
    }

    public HashMap<Player, Selection> getPlayersInEditMode() {
        return playersInEditMode;
    }
    public Protection getProtectionByLocation(Location l){

        World world = l.getWorld();
        HashMap<String, Protection> worldMap = getProtectionsByWorld(world);
        if (worldMap == null || worldMap.isEmpty()) return null;
        
        for (Entry<String, Protection> entry : worldMap.entrySet()) {
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
        for (Entry<String, Protection> entry : getProtectionsByWorld(world).entrySet()) {
            String proteName = entry.getKey();
            Protection prote = entry.getValue(); 

            if (proteName.equalsIgnoreCase(name)){
                return prote;
            }
        }
        return null;
    }
    public List<Protection> getProtectionsByOwner(UUID owner){
        List<Protection> proteList = new ArrayList<>();
        for (HashMap<String, Protection> map : mapList.values()) {
            for (Protection prote : map.values()) {
                if (prote.getOwner().equals(owner)){
                    proteList.add(prote);
                }
            }
        }
        return proteList;
    }
    public boolean canCreateMore(Player player){
        if (player.hasPermission("buildprotection.admin") || player.hasPermission("buildprotection.unlimited") || player.isOp()){
            return true;
        }
        for (int i = 0; i < 100; i++) {
            String permission = "buildprotection.limit." + i;
            if (player.hasPermission(permission)) {
                if (getProtectionsByOwner(player.getUniqueId()).size() < i) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
