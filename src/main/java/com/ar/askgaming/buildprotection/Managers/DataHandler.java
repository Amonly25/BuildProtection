package com.ar.askgaming.buildprotection.Managers;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Main;

import net.md_5.bungee.api.ChatColor;


public class DataHandler {
    
    private final Main plugin;

    public DataHandler(Main main) {
        plugin = main;

        File protectionsFolder = new File(plugin.getDataFolder() + "/protections");
        if (!protectionsFolder.exists()) {
            protectionsFolder.mkdirs();
        }

        // Save default lang file from resources if it doesn't exist
        File defaultLangFile = new File(plugin.getDataFolder() + "/lang/en.yml");
        if (!defaultLangFile.exists()) {
            plugin.saveResource("lang/en.yml", false);
            
        }
        File esLangFile = new File(plugin.getDataFolder() + "/lang/es.yml");
        if (!esLangFile.exists()) {
            plugin.saveResource("lang/es.yml", false);
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
    
    public String getLang(String path, Player p) {

        File file = new File(plugin.getDataFolder() + "/lang/" + p.getLocale().split("_")[0] + ".yml");
        String required = "";
    
        if (!file.exists()) {
            required = getDefaultLang(path);
        } else {
            FileConfiguration langFile = new YamlConfiguration();
            try {
                langFile.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
    
            if (langFile.isList(path)) {
                StringBuilder result = new StringBuilder();
                for (String s : langFile.getStringList(path)) {
                    result.append(s).append("\n");
                }
                required = result.toString().trim();
            } else {
                required = langFile.getString(path, getDefaultLang(path));
            }
        }
        
        return ChatColor.translateAlternateColorCodes('&', required);
    }

    private String getDefaultLang(String path){

        //Add cache maybe

        File file = new File(plugin.getDataFolder() + "/lang/en.yml");
        FileConfiguration lang = new YamlConfiguration();

        try {
            lang.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        if (lang.isList(path)) {
            StringBuilder result = new StringBuilder();
            for (String s : lang.getStringList(path)) {
                result.append(s).append("\n");
            }
            return result.toString().trim();
        }

        return lang.getString(path,"Undefined key: " + path);
    }
}
