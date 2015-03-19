package net.kingdomsofarden.townships;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.CitizenManager;
import net.kingdomsofarden.townships.api.regions.RegionManager;
import net.kingdomsofarden.townships.listeners.PlayerListener;
import net.kingdomsofarden.townships.regions.TownshipsRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class TownshipsPlugin extends JavaPlugin implements ITownshipsPlugin {

    private RegionManager regionManager;
    private PlayerListener playerListener;

    @Override
    public void onEnable() {
        // Initialize
        Townships.setInstance(this);
        regionManager = new TownshipsRegionManager();

        // Register Events
        playerListener = new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(playerListener, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public RegionManager getRegions() {
        return regionManager;
    }

    @Override
    public CitizenManager getCitizens() {
        return null; //TODO
    }
}
