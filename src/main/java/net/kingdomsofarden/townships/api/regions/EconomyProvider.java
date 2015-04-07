package net.kingdomsofarden.townships.api.regions;

public interface EconomyProvider {
    boolean withdraw(double amount);

    double getBalance();
}
