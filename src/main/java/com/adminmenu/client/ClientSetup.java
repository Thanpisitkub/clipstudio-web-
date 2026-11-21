package com.adminmenu.client;

import com.adminmenu.AdminMenuMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class ClientSetup {
    /** Default key: K (changeable in Options > Controls > Admin Menu). */
    public static final KeyMapping OPEN = new KeyMapping(
            "key.adminmenu.open", InputConstants.KEY_K, "key.categories.adminmenu");

    private ClientSetup() {}

    @Mod.EventBusSubscriber(modid = AdminMenuMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent e) {
            e.register(OPEN);
        }
    }

    @Mod.EventBusSubscriber(modid = AdminMenuMod.ID, value = Dist.CLIENT)
    public static final class ForgeBus {
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            while (OPEN.consumeClick()) {
                if (mc.player != null && mc.screen == null) mc.setScreen(new AdminScreen());
            }
        }
    }
}
