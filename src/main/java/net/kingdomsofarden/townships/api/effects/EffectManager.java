package net.kingdomsofarden.townships.api.effects;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Manages loading, scheduling, and creation of new effect instances
 */
public interface EffectManager {
    Effect loadEffect(String name, ConfigurationSection config); //TODO: abstractify configuration (for easier sponge porting)
}
