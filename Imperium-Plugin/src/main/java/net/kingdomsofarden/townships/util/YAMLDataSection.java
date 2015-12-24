package net.kingdomsofarden.townships.util;

import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class YAMLDataSection implements StoredDataSection {
    protected ConfigurationSection backing;

    public YAMLDataSection(ConfigurationSection section) {
        this.backing = section;
    }

    @SuppressWarnings("unchecked") @Override public <T> T getBackingImplementation() {
        return (T) backing;
    }

    @Override public String getCurrentPath() {
        return backing.getCurrentPath();
    }

    @Override public StoredDataSection getSection(String path) {
        return new YAMLDataSection(backing.getConfigurationSection(path));
    }

    @Override public String get(String path, String def) {
        String ret = backing.getString(path);
        if (ret == null) {
            ret = def;
        }
        return ret;
    }

    @Override public <T> T get(String path, Serializer<T> deserializer, T def) {
        String get = backing.getString(path);
        return get == null ? def : deserializer.deserialize(get);
    }

    @Override public List<String> getList(String path) {
        return backing.getList(path).stream().map(o -> (String) o)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override public <T> List<T> getList(String path, Serializer<T> deserializer) {
        return backing.getStringList(path).stream().map(deserializer::deserialize)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override public Set<String> getKeys(boolean deep) {
        return backing.getKeys(deep);
    }

    @Override public void set(String path, Object object) {
        backing.set(path, object);
    }

    @Override public <T> void set(String path, T obj, Serializer<T> serializer) {
        backing.set(path, serializer.serialize(obj));
    }
}
