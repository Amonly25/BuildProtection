package com.ar.askgaming.buildprotection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class Protection implements ConfigurationSerializable{

    private Main plugin = Main.getPlugin(Main.class);

    private UUID owner;

    private String name;
    private Location loc1, loc2;

    private HashMap<String, Area> areas = new HashMap<>();

    public HashMap<String, Area> getAreas() {
        return areas;
    }

    //#region Deserialization
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

    public Protection(Location loc1, Location loc2, Player player, String proteName) {

        owner = player.getUniqueId();
        
        name = proteName;
        this.loc1 = loc1;
        this.loc2 = loc2;

        save();
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

    public void setOwner(OfflinePlayer owner) {
        this.owner = owner.getUniqueId();
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

    public boolean isAdminProtection(Player player){
        
        if (player.hasPermission("buildprotection.admin")){
            return true;
        }
        if (isOwner(player)){
            return true;
        }

        return false;
    }

    public String getOwnerName(){
        return Bukkit.getOfflinePlayer(owner).getName(); 
    }
    public boolean isInside(Location check){

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
    public Area getMainArea(){
        for (Area area : areas.values()) {
            if (area.getPriority() == 1){
                return area;
            }
        }
        return null;
    }
}
