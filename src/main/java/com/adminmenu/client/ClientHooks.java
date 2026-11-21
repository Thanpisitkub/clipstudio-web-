package com.adminmenu.client;

import com.adminmenu.net.Packets;
import net.minecraft.client.Minecraft;

import java.util.List;

/** Entry points called by packets; only ever loaded on the client. */
public final class ClientHooks {
    private ClientHooks() {}

    public static void onList(List<Packets.Entry> list) {
        if (Minecraft.getInstance().screen instanceof AdminScreen s) s.onList(list);
    }
}
