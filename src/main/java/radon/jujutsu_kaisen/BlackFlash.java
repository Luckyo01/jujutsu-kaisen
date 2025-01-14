package radon.jujutsu_kaisen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BlackFlash {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        private static final float MAX_DAMAGE = 50.0F;

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();
            LivingEntity target = event.getEntity();

            if (target.level().isClientSide) return;

            if (source.getEntity() instanceof LivingEntity owner &&
                    !source.isIndirect() &&
                    (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK))) {
                if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

                ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                if (!cap.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get()) && !cap.hasToggled(JJKAbilities.BLUE_FISTS.get()) ||
                        (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu && jujutsu.getAbility() == JJKAbilities.DIVERGENT_FIST.get()))
                    return;

                long lastBlackFlashTime = cap.getLastBlackFlashTime();
                int seconds = (int) (owner.level().getGameTime() - lastBlackFlashTime) / 20;

                if (lastBlackFlashTime == 0 || seconds > 1) {
                    int rng = 150 - (HelperMethods.getGrade(cap.getExperience()).ordinal() * 5);

                    if (HelperMethods.RANDOM.nextInt(rng / (cap.isInZone(owner) ? 2 : 1)) != 0) {
                        return;
                    }
                } else {
                    return;
                }
                cap.onBlackFlash(owner);

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }

                event.setAmount(Math.min(MAX_DAMAGE, (float) Math.pow(event.getAmount(), 2.5D)));

                target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.MASTER, 2.0F, 0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F);
                target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.MASTER, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 8; j++) {
                        double d0 = HelperMethods.RANDOM.nextFloat() * 2.0F - 1.0F;
                        double d1 = HelperMethods.RANDOM.nextFloat() * 2.0F - 1.0F;
                        double d2 = HelperMethods.RANDOM.nextFloat() * 2.0F - 1.0F;

                        if (!(d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)) {
                            double d3 = target.getX(d0 / 4.0D);
                            double d4 = target.getY(0.5D + d1 / 4.0D);
                            double d5 = target.getZ(d2 / 4.0D);
                            ((ServerLevel) target.level()).sendParticles(JJKParticles.BLACK_FLASH.get(), d3, d4, d5,
                                    0, d0, d1 + 0.2D, d2, 1.0D);
                        }
                    }
                }
            }
        }
    }
}
