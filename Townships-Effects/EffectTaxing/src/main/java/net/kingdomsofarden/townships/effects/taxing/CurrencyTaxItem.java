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
            if (provider.getIdentifier().equals("tax-account")) {
                if (!provider.withdraw(amount)) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
