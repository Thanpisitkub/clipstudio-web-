package com.adminmenu;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue ACCENT;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        ACCENT = b.comment("Menu accent colour as an RGB integer (0x4FD8E8 = cyan). Change it in the menu with the Theme button.")
                .defineInRange("accent", 0x4FD8E8, 0, 0xFFFFFF);
        SPEC = b.build();
    }

    private ClientConfig() {}
}
