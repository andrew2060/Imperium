package net.kingdomsofarden.townships;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.characters.TownshipsCitizenManager;
import net.kingdomsofarden.townships.effects.TownshipsEffectManager;
import net.kingdomsofarden.townships.listeners.PlayerListener;
import net.kingdomsofarden.townships.regions.TownshipsRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TownshipsPlugin extends JavaPlugin implements ITownshipsPlugin {

    private TownshipsRegionManager regionManager;
    private TownshipsEffectManager effectManager;

    private PlayerListener playerListener;

    @Override
    public void onEnable() {
        // Initialize
        Townships.setInstance(this);
        regionManager = new TownshipsRegionManager(this);
        effectManager = new TownshipsEffectManager(this);

        // Register Events
        playerListener = new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(playerListener, this);

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
