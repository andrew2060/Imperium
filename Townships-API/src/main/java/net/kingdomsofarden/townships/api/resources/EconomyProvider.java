package net.kingdomsofarden.townships.api.resources;

public interface EconomyProvider extends ResourceProvider {

    public static final String TREASURY = "TREASURY";

    String getIdentifier();

    boolean withdraw(double amount);

    double getBalance();

    double deposit(double amount);
}
