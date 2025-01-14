package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ShadowTravel extends Ability {
    private static final double RANGE = 100.0D;

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.hasLineOfSight(target) && this.getTarget(owner) instanceof EntityHitResult hit && hit.getEntity() == target;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable HitResult getTarget(LivingEntity owner) {
        HitResult hit = HelperMethods.getLookAtHit(owner, RANGE);
        if (hit.getType() == HitResult.Type.MISS) return null;
        if (hit.getType() == HitResult.Type.BLOCK && (owner.level().getBlockState(((BlockHitResult) hit).getBlockPos().above()).canOcclude() ||
                ((BlockHitResult) hit).getDirection() != Direction.UP)) return null;

        if (owner.level().getMaxLocalRawBrightness(BlockPos.containing(hit.getLocation()), 0) > 7) return null;

        return hit;
    }

    @Override
    public void run(LivingEntity owner) {
        HitResult target = this.getTarget(owner);

        if (target != null) {
            owner.swing(InteractionHand.MAIN_HAND);

            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.MASTER, 1.0F, 1.0F);

            if (owner.level() instanceof ServerLevel level) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < owner.getBbHeight() * owner.getBbHeight(); j++) {
                        level.sendParticles(ParticleTypes.SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                                owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), 0,
                                HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D, 1.0D);
                        level.sendParticles(ParticleTypes.LARGE_SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                                owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), 0,
                                HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D, 1.0D);
                    }
                }
            }

            Vec3 pos = target.getLocation();
            owner.setPos(pos.x(), pos.y(), pos.z());

            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.MASTER, 1.0F, 1.0F);

            if (owner.level() instanceof ServerLevel level) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < owner.getBbHeight() * owner.getBbHeight(); j++) {
                        level.sendParticles(ParticleTypes.SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                                owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), 0,
                                HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D, 1.0D);
                        level.sendParticles(ParticleTypes.LARGE_SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                                owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), 0,
                                HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D, 1.0D);
                    }
                }
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        HitResult target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
