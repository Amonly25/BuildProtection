package com.ar.askgaming.buildprotection.Protection;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ar.askgaming.buildprotection.BuildProtection;

public class ProtectionsData {
    
    private final BuildProtection plugin;
    private final File protectionsFolder;

    public ProtectionsData(BuildProtection main) {
        plugin = main;
        protectionsFolder = new File(plugin.getDataFolder() + "/protections");
        load();
    }
    public void load(){
        if (!protectionsFolder.exists()) {
            protectionsFolder.mkdirs();
        }

        //Create world file if dosent exist
        Bukkit.getWorlds().forEach(world -> createWorldFile(world.getName()));

    }
    private void createWorldFile(String world) {
        File worldFile = getWorldFile(world);
        
        if (!worldFile.exists()) {
            try {
                if (worldFile.createNewFile()) {
                    plugin.getLogger().info("Created protection file for world: " + world);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create protection file for world: " + world);
                e.printStackTrace();
            }
        }
    }
    
    private File getWorldFile(String world) {
        return new File(protectionsFolder, world + ".yml");
    }
    
    public FileConfiguration getWorldConfig(String world) {
        if (!existsWorldFile(world)) {
            createWorldFile(world);
        }
        return YamlConfiguration.loadConfiguration(getWorldFile(world));
    }
    
    private boolean existsWorldFile(String world) {
        return getWorldFile(world).exists();
    }
    
    public void saveWorldConfig(FileConfiguration config, String world) {
        try {
            config.save(getWorldFile(world));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save protection file for world: " + world);
            e.printStackTrace();
        }
    }
}
