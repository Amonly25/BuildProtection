package com.ar.askgaming.buildprotection;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.buildprotection.FlagsFromListeners.Break;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Damage;
import com.ar.askgaming.buildprotection.FlagsFromListeners.EntityDamage;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Explode;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Fish;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Flow;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Ignite;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Interact;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Move;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Piston;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Place;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Ride;
import com.ar.askgaming.buildprotection.FlagsFromListeners.Teleport;
import com.ar.askgaming.buildprotection.Listeners.PlayerInteractListener;
import com.ar.askgaming.buildprotection.Listeners.PlayerMoveListener;
import com.ar.askgaming.buildprotection.Listeners.PlayerQuitListener;
import com.ar.askgaming.buildprotection.Managers.DataHandler;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags;
import com.ar.askgaming.buildprotection.Managers.ProtectionsManager;
import com.ar.askgaming.buildprotection.Managers.ShowBordersManager;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin{

    private ShowBordersManager showParticles;
    private DataHandler dataHandler;
    private ProtectionsManager protectionsManager;
    private ProtectionFlags protectionFlags;

    private Economy economy;

    public void onEnable(){

        saveDefaultConfig();

        dataHandler = new DataHandler(this);
        
        ConfigurationSerialization.registerClass(Protection.class,"Protection");
  
        protectionsManager = new ProtectionsManager(this);
        protectionFlags = new ProtectionFlags(this);

        showParticles = new ShowBordersManager(this);
        showParticles.runTaskTimer(this, 0, 20);

        Bukkit.getPluginCommand("prote").setExecutor(new Commands(this));

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        Bukkit.getPluginManager().registerEvents(new Interact(this), this);
        Bukkit.getPluginManager().registerEvents(new Break(this), this);
        Bukkit.getPluginManager().registerEvents(new Place(this), this);
        Bukkit.getPluginManager().registerEvents(new Flow(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamage(this), this);
        Bukkit.getPluginManager().registerEvents(new Damage(this), this);
        Bukkit.getPluginManager().registerEvents(new Ride(this), this);
        Bukkit.getPluginManager().registerEvents(new Fish(this), this);
        Bukkit.getPluginManager().registerEvents(new Piston(this), this);
        Bukkit.getPluginManager().registerEvents(new Teleport(this), this);
        Bukkit.getPluginManager().registerEvents(new Move(this), this);
        Bukkit.getPluginManager().registerEvents(new Ignite(this), this);
        Bukkit.getPluginManager().registerEvents(new Explode(this), this);

        //Vault Integration
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            getLogger().info("Vault found!");
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                getLogger().info("Non economy plugin found! disabling plugin");
                //getServer().getPluginManager().disablePlugin(this);
            } else {
                economy = rsp.getProvider();
                getLogger().info("Vault Economy found!");
            }

        } else {
            getLogger().info("Vault not found! disabling plugin");
            //getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    public void onDisable(){ 
    
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }
    public ShowBordersManager getShowParticles() {
        return showParticles;
    }
    public ProtectionsManager getProtectionsManager() {
        return protectionsManager;
    }
    public Economy getEconomy() {
        return economy;
    }
    public ProtectionFlags getProtectionFlags() {
        return protectionFlags;
    }

    private Economy vaultEconomy = null;

    public Economy getVault() {
        return vaultEconomy;
    }
}
