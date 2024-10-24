package com.ar.askgaming.buildprotection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.buildprotection.Listeners.PlayerInteractListener;
import com.ar.askgaming.buildprotection.Listeners.PlayerMoveListener;
import com.ar.askgaming.buildprotection.Listeners.PlayerQuitListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main extends JavaPlugin{


    public void onEnable(){

        saveDefaultConfig();

        Bukkit.getPluginCommand("prote").setExecutor(new Commands(this));

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        
    }
    public void onDisable(){
        
    }

    public HashMap<Player,Selection> playersInEditMode = new HashMap<>();
    public List<Protection> getAllProtections = new ArrayList<>();
}
