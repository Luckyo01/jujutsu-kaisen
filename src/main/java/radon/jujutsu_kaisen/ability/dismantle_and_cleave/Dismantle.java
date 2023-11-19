package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Dismantle extends Ability implements Ability.IChannelened, Ability.IDurationable, Ability.IDomainAttack {
    public static final float SPEED = 5.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && (JJKAbilities.isChanneling(owner, this) ||HelperMethods.RANDOM.nextInt(3) == 0 && owner.hasLineOfSight(target));
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (this.getCharge(owner) % 2 == 0) {
            owner.swing(InteractionHand.MAIN_HAND);

            DismantleProjectile dismantle = new DismantleProjectile(owner, this.getPower(owner), (owner.isShiftKeyDown() ? 90.0F : 0.0F) + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F);
            dismantle.setDeltaMovement(dismantle.getLookAngle().scale(SPEED));
            owner.level().addFreshEntity(dismantle);

            if (!owner.level().isClientSide) {
                owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void performBlock(LivingEntity owner, @Nullable DomainExpansionEntity domain, BlockPos pos) {
        DismantleProjectile dismantle = new DismantleProjectile(owner, this.getPower(owner), (HelperMethods.RANDOM.nextFloat() - 0.5F) * 360.0F, pos.getCenter(), 5, true);
        owner.level().addFreshEntity(dismantle);

        if (!owner.level().isClientSide) {
            owner.level().playSound(null, pos.getX(), pos.getY(), pos.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public int getDuration() {
        return 10;
    }



    @Override
    public boolean isScalable() {
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {

    }
}
