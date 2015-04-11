package net.kingdomsofarden.townships.util;

import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class YAMLDataSection implements StoredDataSection {
    protected ConfigurationSection backing;

    public YAMLDataSection(ConfigurationSection section) {
        this.backing = section;
    }

    @Override
    public Object getBackingImplementation() {
        return backing;
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
    public String get(String path, String def) {
        String ret = backing.getString(path);
        if (ret == null) {
            ret = def;
        }
        return ret;
    }

    @Override
    public <T> T get(String path, Serializer<T> deserializer, T def) {
        String get = backing.getString(path);
        return get == null ? def : deserializer.deserialize(get);
    }

    @Override
    public List<String> getList(String path) {
        List<String> ret = new LinkedList<String>();
        for (Object o : backing.getList(path)) {
            ret.add((String)o);
        }
        return ret;
    }

    @Override
    public <T> List<T> getList(String path, Serializer<T> deserializer) {
        List<T> ret = new LinkedList<T>();
        for (String o : backing.getStringList(path)) {
            ret.add(deserializer.deserialize(o));
        }
        return ret;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return backing.getKeys(deep);
    }

    @Override
    public void set(String path, Object object) {
        backing.set(path, object);
    }

    @Override
    public void set(String path, Object obj, Serializer serializer) {
        backing.set(path, serializer.serialize(obj));
    }
}
