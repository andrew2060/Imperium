package net.kingdomsofarden.townships.util;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TownshipsConfiguration {

    private File regionConfigs;
    private Map<String, File> regionDefaults;

    public TownshipsConfiguration(TownshipsPlugin plugin) {
        regionDefaults = new HashMap<String, File>();
        regionConfigs = new File(plugin.getDataFolder(), "regions");
        regionConfigs.mkdirs();
        for (File file : FileUtils.listFiles(regionConfigs, new String[] {"yml"}, true)) {
            String name = file.getName().replace(".yml", "");
            regionDefaults.put(name.toLowerCase(), file);
        }
    }

    StoredDataSection getRegionConfiguration(String name) {
        StoredDataSection data = new YAMLDataSection(YamlConfiguration.loadConfiguration(regionDefaults.get(name)));
        data.set("name", name);
        return data;
    }
}
