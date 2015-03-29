package net.kingdomsofarden.townships.util;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.configuration.Configuration;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TownshipsConfiguration extends YAMLDataSection implements Configuration {

    private File regionConfigs;
    private Map<String, File> regionDefaults;
    private ConfigurationSection config;

    public TownshipsConfiguration(TownshipsPlugin plugin) {
        super(plugin.getConfig());
        regionDefaults = new HashMap<String, File>();
        regionConfigs = new File(plugin.getDataFolder(), "regions");
        regionConfigs.mkdirs();
        for (File file : FileUtils.listFiles(regionConfigs, new String[] {"yml"}, true)) {
            String name = file.getName().replace(".yml", "");
            regionDefaults.put(name.toLowerCase(), file);
        }
        config = backing;
        loadConstants();
    }

    private void loadConstants() {
        Constants.EFFECT_SPREAD_DELAY = config.getInt("effects.tick-max-delay");
        Constants.INTERACT_TYPES = new HashSet<Material>();
        for (String mat : config.getStringList("protection.protect-interact")) {
            Constants.INTERACT_TYPES.add(Material.valueOf(mat.toUpperCase()));
        }
        Constants.ACCESS_TYPES = new HashSet<Material>();
        for (String mat : config.getStringList("protection.protect-access")) {
            Constants.ACCESS_TYPES.add(Material.valueOf(mat.toUpperCase()));
        }
        Constants.PROTECT_FIRE = config.getBoolean("protection.protect-flint-and-steel", false);
    }

    @Override
    public Optional<StoredDataSection> getRegionConfiguration(String name) {
        if (!regionDefaults.containsKey(name.toLowerCase())) {
            return Optional.absent();
        }
        StoredDataSection data = new YAMLDataSection(YamlConfiguration.loadConfiguration(regionDefaults.get(name.toLowerCase())));
        data.set("name", name);
        return Optional.of(data);
    }
}
