package com.ar.askgaming.buildprotection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

public class Area implements ConfigurationSerializable {

    private BuildProtection plugin = BuildProtection.getPlugin(BuildProtection.class);

    private String name;
    private int priority = 0;

    private Location loc1, loc2;

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
    }

    private String enterMessage;
    private String exitMessage;

    private boolean isRentable;
    private boolean isRented;
    private UUID rentedOwner;
    private double rentCost;
    private long rentedSince;

    private Protection parentProtection;
    public void setParentProtection(Protection parentProtection) {
        this.parentProtection = parentProtection;
    }

    private HashMap<FlagType, Boolean> flagsMap = new HashMap<>();
    private List<UUID> players = new ArrayList<>();

    public List<UUID> getPlayers() {
        return players;
    }

    public Area(Location loc1, Location loc2, String name, Protection parent) {
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.name = name;
        parentProtection = parent;

        enterMessage = "Welcome to " + parent.getName();
        exitMessage = "Goodbye from " + parent.getName();

        isRentable = false;
        rentedOwner = null;
        rentCost = plugin.getProtectionsManager().calculateM3(loc1, loc2) * plugin.getConfig().getDouble("rent.cost_per_block",0.01);
        rentedSince = 0;
        isRented = false;
        
        plugin.getProtectionFlags().setDefaultsFlags(this);
    }

    public Area(Map<String, Object> map) {
        name = (String) map.get("name");
        loc1 = (Location) map.get("loc1");
        loc2 = (Location) map.get("loc2");
        priority = (int) map.get("priority");
        enterMessage = (String) map.get("enterMessage");
        exitMessage = (String) map.get("exitMessage");

        if (map.get("isRentable") != null){
            isRentable = (boolean) map.get("isRentable");
        } else isRentable = false;
        
        if (map.get("rentedOwner") != null){
            rentedOwner = UUID.fromString((String) map.get("rentedOwner"));
        } else rentedOwner = null;

        if (map.get("rentCost") != null){
            rentCost = (double) map.get("rentCost");
        } else rentCost = 0;

        if (map.get("rentedSince") != null){
            rentedSince = (long) map.get("rentedSince");
        } else rentedSince = 0;

        if (map.get("isRented") != null){
            isRented = (boolean) map.get("isRented");
        } else isRented = false;

        Object playersObj = map.get("players");
        if (playersObj instanceof List<?>) {
            players = new ArrayList<>();
            for (Object obj : (List<?>) playersObj) {
                if (obj instanceof String) {
                    players.add(UUID.fromString((String) obj));
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
        }
    }

    //#region Serialization
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();

        HashMap<String, Boolean> toStringFlag = new HashMap<>();
        flagsMap.forEach((key, value) -> toStringFlag.put(key.toString(), value));

        map.put("name", name);
        map.put("loc1", loc1);
        map.put("loc2", loc2);
        map.put("flags", toStringFlag);
        map.put("enterMessage", enterMessage);
        map.put("exitMessage", exitMessage);
        map.put("priority", priority);
        map.put("players", players.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("isRentable", isRentable);
        map.put("rentedOwner", rentedOwner.toString());
        map.put("rentCost", rentCost);
        map.put("rentedSince", rentedSince);
        map.put("isRented", isRented);
        
        return map;
    }
    public UUID getRentedOwner() {
        return rentedOwner;
    }
    public String getRentedOwnerName(){
        return Bukkit.getOfflinePlayer(rentedOwner).getName();
    }

    public void setRentedOwner(UUID rentedOwner) {
        this.rentedOwner = rentedOwner;
    }
    public List<String> getPlayersNames(){
        List<String> names = new ArrayList<>();
        for (UUID uuid : players){
            names.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        return names;
    }

    public String getEnterMessage() {
        return enterMessage;
    }

    public void setEnterMessage(String msg) {
        enterMessage = msg;
    }
    public String getExitMessage() {
        return exitMessage;
    }
    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }
    public HashMap<FlagType, Boolean> getFlagsMap() {
        return flagsMap;
    }

    public void setFlagsMap(HashMap<FlagType, Boolean> flagsMap) {
        this.flagsMap = flagsMap;
    }
    public boolean addPlayer(String playerName) {
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (player.hasPlayedBefore()){
            players.add(player.getUniqueId());
            return true;
        }
        return false;
    }

    public boolean removePlayer(String playerName) {
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (players.contains(player.getUniqueId())){
            players.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    public void setFlag(FlagType type, boolean value) {
        getFlagsMap().put(type, value);
    }

    public Protection getParentProtection() {
        return parentProtection;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public Location getLoc1() {
        return loc1;
    }
    public boolean isRentable() {
        return isRentable;
    }

    public void setRentable(boolean isRentable) {
        this.isRentable = isRentable;
    }
    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean isRented) {
        this.isRented = isRented;
    }

    public Location getLoc2() {
        return loc2;
    }
    public boolean isMain(){
        return priority == 1;
    }
    public Location getUpperLocation() {
        return getLoc1().getY() > getLoc2().getY() ? loc1 : loc2;
    }
    
    public Location getLowerLocation() {
        return getLoc1().getY() < getLoc2().getY() ? loc1 : loc2;
    }
    
    public Location getNorthLocation() {
        return getLoc1().getZ() < getLoc2().getZ() ? loc1 : loc2;
    }
    
    public Location getSouthLocation() {
        return getLoc1().getZ() > getLoc2().getZ() ? loc1 : loc2;
    }
    
    public Location getEastLocation() {
        return getLoc1().getX() > getLoc2().getX() ? loc1 : loc2;
    }
    
    public Location getWestLocation() {
        return getLoc1().getX() < getLoc2().getX() ? loc1 : loc2;
    }
    public double getRentCost() {
        return rentCost;
    }

    public void setRentCost(double rentCost) {
        this.rentCost = rentCost;
    }
    public long getRentedSince() {
        return rentedSince;
    }

    public void setRentedSince(long rentedSince) {
        this.rentedSince = rentedSince;
    }
}
