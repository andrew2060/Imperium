package net.kingdomsofarden.townships.command;

import com.google.gson.JsonObject;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.command.Command;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.events.RegionRelationChangeEvent;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.relations.RelationState;
import net.kingdomsofarden.townships.util.Constants;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.JSONDataSection;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDeclareRelation implements Command {
    @Override public String[] getIdentifiers() {
        return new String[] {"region declare relation", "town declare relation",
            "nation declare relation"};
    }

    @Override public String getPermission() {
        return "townships.diplomacy";
    }

    @Override public int getMaxArguments() {
        return 3;
    }

    @Override public int getMinArguments() {
        return 3;
    }

    @Override public boolean execute(CommandSender sender, String[] args) {
        boolean bypass = false;
        if (!(sender instanceof Player)) {
            bypass = true;
        }
        RelationState state = RelationState.valueOf(args[0].toUpperCase());
        if (state == null || !state.isDeclarable()) {
            Messaging.sendFormattedMessage(sender, I18N.NO_MATCHING_RELATION_STATE,
                args[0].toUpperCase());
            return true;
        }
        Region declarer = Townships.getRegions().get(args[1]).orNull();
        if (declarer == null) {
            Messaging.sendFormattedMessage(sender, I18N.REGION_NOT_FOUND, args[1]);
            return true;
        }
        Region declaree = Townships.getRegions().get(args[2]).orNull();
        if (declaree == null) {
            Messaging.sendFormattedMessage(sender, I18N.REGION_NOT_FOUND, args[2]);
            return true;
        }
        if (!bypass && !declarer
            .hasAccess(Townships.getCitizens().getCitizen(((Player) sender).getUniqueId()),
                AccessType.DIPLOMAT)) {
            Messaging.sendFormattedMessage(sender, I18N.NO_PERMISSION_DIPLOMACY);
            return true;
        }
        if (declarer.getUid().equals(declaree.getUid())) {
            Messaging.sendFormattedMessage(sender, I18N.CANNOT_RELATION_SELF);
            return true;
        }
        if (declarer.getParents().contains(declaree)) {
            Messaging.sendFormattedMessage(sender, I18N.CANNOT_RELATION_PARENT);
            return true;
        }
        if (declarer.getChildren().contains(declaree)) {
            Messaging.sendFormattedMessage(sender, I18N.CANNOT_RELATION_CHILD);
            return true;
        }
        RelationState currRelation =
            declarer.getRelations().getOrDefault(declaree.getName(), RelationState.PEACE);
        if (currRelation.getBaseType().equals(state)) {
            Messaging.sendFormattedMessage(sender, I18N.SAME_RELATION,
                declaree.getName().get()); // Check parents
            return true;
        }
        // TODO verify fees requirements etc, look over is same type
        RelationState currTargetRelation =
            declaree.getRelations().getOrDefault(declaree.getName(), RelationState.PEACE);

        RelationState baseSource = currRelation.getBaseType();
        RelationState baseTarget = currTargetRelation.getBaseType();
        boolean reciprocal = false;
        switch (state) {
            case WAR:
                if (baseTarget.equals(RelationState.ALLIANCE)) {
                    if (!currTargetRelation.equals(RelationState.ALLIANCE_OFFERED)) {
                        Messaging.sendFormattedMessage(sender, I18N.NO_BETRAYAL,
                            declaree.getName().get());
                        return true;
                    }
                }
                if (baseSource.equals(RelationState.ALLIANCE)) {
                    if (!currRelation.equals(RelationState.ALLIANCE_OFFERED)) {
                        Messaging.sendFormattedMessage(sender, I18N.NO_BETRAYAL,
                            declaree.getName().get());
                        return true;
                    }
                }
                if (baseTarget.equals(RelationState.PEACE)) {
                    if (currTargetRelation.equals(RelationState.ALLIANCE_PENDING_PEACE)) {
                        Messaging.sendFormattedMessage(sender, I18N.NO_BETRAYAL,
                            declaree.getName().get());
                        return true;
                    }
                }
                state = RelationState.WAR_DECLARED;
                break;
            case PEACE:
                if (baseTarget.equals(RelationState.WAR)) {
                    if (currTargetRelation.equals(RelationState.PEACE_OFFERED)) {
                        reciprocal = true;
                        state = RelationState.WAR_PENDING_PEACE;
                    } else {
                        state = RelationState.PEACE_OFFERED;
                    }
                } else {
                    state = RelationState.ALLIANCE_PENDING_PEACE;
                }
                break;
            case ALLIANCE:
                if (currTargetRelation.getBaseType().equals(RelationState.WAR) || currRelation
                    .getBaseType().equals(RelationState.WAR)) {
                    Messaging.sendFormattedMessage(sender, I18N.NO_ALLIANCE_WAR);
                    return true;
                }
                if (currTargetRelation.equals(RelationState.ALLIANCE_OFFERED)) {
                    state = RelationState.ALLIANCE_PENDING;
                    reciprocal = true;
                } else {
                    state = RelationState.ALLIANCE_OFFERED;
                }
                break;
            default:
                break;
        }

        RegionRelationChangeEvent event =
            new RegionRelationChangeEvent(declarer, declaree, currRelation, state);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return true; // Silent quit
        }
        declarer.getRelations().put(declaree, state);
        declaree.getExternRelations().put(declarer, state);
        if (reciprocal) {
            declaree.getRelations().put(declarer, state);
            declarer.getExternRelations().put(declaree, state);
        }
        // TODO remove fees/requirements
        JSONDataSection section = new JSONDataSection(new JsonObject(), "");
        long time = System.currentTimeMillis() + Constants.RELATION_DELAY;
        section.set("start-time", time);
        section.set("relation", state.getBaseType().name());
        section.set("region", declaree.getUid());
        Effect e = Townships.getEffectManager().loadEffect("pending-relation-change", declarer,
            section);
        declarer.addEffect(e, true);
        if (reciprocal) {
            JSONDataSection section2 = new JSONDataSection(new JsonObject(), "");
            section2.set("start-time", time);
            section2.set("relation", state.getBaseType().name());
            section2.set("region", declarer.getUid());
            e = Townships.getEffectManager().loadEffect("pending-relation-change", declaree,
                section2);
            declaree.addEffect(e, true);
        }
        return true;
    }

    @Override public String getUsage() {
        return "town declare relation <relation> ";
    }
}
