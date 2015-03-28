package net.kingdomsofarden.townships.util;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class YAMLDataSection implements StoredDataSection {
    private ConfigurationSection backing;

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
    public <T> Optional<T> get(String path) {
        return Optional.fromNullable((T)backing.get(path));
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
    public <T> List<T> getList(String path) {
        List<T> ret = new LinkedList<T>();
        for (Object o : backing.getList(path)) {
            ret.add((T)o);
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
