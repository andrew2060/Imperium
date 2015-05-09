package net.kingdomsofarden.townships.effects.taxing;

import net.kingdomsofarden.townships.api.regions.Region;
import org.bukkit.Material;

import java.util.Map;

public interface TaxItem {
    boolean tax(Region taxer, Region taxed);

    void registerOutstanding(TaxItem tax, Region taxed);

    double getAmount();

    Map<Material, Integer> getItems();
}
