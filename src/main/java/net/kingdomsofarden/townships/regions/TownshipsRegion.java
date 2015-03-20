package net.kingdomsofarden.townships.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.BoundingBox;
import net.kingdomsofarden.townships.util.Constants;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.UUID;

public class TownshipsRegion implements Region {

    private long load;
    private long profiledTicks;
    private double finalLoad;
    private boolean profiled;

    private UUID regionUid;
    private String name;

    private Collection<Citizen> owners;
    private Collection<Citizen> citizens;

    private Collection<Effect> effects;

    private BoundingBox bounds;
    private Location center;

    public TownshipsRegion(ConfigurationSection config) {
        load = 1;
        profiledTicks = 1;
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

    public void tick(boolean profiling) {
        boolean runProfiler = !profiled && profiling;

        long start = 0;
        if (runProfiler) {
            if (profiledTicks >= Constants.MAX_PROFILING_LENGTH_REGION_TICKS) {
                runProfiler = false;
                profiled = true;
            } else {
                start = System.currentTimeMillis();
            }
        }
        for (Effect effect : effects) {
            effect.onTick(this);
        }
        if (runProfiler) { // TODO: Better Load Determination than having a (by default 10 minutes) profiling upper bound
            long currLoad = System.currentTimeMillis() - start;
            load += currLoad;
            profiledTicks++;
        }
    }

    public double getLoad() {
        return profiled ? finalLoad : profiledTicks > 0 ? load/ (double) profiledTicks : 0;
    }
}
