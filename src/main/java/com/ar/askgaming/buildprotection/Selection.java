package com.ar.askgaming.buildprotection;

import org.bukkit.Location;
import org.bukkit.World;
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

        if (detectCollision(loc1, loc2)){
            player.sendMessage(plugin.getDataHandler().getLang("select.collision", player));
            return;
        }
        Area area = plugin.getProtectionsManager().getAreaByLocation(loc1);
        Area area2 = plugin.getProtectionsManager().getAreaByLocation(loc2);
        if (area != null || area2 != null){
            player.sendMessage(plugin.getDataHandler().getLang("prote.exists", player));
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
            player.sendMessage(plugin.getDataHandler().getLang("misc.no_economy", player));
            //createSucces(name);
        }
    }
    //#region Inner create Area
    public void createArea(String name){
        if (loc1 == null || loc2 == null){
            player.sendMessage(plugin.getDataHandler().getLang("select.must", player));
            return ;
        }

        if (loc1.equals(loc2)){
            player.sendMessage(plugin.getDataHandler().getLang("select.same", player));
            return ;
        }
        
        Area area = plugin.getProtectionsManager().getAreaByLocation(loc1);
        Area area2 = plugin.getProtectionsManager().getAreaByLocation(loc2);
        if (area != area2){
            return;
        }
        createAreaSucces(name,area.getParentProtection());

    }
    //#region expand Area
    public void expandArea(Area area){
        if (detectCollision(loc1, loc2)){
            player.sendMessage(plugin.getDataHandler().getLang("select.collision", player));
            return;
        }
        if (!checkExpand(area)){
            return;
        }
        area.setLoc1(loc1); 
        area.setLoc2(loc2);
        Protection prote = area.getParentProtection();
        prote.setLoc1(loc1);
        prote.setLoc2(loc2);
        prote.save();
        player.sendMessage(plugin.getDataHandler().getLang("prote.area_expand", player));
        plugin.getProtectionsManager().getPlayersInEditMode().remove(player);
    }
     
    private boolean checkExpand(Area area){

        if (plugin.getEconomy() == null){
            plugin.getLogger().warning("No economy plugin found, creating protection without cost.");
            player.sendMessage(plugin.getDataHandler().getLang("misc.no_economy", player));
            return true;
        }

        int actualCost = plugin.getProtectionsManager().calculateM3(area.getLoc1(), area.getLoc2());
        int newCost = plugin.getProtectionsManager().calculateM3(loc1, loc2);

        newCost = newCost - actualCost;
        double price = newCost * plugin.getConfig().getDouble("protection.cost_create_per_block"); 
        
        player.sendMessage(plugin.getDataHandler().getLang("prote.expand_cost", player).replace("%cost%", String.valueOf(price)));
        EconomyResponse e = plugin.getEconomy().withdrawPlayer(player, price);
       
        if (e.transactionSuccess()){
            player.sendMessage(plugin.getDataHandler().getLang("prote.cost", player).replace("%cost%", String.valueOf(newCost)));
           return true;
        } else {
            player.sendMessage(plugin.getDataHandler().getLang("prote.no_money", player.getPlayer()));
        }
        
        return false;
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
        plugin.getProtectionsManager().getPlayersInEditMode().remove(player);
        plugin.getProtectionsManager().getProtectionsByWorld(loc1.getWorld()).put(name, prote);
        player.sendMessage(plugin.getDataHandler().getLang("prote.create", player).replace("%name%", name));
        createAreaSucces("Main",prote);
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
    public boolean detectCollision(Location loc1, Location loc2) {
        // Asegurarse de que ambas ubicaciones estén en el mismo mundo
        if (!loc1.getWorld().equals(loc2.getWorld())) {
            return true;
        }

        World world = loc1.getWorld();

        // Determinar las esquinas inferiores y superiores
        int lowerX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int upperX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int lowerY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int upperY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int lowerZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int upperZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        // Obtener el área de la primera ubicación
        Area initialArea = plugin.getProtectionsManager().getAreaByLocation(new Location(world, lowerX, lowerY, lowerZ));

        // Recorrer todos los bloques en el cuboide
        for (int x = lowerX; x <= upperX; x++) {
            for (int y = lowerY; y <= upperY; y++) {
                for (int z = lowerZ; z <= upperZ; z++) {
                    Location currentLocation = new Location(world, x, y, z);
                    Area currentArea = plugin.getProtectionsManager().getAreaByLocation(currentLocation);

                    // Si el área es diferente, retornar false
                    if (currentArea != null && !currentArea.equals(initialArea)) {
                        return true;
                    }
                }
            }
        }

        // Si todas las áreas son iguales, retornar true
        return false;
    }
}
