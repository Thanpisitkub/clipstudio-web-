package com.fiststyle;

import java.util.List;

/**
 * Static description of one fighting style.
 *
 * <p>Animation names are written as {@code "Class.FIELD"} strings and resolved by reflection in {@link AnimLookup},
 * so a missing animation never crashes the game. {@code "A|B"} means "use A, or B when A is not available"
 * (used for the Epic Sanji moves, which only exist when their keyframe files were added to the jar).
 * Every name below was checked against the Weapons of Miracles 2.0.171 jar. The classes are:
 * {@code WOM} (reascer.wom.gameasset.WOMAnimations), {@code Agony}, {@code Enderblaster}, {@code Nova}, {@code Orbit},
 * {@code Moonless}, {@code Blackstar}, {@code Solar}, {@code Ruine}, {@code Herrscher} (reascer.wom.gameasset.animations.weapons.Anims*)
 * and {@code Sanji} (com.fiststyle.SanjiAnimations).
 *
 * <p>Combo layout (6 slots, same as Weapons of Miracles): [punch 1, punch 2, punch 3, punch 4, running attack, jumping attack].
 * Stance layout: [idle, walk, run, guard].
 *
 * <p>Both lists can be overridden without rebuilding through {@code config/fiststyle-combos.json}.
 */
public record StyleDef(int number, String presetId, String internalName, double defaultMin, double defaultMax,
                       String[] stance, String[] combo) {

    public static final double ATTACK_SPEED = 2.10D;

    public String itemName() {
        return "fight_style_" + number;
    }

    private static final String[] AGONY_STANCE = {"Agony.AGONY_IDLE", "Agony.AGONY_WALK", "Agony.AGONY_RUN", "Agony.AGONY_GUARD"};


    /**
     * Every style is a mix of single moves taken from different Weapons of Miracles / Epic Sanji sets, so no two styles share a combo.
     *
     * <p>Moves of Torment, Antitheus, Moonless and Herrscher read data that only their own weapon skills register, and a player has
     * only one weapon passive, so those four styles keep their own moves in the places that need them (see StylePresets) and
     * mix in moves that need no skill (Ender Blaster, Nova, Orbit, Blackstar, Sanji). The other styles mix freely, including Agony.
     */
    public static final List<StyleDef> ALL = List.of(
        // 1. Light Fist (fast): Ender Blaster one-hand punches with one Nova move. Jump: Ender Blaster jump kick.
        new StyleDef(1, "light_fist", "Light Fist", 3.90, 5.00, AGONY_STANCE, new String[] {
            "Enderblaster.ENDERBLASTER_ONEHAND_AUTO_1", "Enderblaster.ENDERBLASTER_ONEHAND_AUTO_2",
            "Nova.NOVA_ATTACK_3", "Enderblaster.ENDERBLASTER_ONEHAND_AUTO_4",
            "Enderblaster.ENDERBLASTER_ONEHAND_DASH", "Enderblaster.ENDERBLASTER_ONEHAND_JUMPKICK"}),

        // 2. Heavy Fist (slow, heavy): Torment with one Orbit move.
        new StyleDef(2, "heavy_fist", "Heavy Fist", 5.00, 8.00, AGONY_STANCE, new String[] {
            "WOM.TORMENT_AUTO_1", "WOM.TORMENT_AUTO_2", "Orbit.ORBIT_ATTACK_3", "WOM.TORMENT_AUTO_4",
            "WOM.TORMENT_DASH", "WOM.TORMENT_AIRSLAM"}),

        // 3. Drunken Fist: Torment moves in another order mixed with Blackstar; Nova air slash.
        new StyleDef(3, "drunken_fist", "Drunken Fist", 4.50, 7.00, AGONY_STANCE, new String[] {
            "WOM.TORMENT_AUTO_3", "Blackstar.BLACKSTAR_ATTACK_2", "WOM.TORMENT_AUTO_1", "WOM.TORMENT_AUTO_2",
            "WOM.TORMENT_DASH", "Nova.NOVA_ATTACK_AIRSLASH"}),

        // 4. Dragon Fist: Orbit with one Agony move and a Nova air slash.
        new StyleDef(4, "dragon_fist", "Dragon Fist", 4.20, 6.00, AGONY_STANCE, new String[] {
            "Orbit.ORBIT_ATTACK_1", "Orbit.ORBIT_ATTACK_2", "Agony.AGONY_AUTO_3", "Orbit.ORBIT_ATTACK_4",
            "Orbit.ORBIT_SATELITE", "Nova.NOVA_ATTACK_AIRSLASH"}),

        // 5. Shadow Fist: Moonless with an Ender Blaster punch.
        new StyleDef(5, "shadow_fist", "Shadow Fist", 3.60, 5.20, AGONY_STANCE, new String[] {
            "Moonless.MOONLESS_AUTO_1", "Moonless.MOONLESS_AUTO_2", "Enderblaster.ENDERBLASTER_ONEHAND_AUTO_3", "Moonless.MOONLESS_AUTO_3_VERSO",
            "Moonless.MOONLESS_BYPASS", "Moonless.MOONLESS_CRESCENT"}),

        // 6. Tiger Fist: Agony claws with one Blackstar move.
        new StyleDef(6, "tiger_fist", "Tiger Fist", 4.50, 6.50, AGONY_STANCE, new String[] {
            "Agony.AGONY_AUTO_1", "Agony.AGONY_AUTO_2", "Blackstar.BLACKSTAR_ATTACK_3", "Agony.AGONY_AUTO_4",
            "Agony.AGONY_CLAWSTRIKE", "Agony.AGONY_AIR_ATTACK_3"}),

        // 7. Thunder Fist: Nova with one Ender Blaster two-hand move.
        new StyleDef(7, "thunder_fist", "Thunder Fist", 4.00, 5.80, AGONY_STANCE, new String[] {
            "Nova.NOVA_ATTACK_1", "Enderblaster.ENDERBLASTER_TWOHAND_AUTO_2", "Nova.NOVA_ATTACK_3", "Nova.NOVA_ATTACK_4",
            "Nova.NOVA_ATTACK_DASH", "Nova.NOVA_ATTACK_AIRSLASH"}),

        // 8. Wind Fist: Epic Sanji strikes with an Orbit run attack and an Agony air attack (Blackstar when the Sanji keyframes were not added).
        new StyleDef(8, "wind_fist", "Wind Fist", 3.50, 5.00, AGONY_STANCE, new String[] {
            "Sanji.AUTO_1|Blackstar.BLACKSTAR_ATTACK_1", "Sanji.AUTO_2|Blackstar.BLACKSTAR_ATTACK_2",
            "Sanji.AUTO_3|Blackstar.BLACKSTAR_ATTACK_3", "Sanji.AUTO_4|Blackstar.BLACKSTAR_ATTACK_4",
            "Orbit.ORBIT_SATELITE", "Agony.AGONY_AIR_ATTACK_2"}),

        // 9. Iron Fist: Antitheus with one Blackstar move.
        new StyleDef(9, "iron_fist", "Iron Fist", 5.00, 7.50, AGONY_STANCE, new String[] {
            "WOM.ANTITHEUS_AUTO_1", "WOM.ANTITHEUS_AUTO_2", "Blackstar.BLACKSTAR_ATTACK_3", "WOM.ANTITHEUS_AUTO_4",
            "WOM.ANTITHEUS_AGRESSION", "WOM.ANTITHEUS_GUILLOTINE"}),

        // 10. Flame Fist: Epic Sanji Diable Jambe kicks with an Agony air attack. No Solar moves (they need the Solar gauge).
        new StyleDef(10, "flame_fist", "Flame Fist", 4.50, 6.50, AGONY_STANCE, new String[] {
            "Sanji.DIABLE_AUTO_1|Blackstar.BLACKSTAR_ATTACK_1", "Sanji.DIABLE_AUTO_2|Blackstar.BLACKSTAR_ATTACK_2",
            "Sanji.DIABLE_AUTO_3|Blackstar.BLACKSTAR_ATTACK_3", "Sanji.DIABLE_AUTO_4|Blackstar.BLACKSTAR_ATTACK_4",
            "Sanji.DIABLE_ANTIMANER|Blackstar.BLACKSTAR_CHOCKNWAVE", "Agony.AGONY_AIR_ATTACK_4"}),

        // 11. Phantom Fist (fast): Ender Blaster two-hand with Orbit and Blackstar moves, Nova run attack, Tomahawk jump.
        new StyleDef(11, "phantom_fist", "Phantom Fist", 3.80, 5.50, AGONY_STANCE, new String[] {
            "Enderblaster.ENDERBLASTER_TWOHAND_AUTO_1", "Orbit.ORBIT_ATTACK_2", "Enderblaster.ENDERBLASTER_TWOHAND_AUTO_3",
            "Blackstar.BLACKSTAR_ATTACK_4", "Nova.NOVA_ATTACK_DASH", "Enderblaster.ENDERBLASTER_TWOHAND_TOMAHAWK"}),

        // 12. God Fist: Herrscher with an Orbit move.
        new StyleDef(12, "god_fist", "God Fist", 5.50, 8.00, AGONY_STANCE, new String[] {
            "Herrscher.HERRSCHER_AUTO_1", "Orbit.ORBIT_ATTACK_2", "Herrscher.HERRSCHER_AUTO_3", "Herrscher.HERRSCHER_VERDAMMNIS",
            "Herrscher.HERRSCHER_BEFREIUNG", "Herrscher.HERRSCHER_AUSROTTUNG"}),

        // 13. Sanji Fist: Epic Sanji strikes (one Diable kick), Concasser run attack, and the two-handed Ender Blaster gun move as the jump.
        new StyleDef(13, "sanji_fist", "Sanji Fist", 4.00, 5.60, AGONY_STANCE, new String[] {
            "Sanji.AUTO_1|Blackstar.BLACKSTAR_ATTACK_1", "Sanji.AUTO_2|Blackstar.BLACKSTAR_ATTACK_2",
            "Sanji.DIABLE_AUTO_3|Blackstar.BLACKSTAR_ATTACK_3", "Sanji.AUTO_4|Blackstar.BLACKSTAR_ATTACK_4",
            "Sanji.CONCASSER|Blackstar.BLACKSTAR_CHOCKNWAVE", "Enderblaster.ENDERBLASTER_TWOHAND_TISHNAW"})
    );
}
