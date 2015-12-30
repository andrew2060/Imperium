package net.kingdomsofarden.townships.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.kingdomsofarden.townships.command.interactive.ChatInteractiveCommandStep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatBasedInteractiveCommandListener implements Listener {



    static Cache<UUID, ChatInteractiveCommandStep> cache;

    static {
        cache = CacheBuilder.newBuilder().expireAfterAccess(180, TimeUnit.SECONDS).build();
    }

    public static void register(UUID player, ChatInteractiveCommandStep step) {
        cache.put(player, step);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        UUID player = event.getPlayer().getUniqueId();
        if (cache.asMap().containsKey(player)) {
            ChatInteractiveCommandStep entry = cache.getIfPresent(player);
            if (entry != null) {
                cache.invalidate(player);
                entry.process(event.getMessage());
            }
        }
    }
}
