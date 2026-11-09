package com.fiststyle;

import java.util.Map;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;

/**
 * Resolves animation names such as {@code "Agony.AGONY_AUTO_1"} to the animation accessors that Weapons of Miracles
 * (and this mod, for Sanji) registered. Only animations that really exist are returned; an unknown name is logged and skipped,
 * and {@code "A|B"} falls back from A to B.
 */
public final class AnimLookup {
    private static final String WEAPONS = "reascer.wom.gameasset.animations.weapons.Anims";

    private static final Map<String, String> CLASSES = Map.ofEntries(
        Map.entry("WOM", "reascer.wom.gameasset.WOMAnimations"),
        Map.entry("Agony", WEAPONS + "Agony"),
        Map.entry("Enderblaster", WEAPONS + "Enderblaster"),
        Map.entry("Nova", WEAPONS + "Nova"),
        Map.entry("Orbit", WEAPONS + "Orbit"),
        Map.entry("Moonless", WEAPONS + "Moonless"),
        Map.entry("Blackstar", WEAPONS + "Blackstar"),
        Map.entry("Solar", WEAPONS + "Solar"),
        Map.entry("Ruine", WEAPONS + "Ruine"),
        Map.entry("Herrscher", WEAPONS + "Herrscher"),
        Map.entry("Satsujin", WEAPONS + "Satsujin"),
        Map.entry("Sanji", "com.fiststyle.SanjiAnimations"));

    private AnimLookup() {
    }

    /** An attack animation, or null when none of the alternatives exists. */
    @SuppressWarnings("unchecked")
    public static AnimationManager.AnimationAccessor<? extends AttackAnimation> attack(String spec) {
        Object found = find(spec, true);
        return (AnimationManager.AnimationAccessor<? extends AttackAnimation>) found;
    }

    /** A stance animation (idle / walk / run / guard), or null when none of the alternatives exists. */
    @SuppressWarnings("unchecked")
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> stance(String spec) {
        Object found = find(spec, false);
        return (AnimationManager.AnimationAccessor<? extends StaticAnimation>) found;
    }

    private static Object find(String spec, boolean attack) {
        for (String alternative : spec.split("\\|")) {
            int dot = alternative.indexOf('.');

            if (dot < 0) {
                FistStyle.LOGGER.warn("Bad animation name '{}': expected Class.FIELD", alternative);
                continue;
            }

            String className = CLASSES.get(alternative.substring(0, dot));

            if (className == null) {
                FistStyle.LOGGER.warn("Unknown animation class in '{}'", alternative);
                continue;
            }

            try {
                Object value = Class.forName(className).getField(alternative.substring(dot + 1)).get(null);

                if (value instanceof AnimationManager.AnimationAccessor<?> accessor) {
                    Object animation = accessor.get();

                    if (attack ? animation instanceof AttackAnimation : animation instanceof StaticAnimation) {
                        return accessor;
                    }

                    FistStyle.LOGGER.warn("Animation '{}' is not the expected type", alternative);
                }
                // value == null: the animation was not registered (for example the Sanji keyframes are missing)
            } catch (ReflectiveOperationException | LinkageError e) {
                FistStyle.LOGGER.warn("Animation '{}' not found: {}", alternative, e.toString());
            }
        }

        return null;
    }
}
