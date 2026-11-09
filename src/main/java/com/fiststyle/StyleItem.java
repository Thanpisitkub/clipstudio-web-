package com.fiststyle;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * One of the 12 "Fight Style" items. It is a plain item (it can not place a block). Holding it activates the matching
 * Epic Fight weapon type (data/fiststyle/capabilities/weapons/&lt;item&gt;.json), and its attack damage / attack speed attributes
 * come from the server config, so the tooltip reads like a vanilla weapon.
 */
public class StyleItem extends Item {
    private final StyleDef def;

    public StyleItem(Properties properties, StyleDef def) {
        super(properties);
        this.def = def;
    }

    public StyleDef getDef() {
        return this.def;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) {
            return super.getDefaultAttributeModifiers(slot);
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        // The player's own base damage is 1.0 and base attack speed is 4.0, so the tooltip shows exactly min damage / 2.10 speed.
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
            BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", FistConfig.min(this.def) - 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
            BASE_ATTACK_SPEED_UUID, "Weapon modifier", FistConfig.attackSpeed(this.def) - 4.0D, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(this.def.internalName()).withStyle(ChatFormatting.GOLD));
    }
}
