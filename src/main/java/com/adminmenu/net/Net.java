package com.adminmenu.net;

import com.adminmenu.AdminMenuMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class Net {
    private static final String VERSION = "1";

    public static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AdminMenuMod.ID, "main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    private Net() {}

    public static void init() {
        int i = 0;
        CH.registerMessage(i++, Packets.ListReq.class, Packets.ListReq::enc, Packets.ListReq::dec, Packets.ListReq::handle);
        CH.registerMessage(i++, Packets.ListRes.class, Packets.ListRes::enc, Packets.ListRes::dec, Packets.ListRes::handle);
        CH.registerMessage(i++, Packets.Action.class, Packets.Action::enc, Packets.Action::dec, Packets.Action::handle);
    }
}
