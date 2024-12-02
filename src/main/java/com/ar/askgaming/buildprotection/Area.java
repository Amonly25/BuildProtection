package com.ar.askgaming.buildprotection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Managers.ProtectionFlags.FlagType;

import net.milkbowl.vault.economy.EconomyResponse;

public class Area implements ConfigurationSerializable {

    private Main plugin = Main.getPlugin(Main.class);

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

        enterMessage = "Welcome to " + name;
        exitMessage = "Goodbye from " + name;

        plugin.getProtectionFlags().setDefaultsFlags(this);
    }

    public Area(Map<String, Object> map) {
        name = (String) map.get("name");
        loc1 = (Location) map.get("loc1");
        loc2 = (Location) map.get("loc2");
        priority = (int) map.get("priority");
        enterMessage = (String) map.get("enterMessage");
        exitMessage = (String) map.get("exitMessage");
        
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
        
        return map;
    }
    public List<String> getPlayersNames(){
        List<String> names = new ArrayList<>();
        for (UUID uuid : players){
            names.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        return names;
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
    public String getEnterMessage() {
        return enterMessage;
    }

    public void setEnterMessage(String msg) {
        enterMessage = msg;
        parentProtection.save();
    }
    public String getExitMessage() {
        return exitMessage;
    }
    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
        parentProtection.save();
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
            parentProtection.save();
            return true;
        }
        return false;
    }

    public boolean removePlayer(String playerName) {
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (players.contains(player.getUniqueId())){
            players.remove(player.getUniqueId());
            parentProtection.save();
            return true;
        }
        return false;
    }

    public void setFlag(FlagType type, boolean value) {
        getFlagsMap().put(type, value);
        parentProtection.save();
    }
    public LinkedHashMap<FlagType, Boolean> getSortedFlags(){
        List<Map.Entry<FlagType, Boolean>> entryList = new ArrayList<>(flagsMap.entrySet());

        // Ordenar la lista: los valores `true` primero y luego los `false`
        entryList.sort((entry1, entry2) -> Boolean.compare(!entry1.getValue(), !entry2.getValue()));

        // Crear un nuevo LinkedHashMap para mantener el orden
        LinkedHashMap<FlagType, Boolean> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<FlagType, Boolean> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
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
        parentProtection.save();
    }
    public Location getLoc1() {
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }
    public boolean isMain(){
        return priority == 1;
    }
    private Location getUpperLocation() {
        return getLoc1().getY() > getLoc2().getY() ? loc1 : loc2;
    }
    
    private Location getLowerLocation() {
        return getLoc1().getY() < getLoc2().getY() ? loc1 : loc2;
    }
    
    private Location getNorthLocation() {
        return getLoc1().getZ() < getLoc2().getZ() ? loc1 : loc2;
    }
    
    private Location getSouthLocation() {
        return getLoc1().getZ() > getLoc2().getZ() ? loc1 : loc2;
    }
    
    private Location getEastLocation() {
        return getLoc1().getX() > getLoc2().getX() ? loc1 : loc2;
    }
    
    private Location getWestLocation() {
        return getLoc1().getX() < getLoc2().getX() ? loc1 : loc2;
    }
    public enum Direction {
        UP,
        DOWN,
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
    public void expand(Direction direction, int amount) {
        Location loc;
        Location newLoc;

        switch (direction) {
            case UP:
                loc = getUpperLocation();
                newLoc = loc.clone().add(0, amount, 0);
                break;
            case DOWN:
                loc = getLowerLocation();
                newLoc = loc.clone().add(0, -amount, 0);
                break;
            case NORTH:
                loc = getNorthLocation();
                newLoc = loc.clone().add(0, 0, -amount);
                break;
            case SOUTH:
                loc = getSouthLocation();
                newLoc = loc.clone().add(0, 0, amount);
                break;
            case EAST:
                loc = getEastLocation();
                newLoc = loc.clone().add(amount, 0, 0);
                break;
            case WEST:
                loc = getWestLocation();
                newLoc = loc.clone().add(-amount, 0, 0);
                break;
            default:
                loc = null;
                newLoc = null;
                break;
                
        }

        if (loc != null && newLoc != null) {
            Protection protection = getParentProtection();
            Player p = Bukkit.getPlayer(protection.getOwner());
            if (getLoc1().equals(loc)) {
                plugin.getProtectionsManager().getPlayersInEditMode().get(p).setLoc1(newLoc);
                plugin.getProtectionsManager().getPlayersInEditMode().get(p).setLoc2(getLoc2());
            } else if (getLoc2().equals(loc)) {
                plugin.getProtectionsManager().getPlayersInEditMode().get(p).setLoc1(getLoc1());
                plugin.getProtectionsManager().getPlayersInEditMode().get(p).setLoc2(newLoc);

            }
        }
    }
}
