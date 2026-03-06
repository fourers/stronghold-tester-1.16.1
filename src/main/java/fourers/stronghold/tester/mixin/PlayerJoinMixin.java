package fourers.stronghold.tester.mixin;

import fourers.stronghold.tester.core.StrongholdSpawnHandler;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerJoinMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void onPlayerJoin(Connection connection, ServerPlayer player, CallbackInfo ci) {
        // fires when a player joins
        StrongholdSpawnHandler.handleNewPlayer(player);
    }
}
