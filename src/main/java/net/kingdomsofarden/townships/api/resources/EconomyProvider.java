package net.kingdomsofarden.townships.api.resources;

public interface EconomyProvider {
    boolean withdraw(double amount);

    double getBalance();
}
