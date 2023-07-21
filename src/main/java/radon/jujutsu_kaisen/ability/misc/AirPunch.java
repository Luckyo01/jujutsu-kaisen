package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.util.HelperMethods;

public class AirPunch extends Ability {
    private static final double RANGE = 30.0D;
    private static final double JUMP = 1.5D;
    private static final double SPEED = 2.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && HelperMethods.RANDOM.nextInt(3) == 0 && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            return hit.getEntity();
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target != null) {
            owner.setDeltaMovement(owner.getDeltaMovement().add(0.0D, JUMP, 0.0D));

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.delayTickEvent(() -> {
                    Vec3 direction = target.position().subtract(owner.getX(), owner.getY(), owner.getZ())
                            .normalize()
                            .scale(SPEED);
                    owner.setDeltaMovement(direction);

                    cap.scheduleTickEvent(() -> {
                        if (owner.distanceTo(target) < 3.0D) {
                            owner.swing(InteractionHand.MAIN_HAND);
                            owner.level.explode(owner, owner.getX(), owner.getY(), owner.getZ(), 1.0F, Level.ExplosionInteraction.NONE);
                            return true;
                        }
                        return false;
                    }, 20);
                }, 10);
            });
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    public int getCooldown() {
        return 3 * 20;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }
}