package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class HollowPurpleProjectile extends JujutsuProjectile {
    private static final int DELAY = 2 * 20;
    private static final float SPEED = 5.0F;
    private static final int DURATION = 5 * 20;
    private static final float DAMAGE = 30.0F;
    private static final float RADIUS = 1.0F;
    private static final int ANIMATION = 20;

    public HollowPurpleProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HollowPurpleProjectile(LivingEntity owner, float power) {
        super(JJKEntities.HOLLOW_PURPLE.get(), owner.level(), owner, power);

        Vec3 look = owner.getLookAngle();
        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(look.scale(this.getRadius() * 0.5F));
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
    }

    public float getRadius() {
        return RADIUS * this.getPower();
    }

    private void hurtEntities() {
        AABB bounds = this.getBoundingBox();

        if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : HelperMethods.getEntityCollisions(this.level(), bounds)) {
                if (!(entity instanceof LivingEntity living) || !owner.canAttack(living) || entity == owner) continue;
                entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.HOLLOW_PURPLE.get()),
                        DAMAGE * this.getPower());
            }
        }
    }

    private void breakBlocks() {
        for (int i = 0; i < SPEED; i++) {
            double radius = Math.max(Math.PI, this.getRadius());
            AABB bounds = this.getBoundingBox().inflate(radius);
            double centerX = bounds.getCenter().x();
            double centerY = bounds.getCenter().y();
            double centerZ = bounds.getCenter().z();

            for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
                for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                    for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = this.level().getBlockState(pos);

                        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                        if (distance <= radius) {
                            if (state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                                if (state.getFluidState().isEmpty()) {
                                    this.level().destroyBlock(pos, false);
                                } else {
                                    this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onInsideBlock(@NotNull BlockState pState) {
        if (this.getTime() >= DELAY && pState.getBlock().defaultDestroyTime() <= Block.INDESTRUCTIBLE) {
            this.discard();
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return EntityDimensions.fixed(this.getRadius(), this.getRadius());
    }

    private void renderBlue(Vec3 center) {
        float radius = (float) Math.max(Math.PI, this.getRadius()) * 0.5F;
        int count = (int) (radius * Math.PI * 2);

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.1F);
            double y = center.y() + yOffset * (radius * 0.1F);
            double z = center.z() + zOffset * (radius * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.DARK_BLUE_COLOR, radius * 0.2F, 0.2F, true, 5), true,
                    center.x(), center.y(), center.z(), 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.5F * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.5F * 0.1F);
            double y = center.y() + yOffset * (radius * 0.5F * 0.1F);
            double z = center.z() + zOffset * (radius * 0.5F * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_BLUE_COLOR, radius * 0.1F, 0.2F, true, 5), true,
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    private void renderRed(Vec3 center) {
        float radius = (float) Math.max(Math.PI, this.getRadius()) * 0.5F;
        int count = (int) (radius * Math.PI * 2);

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.1F);
            double y = center.y() + yOffset * (radius * 0.1F);
            double z = center.z() + zOffset * (radius * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.DARK_RED_COLOR, radius * 0.2F, 0.2F, true, 5), true,
                    center.x(), center.y(), center.z(), 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.5F * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.5F * 0.1F);
            double y = center.y() + yOffset * (radius * 0.5F * 0.1F);
            double z = center.z() + zOffset * (radius * 0.5F * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_RED_COLOR, radius * 0.1F, 0.2F, true, 5), true,
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    private void animate() {
        float size = this.getRadius() / Mth.PI;
        float fraction = (float) this.getTime() / ANIMATION;
        fraction = fraction < 0.5F ? 2 * fraction * fraction : fraction;
        float offset = Mth.lerp(fraction, size * 2, 0.0F);

        Entity owner = this.getOwner();

        if (owner == null) return;

        Vec3 look = owner.getLookAngle();
        float yaw = owner.getYRot();
        Vec3 right = new Vec3(-Math.sin(Math.toRadians(yaw)), 0.0D, Math.cos(Math.toRadians(yaw)));
        Vec3 pos = look.cross(right).normalize().scale(offset);

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        this.renderRed(center.add(pos));
        this.renderBlue(center.subtract(pos));
    }

    private void spawnParticles() {
        if (this.getTime() <= ANIMATION) {
            this.animate();
            return;
        }

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        float radius = (float) Math.max(Math.PI, this.getRadius());
        int count = (int) (radius * Math.PI * 2);

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.1F);
            double y = center.y() + yOffset * (radius * 0.1F);
            double z = center.z() + zOffset * (radius * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.DARK_PURPLE_COLOR, radius * 0.2F, 0.2F, true, 5), true,
                    center.x(), center.y(), center.z(), 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.5F * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.5F * 0.1F);
            double y = center.y() + yOffset * (radius * 0.5F * 0.1F);
            double z = center.z() + zOffset * (radius * 0.5F * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_PURPLE_COLOR, radius * 0.2F, 0.2F, true, 5), true,
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count / 2; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.5F * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.5F * 0.3F);
            double y = center.y() + yOffset * (radius * 0.5F * 0.3F);
            double z = center.z() + zOffset * (radius * 0.5F * 0.3F);

            this.level().addParticle(new LightningParticle.LightningParticleOptions(ParticleColors.LIGHT_PURPLE_COLOR, radius * 0.05F, 1), true,
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() >= DURATION) {
            this.discard();
        } else {
            if (this.getOwner() instanceof LivingEntity owner) {
                this.spawnParticles();

                if (this.getTime() < DELAY) {
                    if (!owner.isAlive()) {
                        this.discard();
                    } else {
                        if (this.getTime() % 5 == 0) {
                            owner.swing(InteractionHand.MAIN_HAND);
                        }
                        Vec3 look = owner.getLookAngle();
                        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                                .add(look.scale(this.getRadius() * 0.5F));
                        this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
                    }
                } else {
                    if (!this.level().isClientSide) {
                        this.hurtEntities();

                        if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                            this.breakBlocks();
                        }
                    }

                    if (this.getTime() == DELAY) {
                        this.setDeltaMovement(this.getLookAngle().scale(SPEED));
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);
                    }
                }
            }
        }
    }
}