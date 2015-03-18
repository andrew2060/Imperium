package net.kingdomsofarden.townships;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.regions.RegionManager;
import net.kingdomsofarden.townships.regions.TownshipsRegionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TownshipsPlugin extends JavaPlugin implements ITownshipsPlugin {

    private RegionManager regionManager;

    @Override
    public void onEnable() {
        Townships.setInstance(this);
        regionManager = new TownshipsRegionManager();
    }

    @Override
    public RegionManager getRegions() {
        return regionManager;
    }
}
