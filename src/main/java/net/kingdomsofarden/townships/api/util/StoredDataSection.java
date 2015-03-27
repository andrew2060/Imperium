package net.kingdomsofarden.townships.api.util;

import com.google.common.base.Optional;

import java.util.List;
import java.util.Set;

/**
 * Represents a section of stored data, which can further be divided into even more sub-divisions if necessary
 */
public interface StoredDataSection {

    static final String PATH_SEPARATOR = ".";

    /**
     * @return The memory section that backs
     */
    Object getBackingImplementation();

    /**
     * @return The current path represented by the String
     */
    String getCurrentPath();

    /**
     * @param path The path to the data section to obtain, separated by the {@link #PATH_SEPARATOR}
     * @return The corresponding stored data section,
     */
    StoredDataSection getSection(String path);

    /**
     * Gets the object stored at the given path via direct typecasting for most cases
     * @param path The path to the data, separated by the {@link #PATH_SEPARATOR}
     * @param <T> The type of the data to retrieve
     * @return The data stored at the given path, if present
     */
    <T> Optional<T> get(String path);

    /**
     * Gets the object stored at the given path using the parameter serializer
     * @param path The path to the data, separated by the {@link #PATH_SEPARATOR}
     * @param <T> The type of the data to retrieve
     * @param deserializer The serializer to use to deserialize the object from a string
     * @return The data stored at the given path, if present, as deserialized by the parameter serializer
     */
    <T> Optional<T> get(String path, Serializer<T> deserializer);

    /**
     * Gets the list stored at the given path via direct typecasting of elements for most cases
     * @param path The path to retrieve the list from
     * @param <T> The type of a list
     * @return A List of the elements, or an empty list if none exist
     */
    <T> List<T> getList(String path);

    /**
     * Gets the list stored at the given path  using the parameter serializer
     * @param path The path to retrieve the list from
     * @param deserializer The serializer to use to deserialize the elements from a string
     * @param <T> The type of a list
     * @return A List of the elements, or an empty list if none exist
     */
    <T> List<T> getList(String path, Serializer<T> deserializer);

    /**
     * @param deep Whether to get the keys of children as well
     * @return The keys present for the given storage section, or an empty set if not present
     */
    Set<String> getKeys(boolean deep);

    /**
     * Sets the value at path to object, using the object's built in toString method
     * @param path The path to store at
     * @param object The object to store
     */
    void set(String path, Object object);

    /**
     * Sets the value at path to object, using the parameter serializer
     * @param path The path to store at
     * @param object The object to store
     * @param serializer The serializer to serialiaze the object with
     */
    void set(String path, Object object, Serializer serializer);
}
