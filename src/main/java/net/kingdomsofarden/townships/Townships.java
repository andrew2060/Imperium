package net.kingdomsofarden.townships;

import org.bukkit.plugin.java.JavaPlugin;

public class Townships extends JavaPlugin {
    private static Townships instance;

    @Override
    public void onEnable() {
        instance = this;
    }

}
