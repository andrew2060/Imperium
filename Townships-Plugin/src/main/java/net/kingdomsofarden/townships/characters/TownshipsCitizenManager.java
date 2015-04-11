package net.kingdomsofarden.townships.characters;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.characters.CitizenManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TownshipsCitizenManager implements CitizenManager {

    private TownshipsPlugin plugin;
    private Map<UUID, TownshipsCitizen> citizenMap;

    public TownshipsCitizenManager(TownshipsPlugin plugin) {
        this.plugin = plugin;
        this.citizenMap = new HashMap<UUID, TownshipsCitizen>();
    }

    @Override
    public Citizen getCitizen(UUID id) {
        TownshipsCitizen ret = citizenMap.get(id);
        if (ret == null) {
            ret = loadCitizen(id);
            citizenMap.put(id, ret);
        }
        return ret;
    }

    private TownshipsCitizen loadCitizen(UUID id) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<Citizen> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(Citizen citizen) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Citizen> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }
}
