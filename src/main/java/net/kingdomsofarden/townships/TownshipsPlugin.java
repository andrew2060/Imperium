package net.kingdomsofarden.townships;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.characters.TownshipsCitizenManager;
import net.kingdomsofarden.townships.effects.TownshipsEffectManager;
import net.kingdomsofarden.townships.listeners.BlockProtectionListener;
import net.kingdomsofarden.townships.listeners.ExplosiveProtectionListener;
import net.kingdomsofarden.townships.listeners.RegionalUpdateListener;
import net.kingdomsofarden.townships.regions.TownshipsRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TownshipsPlugin extends JavaPlugin implements ITownshipsPlugin {

    private TownshipsRegionManager regionManager;
    private TownshipsEffectManager effectManager;

    private RegionalUpdateListener regionalUpdateListener;

    @Override
    public void onEnable() {
        // Initialize
        Townships.setInstance(this);
        regionManager = new TownshipsRegionManager(this);
        effectManager = new TownshipsEffectManager(this);

        // Register Events
        regionalUpdateListener = new RegionalUpdateListener(this);
        Bukkit.getPluginManager().registerEvents(regionalUpdateListener, this);
        Bukkit.getPluginManager().registerEvents(new ExplosiveProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockProtectionListener(this), this);

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
}
