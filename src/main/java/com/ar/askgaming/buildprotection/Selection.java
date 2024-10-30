package com.ar.askgaming.buildprotection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Selection{
    
    private Main plugin;
    private Location loc1, loc2 = null;
    private Player player;

    public Selection(Player p, Main main) {

        main.getProtectionsManager().getPlayersInEditMode().put(p, this);

        plugin = main;
        player = p;
    }

    public void create(String name){

        //Añadir costo cuando haga la economia

        if (loc1 == null || loc2 == null){
            player.sendMessage(plugin.getDataHandler().getLang("select.must", player));
            return ;
        }
        if (!loc1.getWorld().equals(loc2.getWorld())){
            //REVISAR ESTO
            player.sendMessage("Los puntos deben estar en el mismo mundo.");
            return ;
        }

        Protection prote = new Protection(loc1,loc2,player,name);
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
            return ;
        }
    }
}
