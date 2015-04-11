package net.kingdomsofarden.townships.api.util;

/**
 * Responsible for guiding serialization/deserialization
 * @param <T> The type to serialize/deserialize
 */
public interface Serializer<T> {
    String serialize(T obj);
    T deserialize(String input);
}
