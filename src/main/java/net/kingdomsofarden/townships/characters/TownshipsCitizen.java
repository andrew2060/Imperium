package net.kingdomsofarden.townships.characters;

import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Area;

import java.util.UUID;

public class TownshipsCitizen implements Citizen {

    private UUID uuid;
    private Area currTerminalArea;

    @Override
    public Area getCurrentArea() {
        return currTerminalArea;
    }

    @Override
    public void setCurrentArea(Area area) {
        if (currTerminalArea != null) {
            currTerminalArea.getCitizensInArea().remove(this);
        }
        currTerminalArea = area;
        if (area != null) {
            currTerminalArea.getCitizensInArea().add(this);
        }
    }
}
