package com.ar.askgaming.buildprotection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.buildprotection.Listeners.PlayerInteractListener;
import com.ar.askgaming.buildprotection.Listeners.PlayerMoveListener;
import com.ar.askgaming.buildprotection.Listeners.PlayerQuitListener;
import com.bekvon.bukkit.residence.Residence;

import java.util.HashMap;


public class Main extends JavaPlugin
{

    private Residence residence;

    public Residence getResidence() {
        return residence;
    }
    public void onEnable(){

        saveDefaultConfig();

        Bukkit.getPluginCommand("prote").setExecutor(new Commands(this));

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        if (getServer().getPluginManager().getPlugin("Residence") != null){
            residence = (Residence) getServer().getPluginManager().getPlugin("Residence");
            
        } else {
            getLogger().severe("Residence not found, disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
        
    }
    public void onDisable(){
        
    }

    public HashMap<Player,Selection> playersInEditMode = new HashMap<>();
    public HashMap<Player,Protection> playersProtections = new HashMap<>();
}
