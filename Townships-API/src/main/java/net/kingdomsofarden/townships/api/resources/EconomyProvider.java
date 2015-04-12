package net.kingdomsofarden.townships.api.resources;

public interface EconomyProvider extends ResourceProvider {
    boolean withdraw(double amount);

    double getBalance();

    double deposit(double amount);
}
