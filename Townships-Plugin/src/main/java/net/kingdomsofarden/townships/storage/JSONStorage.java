package net.kingdomsofarden.townships.storage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.storage.Storage;
import net.kingdomsofarden.townships.regions.TownshipsRegion;
import net.kingdomsofarden.townships.util.JSONDataSection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JSONStorage implements Storage {

    private File regionsDir;

    @Override public Region loadRegion(UUID id) {
        return null;
    }

    @Override public void saveRegion(Region r, boolean async) {

    }

    @Override public void removeRegion(UUID id) {

    }

    @Override public void loadAllRegions(Collection<Region> regions) {
        File[] files = regionsDir.listFiles();
        if (files == null) {
            return;
        }
        Map<Integer, List<JsonObject>> loadedObjectsByTier = new HashMap<>();
        for (File f : files) {
            if (!f.getName().toUpperCase().endsWith("JSON")) {
                continue;
            }
            try {
                JsonObject regionalJson =
                    new JsonParser().parse(new JsonReader(new FileReader(f))).getAsJsonObject();
                int tier = regionalJson.get("tier").getAsInt();
                loadedObjectsByTier.computeIfAbsent(tier, t -> new LinkedList<>())
                    .add(regionalJson);
            } catch (FileNotFoundException e) {
                e.printStackTrace(); // ??? - Should never happen
            }
        }
        ArrayList<Integer> tiers = new ArrayList<>(loadedObjectsByTier.keySet());
        Collections.sort(tiers);
        // This scheme is actually slower for small region sizes, but significantly faster for
        // large numbers of regions
        ExecutorService loadThreadPool = Executors.newWorkStealingPool(4);
        for (int tier : tiers) {
            List<Future<Region>> regionCreation = new LinkedList<>();
            for (JsonObject region : loadedObjectsByTier.get(tier)) {
                regionCreation.add(loadThreadPool.submit(() -> new TownshipsRegion(UUID.fromString(region.get("uid").getAsString()),
                    new JSONDataSection(region, null))));
            }
            regionCreation.stream().forEach(f -> {
                try {
                    regions.add(f.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Problem Creating Region!");
                }
            });
        }
    }
}
