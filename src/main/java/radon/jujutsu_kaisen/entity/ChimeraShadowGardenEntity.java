package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.DomainBlock;
import radon.jujutsu_kaisen.block.DomainBlockEntity;
import radon.jujutsu_kaisen.block.fluid.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public class ChimeraShadowGardenEntity extends OpenDomainExpansionEntity {
    public ChimeraShadowGardenEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public AABB getBounds() {
        int width = this.getWidth();
        int height = this.getHeight();
        return new AABB(this.getX() - width, this.getY(), this.getZ() - width,
                this.getX() + width, this.getY() - height, this.getZ() + width);
    }

    @Override
    public boolean isInsideBarrier(BlockPos pos) {
        BlockPos center = this.blockPosition().below();

        int width = this.getWidth();
        int height = this.getHeight();

        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < width * height;
    }

    public ChimeraShadowGardenEntity(LivingEntity owner, DomainExpansion ability, int width, int height, float strength) {
        super(JJKEntities.CHIMERA_SHADOW_GARDEN.get(), owner, ability, width, height, strength);
    }

    private void createBarrier(Entity owner) {
        BlockPos center = this.blockPosition().below();

        int width = this.getWidth();
        int height = this.getHeight();

        for (int i = 0; i < width / 3; i++) {
            for (int j = 0; j < height; j++) {
                int delay = i;

                int horizontal = i;
                int vertical = j;

                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    cap.delayTickEvent(() -> {
                        for (int x = -horizontal; x <= horizontal; x++) {
                            for (int z = -horizontal; z <= horizontal; z++) {
                                double distance = Math.sqrt(x * x + -vertical * -vertical + z * z);

                                if (distance < horizontal && distance >= horizontal - 1) {
                                    BlockPos pos = center.offset(x, -vertical, z);

                                    BlockState state = this.level.getBlockState(pos);

                                    if (!this.isRemoved()) {
                                        BlockState original = null;

                                        if (this.level.getBlockEntity(pos) instanceof DomainBlockEntity be) {
                                            original = be.getOriginal();
                                        } else if (state.getBlock() instanceof DomainBlock) {
                                            return;
                                        }

                                        Block block = JJKBlocks.CHIMERA_SHADOW_GARDEN.get();
                                        owner.level.setBlock(pos, block.defaultBlockState(),
                                                Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);

                                        if (this.level.getBlockEntity(pos) instanceof DomainBlockEntity be) {
                                            be.create(this.uuid, this.getId(), original == null ? state : original);
                                        }
                                    }
                                }
                            }
                        }
                    }, delay);
                });
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return this.isAlive();
    }

    @Override
    protected void doPush(@NotNull Entity p_20971_) {

    }

    @Override
    public void tick() {
        this.refreshDimensions();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            if (!this.level.isClientSide) {
                if (this.getTime() == 0) {
                    this.createBarrier(owner);
                }
            }
        }
        super.tick();
    }
}