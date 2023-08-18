package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.fluid.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;

public class UnlimitedVoid extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public int getRadius() {
        return 20;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.UNLIMITED_VOID.get());
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {
        super.onHitEntity(domain, owner, entity);

        if (!entity.hasEffect(JJKEffects.UNLIMITED_VOID.get())) {
            entity.addEffect(new MobEffectInstance(JJKEffects.UNLIMITED_VOID.get(), 30 * 20, 0, false, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30 * 20, 4));
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int radius = this.getRadius();
            List<Block> blocks = this.getBlocks();

            ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, blocks, radius,
                    cap.getGrade().getPower() + (cap.hasTrait(Trait.STRONGEST) ? 1.0F : 0.0F));
            owner.level.addFreshEntity(domain);

            cap.setDomain(domain);
        });
    }

    @Override
    public boolean bypass() {
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.LIMITLESS;
    }
}