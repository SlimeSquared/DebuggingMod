package com.slimesquared.debuggingmod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

@Mod("debuggingmod")
public class DebuggingMod {

    public static final String MOD_ID = "debuggingmod";
    public static SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "superb_channel"), () -> "1", "1"::equals, "1"::equals);

    public DebuggingMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::onChangeDimension);
    }

    public void setup(final FMLCommonSetupEvent e) {
        CHANNEL.messageBuilder(TriggerPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(TriggerPacket::encode)
                .decoder(TriggerPacket::new)
                .consumer(TriggerPacket::handle)
                .add();
    }

    public void onChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent e) {
        if(!e.getPlayer().level.isClientSide()) {
            var dim = e.getPlayer().level.dimension();
            CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dim), new TriggerPacket());
        }
    }

    public static class TriggerPacket {

        public TriggerPacket() {}

        public TriggerPacket(FriendlyByteBuf buffer) {}

        public void encode(FriendlyByteBuf buffer) {}

        public boolean handle(Supplier<NetworkEvent.Context> ctx) throws NullPointerException {
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
