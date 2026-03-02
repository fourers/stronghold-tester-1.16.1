package fourers.stronghold.tester.mixin;

import fourers.stronghold.tester.core.StrongholdSpawnHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(ServerGamePacketListenerImpl.class)
public class StrongholdPlayerTickMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerPlayer player = ((ServerGamePacketListenerImpl)(Object)this).player;
        StrongholdSpawnHandler.clientTick(player);
    }
}
