package net.kingdomsofarden.townships.effects;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.effects.EffectManager;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.core.PendingRelationChangeEffect;
import net.kingdomsofarden.townships.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class TownshipsEffectManager implements EffectManager {

    private static final String EFFECTS_DIR = "effects";

    private TownshipsPlugin plugin;
    private URLClassLoader loader;
    private EffectTaskManager taskManager;

    private File effectsDir;
    private Map<String, Class<? extends Effect>> effects;

    public TownshipsEffectManager(TownshipsPlugin plugin) {
        this.plugin = plugin;
        loader = (URLClassLoader) plugin.getClass().getClassLoader();
        effectsDir = new File(plugin.getDataFolder(), EFFECTS_DIR);
        effectsDir.mkdir();
        effects = new HashMap<String, Class<? extends Effect>>();
        taskManager = new EffectTaskManager(Constants.EFFECT_SPREAD_DELAY);
        loadEffects(effectsDir);
        loadCoreEffects();
    }

    // Enables dynamic registration by adding entries here
    private void loadCoreEffects() {
        effects.put(new PendingRelationChangeEffect().getName().toLowerCase(), PendingRelationChangeEffect.class);
    }

    public EffectTaskManager getEffectTaskManager() {
        return taskManager;
    }


    @Override
    public Effect loadEffect(String name, Region loader, StoredDataSection config) {
        if (effects.containsKey(name.toLowerCase())) {
            try {
                Class<? extends Effect> clazz = effects.get(name.toLowerCase());
                Constructor<? extends Effect> c = clazz.getConstructor(new Class[]{});
                Effect e = c.newInstance(new Object[] {});
                e.onLoad(plugin, loader, config);
                return e;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    private void loadEffects(File effectsDir) {
        List<File> toLoad = new LinkedList<File>();
        for (final String fileName : effectsDir.list()) {
            toLoad.add(new File(effectsDir, fileName));
        }
        for (final File f : toLoad) { // Add to classloader and load JAR
            try {
                addFile(f);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            try {
                JarFile jarFile = new JarFile(f);
                String mainClass = null;
                final Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry element = entries.nextElement();
                    if (element.getName().equalsIgnoreCase("effect.info")) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
                        String next = reader.readLine();
                        while (next != null) {
                            if (next.toLowerCase().startsWith("main-class: ")) {
                                mainClass = next.substring(12);
                                break;
                            }
                        }
                        break;
                    }
                }
                if (mainClass != null) {
                    addFile(f);
                    Class<? extends Effect> clazz = (Class<? extends Effect>) Class.forName(mainClass);
                    // Make empty copy to get name
                    try {
                        Effect e = (Effect) clazz.getConstructor(new Class[]{}).newInstance(new Object[]{});
                        effects.put(e.getName().toLowerCase(), clazz);
                    } catch (NoSuchMethodException e) {
                        plugin.getLogger().log(Level.SEVERE, "Could not load " + mainClass + " all effects must " +
                                "have an empty constructor");
                        continue;
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // ClassLoader related methods
    private void addURL(URL url) throws IOException {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
            method.setAccessible(true);
            method.invoke(this.loader, new Object[]{url});
        } catch (Exception e) {
            throw new IOException("Error adding URL to ClassLoader", e);
        }
    }

    private void addFile(File file) throws IOException {
        addURL(file.toURI().toURL());
    }




}
