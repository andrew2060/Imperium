package net.kingdomsofarden.townships.listeners;

import net.kingdomsofarden.townships.api.effects.Effect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Collection;
import java.util.HashSet;

public class ExplosiveProtectionListener implements Listener {

    public static ExplosiveProtectionListener instance;

    private Collection<Effect> protectedEffects; // TODO low priority: better algorithmic way to do this than iteration

    public ExplosiveProtectionListener() {
        protectedEffects = new HashSet<Effect>();
        instance = this;
    }

    public void registerProtectionEffect(Effect e) {
        protectedEffects.add(e);
    }

    public void unregisterProtectionEffect(Effect e) {
        protectedEffects.remove(e);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Effect effect : protectedEffects) {
            if (effect.getRegion().getBounds().isInBounds(event.getLocation())) {
                event.setYield(0.0F);
                break;
            }
        }
    }

}
