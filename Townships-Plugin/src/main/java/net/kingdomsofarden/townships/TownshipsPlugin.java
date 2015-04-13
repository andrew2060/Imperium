package net.kingdomsofarden.townships;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.configuration.Configuration;
import net.kingdomsofarden.townships.api.storage.Storage;
import net.kingdomsofarden.townships.characters.TownshipsCitizenManager;
import net.kingdomsofarden.townships.effects.TownshipsEffectManager;
import net.kingdomsofarden.townships.listeners.RegionalUpdateListener;
import net.kingdomsofarden.townships.regions.TownshipsRegionManager;
import net.kingdomsofarden.townships.storage.YAMLStorage;
import net.kingdomsofarden.townships.util.TownshipsConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TownshipsPlugin extends JavaPlugin implements ITownshipsPlugin {

    private TownshipsRegionManager regionManager;
    private TownshipsEffectManager effectManager;

    private TownshipsConfiguration config;

    private RegionalUpdateListener regionalUpdateListener;
    private YAMLStorage storage;


    @Override
    public void onEnable() {
        // Initialize
        Townships.setInstance(this);
        regionManager = new TownshipsRegionManager(this);
        effectManager = new TownshipsEffectManager(this);
        config = new TownshipsConfiguration(this);
        storage = new YAMLStorage(this);

        // Register Events
        regionalUpdateListener = new RegionalUpdateListener(this);
        Bukkit.getPluginManager().registerEvents(regionalUpdateListener, this);

        // Start tasks
        Bukkit.getScheduler().runTaskTimer(this, effectManager.getEffectTaskManager(), 0, 1);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public TownshipsRegionManager getRegions() {
        return regionManager;
    }

    @Override
    public TownshipsCitizenManager getCitizens() {
        return null; //TODO
    }

    @Override
    public TownshipsEffectManager getEffectManager() {
        return effectManager;
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    public <T extends ITownshipsPlugin> T getBackingImplementation() {
        return (T) this;
    }
}
