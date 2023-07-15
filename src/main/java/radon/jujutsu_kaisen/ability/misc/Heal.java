package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.concurrent.atomic.AtomicReference;

public class Heal extends Ability {
    private static final float AMOUNT = 1.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return owner.getHealth() < owner.getMaxHealth();
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                owner.heal(AMOUNT * cap.getGrade().getPower()));
    }

    @Override
    public float getCost(LivingEntity owner) {
        AtomicReference<Float> result = new AtomicReference<>(0.0F);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (owner.getHealth() < owner.getMaxHealth()) {
                result.set(10.0F);
            }
        });
        return result.get();
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }
}