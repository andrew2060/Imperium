package net.kingdomsofarden.townships.command;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.command.Command;
import net.kingdomsofarden.townships.api.events.RegionCreateEvent;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.command.selection.SelectionManager;
import net.kingdomsofarden.townships.regions.TownshipsRegion;
import net.kingdomsofarden.townships.regions.bounds.CuboidSelection;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CommandCreateRegion implements Command {

    @Override public String[] getIdentifiers() {
        return new String[] {"region create", "town create"};
    }

    @Override public String getPermission() {
        return "townships.regions.create";
    }

    @Override public int getMaxArguments() {
        return 2;
    }

    @Override public int getMinArguments() {
        return 1;
    }

    @Override public boolean execute(CommandSender sender, String[] args) {
        // Find the type of region being created
        if (!(sender instanceof Player)) {
            Messaging.sendFormattedMessage(sender, I18N.PLAYER_ONLY_COMMAND);
            return true;
        }
        Optional<StoredDataSection> dataOptional =
            Townships.getConfiguration().getRegionConfiguration(args[0]);
        if (!dataOptional.isPresent()) {
            Messaging
                .sendFormattedMessage(sender, I18N.REGION_TYPE_NOT_FOUND, args[0].toLowerCase());
        }
        Serializer<Integer> intSerializer = new Serializer<Integer>() {
            @Override public String serialize(Integer obj) {
                return obj + "";
            }

            @Override public Integer deserialize(String input) {
                return (int) Double.valueOf(input).doubleValue();
            }
        };
        // Check selection size
        StoredDataSection data = dataOptional.get();
        // Selection bounds check
        int maxX = data.get("max-width-x", intSerializer, -1);
        int maxZ = data.get("max-width-z", intSerializer, -1);
        int maxHeight = data.get("max-height", intSerializer, -1);
        int minX = data.get("min-width-x", intSerializer, -1);
        int minZ = data.get("min-width-z", intSerializer, -1);
        int minHeight = data.get("min-height", intSerializer, -1);
        CuboidSelection selection;
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
        if ((maxX != -1 && selection.getMaxX() - selection.getMinX() > maxX) || (maxHeight != -1
            && selection.getMaxY() - selection.getMinY() > maxHeight) || (maxZ != -1
            && selection.getMaxZ() - selection.getMinZ() > maxZ)) {
            Messaging.sendFormattedMessage(sender, I18N.SELECTION_TOO_LARGE, maxX, maxHeight, maxZ);
            return true;
        }
        if ((minX != -1 && selection.getMaxX() - selection.getMinX() < minX) || (minHeight != -1
            && selection.getMaxY() - selection.getMinY() < minHeight) || (minZ != -1
            && selection.getMaxZ() - selection.getMinZ() < minZ)) {
            Messaging.sendFormattedMessage(sender, I18N.SELECTION_TOO_SMALL, maxX, maxHeight, maxZ);
            return true;
        }
        // Check region permissions
        TreeSet<Region> intersections = Townships.getRegions().getIntersectingRegions(selection);
        Citizen c = Townships.getCitizens().getCitizen(((Player) sender).getUniqueId());
        int regionTier = data.get("tier", intSerializer, Integer.MIN_VALUE);
        if (!intersections.last().hasAccess(c, AccessType.ZONING)) {
            Messaging.sendFormattedMessage(sender, I18N.NO_PERMISSION_AREA_ZONING);
            return true;
        }
        // Requirements checking
        StoredDataSection requirements = data.getSection("requirements");
        final Map<Material, Integer> blockReq = new HashMap<>();
        StoredDataSection blockReqSection = requirements.getSection("block-requirements");
        for (String matName : blockReqSection.getKeys(false)) {
            Material mat = Material.valueOf(matName.toUpperCase());
            if (mat == null) {
                // Error in console TODO
                Messaging.sendFormattedMessage(sender, I18N.INVALID_REGION_CONFIGURATION,
                    args[0].toLowerCase());
                return true;
            }
            int amt = blockReqSection.get(matName, intSerializer, 0);
            if (amt > 0) {
                blockReq.put(mat, amt);
            }
        }
        if (!blockReq.isEmpty()) { // Scan selection
            World world = selection.getWorld(); // TODO find better way to do this, or async it
            locationLoop:
            for (int x = selection.getMinX(); x <= selection.getMaxX(); x++) {
                for (int z = selection.getMinZ(); z <= selection.getMaxZ(); z++) {
                    for (int y = selection.getMinY(); y <= selection.getMaxY(); y++) {
                        Material type = new Location(world, x, y, z).getBlock().getType();
                        if (blockReq.containsKey(type)) {
                            int cnt = blockReq.get(type) - 1;
                            if (cnt != 0) {
                                blockReq.put(type, cnt);
                            } else {
                                blockReq.remove(type);
                                if (blockReq.isEmpty()) {
                                    break locationLoop;
                                }
                            }
                        }
                    }
                }
            }
            if (!blockReq.isEmpty()) {
                Messaging.sendFormattedMessage(sender, I18N.INSUFFICIENT_REQUIREMENT_BLOCKS);
                return true;
            }
        }
        // Check region dependency requirements
        Map<String, Integer> regionTypeMinReq;
        Map<Integer, Integer> regionTierMinReq;
        Map<String, Integer> regionTypeMaxReq;
        Map<Integer, Integer> regionTierMaxReq;
        Set<Integer> excludeTiers;
        Set<String> excludeTypes;
        boolean excludeAll;
        try {
            regionTypeMinReq = new HashMap<>();
            regionTierMinReq = new HashMap<>();
            regionTypeMaxReq = new HashMap<>();
            regionTierMaxReq = new HashMap<Integer, Integer>();
            excludeTiers = new HashSet<Integer>();
            excludeTypes = new HashSet<String>();
            StoredDataSection regionReqSection = requirements.getSection("region-types-min");
            for (String type : regionReqSection.getKeys(false)) {
                int amt = regionReqSection.get(type, intSerializer, 0);
                if (amt > 0) {
                    regionTypeMinReq.put(type.toLowerCase(), amt);
                }
            }
            StoredDataSection tierReqSection = requirements.getSection("region-tiers-min");
            for (String tierNum : tierReqSection.getKeys(false)) {
                int tier = Integer.parseInt(tierNum);
                int arg = regionReqSection.get(tierNum, intSerializer, 0);
                regionTierMinReq.put(tier, arg);
            }
            StoredDataSection maxRegionReqSection = requirements.getSection("region-types-max");
            for (String type : maxRegionReqSection.getKeys(false)) {
                int amt = maxRegionReqSection.get(type, intSerializer, 0);
                if (amt > 0) {
                    regionTypeMaxReq.put(type.toLowerCase(), amt);
                }
            }
            StoredDataSection maxTierReqSection = requirements.getSection("region-tiers-max");
            for (String tierNum : maxTierReqSection.getKeys(false)) {
                int tier = Integer.parseInt(tierNum);
                int arg = maxTierReqSection.get(tierNum, intSerializer, 0);
                regionTierMaxReq.put(tier, arg);
            }
            StoredDataSection excludeSection = requirements.getSection("exclude");
            excludeAll = Boolean.valueOf(excludeSection.get("all", "false"));
            if (!excludeAll) {
                for (String tierNum : excludeSection.getList("tiers")) {
                    int tier = Integer.parseInt(tierNum);
                    excludeTiers.add(tier);
                }
                excludeTypes.addAll(
                    excludeSection.getList("types").stream().map(String::toLowerCase)
                        .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            Messaging.sendFormattedMessage(sender, I18N.INVALID_REGION_CONFIGURATION,
                args[0].toLowerCase());
            e.printStackTrace(); // TODO print to debug instead
            return true;
        }
        if (excludeAll && !(intersections.isEmpty())) {
            Messaging.sendFormattedMessage(sender, I18N.REGION_COLLISION_MUTEX_FAIL);
            return true;
        }
        for (Region region : intersections) {
            int tier = region.getTier();
            String type = region.getType().toLowerCase();
            if (excludeTiers.contains(tier) || excludeTypes.contains(type) || tier == regionTier) {
                Messaging.sendFormattedMessage(sender, I18N.REGION_COLLISION_GENERAL);
                return true;
            }
            if (regionTypeMinReq.containsKey(type)) {
                int amt = regionTypeMinReq.get(type) - 1;
                if (amt > 0) {
                    regionTypeMinReq.put(type.toLowerCase(), amt);
                } else {
                    regionTypeMinReq.remove(type);
                }
            }
            if (regionTypeMaxReq.containsKey(type)) {
                int amt = regionTypeMaxReq.get(type) - 1;
                if (amt < 0) {
                    Messaging.sendFormattedMessage(sender, I18N.MAX_REGION_TYPE);
                    return true;
                } else {
                    regionTypeMaxReq.put(type.toLowerCase(), amt);
                }
            }
            if (regionTierMinReq.containsKey(tier)) {
                int amt = regionTierMinReq.get(tier) - 1;
                if (amt < 0) {
                    Messaging.sendFormattedMessage(sender, I18N.MAX_REGION_TIER);
                    return true;
                } else {
                    regionTierMinReq.put(tier, amt);
                }
            }
            if (regionTierMaxReq.containsKey(tier)) {
                int amt = regionTierMaxReq.get(tier) - 1;
                if (amt < 0) {
                    Messaging.sendFormattedMessage(sender, I18N.MAX_REGION_TIER);
                    return true;
                } else {
                    regionTierMaxReq.put(tier, amt);
                }
            }
            if (tier < regionTier && !selection.encapsulates(region.getBounds())) {
                Messaging.sendFormattedMessage(sender, I18N.LOWER_TIER_MUST_BE_ENCOMPASSED);
                return true;
            } else if (tier > regionTier && !region.getBounds().encapsulates(selection)) {
                Messaging.sendFormattedMessage(sender, I18N.LOWER_TIER_MUST_BE_ENCOMPASSED);
                return true;
            }
        }
        if (!regionTypeMinReq.isEmpty()) {
            Messaging.sendFormattedMessage(sender, I18N.INSUFFICIENT_REQUIREMENT_REGION_TYPE);
            return true;
        }
        if (!regionTierMinReq.isEmpty()) {
            Messaging.sendFormattedMessage(sender, I18N.INSUFFICIENT_REQUIREMENT_REGION_TIER);
            return true;
        }
        // TODO check other requirements (money, funding)
        UUID createdId = UUID.randomUUID();
        if (args.length == 2) {
            data.set("name", args[1]);
        }
        data.set("position-1", selection.getLoc1());
        data.set("position-2", selection.getLoc2());
        Region created = new TownshipsRegion(createdId, data); // TODO grant administrative rights
        for (Region intersect : intersections) {
            if (!intersect.isCompatible(created)) {
                Messaging.sendFormattedMessage(sender, I18N.REGION_COLLISION_GENERAL);
                return true;
            }
        }
        RegionCreateEvent e = new RegionCreateEvent(c, created);
        Bukkit.getPluginManager().callEvent(e);
        if (!e.isCancelled()) {
            Townships.getRegions().add(created);
            Messaging.sendFormattedMessage(sender, I18N.REGION_SUCCESSFULLY_CREATED,
                created.getName().isPresent() ? created.getName().get() : createdId);
        }
        return true;
    }

    @Override public String getUsage() {
        return "/region create <type> [name]";
    }
}
