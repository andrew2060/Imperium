package net.kingdomsofarden.townships.util;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public class YAMLDataSection implements StoredDataSection {
    private ConfigurationSection backing;

    public YAMLDataSection(ConfigurationSection section) {
        this.backing = section;
    }

    @Override
    public String getCurrentPath() {
        return backing.getCurrentPath();
    }

    @Override
    public StoredDataSection getSection(String path) {
        return new YAMLDataSection(backing.getConfigurationSection(path));
    }

    @Override
    public <T> Optional<T> get(String path) {
        return null;
    }

    @Override
    public <T> Optional<T> get(String path, Serializer<T> deserializer) {
        String get = backing.getString(path);
        if (get != null) {
            return Optional.fromNullable(deserializer.deserialize(get));
        } else {
            return Optional.absent();
        }
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return backing.getKeys(deep);
    }
}
