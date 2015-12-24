package net.kingdomsofarden.townships.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

import java.util.*;

public class JSONDataSection implements StoredDataSection {

    private static final String PATHS_DELIMITER = "\\.";
    private JsonObject currObject;
    private String parentPath;

    public JSONDataSection(JsonObject object, String pathname) {
        this.currObject = object;
        this.parentPath = pathname;
    }

    @SuppressWarnings("unchecked") @Override public <T> T getBackingImplementation() {
        return (T) currObject;
    }

    @Override public String getCurrentPath() {
        return parentPath;
    }

    @Override public StoredDataSection getSection(String path) {
        if (!currObject.has(path)) {
            currObject.add(path, new JsonObject());
        }
        return new JSONDataSection(currObject.getAsJsonObject(path), parentPath +
            "." + path);
    }

    @Override public String get(String path, String def) {
        String[] parsed = path.split(PATHS_DELIMITER);
        if (parsed.length > 1) {
            if (!currObject.has(parsed[0])) {
                return def;
            }
            JsonObject childObj = currObject.getAsJsonObject(path);
            StringBuilder child = new StringBuilder();
            boolean first = true;
            for (int i = 1; i < parsed.length; i++) {
                if (first) {
                    first = false;
                } else {
                    child.append(".");
                }
                child.append(parsed[i]);
            }
            return new JSONDataSection(childObj, parentPath + "." + path)
                .get(child.toString(), def);
        } else {
            return currObject.has(path) ? currObject.get(path).getAsString() : def;
        }
    }

    @Override public <T> T get(String path, Serializer<T> deserializer, T def) {
        String[] parsed = path.split(PATHS_DELIMITER);
        if (parsed.length > 1) {
            if (!currObject.has(parsed[0])) {
                return def;
            }
            JsonObject childObj = currObject.getAsJsonObject(path);
            StringBuilder child = new StringBuilder();
            boolean first = true;
            for (int i = 1; i < parsed.length; i++) {
                if (first) {
                    first = false;
                } else {
                    child.append(".");
                }
                child.append(parsed[i]);
            }
            return new JSONDataSection(childObj, parentPath + "." + path)
                .get(child.toString(), deserializer, def);
        } else {
            return currObject.has(path) ?
                deserializer.deserialize(currObject.get(path).getAsString()) :
                def;
        }
    }

    @Override public List<String> getList(String path) {
        String[] parsed = path.split(PATHS_DELIMITER);
        if (parsed.length > 1) {
            if (!currObject.has(parsed[0])) {
                return Collections.emptyList();
            }
            JsonObject childObj = currObject.getAsJsonObject(path);
            StringBuilder child = new StringBuilder();
            boolean first = true;
            for (int i = 1; i < parsed.length; i++) {
                if (first) {
                    first = false;
                } else {
                    child.append(".");
                }
                child.append(parsed[i]);
            }
            return new JSONDataSection(childObj, parentPath + "." + path).getList(child.toString());
        } else {
            if (currObject.has(path)) {
                JsonArray arr = currObject.get(path).getAsJsonArray();
                List<String> ret = new ArrayList<>(arr.size());
                for (int i = 0; i < arr.size(); i++) {
                    ret.add(i, arr.get(i).getAsString());
                }
                return ret;
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override public <T> List<T> getList(String path, Serializer<T> deserializer) {
        String[] parsed = path.split(PATHS_DELIMITER);
        if (parsed.length > 1) {
            if (!currObject.has(parsed[0])) {
                return Collections.emptyList();
            }
            JsonObject childObj = currObject.getAsJsonObject(path);
            StringBuilder child = new StringBuilder();
            boolean first = true;
            for (int i = 1; i < parsed.length; i++) {
                if (first) {
                    first = false;
                } else {
                    child.append(".");
                }
                child.append(parsed[i]);
            }
            return new JSONDataSection(childObj, parentPath + "." + path)
                .getList(child.toString(), deserializer);
        } else {
            if (currObject.has(path)) {
                JsonArray arr = currObject.get(path).getAsJsonArray();
                List<T> ret = new ArrayList<>(arr.size());
                for (int i = 0; i < arr.size(); i++) {
                    ret.add(i, deserializer.deserialize(arr.get(i).getAsString()));
                }
                return ret;
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override public Set<String> getKeys(boolean deep) {
        Set<String> ret = new HashSet<>();
        if (deep) {
            Set<String> sub = new HashSet<>();
            currObject.entrySet().stream().forEach(e -> {
                ret.add(e.getKey());
                if (e.getValue().isJsonObject()) {
                    sub.add(e.getKey());
                }
            });
            String currPath = parentPath + ".";
            for (String path : sub) {
                new JSONDataSection(currObject.getAsJsonObject(path), parentPath + "." + path)
                    .getKeys(true).stream().forEach(key -> ret.add(key.replaceFirst(currPath, "")));
            }
        } else {
            currObject.entrySet().stream().forEach(e -> ret.add(e.getKey()));
        }
        return ret;
    }

    @Override public void set(String path, Object object) {
        String[] parsed = path.split(PATHS_DELIMITER);
        if (parsed.length > 1) {
            StringBuilder child = new StringBuilder();
            boolean first = true;
            for (int i = 1; i < parsed.length; i++) {
                if (first) {
                    first = false;
                } else {
                    child.append(".");
                }
                child.append(parsed[i]);
            }
            getSection(parsed[0]).set(child.toString(), object);
        } else {
            currObject.addProperty(path, object.toString());
        }
    }

    @Override public <T> void set(String path, T object, Serializer<T> serializer) {
        String[] parsed = path.split(PATHS_DELIMITER);
        if (parsed.length > 1) {
            StringBuilder child = new StringBuilder();
            boolean first = true;
            for (int i = 1; i < parsed.length; i++) {
                if (first) {
                    first = false;
                } else {
                    child.append(".");
                }
                child.append(parsed[i]);
            }
            getSection(parsed[0]).set(child.toString(), object, serializer);
        } else {
            currObject.addProperty(path, serializer.serialize(object));
        }
    }
}
