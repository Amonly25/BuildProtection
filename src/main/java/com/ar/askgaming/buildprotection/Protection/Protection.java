package com.ar.askgaming.buildprotection.Protection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class Protection implements ConfigurationSerializable{

    private final UUID owner;
    private final HashMap<String, Area> areas = new HashMap<>();
    private String name;
    private Location loc1, loc2;

    //#region Constructors
    public Protection(Location loc1, Location loc2, Player player, String proteName) {

        owner = player.getUniqueId();
        
        name = proteName;
        this.loc1 = loc1;
        this.loc2 = loc2;
    }
    @SuppressWarnings("unchecked")
    public Protection(Map<String, Object> map) {

        loc1 =  (Location) map.get("loc1");
        loc2 = (Location) map.get("loc2");
        owner = UUID.fromString((String) map.get("owner"));

        Map<String, Object> areasMap = (Map<String, Object>) map.get("areas");
        areasMap.forEach((key, value) -> {
            Area area = (Area) value;
            area.setParentProtection(this);
            areas.put(key, area);
        });

    }

    //#region Serialization
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("loc1", loc1);
        map.put("loc2", loc2);
        map.put("owner", owner.toString());
        map.put("areas", areas);

        return map;
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

    public Area getMainArea(){
        for (Area area : areas.values()) {
            if (area.getPriority() == 1){
                return area;
            }
        }
        return null;
    }
    //#region getters
    public HashMap<String, Area> getAreas() {
        return areas;
    }
    public String getOwnerName(){
        return Bukkit.getOfflinePlayer(owner).getName(); 
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }
    public boolean isOwner(Player player){
        return owner.equals(player.getUniqueId());
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

}
