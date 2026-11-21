package com.adminmenu.net;

import com.adminmenu.AdminActions;
import com.adminmenu.client.ClientHooks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public final class Packets {
    private Packets() {}

    /** One row of the player list. */
    public record Entry(UUID id, String name, int mode, String dim, int x, int y, int z) {
        void write(FriendlyByteBuf b) {
            b.writeUUID(id);
            b.writeUtf(name, 64);
            b.writeVarInt(mode);
            b.writeUtf(dim, 128);
            b.writeInt(x);
            b.writeInt(y);
            b.writeInt(z);
        }

        static Entry read(FriendlyByteBuf b) {
            return new Entry(b.readUUID(), b.readUtf(64), b.readVarInt(), b.readUtf(128),
                    b.readInt(), b.readInt(), b.readInt());
        }
    }

    // ---------- client -> server: ask for the player list ----------
    public static final class ListReq {
        public ListReq() {}

        static void enc(ListReq m, FriendlyByteBuf b) {}

        static ListReq dec(FriendlyByteBuf b) { return new ListReq(); }

        static void handle(ListReq m, Supplier<NetworkEvent.Context> s) {
            NetworkEvent.Context c = s.get();
            c.enqueueWork(() -> {
                ServerPlayer p = c.getSender();
                if (AdminActions.allowed(p)) {
                    Net.CH.send(PacketDistributor.PLAYER.with(() -> p), new ListRes(AdminActions.snapshot(p.server)));
                }
            });
            c.setPacketHandled(true);
        }
    }

    // ---------- server -> client: the player list ----------
    public static final class ListRes {
        public final List<Entry> list;

        public ListRes(List<Entry> list) { this.list = list; }

        static void enc(ListRes m, FriendlyByteBuf b) {
            b.writeVarInt(m.list.size());
            for (Entry e : m.list) e.write(b);
        }

        static ListRes dec(FriendlyByteBuf b) {
            int n = b.readVarInt();
            List<Entry> l = new ArrayList<>(n);
            for (int i = 0; i < n; i++) l.add(Entry.read(b));
            return new ListRes(l);
        }

        static void handle(ListRes m, Supplier<NetworkEvent.Context> s) {
            NetworkEvent.Context c = s.get();
            c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.onList(m.list)));
            c.setPacketHandled(true);
        }
    }

    // ---------- client -> server: do something to a player ----------
    public enum Type { GAMEMODE, TP_TO, BRING, KILL, GIVE, SPECTATE, SKILL, VIEW }

    public static final class Action {
        public final Type type;
        public final UUID target;
        public final String s;
        public final String s2;
        public final int n;

        public Action(Type type, UUID target, String s, String s2, int n) {
            this.type = type;
            this.target = target;
            this.s = s;
            this.s2 = s2;
            this.n = n;
        }

        static void enc(Action m, FriendlyByteBuf b) {
            b.writeEnum(m.type);
            b.writeUUID(m.target);
            b.writeUtf(m.s, 256);
            b.writeUtf(m.s2, 64);
            b.writeVarInt(m.n);
        }

        static Action dec(FriendlyByteBuf b) {
            return new Action(b.readEnum(Type.class), b.readUUID(), b.readUtf(256), b.readUtf(64), b.readVarInt());
        }

        static void handle(Action m, Supplier<NetworkEvent.Context> s) {
            NetworkEvent.Context c = s.get();
            c.enqueueWork(() -> {
                ServerPlayer p = c.getSender();
                if (AdminActions.allowed(p)) AdminActions.run(p, m);
            });
            c.setPacketHandled(true);
        }
    }
}
