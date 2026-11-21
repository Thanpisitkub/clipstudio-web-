package com.adminmenu;

import com.adminmenu.net.Packets;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/** Server side of every menu action. Every call re-checks that the sender is an operator. */
public final class AdminActions {
    private static final Pattern SKILL_ID = Pattern.compile("[a-z0-9_.-]+:[a-z0-9_./-]+");
    private static final Pattern SKILL_SLOT = Pattern.compile("[a-z0-9_]+");
    /** Inv View provider ids: inv = inventory, echest = ender chest. */
    private static final Set<String> VIEW_IDS = Set.of("inv", "echest");

    private AdminActions() {}

    public static boolean allowed(ServerPlayer p) {
        return p != null && p.hasPermissions(2);
    }

    public static List<Packets.Entry> snapshot(MinecraftServer server) {
        List<Packets.Entry> out = new ArrayList<>();
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            out.add(new Packets.Entry(
                    p.getUUID(),
                    p.getGameProfile().getName(),
                    p.gameMode.getGameModeForPlayer().getId(),
                    p.level().dimension().location().getPath(),
                    p.getBlockX(), p.getBlockY(), p.getBlockZ()));
        }
        out.sort((a, b) -> a.name().compareToIgnoreCase(b.name()));
        return out;
    }

    private static void msg(ServerPlayer p, String text) {
        p.sendSystemMessage(Component.literal("[Admin] " + text));
    }

    public static void run(ServerPlayer admin, Packets.Action a) {
        ServerPlayer t = admin.server.getPlayerList().getPlayer(a.target);
        if (t == null) {
            msg(admin, "That player is offline.");
            return;
        }
        String name = t.getGameProfile().getName();

        switch (a.type) {
            case GAMEMODE -> {
                GameType gt = GameType.byId(a.n);
                t.setGameMode(gt);
                msg(admin, name + " is now in " + gt.getName() + " mode.");
            }
            case TP_TO -> {
                if (t == admin) return;
                admin.teleportTo(t.serverLevel(), t.getX(), t.getY(), t.getZ(), t.getYRot(), t.getXRot());
            }
            case BRING -> {
                if (t == admin) return;
                t.teleportTo(admin.serverLevel(), admin.getX(), admin.getY(), admin.getZ(), admin.getYRot(), admin.getXRot());
                msg(admin, "Brought " + name + " to you.");
            }
            case KILL -> {
                t.hurt(t.damageSources().genericKill(), Float.MAX_VALUE);
                msg(admin, "Killed " + name + ".");
            }
            case SPECTATE -> {
                if (t == admin) return;
                admin.setGameMode(GameType.SPECTATOR);
                admin.setCamera(t);
            }
            case GIVE -> give(admin, t, a.s, a.n);
            case SKILL -> skill(admin, t, a.s, a.s2);
            case VIEW -> view(admin, t, a.s);
        }
    }

    private static void give(ServerPlayer admin, ServerPlayer t, String id, int count) {
        ResourceLocation rl = ResourceLocation.tryParse(id.trim());
        Item item = rl == null ? null : ForgeRegistries.ITEMS.getValue(rl);
        if (item == null || item == Items.AIR) {
            msg(admin, "Unknown item: " + id);
            return;
        }
        int left = Mth.clamp(count, 1, 2304);
        int total = left;
        while (left > 0) {
            ItemStack st = new ItemStack(item, Math.min(left, item.getMaxStackSize()));
            left -= st.getCount();
            if (!t.getInventory().add(st)) t.drop(st, false);
        }
        msg(admin, "Gave " + total + " x " + rl + " to " + t.getGameProfile().getName() + ".");
    }

    /** Runs Epic Fight's "/skill add <player> <slot> <skill>" with the sender's own permissions. */
    private static void skill(ServerPlayer admin, ServerPlayer t, String id, String slot) {
        if (!SKILL_ID.matcher(id).matches() || !SKILL_SLOT.matcher(slot).matches()) {
            msg(admin, "Invalid skill or slot name.");
            return;
        }
        runCommand(admin, "skill add " + t.getGameProfile().getName() + " " + slot + " " + id);
    }

    /** Opens the target's inventory / ender chest through the Inv View mod ("/view inv|echest <player>"). */
    private static void view(ServerPlayer admin, ServerPlayer t, String provider) {
        if (!VIEW_IDS.contains(provider)) return;
        if (admin.server.getCommands().getDispatcher().getRoot().getChild("view") == null) {
            msg(admin, "The Inv View mod is not installed on this server.");
            return;
        }
        runCommand(admin, "view " + provider + " " + t.getGameProfile().getName());
    }

    private static void runCommand(ServerPlayer admin, String command) {
        admin.server.getCommands().performPrefixedCommand(admin.createCommandSourceStack(), command);
    }
}
