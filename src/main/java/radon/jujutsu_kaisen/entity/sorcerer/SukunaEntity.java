package radon.jujutsu_kaisen.entity.sorcerer;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.JJKEntityDataSerializers;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SukunaEntity extends SorcererEntity {
    private static final EntityDataAccessor<Optional<ResourceLocation>> DATA_ENTITY = SynchedEntityData.defineId(SukunaEntity.class, JJKEntityDataSerializers.OPTIONAL_RESOURCE_LOCATION.get());
    private static final EntityDataAccessor<Optional<CompoundTag>> DATA_PLAYER = SynchedEntityData.defineId(SukunaEntity.class, JJKEntityDataSerializers.OPTIONAL_COMPOUND_TAG.get());

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    protected int fingers;
    private boolean vessel;

    @Nullable
    private GameType original;

    public SukunaEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SukunaEntity(LivingEntity owner, int fingers, boolean vessel) {
        super(JJKEntities.SUKUNA.get(), owner.level());

        this.setOwner(owner);

        this.fingers = fingers;
        this.vessel = vessel;

        ResourceLocation key = this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE).getKey(owner.getType());

        if (key != null) {
            this.entityData.set(DATA_ENTITY, Optional.of(key));
        }

        if (owner instanceof Player player) {
            CompoundTag nbt = new CompoundTag();
            NbtUtils.writeGameProfile(nbt, player.getGameProfile());
            this.entityData.set(DATA_PLAYER, Optional.of(nbt));
        }
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean targetsSorcerers() {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        ISorcererData cap = this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.hasTechnique(CursedTechnique.TEN_SHADOWS)) {
            for (Entity entity : cap.getSummons(((ServerLevel) this.level()))) {
                if (entity instanceof TenShadowsSummon) return;
            }

            Summon<?> mahoraga = JJKAbilities.MAHORAGA.get();

            if (!mahoraga.isTamed(this)) {
                AbilityHandler.trigger(this, mahoraga);
                return;
            }

            for (Ability ability : CursedTechnique.TEN_SHADOWS.getAbilities()) {
                if (!(ability instanceof Summon<?> summon) || summon.isTamed(this)) continue;

                AbilityHandler.trigger(this, ability);
            }
        }
    }

    public EntityType<?> getKey() {
        return this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE).get(this.entityData.get(DATA_ENTITY).orElseThrow());
    }

    public GameProfile getPlayer() {
        return NbtUtils.readGameProfile(this.entityData.get(DATA_PLAYER).orElseThrow());
    }

    public GameType getOriginal(ServerPlayer player) {
        return this.original == null ? player.server.getDefaultGameType() : this.original;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_ENTITY, Optional.empty());
        this.entityData.define(DATA_PLAYER, Optional.empty());
    }

    @Override
    public boolean is(@NotNull Entity pEntity) {
        return this == pEntity || pEntity == this.getOwner();
    }

    @Override
    public void tick() {
        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && this.vessel && owner instanceof Player && (owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();

            if (this.level().isClientSide) return;

            if (owner instanceof ServerPlayer player) {
                if (this.original == null) {
                    this.original = player.gameMode.getGameModeForPlayer();
                }
                player.setGameMode(GameType.SPECTATOR);
                player.setCamera(this);
            } else if (owner != null) {
                owner.discard();
            }
        }
    }

    @Override
    public float getExperience() {
        return this.fingers * (ConfigHolder.SERVER.maximumExperienceAmount.get().floatValue() / 20);
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return CursedTechnique.DISMANTLE_AND_CLEAVE;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public List<Ability> getUnlocked() {
        return List.of(JJKAbilities.SIMPLE_DOMAIN.get(), JJKAbilities.MALEVOLENT_SHRINE.get(), JJKAbilities.DOMAIN_AMPLIFICATION.get());
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.MALEVOLENT_SHRINE.get();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }

        pCompound.putInt("fingers", this.fingers);
        pCompound.putBoolean("vessel", this.vessel);

        if (this.original != null) {
            pCompound.putInt("original", this.original.ordinal());
        }

        this.entityData.get(DATA_ENTITY).ifPresent(entity ->
                pCompound.putString("entity", entity.toString()));
        this.entityData.get(DATA_PLAYER).ifPresent(player ->
                pCompound.put("player", player));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }

        this.fingers = pCompound.getInt("fingers");
        this.vessel = pCompound.getBoolean("vessel");

        if (pCompound.contains("original")) {
            this.original = GameType.values()[pCompound.getInt("original")];
        }

        if (pCompound.contains("entity")) {
            this.entityData.set(DATA_ENTITY, Optional.of(new ResourceLocation(pCompound.getString("entity"))));
        }
        if (pCompound.contains("player")) {
            this.entityData.set(DATA_PLAYER, Optional.of(pCompound.getCompound("player")));
        }
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(src -> {
                this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(dst -> {
                    dst.setTraits(src.getTraits());
                    dst.setAdditional(src.getTechnique());
                    dst.setTamed(src.getTamed());
                    dst.setDead(src.getDead());
                });
            });
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (this.getOwner() instanceof ServerPlayer player) {
            player.setGameMode(this.original == null ? player.server.getDefaultGameType() : this.original);
        }
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!(this instanceof HeianSukunaEntity)) {
                if (!cap.hasTrait(Trait.VESSEL)) {
                    HelperMethods.convertTo(this, new HeianSukunaEntity(this.level(), this.fingers), true, false);
                }
            }
            if (owner instanceof ServerPlayer player) {
                player.setGameMode(this.original == null ? player.server.getDefaultGameType() : this.original);
            }
            owner.kill();
        } else if (!(this instanceof HeianSukunaEntity)) {
            HelperMethods.convertTo(this, new HeianSukunaEntity(this.level(), this.fingers), true, false);
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        LivingEntity owner = (LivingEntity) this.level().getEntity(pPacket.getData());

        if (owner != null) {
            this.setOwner(owner);
        }
    }
}
