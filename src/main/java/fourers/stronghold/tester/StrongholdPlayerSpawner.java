package fourers.stronghold.tester;

import java.io.File;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrongholdPlayerSpawner {
    public static final String MOD_ID = "stronghold-tester";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void onPlayerJoin(ServerPlayer player) {
        MinecraftServer server = player.server;
        if (isNewPlayer(server, player)) {
            LOGGER.info("New player detected: {}", player.getName());
            movePlayer(server, player);
        }
    }

    private static void movePlayer(MinecraftServer server, ServerPlayer player) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        BlockPos stronghold = overworld.findNearestMapFeature(
            StructureFeature.STRONGHOLD,
            new BlockPos(0, 64, 0),
            100,
            false
        );

        if (stronghold == null) return;

        BlockPos safePos = findSafePos(overworld, stronghold);
        if (safePos == null) {
            LOGGER.warn("No safe position found near stronghold!");
            return;
        }

        player.teleportTo(
            overworld,
            safePos.getX() + 0.5,
            safePos.getY(),
            safePos.getZ() + 0.5,
            player.yRot,
            player.xRot
        );
    }

    private static boolean isNewPlayer(MinecraftServer server, ServerPlayer player) {
        File playerDataDir = new File(
            server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile(),
            player.getStringUUID() + ".dat"
        );
        return !playerDataDir.exists();
    }

    private static BlockPos findSafePos(ServerLevel world, BlockPos target) {
        for (int r = 0; r <= 16; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    for (int dy = -r; dy <= r; dy++) {
                        BlockPos candidate = target.offset(dx, dy, dz);
                        if (candidate.getY() < 1 || candidate.getY() > 255) continue;
                        if (isSafe(world, candidate)) return candidate;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isSafe(ServerLevel world, BlockPos pos) {
        return world.getBlockState(pos.below()).isSolidRender(world, pos.below())
            && world.getBlockState(pos).isAir()
            && world.getBlockState(pos.above()).isAir();
    }
}
