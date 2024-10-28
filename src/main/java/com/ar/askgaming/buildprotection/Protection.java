package com.ar.askgaming.buildprotection;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;


import com.ar.askgaming.buildprotection.ProtectionFlags.FlagType;

public class Protection implements ConfigurationSerializable{

    private Main plugin = Main.getPlugin(Main.class);
    private String message;
    private String owner;
    private String name;
    private Location loc1, loc2;
    private HashMap<FlagType, Boolean> flagsMap = new HashMap<>();
    private List<String> players = new ArrayList<>();

    public List<String> getPlayers() {
        return players;
    }

    public Protection(Map<String, Object> map) {
        message = (String) map.get("message");
        loc1 =  (Location) map.get("loc1");
        loc2 = (Location) map.get("loc2");
        owner = (String) map.get("owner");

        Object playersObj = map.get("players");
        if (playersObj instanceof List<?>) {
            players = new ArrayList<>();
            for (Object obj : (List<?>) playersObj) {
                if (obj instanceof String) {
                    players.add((String) obj);
                }
            }
        }

        Object flagsObj = map.get("flags");
        if (flagsObj instanceof HashMap<?, ?>) {
            flagsMap = new HashMap<>();
            ((HashMap<?, ?>) flagsObj).forEach((key, value) -> {
                if (key instanceof String && value instanceof Boolean) {
                    flagsMap.put(FlagType.valueOf((String) key), (Boolean) value);
                }
            });
        } else {
            flagsMap = new HashMap<>();
        }
    }

    public Protection(Location loc1, Location loc2, Player player, String proteName) {

        owner = player.getName();
        message = "Welcome to " + proteName;
        
        name = proteName;
        this.loc1 = loc1;
        this.loc2 = loc2;

        plugin.getProtectionsManager().setDefaultsFlags(this);

        save();
    }

    public boolean isInsideArea(Location check){

        double x1 = Math.min(loc1.getX(), loc2.getX());
        double x2 = Math.max(loc1.getX(), loc2.getX());
        double y1 = Math.min(loc1.getY(), loc2.getY());
        double y2 = Math.max(loc1.getY(), loc2.getY());
        double z1 = Math.min(loc1.getZ(), loc2.getZ());
        double z2 = Math.max(loc1.getZ(), loc2.getZ());

        double checkX = check.getX();
        double checkY = check.getY();
        double checkZ = check.getZ();

        if (checkX >= x1 && checkX <= x2 && checkY >= y1 && checkY <= y2 && checkZ >= z1 && checkZ <= z2) {
            return true;
        }

        return false;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }
    public boolean isOwner(Player player){
        return owner.equals(player);
    }

    public void setOwner(String owner) {
        this.owner = owner;
        save();
    }
    public Location getLoc1() {
        return loc1;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        message = msg;
        save();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        //Convertir los flags a string para su guardado.
        HashMap<String, Boolean> toStringFlag = new HashMap<>();
        flagsMap.forEach((key, value) -> toStringFlag.put(key.toString(), value));

        map.put("message", message);
        map.put("loc1", loc1);
        map.put("loc2", loc2);
        map.put("owner", owner);
        map.put("flags", toStringFlag);
        map.put("players", players);
        return map;
    }
    public static Protection deserialize(Map<String, Object> map) {
        return new Protection(map);
    }

    public void save(){

        String wName = loc1.getWorld().getName();
        FileConfiguration cfg = plugin.getDataHandler().getWorldConfig(wName);
        if (cfg != null){
            cfg.set(name, this);
            plugin.getDataHandler().saveWorldConfig(cfg, wName);
            plugin.getLogger().info(wName + " guardado");
        }
    }
    public Location getCenter() {
        double centerX = (loc1.getX() + loc2.getX()) / 2;
        double centerZ = (loc1.getZ() + loc2.getZ()) / 2;
        double centerY = (loc1.getY() + loc2.getY()) / 2;
    
        // Obtén el mundo y calcula la altura del centro en el rango de Y de loc1 a loc2
        World world = loc1.getWorld();
    
        // Busca la primera posición con un bloque sólido y dos bloques de aire arriba
        for (int i = 0; i < 255; i++) {
            Location center = new Location(world, centerX, centerY+i, centerZ);
            if (world.getBlockAt(center).getType() == Material.AIR &&
                world.getBlockAt(center.clone().add(0, 1, 0)).getType() == Material.AIR) {
                return center;
            }
        }
    
        // Si no se encuentra una posición válida, devuelve el centro en el punto medio de Y

        return null;
    }
    public HashMap<FlagType, Boolean> getFlagsMap() {
        return flagsMap;
    }

    public void setFlagsMap(HashMap<FlagType, Boolean> flagsMap) {
        this.flagsMap = flagsMap;
    }
    public boolean isAdminProtection(Player player){
        
        if (player.hasPermission("buildprotection.admin")){
            return true;
        }
        if (isOwner(player)){
            return true;
        }

        return false;
    }

    public void addPlayer(String string) {
        players.add(string);
        save();
    }

    public void removePlayer(String string) {
        players.remove(string);
        save();
    }

    public void setFlag(FlagType type, boolean value) {
        getFlagsMap().put(type, value);
        save();
    }
}
