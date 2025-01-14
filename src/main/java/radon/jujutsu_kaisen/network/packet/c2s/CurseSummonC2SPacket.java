package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.function.Supplier;

public class CurseSummonC2SPacket {
    private final ResourceLocation key;
    private final int count;

    public CurseSummonC2SPacket(ResourceLocation key, int count) {
        this.key = key;
        this.count = count;
    }

    public CurseSummonC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
        buf.writeInt(this.count);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            Registry<EntityType<?>> registry = sender.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            EntityType<?> type = registry.get(this.key);

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (type != null) {
                JJKAbilities.summonCurse(sender, type, Math.min(cap.getCurseCount(registry, type), this.count));
            }
        });
        ctx.setPacketHandled(true);
    }
}