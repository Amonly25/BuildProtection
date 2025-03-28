package com.ar.askgaming.buildprotection;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.buildprotection.FlagsFromListeners.Break;
import com.ar.askgaming.buildprotection.FlagsFromListeners.EntityDamage;
import com.ar.askgaming.buildprotection.FlagsFromListeners.EntitySpawn;
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
import com.ar.askgaming.buildprotection.Misc.LanguageManager;
import com.ar.askgaming.buildprotection.Misc.RandomTeleport;
import com.ar.askgaming.buildprotection.Misc.RentScheduler;
import com.ar.askgaming.buildprotection.Misc.ShowBordersManager;
import com.ar.askgaming.buildprotection.Protection.Area;
import com.ar.askgaming.buildprotection.Protection.Protection;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags;
import com.ar.askgaming.buildprotection.Protection.ProtectionsData;
import com.ar.askgaming.buildprotection.Protection.ProtectionsManager;
import com.ar.askgaming.buildprotection.Selection.SelectionManager;
import com.ar.askgaming.realisticeconomy.RealisticEconomy;

import net.milkbowl.vault.economy.Economy;

public class BuildProtection extends JavaPlugin{

    private static BuildProtection instance;

    private ShowBordersManager showParticles;
    private ProtectionsData protectionsData;
    private ProtectionsManager protectionsManager;
    private SelectionManager selectionManager;
    private RentScheduler rentScheduler;
    private ProtectionFlags protectionFlags;
    private RandomTeleport randomTeleport;
    private Economy economy;
    private RealisticEconomy realisticEconomy;
    private LanguageManager langManager;

    public void onEnable(){

        instance = this;
        saveDefaultConfig();

        protectionsData = new ProtectionsData(this);
        
        ConfigurationSerialization.registerClass(Protection.class,"Protection");
        ConfigurationSerialization.registerClass(Area.class,"Area");
  
        protectionsManager = new ProtectionsManager(this);
        selectionManager = new SelectionManager(this);
        protectionFlags = new ProtectionFlags(this);
        randomTeleport = new RandomTeleport(this);
        langManager = new LanguageManager(this);
        
        showParticles = new ShowBordersManager(this);

        rentScheduler = new RentScheduler(this);

        Bukkit.getPluginCommand("prote").setExecutor(new Commands(this));

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        Bukkit.getPluginManager().registerEvents(new Interact(this), this);
        Bukkit.getPluginManager().registerEvents(new Break(this), this);
        Bukkit.getPluginManager().registerEvents(new Place(this), this);
        Bukkit.getPluginManager().registerEvents(new Flow(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamage(this), this);
        Bukkit.getPluginManager().registerEvents(new Ride(this), this);
        Bukkit.getPluginManager().registerEvents(new Fish(this), this);
        Bukkit.getPluginManager().registerEvents(new Piston(this), this);
        Bukkit.getPluginManager().registerEvents(new Teleport(this), this);
        Bukkit.getPluginManager().registerEvents(new Move(this), this);
        Bukkit.getPluginManager().registerEvents(new Ignite(this), this);
        Bukkit.getPluginManager().registerEvents(new Explode(this), this);
        Bukkit.getPluginManager().registerEvents(new EntitySpawn(this), this);

        //Vault Integration
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            getLogger().info("Vault found!");
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                getLogger().info("Non economy plugin found!");
            } else {
                economy = rsp.getProvider();
                getLogger().info("Vault Economy found!");
            }

        } else {
            getLogger().info("Vault not found!");
            return;
        }
        // Not supported yet
        // if (getServer().getPluginManager().isPluginEnabled("RealisticEconomy")) {
        //     getLogger().info("RealisticEconomy found!");
        //     realisticEconomy = (RealisticEconomy) Bukkit.getPluginManager().getPlugin("RealisticEconomy");
        // } else {
        //     getLogger().info("RealisticEconomy not found!");
        // }
    }

    public void onDisable(){ 
       getServer().shutdown();
    }

    public ProtectionsData getProtectionsData() {
        return protectionsData;
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
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    public RealisticEconomy getRealisticEconomy() {
        return realisticEconomy;
    }
    public RandomTeleport getRandomTeleport() {
        return randomTeleport;
    }
    public LanguageManager getLangManager() {
        return langManager;
    }
    public static BuildProtection getInstance() {
        return instance;
    }
    public RentScheduler getRentScheduler() {
        return rentScheduler;
    }
    
}
