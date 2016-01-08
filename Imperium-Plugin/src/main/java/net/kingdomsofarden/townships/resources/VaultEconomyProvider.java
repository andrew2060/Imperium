package net.kingdomsofarden.townships.resources;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;

import java.util.UUID;


public class VaultEconomyProvider implements EconomyProvider {

    private UUID accountUid;
    private String identifier;

    public VaultEconomyProvider(UUID accountUid, FunctionalRegion region, String identifier) {
        this.accountUid = accountUid;
        this.identifier = identifier;
        if (!TownshipsPlugin.economy.getBanks().contains(accountUid.toString())) {
            TownshipsPlugin.economy.createBank(accountUid.toString(), Bukkit.getOfflinePlayer(
                region.getRole(RoleGroup.ROOT).iterator().next())); // Guaranteed to always have one
        }
    }

    @Override public String getIdentifier() {
        return identifier;
    }

    @Override public boolean withdraw(double amount) {
        if (TownshipsPlugin.economy.bankHas(accountUid.toString(), amount).type
            .equals(ResponseType.SUCCESS))
            return TownshipsPlugin.economy.bankWithdraw(accountUid.toString(), amount)
                .transactionSuccess();
        else
            return false;
    }

    @Override public double getBalance() {
        return TownshipsPlugin.economy.bankBalance(accountUid.toString()).balance;
    }

    @Override public double deposit(double amount) {
        return TownshipsPlugin.economy.bankDeposit(accountUid.toString(), amount).amount;
    }

}
