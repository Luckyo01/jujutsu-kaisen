package radon.jujutsu_kaisen.ability.misc.lightning;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.entity.effect.LightningEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Lightning extends Ability {
    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        LightningEntity lightning = new LightningEntity(owner, this.getPower(owner));
        owner.level().addFreshEntity(lightning);

        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.MASTER, 1.0F, 1.0F);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getNature() == CursedEnergyNature.LIGHTNING && JJKAbilities.hasToggled(owner, JJKAbilities.CURSED_ENERGY_FLOW.get()) && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}