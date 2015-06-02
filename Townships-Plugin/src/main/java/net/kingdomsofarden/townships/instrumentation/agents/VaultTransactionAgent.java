package net.kingdomsofarden.townships.instrumentation.agents;

import net.kingdomsofarden.townships.api.events.BankEconomyTransactionEvent;
import net.kingdomsofarden.townships.api.events.EconomyTransactionEvent.TransactionType;
import net.kingdomsofarden.townships.api.events.PlayerEconomyTransactionEvent;
import net.kingdomsofarden.townships.instrumentation.InstrumentationManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.objectweb.asm.*;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class VaultTransactionAgent implements ClassFileTransformer {

    private static Instrumentation instrumentation = null;
    private static VaultTransactionAgent transformer;

    public static void agentmain(String s, Instrumentation i) {
        transformer = new VaultTransactionAgent();
        instrumentation = i;
        instrumentation.addTransformer(transformer);
        try {
            instrumentation.redefineClasses(new ClassDefinition(Economy.class,
                InstrumentationManager.getBytesFromClass(Economy.class)));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to redefine class!");
        }
    }

    @SuppressWarnings("deprecation") public static OfflinePlayer convertPlayerName(String name) {
        return Bukkit.getOfflinePlayer(name);
    }

    public static PlayerEconomyTransactionEvent injectPlayerEvent(OfflinePlayer player,
        double amount, TransactionType type) {
        PlayerEconomyTransactionEvent event =
            new PlayerEconomyTransactionEvent(player, amount, type);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static BankEconomyTransactionEvent injectBankEvent(String bank, double amount,
        TransactionType type) {
        BankEconomyTransactionEvent event = new BankEconomyTransactionEvent(bank, amount, type);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classBuffer) throws IllegalClassFormatException {
        if (loader != ClassLoader.getSystemClassLoader()) {
            return classBuffer;
        }

        if (!className.equals(Economy.class.getName().replace(".", "/"))) {
            return classBuffer;
        }

        byte[] result = classBuffer;
        try {
            ClassReader reader = new ClassReader(classBuffer);
            ClassWriter writer =
                new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            ClassVisitor visitor = new EconomyClassAdapter(Opcodes.ASM4, writer);
            reader.accept(visitor, 0);
            result = writer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static class PlayerTransactionVisitor extends MethodVisitor implements Opcodes {
        private TransactionType type;
        private String signature;
        private String methodName;

        public PlayerTransactionVisitor(int api, MethodVisitor visitor, String methodName,
            String descriptor, TransactionType type) {
            super(api, visitor);
            this.methodName = methodName;
            this.signature = descriptor;
            this.type = type;
        }

        public void visitCode() {
            int idx = 0;
            if (signature.startsWith("(Lorg/bukkit/OfflinePlayer;D)")) {
                this.visitVarInsn(ALOAD, 1);
                this.visitVarInsn(DLOAD, 2);
                idx = 3;
            } else if (signature.startsWith("(Lorg/bukkit/OfflinePlayer;Ljava/lang/String;D)")) {
                this.visitVarInsn(ALOAD, 1);
                this.visitVarInsn(DLOAD, 3);
                idx = 4;
            } else if (signature.startsWith("(Ljava/lang/String;D)")) {
                this.visitVarInsn(ALOAD, 1);
                this.visitMethodInsn(INVOKESTATIC,
                    "net/kingdomsofarden/townships/instrumentation/agents/VaultTransactionAgent",
                    "convertPlayerName", "(Ljava/lang/String)Lorg/bukkit/OfflinePlayer");
                this.visitVarInsn(DLOAD, 2);
                idx = 3;
            } else {
                this.visitVarInsn(ALOAD, 1);
                this.visitMethodInsn(INVOKESTATIC,
                    "net/kingdomsofarden/townships/instrumentation/agents/VaultTransactionAgent",
                    "convertPlayerName", "(Ljava/lang/String)Lorg/bukkit/OfflinePlayer");
                this.visitVarInsn(DLOAD, 3);
                idx = 4;
            }
            this.visitLdcInsn(type);
            this.visitMethodInsn(INVOKESTATIC,
                "net/kingdomsofarden/townships/instrumentation/agents/VaultTransactionAgent",
                "injectPlayerEvent",
                "(Lorg/bukkit/OfflinePlayer;D;Lnet/kingdomsofarden/townships/api/events/EconomyTransactionEvent$TransactionType)"
                    + "L/net/kingdomsofarden/townships/api/events/PlayerEconomyTransactionEvent");
            this.visitVarInsn(ASTORE, idx);
            this.visitVarInsn(ALOAD, idx);
            this.visitMethodInsn(INVOKEVIRTUAL,
                "net/kingdomsofarden/townships/api/events/PlayerEconomyTransactionEvent",
                "isCancelled", "()Z");
            Label other = new Label();
            this.visitJumpInsn(IFEQ, other);
            this.visitMethodInsn(INVOKEVIRTUAL,
                "net/kingdomsofarden/townships/api/events/PlayerEconomyTransactionEvent",
                "getAmount", "()D");
            this.visitVarInsn(DSTORE,
                idx - 1); // Overwrite default parameter value with the event's
            super.visitCode();
            this.visitLabel(other);
            this.visitLdcInsn(0.00);
            this.visitVarInsn(DSTORE, idx - 1); // Set amount to 0
            super.visitCode();
        }
    }


    public static class BankTransactionVisitor extends MethodVisitor implements Opcodes {
        private TransactionType type;
        private String signature;
        private String methodName;

        public BankTransactionVisitor(int api, MethodVisitor visitor, String methodName,
            String descriptor, TransactionType type) {
            super(api, visitor);
            this.methodName = methodName;
            this.signature = descriptor;
            this.type = type;
        }

        public void visitCode() {
            int idx = 3;
            this.visitVarInsn(ALOAD, 1);
            this.visitVarInsn(DLOAD, 2);
            this.visitLdcInsn(type);
            this.visitMethodInsn(INVOKESTATIC,
                "net/kingdomsofarden/townships/instrumentation/agents/VaultTransactionAgent",
                "injectBankEvent",
                "(Ljava/lang/String;D;Lnet/kingdomsofarden/townships/api/events/EconomyTransactionEvent$TransactionType)"
                    + "L/net/kingdomsofarden/townships/api/events/BankEconomyTransactionEvent");
            this.visitVarInsn(ASTORE, idx);
            this.visitVarInsn(ALOAD, idx);
            this.visitMethodInsn(INVOKEVIRTUAL,
                "net/kingdomsofarden/townships/api/events/BankEconomyTransactionEvent",
                "isCancelled", "()Z");
            Label other = new Label();
            this.visitJumpInsn(IFEQ, other);
            this.visitMethodInsn(INVOKEVIRTUAL,
                "net/kingdomsofarden/townships/api/events/BankEconomyTransactionEvent", "getAmount",
                "()D");
            this.visitVarInsn(DSTORE,
                idx - 1); // Overwrite default parameter value with the event's
            super.visitCode();
            this.visitLabel(other);
            this.visitLdcInsn(0.00);
            this.visitVarInsn(DSTORE, idx - 1); // Set amount to 0
            super.visitCode();
        }
    }


    public class EconomyClassAdapter extends ClassVisitor {

        public EconomyClassAdapter(int api, ClassVisitor visitor) {
            super(api, visitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature,
            String[] exceptions) {
            if (name.equalsIgnoreCase("depositPlayer")) {
                return new PlayerTransactionVisitor(Opcodes.ASM4,
                    super.visitMethod(access, name, desc, signature, exceptions), name, desc,
                    TransactionType.DEPOSIT);
            } else if (name.equalsIgnoreCase("withdrawPlayer")) {
                return new PlayerTransactionVisitor(Opcodes.ASM4,
                    super.visitMethod(access, name, desc, signature, exceptions), name, desc,
                    TransactionType.WITHDRAW);
            } else if (name.equalsIgnoreCase("bankDeposit")) {
                return new BankTransactionVisitor(Opcodes.ASM4,
                    super.visitMethod(access, name, desc, signature, exceptions), name, desc,
                    TransactionType.DEPOSIT);
            } else if (name.equalsIgnoreCase("bankWithdraw")) {
                return new BankTransactionVisitor(Opcodes.ASM4,
                    super.visitMethod(access, name, desc, signature, exceptions), name, desc,
                    TransactionType.WITHDRAW);
            }
            return null;
        }
    }


}


