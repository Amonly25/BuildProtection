package com.ar.askgaming.buildprotection.Selection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.Area;
import com.ar.askgaming.buildprotection.Protection.Protection;

import net.milkbowl.vault.economy.EconomyResponse;

public class Selection{
    
    private BuildProtection plugin;
    private Location loc1, loc2 = null;
    private Player player;

    public Selection(Player p, BuildProtection main) {

        main.getProtectionsManager().getPlayersInEditMode().put(p, this);

        plugin = main;
        player = p;
    }

    private String getLang(String key){
        return plugin.getLangManager().getLang(key, player);
    }

    //#region Create protection
    public void preCreateProtection(String name){

        if (!isValidSelection()){
            return;
        }

        if (detectCollision()){
            player.sendMessage(getLang("select.collision"));
            return;
        }
        Area area = plugin.getProtectionsManager().getAreaByLocation(loc1);
        Area area2 = plugin.getProtectionsManager().getAreaByLocation(loc2);
        if (area != null || area2 != null){
            player.sendMessage(getLang("select.collision"));
            return;
        }
        if (player.hasPermission("buildprotection.admin")){
            createProtection(name);
            return;
        }
        
        if (plugin.getEconomy() != null){
            int cost = plugin.getProtectionsManager().calculateM3(loc1, loc2);
            
            double price = cost * plugin.getConfig().getDouble("protection.cost_create_per_block"); 
            EconomyResponse e = plugin.getEconomy().withdrawPlayer(player, price);
    
            if (e.transactionSuccess()){
                createProtection(name);
                player.sendMessage(getLang("prote.cost").replace("%cost%", String.valueOf(cost)));
            } else {
                player.sendMessage(getLang("prote.no_money"));
            }
        } else {
            plugin.getLogger().warning("No economy plugin found, creating protection without cost.");
            player.sendMessage(getLang("misc.no_economy"));
            //createSucces(name);
        }
    }
    private void createProtection(String name){
        Protection prote = new Protection(loc1,loc2,player,name);
        plugin.getProtectionsManager().getPlayersInEditMode().remove(player);
        plugin.getProtectionsManager().getProtectionsByWorld(loc1.getWorld()).put(name, prote);
        player.sendMessage(getLang("prote.create").replace("%name%", name));
        createArea("Main",prote);
        
    }
    //#endregion
    //#region Create Area
    public void preCreateArea(String name){
        
        if (!isValidSelection()){
            return;
        }
        
        Area area = plugin.getProtectionsManager().getAreaByLocation(loc1);
        Area area2 = plugin.getProtectionsManager().getAreaByLocation(loc2);
        if (area != area2){
            player.sendMessage(getLang("select.same_area"));
            return;
        }
        createArea(name,area.getParentProtection());

    }
    private void createArea(String name, Protection prote){

        Area subzone = new Area(loc1, loc2, name, prote);
        subzone.setPriority(prote.getAreas().size()+1);
        prote.getAreas().put(name, subzone);
        plugin.getProtectionsManager().save(prote);

        plugin.getProtectionsManager().getPlayersInEditMode().remove(player);
        player.sendMessage(getLang("prote.subzone_create"));
    }
    //#region Set by radius
    public void setByRadius(int radius){
        Location l = player.getLocation();
        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(l);
        int limit = plugin.getConfig().getInt("protection.max_radius");
        if (prote != null){
            player.sendMessage(getLang("select.cant"));
            return;
        }
        if (radius < 1) {
            player.sendMessage(getLang("select.too_low"));
            return;
        }
        if (radius > limit) {
            player.sendMessage(getLang("select.too_high"));
            return;
        }

        player.sendMessage(getLang("select.radius").replace("%radius%", String.valueOf(radius)));
        player.sendMessage(getLang("select.show"));
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        loc1 = new Location(player.getWorld(), x-radius, y-radius, z-radius);
        loc2 = new Location(player.getWorld(), x+radius, y+radius, z+radius);

        int cost = plugin.getProtectionsManager().calculateM3(loc1, loc2);
        player.sendMessage(getLang("select.cost").replace("%cost%", String.valueOf(cost)));

    }
    //#region setters
    public double getDistanceBetwennCorners(){

        if (loc1 != null && loc2 != null){
            return loc1.distance(loc2);
        } else return 0;
        
    }

    public Location getLoc1() {
        return loc1;
    }
    public Location getLoc2() {
        return loc2;
    }
    public Player getPlayer() {
        return player;
    }
    
    private boolean isRentedArea(Location loc){
        Area area = plugin.getProtectionsManager().getAreaByLocation(loc);
        if (area != null){
            return area.isRentable();
        }
        return false;
    }
    public void setLoc1(Location loc) {
        if (!insideLimits(loc)){
            return;
        }
        if (isRentedArea(loc)){
            player.sendMessage(getLang("rent.select.rented"));
            return;
        }
        this.loc1 = loc;
        if (loc1 != null && loc2 != null){
            player.sendMessage(getLang("select.show"));
            int cost = plugin.getProtectionsManager().calculateM3(loc1, loc2);
            player.sendMessage(getLang("select.cost").replace("%cost%", String.valueOf(cost)));
            return ;
        }
    }
    public void setLoc2(Location loc) {
        if (!insideLimits(loc)){
            return;
        }
        if (isRentedArea(loc)){
            player.sendMessage(getLang("rent.select.rented"));
            return;
        }
        this.loc2 = loc;
        if (loc1 != null && loc2 != null){
            player.sendMessage(getLang("select.show"));
            int cost = plugin.getProtectionsManager().calculateM3(loc1, loc2);
            player.sendMessage(getLang("select.cost").replace("%cost%", String.valueOf(cost)));
            return ;
        }
    }
    private boolean insideLimits(Location toVerify) {
        int limitY = plugin.getConfig().getInt("protection.max_height");
        int limitXZ = plugin.getConfig().getInt("protection.max_width");
    
        if (player.hasPermission("protection.admin")) {
            return true;
        }

        if (loc1 == null && loc2 == null) {
            return true;
        }
    
        Location reference = (loc1 != null) ? loc1 : loc2;
    
        if (Math.abs(reference.getBlockY() - toVerify.getBlockY()) > limitY) {
            player.sendMessage(getLang("select.too_high"));
            return false;
        }
    
        if (Math.abs(reference.getBlockX() - toVerify.getBlockX()) > limitXZ || 
            Math.abs(reference.getBlockZ() - toVerify.getBlockZ()) > limitXZ) {
            player.sendMessage(getLang("select.too_high"));
            return false;
        }
        return true;
    }
    //#region Detect collision
    public boolean detectCollision() {
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
        Area initialArea = plugin.getProtectionsManager().getAreaByLocation(loc1);
        if (initialArea == null){
            initialArea = plugin.getProtectionsManager().getAreaByLocation(loc2);
        }
        // Test, esto no debería ser necesario
        // if (initialArea == null){
        //     return true;
        // }
        
        // Recorrer todos los bloques en el cuboide
        for (int x = lowerX; x <= upperX; x++) {
            for (int y = lowerY; y <= upperY; y++) {
                for (int z = lowerZ; z <= upperZ; z++) {
                    Location currentLocation = new Location(world, x, y, z);
                    Area currentArea = plugin.getProtectionsManager().getAreaByLocation(currentLocation);

                    // Si el área es diferente, true
                    if (currentArea != null && !currentArea.equals(initialArea)) {
                        return true;
                    }
                }
            }
        }

        // Si no se detecta ninguna colisión, false
        return false;
    }
    //#region isValid
    private boolean isValidSelection(){
        if (loc1 == null || loc2 == null){
            player.sendMessage(getLang("select.must"));
            return false;
        }
        if (loc1.equals(loc2)){
            player.sendMessage(getLang("select.same"));
            return false;
        }
        if (loc1.getZ() == loc2.getZ() || loc1.getX() == loc2.getX()|| loc1.getY() == loc2.getY()){
            player.sendMessage(getLang("select.flat"));
            return false;
        }
        return true;
    }
    
}
