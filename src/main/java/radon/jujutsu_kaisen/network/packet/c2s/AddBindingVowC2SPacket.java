package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;

import java.util.function.Supplier;

public class AddBindingVowC2SPacket {
    private final BindingVow vow;

    public AddBindingVowC2SPacket(BindingVow vow) {
        this.vow = vow;
    }

    public AddBindingVowC2SPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(BindingVow.class));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.vow);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!cap.isCooldownDone(this.vow)) return;

            cap.addBindingVow(this.vow);
            cap.addBindingVowCooldown(this.vow);
        });
        ctx.setPacketHandled(true);
    }
}