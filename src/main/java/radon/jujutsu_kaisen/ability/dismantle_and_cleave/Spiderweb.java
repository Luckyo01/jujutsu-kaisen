package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Spiderweb extends Ability {
    private static final int RANGE = 3;
    private static final int DELAY = 20;
    private static final float EXPLOSIVE_POWER = 2.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && HelperMethods.RANDOM.nextInt(10) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable BlockHitResult getBlockHit(LivingEntity owner) {
        Vec3 start = owner.getEyePosition();
        Vec3 look = owner.getLookAngle();
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = HelperMethods.getHitResult(owner, start, end);

        if (result.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) result;
        } else if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            Vec3 offset = entity.position().subtract(0.0D, 5.0D, 0.0D);
            return owner.level().clip(new ClipContext(entity.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        owner.swing(InteractionHand.MAIN_HAND, true);

        BlockHitResult hit = this.getBlockHit(owner);

        if (hit != null) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            float radius = EXPLOSIVE_POWER * this.getPower(owner);
            float real = (radius % 2 == 0) ? radius + 1 : radius;

            Vec3 center = hit.getBlockPos().getCenter().add(owner.getLookAngle().scale(real * 0.5F));

            AABB bounds = AABB.ofSize(center, real, real, real);

            for (int i = 0; i < HelperMethods.RANDOM.nextInt(DELAY / 4, DELAY / 2); i++) {
                cap.delayTickEvent(() -> {
                    owner.level().playSound(null, center.x(), center.y(), center.z(),
                            JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);

                    BlockPos.betweenClosedStream(bounds).forEach(pos -> {
                        if (HelperMethods.RANDOM.nextInt(Math.round(radius) * 2) == 0) {
                            Vec3 current = pos.getCenter();
                            owner.level().addFreshEntity(new DismantleProjectile(owner, this.getPower(owner),
                                    (HelperMethods.RANDOM.nextFloat() - 0.5F) * 360.0F, current, 3, true, true));
                        }
                    });
                }, i * 2);
            }
        }
    }


    @Override
    public Status checkTriggerable(LivingEntity owner) {
        BlockHitResult hit = this.getBlockHit(owner);

        if (hit == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
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
}