package radon.jujutsu_kaisen.ability.ten_shadows.ability;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.TenShadowsMode;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.WheelEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;

import java.util.concurrent.atomic.AtomicBoolean;

public class Wheel extends Summon<WheelEntity> {
    public Wheel() {
        super(WheelEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (owner instanceof MahoragaEntity) return true;
        if (target == null) return false;

        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(ownerCap -> {
            target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(targetCap -> {
                result.set(targetCap.getTechnique() != null && !ownerCap.isAdaptedTo(targetCap.getTechnique()));
            });
        });
        return result.get();
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        if (owner instanceof MahoragaEntity) return true;

        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.getMode() == TenShadowsMode.ABILITY));
        return result.get();
    }

    @Override
    public EntityType<WheelEntity> getType() {
        return JJKEntities.WHEEL.get();
    }

    @Override
    public boolean isTenShadows() {
        return true;
    }

    @Override
    protected WheelEntity summon(int index, LivingEntity owner) {
        return new WheelEntity(owner);
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.hasSummonOfClass(level, MahoragaEntity.class)));
        }
        return result.get() ? Status.FAILURE : super.checkStatus(owner);
    }

    @Override
    public Status checkToggleable(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.hasSummonOfClass(level, MahoragaEntity.class)));
        }
        return result.get() ? Status.FAILURE : super.checkToggleable(owner);
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.0F;
    }

    @Override
    public boolean shouldLog() {
        return false;
    }
}