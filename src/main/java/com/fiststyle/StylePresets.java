package com.fiststyle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.gameasset.colliders.WOMWeaponColliders;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

/**
 * The 12 Epic Fight weapon types ("presets") fiststyle:light_fist ... fiststyle:god_fist.
 *
 * <p>Epic Fight does the rest of the work for these types: it plays the combo in order, resets it when the player stops
 * attacking, refuses to start a new attack animation while another one is running, hits every target once per attack phase,
 * applies knockback / stun on the server and sends the animation to the other players. Nothing in this class replaces that.
 */
public final class StylePresets {
    /** The animations each style really uses (after the Sanji fallbacks were resolved), used to find the combo step of a hit. */
    private static final Map<String, AnimationManager.AnimationAccessor<?>[]> RESOLVED = new ConcurrentHashMap<>();

    private StylePresets() {
    }

    public static void register(WeaponCapabilityPresetRegistryEvent event) {
        Map<ResourceLocation, Function<Item, CapabilityItem.Builder>> types = event.getTypeEntry();

        for (StyleDef def : StyleDef.ALL) {
            types.put(new ResourceLocation(FistStyle.MODID, def.presetId()), item -> build(def));
        }
    }

    @SuppressWarnings("unchecked")
    private static CapabilityItem.Builder build(StyleDef def) {
        WeaponCapability.Builder builder = WeaponCapability.builder()
            .category(CapabilityItem.WeaponCategories.FIST)
            .styleProvider(entitypatch -> CapabilityItem.Styles.TWO_HAND)
            .collider(WOMWeaponColliders.AGONY)
            .hitSound(EpicFightSounds.BLUNT_HIT.get())
            .canBePlacedOffhand(false)
            .innateSkill(CapabilityItem.Styles.TWO_HAND, itemstack -> WOMSkills.AGONY_PLUNGE);

        String[] stance = FistConfig.stance(def);
        motion(builder, LivingMotions.IDLE, stance[0], def);
        motion(builder, LivingMotions.WALK, stance[1], def);
        motion(builder, LivingMotions.CHASE, stance[2], def);
        motion(builder, LivingMotions.RUN, stance[2], def);
        motion(builder, LivingMotions.BLOCK, stance[3], def);

        String[] names = FistConfig.combo(def);
        AnimationManager.AnimationAccessor<? extends AttackAnimation>[] combo = new AnimationManager.AnimationAccessor[names.length];

        for (int i = 0; i < names.length; i++) {
            combo[i] = AnimLookup.attack(names[i]);

            if (combo[i] == null) {
                // Never leave a hole in the combo: reuse the first available move of this style.
                FistStyle.LOGGER.error("{}: animation '{}' is not available, slot {} falls back to another move", def.presetId(), names[i], i + 1);
                combo[i] = firstAvailable(def, i);
            }
        }

        RESOLVED.put(def.presetId(), combo);
        builder.newStyleCombo(CapabilityItem.Styles.TWO_HAND, combo);
        return builder;
    }

    private static void motion(WeaponCapability.Builder builder, LivingMotions motion, String name, StyleDef def) {
        AnimationManager.AnimationAccessor<? extends StaticAnimation> animation = AnimLookup.stance(name);

        if (animation == null) {
            FistStyle.LOGGER.warn("{}: stance animation '{}' is not available, Epic Fight's own pose is used", def.presetId(), name);
            return;
        }

        builder.livingMotionModifier(CapabilityItem.Styles.TWO_HAND, motion, animation);
    }

    @SuppressWarnings("unchecked")
    private static AnimationManager.AnimationAccessor<? extends AttackAnimation> firstAvailable(StyleDef def, int slot) {
        for (String candidate : def.combo()) {
            for (String alternative : candidate.split("\\|")) {
                AnimationManager.AnimationAccessor<? extends AttackAnimation> found = AnimLookup.attack(alternative);

                if (found != null) {
                    return found;
                }
            }
        }

        throw new IllegalStateException("Fight Style '" + def.presetId() + "' has no usable attack animation at all (slot " + (slot + 1) + ")");
    }

    /**
     * Index (0-3 punches, 4 running attack, 5 jumping attack) of the combo move that is playing, or -1 when the
     * current animation is not one of this style's combo moves (for example a skill).
     */
    public static int stepOf(StyleDef def, Object currentAnimation) {
        AnimationManager.AnimationAccessor<?>[] combo = RESOLVED.get(def.presetId());

        if (combo == null || currentAnimation == null) {
            return -1;
        }

        for (int i = 0; i < combo.length; i++) {
            if (combo[i].equals(currentAnimation)) {
                return i;
            }

            if (currentAnimation instanceof AssetAccessor<?> accessor && accessor.get() == combo[i].get()) {
                return i;
            }
        }

        return -1;
    }
}
