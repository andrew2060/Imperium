package net.kingdomsofarden.townships.instrumentation;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import net.milkbowl.vault.economy.Economy;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;


/**
 * Adapted from Xyene's excellent guide to ASM manipulation
 * <p/>
 * Original code below written by Xyene (http://www.github.com/Xyene)
 */
public class InstrumentationManager {

    /**
     * Loads an agent into a JVM.
     *
     * @param agent     The main agent class.
     * @param resources Array of classes to be included with agent.
     * @throws IOException
     * @throws AttachNotSupportedException
     * @throws AgentLoadException
     * @throws AgentInitializationException
     */
    public static void attachAgentToJVM(Class agent, Class... resources)
        throws IOException, AttachNotSupportedException, AgentLoadException,
        AgentInitializationException {

        VirtualMachine vm = VirtualMachine.attach(getPidFromRuntimeMBean());
        vm.loadAgent(generateAgentJar(agent, resources).getAbsolutePath());
        vm.detach();
    }

    public static String getPidFromRuntimeMBean() {
        String jvm = ManagementFactory.getRuntimeMXBean().getName();
        String pid = jvm.substring(0, jvm.indexOf('@'));
        return pid;
    }

    /**
     * Generates a temporary agent file to be loaded.
     *
     * @param agent     The main agent class.
     * @param resources Array of classes to be included with agent.
     * @return Returns a temporary jar file with the specified classes included.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File generateAgentJar(Class agent, Class... resources) throws IOException {
        File jarFile = File.createTempFile("agent", ".jar");
        jarFile.deleteOnExit();

        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        // Create manifest stating that agent is allowed to transform classes
        mainAttributes.put(Name.MANIFEST_VERSION, "1.0");
        mainAttributes.put(new Name("Agent-Class"), agent.getName());
        mainAttributes.put(new Name("Can-Retransform-Classes"), "true");
        mainAttributes.put(new Name("Can-Redefine-Classes"), "true");

        JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest);

        jos.putNextEntry(new JarEntry(agent.getName().replace('.', '/') + ".class"));

        jos.write(getBytesFromStream(agent.getClassLoader().getResourceAsStream(unqualify(agent))));
        jos.closeEntry();

        for (Class clazz : resources) {
            String name = unqualify(clazz);
            jos.putNextEntry(new JarEntry(name));
            jos.write(getBytesFromStream(clazz.getClassLoader().getResourceAsStream(name)));
            jos.closeEntry();
        }

        jos.close();
        return jarFile;
    }

    private static String unqualify(Class clazz) {
        return clazz.getName().replace('.', '/') + ".class";
    }

    public static byte[] getBytesFromStream(InputStream stream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[65536];
        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();

    }

    public static byte[] getBytesFromClass(Class<Economy> clazz) throws IOException {
        return getBytesFromStream(clazz.getClassLoader()
            .getResourceAsStream(clazz.getName().replace('.', '/') + ".class"));
    }
}
