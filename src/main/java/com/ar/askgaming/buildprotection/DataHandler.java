package com.ar.askgaming.buildprotection;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;


public class DataHandler {
    
    private final Main plugin;

    public DataHandler(Main main) {
        plugin = main;

        File protectionsFolder = new File(plugin.getDataFolder() + "/protections");
        if (!protectionsFolder.exists()) {
            protectionsFolder.mkdirs();
        }

        //Create world file if dosent exist
        Bukkit.getWorlds().forEach(world -> {
            
            File wFile = new File(plugin.getDataFolder()+"/protections/" + world.getName()+".yml");

            if (!wFile.exists()){
                try {
                    wFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public FileConfiguration getWorldConfig(String world){

        //Add cache maybe

        File wFile = new File(plugin.getDataFolder()+"/protections/" + world+".yml");
        FileConfiguration wConfig = new YamlConfiguration();

        try {
            wConfig.load(wFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

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
