package net.kingdomsofarden.townships.command;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.command.Command;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.command.selection.Selection;
import net.kingdomsofarden.townships.command.selection.SelectionManager;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CommandCreateRegion implements Command {

    @Override
    public String[] getIdentifiers() {
        return new String[] {"region create", "town create"};
    }

    @Override
    public String getPermission() {
        return "townships.regions.create";
    }

    @Override
    public int getMaxArguments() {
        return 1;
    }

    @Override
    public int getMinArguments() {
        return 1;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // Find the type of region being created
        if (!(sender instanceof Player)) {
            Messaging.sendFormattedMessage(sender, I18N.PLAYER_ONLY_COMMAND);
            return true;
        }
        Optional<StoredDataSection> dataOptional = Townships.getConfiguration().getRegionConfiguration(args[0]);
        if (!dataOptional.isPresent()) {
            Messaging.sendFormattedMessage(sender, I18N.REGION_TYPE_NOT_FOUND, args[0].toLowerCase());
        }
        Serializer<Integer> intSerializer = new Serializer<Integer>() {
            @Override
            public String serialize(Integer obj) {
                return obj + "";
            }

            @Override
            public Integer deserialize(String input) {
                return (int) Double.valueOf(input).doubleValue();
            }
        };
        // Check selection size
        StoredDataSection data = dataOptional.get();
        int widthX = data.get("half-width-x", intSerializer, -1);
        int widthZ = data.get("half-width-z", intSerializer, -1);
        int height = data.get("half-height", intSerializer, -1);
        if (widthX < 0 || widthZ < 0 || height < 0) {
            Messaging.sendFormattedMessage(sender, I18N.INVALID_REGION_CONFIGURATION, args[0].toLowerCase());
            return true;
        }
        Selection selection;
        try {
            selection = SelectionManager.selections.get(((Player) sender).getUniqueId());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Messaging.sendFormattedMessage(sender, I18N.INTERNAL_ERROR);
            return true;
        }
        if (!selection.isValid()) {
            Messaging.sendFormattedMessage(sender, I18N.SELECTION_REQUIRED);
            return true;
        }
        if (selection.getMaxX() - selection.getMinX() > widthX * 2 + 1
                || selection.getMaxY() - selection.getMinY() > height * 2 + 1
                || selection.getMaxZ() - selection.getMinZ() > widthZ * 2 + 1) {
            Messaging.sendFormattedMessage(sender, I18N.SELECTION_TOO_LARGE, widthX * 2 + 1, height * 2 + 1, widthZ * 2 + 1);
            return true;
        }
        final Map<Material, Integer> reqs = new HashMap<Material, Integer>();
        StoredDataSection reqSection = data.getSection("block-requirements");
        for (String matName : reqSection.getKeys(false)) {
            Material mat = Material.valueOf(matName.toUpperCase());
            if (mat == null) {
                // Error in console TODO
                Messaging.sendFormattedMessage(sender, I18N.INVALID_REGION_CONFIGURATION, args[0].toLowerCase());
                return true;
            }
            reqs.put(mat, reqSection.get(matName, intSerializer, 0));
        }
        if (!reqs.isEmpty()) { // Scan selection
            World world = selection.getWorld();          // TODO find better way to do this, or async it
            locationLoop:
            for (int x = selection.getMinX(); x <= selection.getMaxX(); x++) {
                for (int z = selection.getMinZ(); z <= selection.getMaxZ(); z++) {
                    for (int y = selection.getMinY(); y <= selection.getMaxY(); y++) {
                        Material type = new Location(world, x, y, z).getBlock().getType();
                        if (reqs.containsKey(type)) {
                            int cnt = reqs.get(type) - 1;
                            if (cnt != 0) {
                                reqs.put(type, cnt);
                            } else {
                                reqs.remove(type);
                                if (reqs.isEmpty()) {
                                    break locationLoop;
                                }
                            }
                        }
                    }
                }
            }
            if (!reqs.isEmpty()) {
                Messaging.sendFormattedMessage(sender, I18N.INSUFFICIENT_REQUIREMENT_BLOCKS);
            }
        }
        return false;
    }

    @Override
    public String getUsage() {
        return "/region create <type>";
    }
}
