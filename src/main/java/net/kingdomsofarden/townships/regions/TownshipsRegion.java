package net.kingdomsofarden.townships.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.BoundingBox;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.UUID;

public class TownshipsRegion implements Region {

    private UUID regionUid;
    private String name;

    private Collection<Citizen> owners;
    private Collection<Citizen> citizens;

    private Collection<Effect> effects;
    private Collection<TickableEffect> tickableEffects;

    private BoundingBox bounds;
    private Location center;

    public TownshipsRegion(ConfigurationSection config) {
        //TODO
    }

    @Override
    public UUID getUid() {
        return regionUid;
    }

    @Override
    public Optional<String> getName() {
        return Optional.fromNullable(name);
    }

    @Override
    public Collection<Citizen> getOwners() {
        return owners;
    }

    @Override
    public Collection<Citizen> getCitizens() {
        return citizens;
    }

    @Override
    public Location getLocation() {
        return center;
    }

    @Override
    public BoundingBox getBounds() {
        return bounds;
    }

    @Override
    public Collection<Effect> getEffects() {
        return effects;
    }
}
