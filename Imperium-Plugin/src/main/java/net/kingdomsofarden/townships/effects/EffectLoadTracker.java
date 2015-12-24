package net.kingdomsofarden.townships.effects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kingdomsofarden.townships.api.effects.Effect;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for tracking load across all effects of a given class
 */
public class EffectLoadTracker {

    private static Map<Class<? extends Effect>, RunningAverage> LOAD_CACHE;

    static {
        LOAD_CACHE = new HashMap<>();
    }

    public static void update(Effect effect, double amt) {
        update(effect.getClass(), amt);
    }

    public static void update(Class<? extends Effect> clazz, double amt) {
        if (LOAD_CACHE.containsKey(clazz)) {
            LOAD_CACHE.get(clazz).update(amt);
        } else {
            RunningAverage avg = new RunningAverage(amt);
            LOAD_CACHE.put(clazz, avg);
        }
    }

    public static double getLoad(Effect effect) {
        return getLoad(effect.getClass());
    }

    public static double getLoad(Class<? extends Effect> clazz) {
        return LOAD_CACHE.containsKey(clazz) ? LOAD_CACHE.get(clazz).value() : 0.00;
    }

    @SuppressWarnings("unchecked") public static void loadFromJson(JsonArray arr) {
        for (JsonElement e : arr) {
            JsonObject o = (JsonObject) e;
            try {
                LOAD_CACHE
                    .put((Class<? extends Effect>) Class.forName(o.get("class").getAsString()),
                        new RunningAverage(o.get("load").getAsDouble()));
            } catch (ClassNotFoundException ignored) {
            }
        }
    }

    public static JsonElement dumpLoadCache() {
        JsonArray arr = new JsonArray();
        for (Map.Entry<Class<? extends Effect>, RunningAverage> e : LOAD_CACHE.entrySet()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("class", e.getKey().getName());
            obj.addProperty("load", e.getValue().value());
            arr.add(obj);
        }
        return arr;
    }


    private static class RunningAverage {
        private double[] queue;
        private int size;
        private double average;
        private int currIdx;

        public RunningAverage(double val) {
            this(10, val);
        }

        public RunningAverage(int size, double val) {
            this.size = size;
            this.queue = new double[size];
            this.average = val;
            this.currIdx = 0;
            double add = val / size;
            for (int i = 0; i < size; i++) {
                queue[i] = add;
            }
        }

        public void update(double val) {
            average -= queue[currIdx];
            double mod = val / size;
            average += mod;
            queue[currIdx] = mod;
            currIdx++;
            if (currIdx == size) {
                currIdx = 0;
            }
        }

        public double value() {
            return average;
        }
    }
}
