package net.kingdomsofarden.townships.api.util;

public interface Serializer<T> {
    String serialize(T obj);
    T deserialize(String input);
}
