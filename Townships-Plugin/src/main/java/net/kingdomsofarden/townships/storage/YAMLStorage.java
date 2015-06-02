package net.kingdomsofarden.townships.storage;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.storage.Storage;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.regions.TownshipsRegion;
import net.kingdomsofarden.townships.util.YAMLDataSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public class YAMLStorage implements Storage {

    private final TownshipsPlugin plugin;
    private final File regionSaves;

    public YAMLStorage(TownshipsPlugin plugin) {
        this.plugin = plugin;
        this.regionSaves = new File(plugin.getDataFolder(), "saves");
        this.regionSaves.mkdir();
    }

    @Override public Region loadRegion(UUID id) {
        File f = new File(regionSaves,
            id + ".yml"); // An exist check is not necessary as a blank configuration is retrieved
        StoredDataSection section = new YAMLDataSection(YamlConfiguration.loadConfiguration(f));
        Region r = new TownshipsRegion(id, section);
        plugin.getRegions().add(r);
        return r;
    }

    @Override public void saveRegion(Region r, boolean async) {
        File f = new File(regionSaves, r.getUid() + ".yml");
        FileConfiguration save = new YamlConfiguration();
        r.saveConfigs(new YAMLDataSection(save));
        try {
            save.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void removeRegion(UUID id) {
        File f = new File(regionSaves, id + ".yml");
        if (f.exists()) {
            f.delete();
        }
    }

    @Override public void loadAllRegions(Collection<Region> regions) {
        for (String name : regionSaves.list()) {
            try {
                if (name.toLowerCase().endsWith(".yml")) {
                    UUID uid = UUID.fromString(name.substring(0, name.length() - 4));
                    loadRegion(uid);
                }
            } catch (Exception e) {
                // TODO debug
            }
        }
    }
}
