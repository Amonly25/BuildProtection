package com.ar.askgaming.buildprotection;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Protection implements Serializable{

    private static final long serialVersionUID = 1L;

    private Main plugin;
    private Player owner;
    private ProtectionFlags flags;
    private String name;
    private Location loc1, loc2;

    public Protection() {}

    public Protection(Main main, Location loc1, Location loc2, Player player) {
        plugin = main;
        owner = player;
        name = player.getName() + "'s protection";
        this.loc1 = loc1;
        this.loc2 = loc2;
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

    public ProtectionFlags getFlags() {
        return flags;
    }

    public void setFlags(ProtectionFlags flags) {
        this.flags = flags;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
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
