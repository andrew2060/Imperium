package net.kingdomsofarden.townships.effects.taxing;

import net.kingdomsofarden.townships.api.regions.Region;

public interface TaxItem {
    boolean tax(Region taxer, Region taxed);
}
