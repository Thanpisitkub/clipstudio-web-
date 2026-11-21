package com.adminmenu;

import com.adminmenu.net.Net;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(AdminMenuMod.ID)
public class AdminMenuMod {
    public static final String ID = "adminmenu";

    public AdminMenuMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        Net.init();
    }
}
