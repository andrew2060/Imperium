package net.kingdomsofarden.townships.command;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.command.Command;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.relations.RelationState;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.effects.core.EffectGovernable;
import net.kingdomsofarden.townships.effects.core.EffectIncomeTax;
import net.kingdomsofarden.townships.effects.core.PendingRelationChangeEffect;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class CommandGovernGUI implements Command {
    @Override public String[] getIdentifiers() {
        return new String[] {"govern"};
    }

    @Override public String getPermission() {
        return "townships.govern";
    }

    @Override public int getMaxArguments() {
        return 3;
    }

    @Override public int getMinArguments() {
        return 1;
    }

    @Override public boolean execute(CommandSender sender, String[] args) {
        Region region =
            Townships.getRegions().get(args[0]).orNull(); // Only allow management for named regions
        if (region == null) {
            Messaging.sendFormattedMessage(sender, I18N.REGION_NOT_FOUND, args[0]);
            return true;
        }
        if (!(sender instanceof Player)) {
            Messaging.sendFormattedMessage(sender, I18N.PLAYER_ONLY_COMMAND);
            return true;
        }
        if (!region.hasEffect("governable")) {
            Messaging.sendFormattedMessage(sender, I18N.SUPER_REGION_REQUIRED);
            return true;
        }
        switch (args.length) {
            case 1:
                return displayGeneral(sender, args, region);
            default: {
                GovernActions action = GovernActions.valueOf(args[1].toUpperCase());
                if (action == null) {
                    Messaging.sendFormattedMessage(sender, I18N.COMMAND_NOT_FOUND);
                    return true;
                }
                switch (action) {
                    case DEMOGRAPHICS:
                        return processDemographics(sender, args, region);
                    case ACCESS:
                        return processPermissions(sender, args, region);
                    case IMMIGRATION:
                        return processInvitations(sender, args, region);
                    case DIPLOMACY:
                        return processDiplomacy(sender, args, region);
                    case FISCAL:
                        return processEconomics(sender, args, region);
                }
                return true;
            }
        }
    }

    private boolean processEconomics(CommandSender sender, String[] args, Region region) {
        return false; // TODO
    }

    private boolean processDiplomacy(CommandSender sender, String[] args, Region region) {
        Citizen c = Townships.getCitizens().getCitizen(((Player) sender).getUniqueId());
        if (!region.hasAccess(c, AccessType.DIPLOMAT)) {
            Messaging.sendFormattedMessage(sender, I18N.NO_PERMISSION_DIPLOMACY);
            return true;
        }
        int pagination = 1;
        if (args.length >= 3) { // has a page number
            try {
                pagination = Integer.valueOf(args[2]);
            } catch (NumberFormatException ignored) {
            }
        }

        if (args.length == 4) { // has a sort function
            String sortBy = args[3];

        }

        class RelationStateEntry {

            RelationState actualRelation;
            long start;
            // out of
            Region r;

            RelationStateEntry(Region region, RelationState state) {
                this(region, state, 0);
            }

            RelationStateEntry(Region region, RelationState state, long begin) {
                actualRelation = state;
                start = begin;
                r = region;
            }
        }

        Collection<RelationStateEntry> warringRelation = new ArrayList<>();
        Collection<RelationStateEntry> alliedRelation = new ArrayList<>();

        region.getRelations().entrySet().stream().forEach(e -> {
            switch (e.getValue()) {
                case PEACE:
                    break;
                case PEACE_OFFERED:
                    break;
                case WAR:
                    warringRelation.add(new RelationStateEntry(region, RelationState.WAR));
                    break;
                case WAR_DECLARED: {
                    // Find start time TODO might need to be a better method of doing this
                    PendingRelationChangeEffect eff =
                        e.getKey().getEffect("pending-relation-change");
                    long start = eff.startTime();
                    if (eff.startTime() > 0) {
                        warringRelation
                            .add(new RelationStateEntry(region, RelationState.WAR_DECLARED, start));
                    } else {
                        warringRelation.add(new RelationStateEntry(region, RelationState.WAR));
                        // War's started, just not ticked yet
                    }
                    break;
                }
                case WAR_MUTUAL:
                    warringRelation.add(new RelationStateEntry(region, RelationState.WAR));
                    break;
                case WAR_PENDING_PEACE: {
                    // Find start time TODO might need to be a better method of doing this
                    PendingRelationChangeEffect eff =
                        e.getKey().getEffect("pending-relation-change");
                    long start = eff.startTime();
                    if (eff.startTime() > 0) {
                        warringRelation.add(
                            new RelationStateEntry(region, RelationState.WAR_PENDING_PEACE, start));
                    } // Otherwise war has ended already and not ticked, don't do anything
                    break;
                } case ALLIANCE:
                    alliedRelation.add(new RelationStateEntry(region, RelationState.ALLIANCE));
                    break;
                case ALLIANCE_OFFERED:
                    break;
                case ALLIANCE_PENDING: {
                    // Find start time TODO might need to be a better method of doing this
                    PendingRelationChangeEffect eff =
                        e.getKey().getEffect("pending-relation-change");
                    long start = eff.startTime();
                    if (eff.startTime() > 0) {
                        alliedRelation.add(new RelationStateEntry(region, RelationState.ALLIANCE_PENDING, start));
                    } // Otherwise war has ended already and not ticked, don't do anything
                    break;
                }
                case ALLIANCE_PENDING_PEACE: {
                    // Find start time TODO might need to be a better method of doing this
                    PendingRelationChangeEffect eff =
                        e.getKey().getEffect("pending-relation-change");
                    long start = eff.startTime();
                    if (eff.startTime() > 0) {
                        alliedRelation.add(new RelationStateEntry(region, RelationState
                            .ALLIANCE_PENDING_PEACE, start));
                    } // Otherwise war has ended already and not ticked, don't do anything
                    break;
                }
                case SELF:
                    break;
            }
        });

        return true; // TODO
    }



    private boolean processInvitations(CommandSender sender, String[] args, Region region) {
        return false; // TODO
    }

    private boolean processPermissions(CommandSender sender, String[] args, Region region) {
        return false; // TODO
    }

    private boolean processDemographics(CommandSender sender, String[] args, Region region) {
        int num = 1;
        if (args.length == 3) {
            try {
                num = Integer.valueOf(args[2]);
            } catch (NumberFormatException ignored) {
            }
        }
        String title = "======= " + region.getType() + "Demographics: " + region.getName().get()
            + " =======\n";
        EffectGovernable effect = region.getEffect("governable");
        effect.updateImmed();
        ComponentBuilder page;
        switch (num) {
            default:
                EconomyProvider provider =
                    region.getEconomyProviders().get(EconomyProvider.TREASURY);
                page = new ComponentBuilder(title).color(ChatColor.YELLOW)
                    .append("=== General Information ===").color(ChatColor.GREEN).append("\n")
                    .append("Population: ").color(ChatColor.AQUA)
                    .append(effect.getCurrentPop() + " Citizens").append("\n").append("Land: ")
                    .color(ChatColor.AQUA).append(effect.getCurrLand() + " m^2").append("\n")
                    .append("Raw Production: ").color(ChatColor.AQUA)
                    .append(effect.getProd() + " units per year").append("\n").append("Treasury: ")
                    .color(ChatColor.AQUA)
                    .append(provider.getBalance() + TownshipsPlugin.economy.currencyNamePlural())
                    .append("\n");
                break;
            case 2: {
                EffectIncomeTax incomeTaxEffect = region.getEffect("income-tax");
                page = new ComponentBuilder(title).color(ChatColor.YELLOW)
                    .append("\n=== Fiscal Information ===\n").color(ChatColor.GREEN)
                    .append("Income Tax Rate: ").color(ChatColor.AQUA).append(
                        incomeTaxEffect != null ? incomeTaxEffect.getAmount() * 100 + "%" : "0%")
                    .append("\n").append("Gross National Product: ").color(ChatColor.AQUA).append(
                        effect.getGnp() + TownshipsPlugin.economy.currencyNamePlural()
                            + " per year").append("\n").append("Expenditures: ")
                    .color(ChatColor.AQUA).append(
                        effect.getExp() + TownshipsPlugin.economy.currencyNamePlural()
                            + " per year").append("\n").append("Net National Income: ")
                    .color(ChatColor.AQUA).append(
                        effect.getNNI() + TownshipsPlugin.economy.currencyNamePlural()
                            + " per year").append("\n");
                break;
            }
        }
        if (num > 1) {
            page.append("[<<]").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "govern " + region.getName().get() + " " + (num - 1)));
        }
        if (num < 2) { // Future proofing if we are adding more pages
            page.append("[>>]").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "govern " + region.getName().get() + " " + (num + 1)));
        }
        ((Player) sender).spigot().sendMessage(page.create());
        return true;
    }

    private boolean displayGeneral(CommandSender sender, String[] args, Region region) {
        Citizen c = Townships.getCitizens().getCitizen(((Player) sender).getUniqueId());
        String title =
            "======= " + region.getType() + "Government: " + region.getName().get() + " =======\n";
        String dem = Messaging.format(I18N.DEMOGRAPHICS, region.getName().get());
        ComponentBuilder msgBuilder =
            new ComponentBuilder(title).color(ChatColor.YELLOW).append("Demographics").event(
                new HoverEvent(Action.SHOW_TEXT,
                    new ComponentBuilder(dem).color(ChatColor.GREEN).create())).event(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "govern " + args[0] + " demographics 1")).append("\n");
        if (region.hasAccess(c, AccessType.GOVERNOR)) {
            msgBuilder.append("Governance").event(new HoverEvent(Action.SHOW_TEXT,
                new ComponentBuilder("Manage access roles").color(ChatColor.GREEN).create())).event(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "govern " + args[0] + "access"))
                .append("\n");
        }
        if (region.hasAccess(c, AccessType.IMMIGRATION)) {
            msgBuilder.append("Immigration and Naturalization").event(
                new HoverEvent(Action.SHOW_TEXT,
                    new ComponentBuilder("Manage citizenship applications").color(ChatColor.GREEN)
                        .create())).event(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "govern " + args[0] + "immigration"))
                .append("\n");
        }
        if (region.hasAccess(c, AccessType.DIPLOMAT)) {
            String dipl = Messaging.format(I18N.DIPLOMACY, region.getName().get());
            msgBuilder.append("Diplomacy").event(new HoverEvent(Action.SHOW_TEXT,
                new ComponentBuilder(dipl).color(ChatColor.GREEN).create())).event(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "govern " + args[0] + "diplomacy"))
                .append("\n");
        }
        if (region.hasAccess(c, AccessType.TREASURER)) {
            String dipl = Messaging.format(I18N.DIPLOMACY, region.getName().get());
            msgBuilder.append("Fiscal Policy").event(new HoverEvent(Action.SHOW_TEXT,
                new ComponentBuilder(dipl).color(ChatColor.GREEN).create())).event(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "govern " + args[0] + "fiscal"))
                .append("\n");
        }
        ((Player) sender).spigot().sendMessage(msgBuilder.create());
        return true;
    }

    @Override public String getUsage() {
        return "/govern <regionname>";
    }

    private enum GovernActions {
        DEMOGRAPHICS, ACCESS, IMMIGRATION, DIPLOMACY, FISCAL
    }
}
