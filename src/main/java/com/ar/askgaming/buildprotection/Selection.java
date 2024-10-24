package com.ar.askgaming.buildprotection;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Selection extends BukkitRunnable{
    
    private Main plugin;
    private Location loc1, loc2 = null;
    private Player player;

    public Selection(Player p, Main main) {

        main.playersInEditMode.put(p, this);
        this.runTaskTimer(main, 0L, 20L);
        plugin = main;
        player = p;
    }

    @Override
    public void run() {
        
       if (loc1 != null && loc2 != null){
          // generateParticles(loc1, loc2);
       }
    }

    public void create(){
        if (loc1 != null && loc2 != null){
           // Protection prote = new Protection(plugin,loc1,loc2,player);
            plugin.playersInEditMode.remove(player);
           // plugin.playersProtections.put(player, prote);
           // plugin.getConfig().set("test", prote);
           // plugin.saveConfig();
           plugin.getResidence().getResidenceManager().addResidence(player,"ByProte", loc1, loc2,true);
            player.sendMessage("Has creado con exito tu proteccion.");
       } else {
            player.sendMessage("Selecciona primero las dos esquinas o establece un radio.");
       }
    }

    public void setByRadius(int radius,Player p){

        int x = p.getLocation().getBlockX();
        int y = p.getLocation().getBlockY();
        int z = p.getLocation().getBlockZ();

        loc1 = new Location(p.getWorld(), x-radius, y-radius, z-radius);
        loc2 = new Location(p.getWorld(), x+radius, y+radius, z+radius);

        plugin.getResidence().getSelectionManager().placeLoc1(p, loc1,true);
        plugin.getResidence().getSelectionManager().placeLoc2(p, loc2,true);
      //  plugin.getResidence().getSelectionManager().placeLoc1
    }

    private Particle.DustOptions dustOptions = new Particle.DustOptions(Color.YELLOW, 1);

     private void generateParticles(Location loc1, Location loc2) {
        double x1 = Math.min(loc1.getX(), loc2.getX());
        double y1 = Math.min(loc1.getY(), loc2.getY());
        double z1 = Math.min(loc1.getZ(), loc2.getZ());

        double x2 = Math.max(loc1.getX(), loc2.getX());
        double y2 = Math.max(loc1.getY(), loc2.getY());
        double z2 = Math.max(loc1.getZ(), loc2.getZ());

        // Generate particles along the X edges
        for (double x = x1; x <= x2; x++) {
            loc1.getWorld().spawnParticle(Particle.DUST, x, y1, z1, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x, y1, z2, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x, y2, z1, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x, y2, z2, 1, 0, 0, 0, 0, dustOptions);
        }

        // Generate particles along the Y edges
        for (double y = y1; y <= y2; y++) {
            loc1.getWorld().spawnParticle(Particle.DUST, x1, y, z1, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x1, y, z2, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x2, y, z1, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x2, y, z2, 1, 0, 0, 0, 0, dustOptions);
        }

        // Generate particles along the Z edges
        for (double z = z1; z <= z2; z++) {
            loc1.getWorld().spawnParticle(Particle.DUST, x1, y1, z, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x1, y2, z, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x2, y1, z, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x2, y2, z, 1, 0, 0, 0, 0, dustOptions);
        }
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
