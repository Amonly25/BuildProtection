package com.ar.askgaming.buildprotection.Managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.buildprotection.Main;
import com.ar.askgaming.buildprotection.Protection;

public class ShowBordersManager extends BukkitRunnable {

    private Main plugin;

    public ShowBordersManager(Main main) {
        plugin = main;
    }

    private List<Protection> showProtections = new ArrayList<>();
    public List<Protection> getShowProtections() {
        return showProtections;
    }

    @Override
    public void run() {
      
        if (!showProtections.isEmpty()) {
            showProtections.forEach(prote -> {
                generateParticles(prote.getLoc1(), prote.getLoc2());
            });
        }
        if (!plugin.getProtectionsManager().getPlayersInEditMode().isEmpty()) {
            plugin.getProtectionsManager().getPlayersInEditMode().forEach((player, selection) -> {
                if (selection.getLoc1() != null && selection .getLoc2() != null){
                    if (selection.getDistanceBetwennCorners() < 100){
                        generateParticles(selection.getLoc1(), selection.getLoc2());
                    }
                }
            });
        }
    }
    private Particle.DustOptions dustOptions = new Particle.DustOptions(Color.YELLOW, 1);

    private void generateParticles(Location loc1, Location loc2) {

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        // Crear partículas en los bordes del cubo
        World world = loc1.getWorld();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    // Partículas en los lados exteriores del cuadro
                    if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                        Location particleLocation = new Location(world, x + 0.5, y + 0.5, z + 0.5);
                        loc1.getWorld().spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, dustOptions);
                    }
                }
            }
        }
    } 
}
