package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.projectile.MaximumBlueProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MaximumBlue extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        MaximumBlueProjectile blue = new MaximumBlueProjectile(owner);
        owner.level.addFreshEntity(blue);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 200.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}