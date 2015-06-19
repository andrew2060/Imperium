package net.kingdomsofarden.townships.characters;

import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TownshipsCitizen implements Citizen {

    //TODO load citizen, constructor

    private UUID uuid;
    private Area currTerminalArea;
    private StoredDataSection effectSettings;
    private Region region;
    private Set<Region> activeRegions;

    public TownshipsCitizen() {
        this.activeRegions = new HashSet<>();
    }

    @Override public StoredDataSection getStoredEffectSettings(Effect effect) {
        return effectSettings;
    }

    @Override public Collection<RoleGroup> getRoles(Region region) {
        return region.getRoles(this);
    }

    @Override public Area getCurrentArea() {
        return currTerminalArea;
    }

    @Override public void setCurrentArea(Area area) {
        if (currTerminalArea != null) {
            currTerminalArea.getCitizensInArea().remove(this);
        }
        currTerminalArea = area;
        if (area != null) {
            currTerminalArea.getCitizensInArea().add(this);
        }
    }

    @Override public UUID getUid() {
        return uuid;
    }

    @Override public boolean isRoot() {
        return false; //TODO
    }

    @Override public Region getCitizenRegion() {
        return region;
    }

    @Override public Set<Region> getActiveRegions() {
        return activeRegions;
    }

    @Override public void setActiveRegions(Set<Region> currActive) {
        this.activeRegions = currActive;
    }
}
