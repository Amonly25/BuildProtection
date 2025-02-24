package com.ar.askgaming.buildprotection.Protection;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ar.askgaming.buildprotection.BuildProtection;


public class ProtectionsData {
    
    private final BuildProtection plugin;

    public ProtectionsData(BuildProtection main) {
        plugin = main;

        File protectionsFolder = new File(plugin.getDataFolder() + "/protections");
        if (!protectionsFolder.exists()) {
            protectionsFolder.mkdirs();
        }

        //Create world file if dosent exist
        Bukkit.getWorlds().forEach(world -> {
            createWorldFile(world.getName());
        });
    }
    public void createWorldFile(String world){
        File wFile = new File(plugin.getDataFolder()+"/protections/" + world+".yml");

        if (!wFile.exists()){
            try {
                wFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean existsWorldFile(String world){
        File wFile = new File(plugin.getDataFolder()+"/protections/" + world+".yml");
        return wFile.exists();
    }
    public FileConfiguration getWorldConfig(String world){

        plugin.getLogger().info("Loading protections from world file: "+world);

        //Add cache maybe
        if (!existsWorldFile(world)){
            createWorldFile(world);
        }
        
        File wFile = new File(plugin.getDataFolder()+"/protections/" + world+".yml");
        FileConfiguration wConfig = YamlConfiguration.loadConfiguration(wFile);
        return wConfig;
    }

    public void saveWorldConfig(FileConfiguration config, String world){

        File wFile = new File(plugin.getDataFolder()+"/protections/" + world+".yml");

        try {
            config.save(wFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
