package net.kingdomsofarden.townships.effects.taxing;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;

public class CurrencyTaxItem implements TaxItem {

    private final double amount;

    public CurrencyTaxItem(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean tax(Region taxer, Region taxed) {
        EconomyProvider selected = null;
        for (EconomyProvider provider : taxer.getEconomyProviders()) {
            if (provider.getIdentifier().equals("tax-accot"))
            }
        }

        return false;
    }
}
