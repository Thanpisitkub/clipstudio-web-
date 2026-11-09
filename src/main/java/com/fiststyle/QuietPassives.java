package com.fiststyle;

import java.util.function.Function;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.weaponpassive.DemonMarkPassiveSkill;
import reascer.wom.skill.weaponpassive.HerrscherPassive;
import reascer.wom.skill.weaponpassive.LunarEchoPassiveSkill;
import reascer.wom.skill.weaponpassive.RuinePassive;
import reascer.wom.skill.weaponpassive.SatsujinPassive;
import reascer.wom.skill.weaponpassive.TormentPassiveSkill;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;

/**
 * Copies of the Weapons of Miracles weapon passives that work exactly the same but never draw their gauge on the screen.
 *
 * <p>Each copy extends the original class, so all of its logic (data keys, damage events, stamina...) is unchanged; only
 * {@code shouldDraw} / {@code drawOnGui} are replaced. Weapons of Miracles compares the player's passive with the static
 * fields of {@link WOMSkills} in several places (for example Satsujin and Ruine animations), so after registering the copies the
 * fields are pointed at them. This hides the gauge for Weapons of Miracles' own weapons too while Fight Style is installed.
 */
@Mod.EventBusSubscriber(modid = FistStyle.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class QuietPassives {
    private QuietPassives() {
    }

    /** Runs after Weapons of Miracles has registered its own skills (lowest priority), then replaces its passives. */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSkillBuild(SkillBuildEvent event) {
        SkillBuildEvent.ModRegistryWorker worker = event.createRegistryWorker(FistStyle.MODID);

        WOMSkills.TORMENT_PASSIVE = register(worker, "torment_passive", b -> new Torment((SkillBuilder) b));
        WOMSkills.LUNAR_ECHO_PASSIVE = register(worker, "lunar_echo_passive", b -> new LunarEcho((SkillBuilder) b));
        WOMSkills.DEMON_MARK_PASSIVE = register(worker, "demon_mark_passive", b -> new DemonMark((SkillBuilder) b));
        WOMSkills.HERRSCHER_PASSIVE = register(worker, "herrscher_passive", b -> new Herrscher((SkillBuilder) b));
        WOMSkills.SATSUJIN_PASSIVE = register(worker, "satsujin_passive", b -> new Satsujin((SkillBuilder) b));
        WOMSkills.RUINE_PASSIVE = register(worker, "ruine_passive", b -> new Ruine((SkillBuilder) b));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Skill register(SkillBuildEvent.ModRegistryWorker worker, String name, Function factory) {
        SkillBuilder builder = PassiveSkill.createPassiveBuilder().setCategory(SkillCategories.WEAPON_PASSIVE);
        return worker.build(name, factory, builder);
    }

    public static class Torment extends TormentPassiveSkill {
        public Torment(SkillBuilder<? extends PassiveSkill> builder) {
            super(builder);
        }

        @Override
        public boolean shouldDraw(SkillContainer container) {
            return false;
        }

        @Override
        public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics graphics, float x, float y, float partialTick) {
        }
    }

    public static class LunarEcho extends LunarEchoPassiveSkill {
        public LunarEcho(SkillBuilder<? extends PassiveSkill> builder) {
            super(builder);
        }

        @Override
        public boolean shouldDraw(SkillContainer container) {
            return false;
        }

        @Override
        public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics graphics, float x, float y, float partialTick) {
        }
    }

    public static class DemonMark extends DemonMarkPassiveSkill {
        public DemonMark(SkillBuilder<? extends PassiveSkill> builder) {
            super(builder);
        }

        @Override
        public boolean shouldDraw(SkillContainer container) {
            return false;
        }

        @Override
        public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics graphics, float x, float y, float partialTick) {
        }
    }

    public static class Herrscher extends HerrscherPassive {
        public Herrscher(SkillBuilder<? extends PassiveSkill> builder) {
            super(builder);
        }

        @Override
        public boolean shouldDraw(SkillContainer container) {
            return false;
        }

        @Override
        public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics graphics, float x, float y, float partialTick) {
        }
    }

    public static class Satsujin extends SatsujinPassive {
        public Satsujin(SkillBuilder<? extends PassiveSkill> builder) {
            super(builder);
        }

        @Override
        public boolean shouldDraw(SkillContainer container) {
            return false;
        }

        @Override
        public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics graphics, float x, float y, float partialTick) {
        }
    }

    public static class Ruine extends RuinePassive {
        public Ruine(SkillBuilder<? extends PassiveSkill> builder) {
            super(builder);
        }

        @Override
        public boolean shouldDraw(SkillContainer container) {
            return false;
        }

        @Override
        public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics graphics, float x, float y, float partialTick) {
        }
    }
}
