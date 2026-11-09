package com.fiststyle;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Fight Style: 13 invisible items that switch the player to one of 13 Epic Fight fist styles. */
@Mod(FistStyle.MODID)
public class FistStyle {
    public static final String MODID = "fiststyle";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    /** fiststyle:fight_style_1 ... fiststyle:fight_style_13, in order. Plain Items (not BlockItems), so none can place a block. */
    public static final List<RegistryObject<Item>> STYLE_ITEMS = new ArrayList<>();

    static {
        for (StyleDef def : StyleDef.ALL) {
            STYLE_ITEMS.add(ITEMS.register(def.itemName(),
                () -> new StyleItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), def)));
        }
    }

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("fiststyle", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.fiststyle"))
        .icon(() -> new ItemStack(STYLE_ITEMS.get(0).get()))
        .displayItems((params, output) -> {
            for (RegistryObject<Item> item : STYLE_ITEMS) {
                output.accept(item.get());
            }
        })
        .build());

    public FistStyle() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        TABS.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, FistConfig.SPEC, "fiststyle-server.toml");
        // The animation names are read before Epic Fight builds the weapon types.
        FistConfig.loadComboOverrides();

        // Registers the fiststyle:* weapon types (needs Epic Fight and Weapons of Miracles)
        bus.addListener(StylePresets::register);
        // Registers the Epic Sanji moves (only when their keyframe files were added to the jar)
        bus.addListener(SanjiAnimations::register);
        bus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(DamageHook.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Fight Style loaded: {} styles, Sanji animations {}", StyleDef.ALL.size(),
            SanjiAnimations.available() ? "available" : "not added to the jar (fallback moves are used)");
    }
}
