package com.ar.askgaming.buildprotection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.EconomyResponse;

public class Selection{
    
    private Main plugin;
    private Location loc1, loc2 = null;
    private Player player;


    public Selection(Player p, Main main) {

        main.getProtectionsManager().getPlayersInEditMode().put(p, this);

        plugin = main;
        player = p;
    }

    //#region Create protection
    public void create(String name){

        if (loc1 == null || loc2 == null){
            player.sendMessage(plugin.getDataHandler().getLang("select.must", player));
            return ;
        }
        if (loc1.equals(loc2)){
            player.sendMessage(plugin.getDataHandler().getLang("select.same", player));
            return ;
        }

        if (!loc1.getWorld().equals(loc2.getWorld())){
            //REVISAR ESTO
            player.sendMessage("Los puntos deben estar en el mismo mundo.");
            return ;
        }
        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(loc1);
        Protection prote2 = plugin.getProtectionsManager().getProtectionByLocation(loc2);
        if (prote != null || prote2 != null){
            player.sendMessage("Usa /prote create_subzone <nombre> para crear una subprotección.");
            return;
        }

        if (plugin.getEconomy() != null){
            int cost = plugin.getProtectionsManager().calculateM3(loc1, loc2);

            if (player.hasPermission("buildprotection.admin")){
                createSucces(name);
                return;
            }
            
            double price = cost * plugin.getConfig().getDouble("protection.cost_create_per_block"); 
            EconomyResponse e = plugin.getEconomy().withdrawPlayer(player, price);
    
            if (e.transactionSuccess()){
                createSucces(name);
                player.sendMessage(plugin.getDataHandler().getLang("prote.cost", player).replace("%cost%", String.valueOf(cost)));
            } else {
                player.sendMessage(plugin.getDataHandler().getLang("prote.no_money", player));
            }
        } else {
            plugin.getLogger().warning("No economy plugin found, creating protection without cost.");
            createSucces(name);
        }
    }
    //#region Inner create Area
    public void createArea(String name){
        if (loc1 == null || loc2 == null){
            player.sendMessage(plugin.getDataHandler().getLang("select.must", player));
            return ;
        }
        if (!loc1.getWorld().equals(loc2.getWorld())){
            //REVISAR ESTO
            player.sendMessage("Los puntos deben estar en el mismo mundo.");
            return ;
        }
        if (loc1.equals(loc2)){
            player.sendMessage(plugin.getDataHandler().getLang("select.same", player));
            return ;
        }
        Area area = plugin.getProtectionsManager().getAreaByLocation(loc1);
        Area area2 = plugin.getProtectionsManager().getAreaByLocation(loc2);
        if (area != area2){
            player.sendMessage("Los puntos deben estar dentro de la misma area.");
            return;
        }
        createAreaSucces(name,area.getParentProtection());

    }
    //#region Create Area
    private void createAreaSucces(String name, Protection prote){

        Location l1 = plugin.getProtectionsManager().getPlayersInEditMode().get(player).getLoc1();
        Location l2 = plugin.getProtectionsManager().getPlayersInEditMode().get(player).getLoc2();
        Area subzone = new Area(l1, l2, name, prote);
        subzone.setPriority(prote.getAreas().size()+1);
        prote.getAreas().put(name, subzone);
        prote.save();

        plugin.getProtectionsManager().getPlayersInEditMode().remove(player);
        player.sendMessage(plugin.getDataHandler().getLang("prote.subzone_create", player));
    }
    private void createSucces(String name){
        Protection prote = new Protection(loc1,loc2,player,name);
        createAreaSucces("Main",prote);
        plugin.getProtectionsManager().getPlayersInEditMode().remove(player);
        plugin.getProtectionsManager().getProtectionsByWorld(loc1.getWorld()).put(name, prote);
        player.sendMessage(plugin.getDataHandler().getLang("prote.create", player));
    }
    public void setByRadius(int radius){
        Location l = player.getLocation();
        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(l);

        if (prote != null){
            player.sendMessage(plugin.getDataHandler().getLang("select.cant", player));
            return;
        }
        if (radius < 1) {
            player.sendMessage(plugin.getDataHandler().getLang("select.too_low", player));
            return;
        }
        if (radius > 100) {
            player.sendMessage(plugin.getDataHandler().getLang("select.too_high", player));
            return;
        }

        player.sendMessage(plugin.getDataHandler().getLang("select.radius",player).replace("%radius%", String.valueOf(radius)));
        player.sendMessage(plugin.getDataHandler().getLang("select.show", player));
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        loc1 = new Location(player.getWorld(), x-radius, y-radius, z-radius);
        loc2 = new Location(player.getWorld(), x+radius, y+radius, z+radius);

        int cost = plugin.getProtectionsManager().calculateM3(loc1, loc2);
        player.sendMessage(plugin.getDataHandler().getLang("select.cost", player).replace("%cost%", String.valueOf(cost)));

    }
    public double getDistanceBetwennCorners(){

        if (loc1 != null && loc2 != null){
            return loc1.distance(loc2);
        } else return 0;
        
    }

    public Location getLoc1() {
        return loc1;
    }

    public void setLoc1(Location loc1) {

        this.loc1 = loc1;
        if (loc1 != null && loc2 != null){
            player.sendMessage(plugin.getDataHandler().getLang("select.show", player));
            int cost = plugin.getProtectionsManager().calculateM3(loc1, loc2);
            player.sendMessage(plugin.getDataHandler().getLang("select.cost", player).replace("%cost%", String.valueOf(cost)));
            return ;
        }
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
        if (loc1 != null && loc2 != null){
            player.sendMessage(plugin.getDataHandler().getLang("select.show", player));
            int cost = plugin.getProtectionsManager().calculateM3(loc1, loc2);
            player.sendMessage(plugin.getDataHandler().getLang("select.cost", player).replace("%cost%", String.valueOf(cost)));
            return ;
        }
    }
}
