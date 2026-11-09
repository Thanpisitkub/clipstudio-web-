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

    private static String[] stance(String idle, String walk, String run) {
        return new String[] {idle, walk, run, "Agony.AGONY_GUARD"};
    }

    public static final List<StyleDef> ALL = List.of(
        // 1. Light Fist: Ender Blaster one-hand punches. PUNCH 3 (the uppercut slot) is ENDERBLASTER_ONEHAND_AUTO_3 and has NOT been
        //    verified to be an upward punch: check it in game and change it in config/fiststyle-combos.json if needed.
        new StyleDef(1, "light_fist", "Light Fist", 3.90, 5.00, AGONY_STANCE, new String[] {
            "Enderblaster.ENDERBLASTER_ONEHAND_AUTO_1", "Enderblaster.ENDERBLASTER_ONEHAND_AUTO_2",
            "Enderblaster.ENDERBLASTER_ONEHAND_AUTO_3", "Enderblaster.ENDERBLASTER_ONEHAND_AUTO_4",
            "Agony.AGONY_CLAWSTRIKE", "Agony.AGONY_RIPPING_FANGS"}),

        // 2. Heavy Fist: the Torment (axe) heavy moveset, slow and powerful.
        new StyleDef(2, "heavy_fist", "Heavy Fist", 5.00, 8.00,
            stance("WOM.TORMENT_IDLE", "WOM.TORMENT_WALK", "WOM.TORMENT_RUN"), new String[] {
            "WOM.TORMENT_AUTO_1", "WOM.TORMENT_AUTO_2", "WOM.TORMENT_AUTO_3", "WOM.TORMENT_AUTO_4",
            "WOM.TORMENT_DASH", "WOM.TORMENT_AIRSLAM"}),

        // 3. Drunken Fist: reuses the Heavy Fist animations in a different order (same Torment stance: mixing in Moonless
        //    would need a second weapon passive skill, and a player can only have one).
        new StyleDef(3, "drunken_fist", "Drunken Fist", 4.50, 7.00,
            stance("WOM.TORMENT_IDLE", "WOM.TORMENT_WALK", "WOM.TORMENT_RUN"), new String[] {
            "WOM.TORMENT_AUTO_3", "WOM.TORMENT_AUTO_1", "WOM.TORMENT_AUTO_4", "WOM.TORMENT_AUTO_2",
            "WOM.TORMENT_DASH", "WOM.TORMENT_AIRSLAM"}),

        // 4. Dragon Fist: the Orbit set.
        new StyleDef(4, "dragon_fist", "Dragon Fist", 4.20, 6.00,
            stance("Orbit.ORBIT_IDLE", "Orbit.ORBIT_WALK", "Orbit.ORBIT_RUN"), new String[] {
            "Orbit.ORBIT_ATTACK_1", "Orbit.ORBIT_ATTACK_2", "Orbit.ORBIT_ATTACK_3", "Orbit.ORBIT_ATTACK_4",
            "Orbit.ORBIT_SATELITE", "Orbit.ORBIT_MAD_REACH"}),

        // 5. Shadow Fist: the Moonless set (normal and verso strikes).
        new StyleDef(5, "shadow_fist", "Shadow Fist", 3.60, 5.20,
            stance("Moonless.MOONLESS_IDLE", "Moonless.MOONLESS_WALK", "Moonless.MOONLESS_RUN"), new String[] {
            "Moonless.MOONLESS_AUTO_1", "Moonless.MOONLESS_AUTO_2", "Moonless.MOONLESS_AUTO_3", "Moonless.MOONLESS_AUTO_3_VERSO",
            "Moonless.MOONLESS_BYPASS", "Moonless.MOONLESS_CRESCENT"}),

        // 6. Tiger Fist: the Agony claw set.
        new StyleDef(6, "tiger_fist", "Tiger Fist", 4.50, 6.50, AGONY_STANCE, new String[] {
            "Agony.AGONY_AUTO_1", "Agony.AGONY_AUTO_2", "Agony.AGONY_AUTO_3", "Agony.AGONY_AUTO_4",
            "Agony.AGONY_CLAWSTRIKE", "Agony.AGONY_AIR_ATTACK_1"}),

        // 7. Thunder Fist: the Nova set.
        new StyleDef(7, "thunder_fist", "Thunder Fist", 4.00, 5.80,
            stance("Nova.NOVA_IDLE", "Nova.NOVA_WALK", "Nova.NOVA_RUN"), new String[] {
            "Nova.NOVA_ATTACK_1", "Nova.NOVA_ATTACK_2", "Nova.NOVA_ATTACK_3", "Nova.NOVA_ATTACK_4",
            "Nova.NOVA_ATTACK_DASH", "Nova.NOVA_ATTACK_AIRSLASH"}),

        // 8. Wind Fist: Epic Sanji's four strikes and jump attack (Blackstar strikes when the Sanji keyframes were not added to the jar).
        new StyleDef(8, "wind_fist", "Wind Fist", 3.50, 5.00, AGONY_STANCE, new String[] {
            "Sanji.AUTO_1|Blackstar.BLACKSTAR_ATTACK_1", "Sanji.AUTO_2|Blackstar.BLACKSTAR_ATTACK_2",
            "Sanji.AUTO_3|Blackstar.BLACKSTAR_ATTACK_3", "Sanji.AUTO_4|Blackstar.BLACKSTAR_ATTACK_4",
            "Blackstar.BLACKSTAR_CHOCKNWAVE", "Sanji.CONCASSER|Agony.AGONY_AIR_ATTACK_2"}),

        // 9. Iron Fist: the Antitheus set, heavy and armour-piercing.
        new StyleDef(9, "iron_fist", "Iron Fist", 5.00, 7.50,
            stance("WOM.ANTITHEUS_IDLE", "WOM.ANTITHEUS_WALK", "WOM.ANTITHEUS_RUN"), new String[] {
            "WOM.ANTITHEUS_AUTO_1", "WOM.ANTITHEUS_AUTO_2", "WOM.ANTITHEUS_AUTO_3", "WOM.ANTITHEUS_AUTO_4",
            "WOM.ANTITHEUS_AGRESSION", "WOM.ANTITHEUS_GUILLOTINE"}),

        // 10. Flame Fist: Epic Sanji's Diable Jambe kicks. No Solar animations (they need the Solar heat gauge), so without the
        //     Sanji keyframes the Blackstar strikes are used instead.
        new StyleDef(10, "flame_fist", "Flame Fist", 4.50, 6.50, AGONY_STANCE, new String[] {
            "Sanji.DIABLE_AUTO_1|Blackstar.BLACKSTAR_ATTACK_1", "Sanji.DIABLE_AUTO_2|Blackstar.BLACKSTAR_ATTACK_2",
            "Sanji.DIABLE_AUTO_3|Blackstar.BLACKSTAR_ATTACK_3", "Sanji.DIABLE_AUTO_4|Blackstar.BLACKSTAR_ATTACK_4",
            "Sanji.DIABLE_ANTIMANER|Blackstar.BLACKSTAR_CHOCKNWAVE", "Sanji.DIABLE_ANTIMANER|Agony.AGONY_AIR_ATTACK_1"}),

        // 11. Phantom Fist: the Ender Blaster two-hand set.
        new StyleDef(11, "phantom_fist", "Phantom Fist", 3.80, 5.50, AGONY_STANCE, new String[] {
            "Enderblaster.ENDERBLASTER_TWOHAND_AUTO_1", "Enderblaster.ENDERBLASTER_TWOHAND_AUTO_2",
            "Enderblaster.ENDERBLASTER_TWOHAND_AUTO_3", "Enderblaster.ENDERBLASTER_TWOHAND_AUTO_4",
            "Enderblaster.ENDERBLASTER_ONEHAND_DASH", "Enderblaster.ENDERBLASTER_TWOHAND_TISHNAW"}),

        // 12. God Fist: the Herrscher set.
        new StyleDef(12, "god_fist", "God Fist", 5.50, 8.00,
            stance("Herrscher.HERRSCHER_IDLE", "Herrscher.HERRSCHER_WALK", "Herrscher.HERRSCHER_RUN"), new String[] {
            "Herrscher.HERRSCHER_AUTO_1", "Herrscher.HERRSCHER_AUTO_2", "Herrscher.HERRSCHER_AUTO_3", "Herrscher.HERRSCHER_VERDAMMNIS",
            "Herrscher.HERRSCHER_BEFREIUNG", "Herrscher.HERRSCHER_AUSROTTUNG"}),

        // 13. Sanji Fist: Epic Sanji's four strikes and Concasser run attack (Blackstar when the Sanji keyframes were not added),
        //     with the two-handed Ender Blaster gun move (Tishnaw) as the jumping attack. Stance and run: Blackstar.
        new StyleDef(13, "sanji_fist", "Sanji Fist", 4.00, 5.60,
            stance("Blackstar.BLACKSTAR_IDLE", "Blackstar.BLACKSTAR_WALK", "Blackstar.BLACKSTAR_RUN"), new String[] {
            "Sanji.AUTO_1|Blackstar.BLACKSTAR_ATTACK_1", "Sanji.AUTO_2|Blackstar.BLACKSTAR_ATTACK_2",
            "Sanji.AUTO_3|Blackstar.BLACKSTAR_ATTACK_3", "Sanji.AUTO_4|Blackstar.BLACKSTAR_ATTACK_4",
            "Sanji.CONCASSER|Blackstar.BLACKSTAR_CHOCKNWAVE", "Enderblaster.ENDERBLASTER_TWOHAND_TISHNAW"})
    );
}
