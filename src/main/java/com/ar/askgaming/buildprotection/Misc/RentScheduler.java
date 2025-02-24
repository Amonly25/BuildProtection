package com.ar.askgaming.buildprotection.Misc;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.buildprotection.BuildProtection;
import com.ar.askgaming.buildprotection.Protection.Area;

public class RentScheduler extends BukkitRunnable{

    private int rentDays = 7;
    private boolean autoRent = true;

    private BuildProtection plugin;
    public RentScheduler(BuildProtection main){
        plugin = main;

        rentDays = plugin.getConfig().getInt("rent.time_in_days");
        autoRent = plugin.getConfig().getBoolean("rent.auto_rent");
    }

    @Override
    public void run() {

        if (!autoRent){
            return;
        }
        long time = System.currentTimeMillis();

        plugin.getProtectionsManager().getAllProtections().forEach((world, protections) -> {
            protections.forEach((name, protection) -> {
                for (Area area : protection.getAreas().values()){
                    if (area.isRented()){

                        long rentTime = area.getRentedSince();
                        long inDays = rentDays * 24 * 60 * 60 * 1000;
                        if (time - rentTime > inDays){

                            OfflinePlayer renter = Bukkit.getOfflinePlayer(area.getRentedOwner());
                            plugin.getProtectionsManager().autoRent(area, renter);
                        }
                    }
                }
            });
        });
    }
}
