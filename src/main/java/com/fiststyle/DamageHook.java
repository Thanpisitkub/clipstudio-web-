package com.fiststyle;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * Server-side damage of the Fight Style combos.
 *
 * <p>The hit itself (who was hit, when, how often) is decided by Epic Fight on the server. Here the damage of that hit is set to
 * the value of the combo move that is playing: punch 1 = damageMin, punch 4 = damageMax, the running and jumping attacks = the
 * middle value. Extra damage the player has from other sources (Strength and so on) is kept. The event only fires for a
 * {@link ServerPlayer} on the logical server, so a client can not change the result.
 */
public final class DamageHook {
    private DamageHook() {
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack held = player.getMainHandItem();

        if (!(held.getItem() instanceof StyleItem item)) {
            return;
        }

        ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);

        if (patch == null) {
            return;
        }

        int step = StylePresets.stepOf(item.getDef(), patch.getServerAnimator().animationPlayer.getAnimation());

        if (step < 0) {
            return;
        }

        double min = FistConfig.min(item.getDef());
        double max = FistConfig.max(item.getDef());
        double target = comboDamage(min, max, step);
        double extra = player.getAttributeValue(Attributes.ATTACK_DAMAGE) - min;
        event.setAmount((float) Math.max(0.0D, target + extra));
    }

    /** Damage of a combo move: punches 1-4 rise from min to max, the running and jumping attacks use the middle value. */
    static double comboDamage(double min, double max, int step) {
        if (step <= 3) {
            return min + (max - min) * step / 3.0D;
        }

        return (min + max) / 2.0D;
    }
}
