package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.entity.effect.ForestWaveEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ForestWave extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final int OFFSET = 3;
    private static final int DELAY = 3;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        int charge = this.getCharge(owner) + OFFSET;

        float xRot = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 90.0F;
        float yRot = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 90.0F;

        for (int i = -1; i < 2; i++) {
            ForestWaveEntity forest = new ForestWaveEntity(owner);
            Vec3 look = HelperMethods.getLookAngle(owner);
            Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (forest.getBbHeight() / 2.0F), owner.getZ()).add(look.scale(charge + i));
            forest.moveTo(spawn.x(), spawn.y(), spawn.z(), yRot, xRot);

            if (charge - OFFSET != 0 && HelperMethods.getEntityCollisionsOfClass(ForestWaveEntity.class, owner.level(), forest.getBoundingBox()).isEmpty()) continue;

            forest.setDamage(charge - OFFSET >= DELAY);

            owner.level().addFreshEntity(forest);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public void onRelease(LivingEntity owner, int charge) {

    }

    @Override
    public int getDuration() {
        return 20;
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}