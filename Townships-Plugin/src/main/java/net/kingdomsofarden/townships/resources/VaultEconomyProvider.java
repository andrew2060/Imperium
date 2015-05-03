package net.kingdomsofarden.townships.resources;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import org.bukkit.Bukkit;

import java.util.UUID;


public class VaultEconomyProvider implements EconomyProvider {

    private UUID accountUid;
    private int priority;
    private String identifier;

    public VaultEconomyProvider(UUID accountUid, Region region, int priority, String identifier) {
        this.accountUid = accountUid;
        this.priority = priority;
        this.identifier = identifier;
        if (!TownshipsPlugin.economy.getBanks().contains(accountUid.toString())) {
            TownshipsPlugin.economy.createBank(accountUid.toString(), Bukkit.getOfflinePlayer(region.getRole(RoleGroup.ROOT).iterator().next())); // Guaranteed to always have one
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean withdraw(double amount) {
        return TownshipsPlugin.economy.bankWithdraw(accountUid.toString(), amount).transactionSuccess();
    }

    @Override
    public double getBalance() {
        return TownshipsPlugin.economy.bankBalance(accountUid.toString()).balance;
    }

    @Override
    public double deposit(double amount) {
        return TownshipsPlugin.economy.bankDeposit(accountUid.toString(), amount).amount;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
