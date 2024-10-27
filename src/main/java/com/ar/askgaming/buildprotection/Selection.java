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
            player.sendMessage("Selecciona primero las dos esquinas o establece un radio.");
            return ;
        }
        if (!loc1.getWorld().equals(loc2.getWorld())){
            player.sendMessage("Los puntos deben estar en el mismo mundo.");
            return ;
        }

        Protection prote = new Protection(loc1,loc2,player,name);
        plugin.getProtectionsManager().getPlayersInEditMode().remove(player);
        plugin.getProtectionsManager().getProtectionsByWorld(loc1.getWorld()).put(name, prote);
        player.sendMessage("Has creado con exito tu proteccion.");
    
    }

    public void setByRadius(int radius,Player p, Location l){
        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(l);

        if (prote != null){
            p.sendMessage("No puedes establecer una proteccion dentro de otra.");
            return;
        }
        if (radius < 1) {
            p.sendMessage("El radio no puede ser menos a 1.");
            return;
        }
        if (radius > 100) {
            p.sendMessage("El radio no puede ser mayor a 100.");
            return;
        }
 
        p.sendMessage("Has establecido el radio en " + radius);
        p.sendMessage("Mostrando seleccion.");
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        loc1 = new Location(p.getWorld(), x-radius, y-radius, z-radius);
        loc2 = new Location(p.getWorld(), x+radius, y+radius, z+radius);

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
            player.sendMessage("Mostrando seleccion.");
            return ;
        }
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
        if (loc1 != null && loc2 != null){
            player.sendMessage("Mostrando seleccion.");
            return ;
        }
    }
}
