package com.ar.askgaming.buildprotection.Managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Area;
import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection;
import com.ar.askgaming.buildprotection.Selection;

import net.milkbowl.vault.economy.EconomyResponse;

public class SelectionManager {

    private BuildProtection plugin;
    public SelectionManager(BuildProtection main){
        plugin = main;
    }

    //#region Expand area
    public boolean expandArea(Area area, Selection selection){
        Player player = selection.getPlayer();
        if (selection.detectCollision()){
            player.sendMessage(plugin.getDataHandler().getLang("select.collision", player));
            return false;
        }

        if (player.hasPermission("buildprotection.admin")){
            expandSuccess(area, selection);
            return true;
        }

        double get = getExpandCost(area, selection);
        EconomyResponse e = plugin.getEconomy().withdrawPlayer(player, get);
       
        if (e.transactionSuccess()){
            expandSuccess(area, selection);
            return true;

        } else {
            player.sendMessage(plugin.getDataHandler().getLang("prote.no_money", player.getPlayer()));
            return false;
        }
    }
    private void expandSuccess(Area area, Selection selection){
        Player player = selection.getPlayer();
        area.setLoc1(selection.getLoc1());
        area.setLoc2(selection.getLoc2());
        Protection prote = area.getParentProtection();
        prote.setLoc1(selection.getLoc1());
        prote.setLoc2(selection.getLoc2());
        plugin.getProtectionsManager().save(prote);
        plugin.getProtectionsManager().getPlayersInEditMode().remove(player);
    }
    public boolean hasExpandCost(Area area, Selection selection){

        Player player = selection.getPlayer();

        if (player.hasPermission("buildprotection.admin")){
            return true;
        }
        if (plugin.getEconomy() == null){
            plugin.getLogger().warning("No economy plugin found, creating protection without cost.");
            player.sendMessage(plugin.getDataHandler().getLang("misc.no_economy", player));
            return false;
        }
        if (plugin.getEconomy().getBalance(player) >= getExpandCost(area, selection)){
            return true;
        }

        return false;
    }
    public double getExpandCost(Area area, Selection selection){
        int actualCost = plugin.getProtectionsManager().calculateM3(area.getLoc1(), area.getLoc2());
        int newCost = plugin.getProtectionsManager().calculateM3(selection.getLoc1(), selection.getLoc2());

        newCost = newCost - actualCost;
        double price = newCost * plugin.getConfig().getDouble("protection.cost_create_per_block",1); 

        return price;
    }
    public enum Direction {
        UP,
        DOWN,
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
    public void expandSelection(Area area, Selection sel, Direction direction, int amount) {
        Location loc;
        Location newLoc;

        switch (direction) {
            case UP:
                loc = area.getUpperLocation();
                newLoc = loc.clone().add(0, amount, 0);
                break;
            case DOWN:
                loc = area.getLowerLocation();
                newLoc = loc.clone().add(0, -amount, 0);
                break;
            case NORTH:
                loc = area.getNorthLocation();
                newLoc = loc.clone().add(0, 0, -amount);
                break;
            case SOUTH:
                loc = area.getSouthLocation();
                newLoc = loc.clone().add(0, 0, amount);
                break;
            case EAST:
                loc = area.getEastLocation();
                newLoc = loc.clone().add(amount, 0, 0);
                break;
            case WEST:
                loc = area.getWestLocation();
                newLoc = loc.clone().add(-amount, 0, 0);
                break;
            default:
                loc = null;
                newLoc = null;
                break;
                
        }

        if (area.getLoc1().equals(loc)) {
            sel.setLoc1(newLoc);
            sel.setLoc2(area.getLoc2());
        } else if (area.getLoc2().equals(loc)) {
            sel.setLoc1(area.getLoc1());
            sel.setLoc2(newLoc);
        }
    }
}
