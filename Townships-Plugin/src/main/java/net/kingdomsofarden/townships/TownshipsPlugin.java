package net.kingdomsofarden.townships;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.configuration.Configuration;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.storage.Storage;
import net.kingdomsofarden.townships.characters.TownshipsCitizenManager;
import net.kingdomsofarden.townships.effects.TownshipsEffectManager;
import net.kingdomsofarden.townships.instrumentation.InstrumentationManager;
import net.kingdomsofarden.townships.instrumentation.agents.VaultTransactionAgent;
import net.kingdomsofarden.townships.listeners.RegionalConsistancyListener;
import net.kingdomsofarden.townships.listeners.RegionalUpdateListener;
import net.kingdomsofarden.townships.regions.TownshipsRegionManager;
import net.kingdomsofarden.townships.storage.YAMLStorage;
import net.kingdomsofarden.townships.util.TownshipsConfiguration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class TownshipsPlugin extends JavaPlugin implements ITownshipsPlugin {

    public static Economy economy;

    private TownshipsRegionManager regionManager;
    private TownshipsEffectManager effectManager;

    private TownshipsConfiguration config;

    private RegionalUpdateListener regionalUpdateListener;
    private YAMLStorage storage;


    @Override
    public void onEnable() {
        // Initialize
        Townships.setInstance(this);
        config = new TownshipsConfiguration(this);
        storage = new YAMLStorage(this);
        effectManager = new TownshipsEffectManager(this);
        regionManager = new TownshipsRegionManager(this);
        storage.loadAllRegions(regionManager);

        // Load Vault
        if (!loadEconomy()) {
            // TODO DEBUG
        }

        // Do ASM Stuff with Vault
        try {
            InstrumentationManager.attachAgentToJVM(VaultTransactionAgent.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        }

        // Register Events
        regionalUpdateListener = new RegionalUpdateListener(this);
        Bukkit.getPluginManager().registerEvents(regionalUpdateListener, this);
        Bukkit.getPluginManager().registerEvents(new RegionalConsistancyListener(), this);

        // Start tasks
        Bukkit.getScheduler().runTaskTimer(this, effectManager.getEffectTaskManager(), 0, 1);
    }

    private boolean loadEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        for (Region r : getRegions()) {
            getStorage().saveRegion(r, false);
        }
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
