package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFloatGoal;
import radon.jujutsu_kaisen.entity.ai.goal.LookAtTargetGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableCurseGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.ArrayList;
import java.util.List;

public class YutaOkkotsuEntity extends SorcererEntity {
    private static final int CHANGE_INTERVAL = 10 * 20;

    public YutaOkkotsuEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return CursedTechnique.MIMICRY;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.SHOOT_RCT.get());
    }

    @Override
    public List<Ability> getUnlocked() {
        return List.of(JJKAbilities.SIMPLE_DOMAIN.get());
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    @Override
    public void init(ISorcererData data) {
        super.init(data);

        data.copy(CursedTechnique.CURSED_SPEECH);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        ISorcererData cap = this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getCurrentCopied() == null || this.tickCount % CHANGE_INTERVAL == 0) {
            List<CursedTechnique> copied = new ArrayList<>(cap.getCopied());

            if (!copied.isEmpty()) {
                cap.setCurrentCopied(copied.get(this.random.nextInt(copied.size())));
            }
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.YUTA_OKKOTSU_SWORD.get()));

        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(JJKItems.YUTA_CHESTPLATE.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(JJKItems.YUTA_LEGGINGS.get()));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(JJKItems.YUTA_BOOTS.get()));
    }
}
