package fourers.stronghold.tester;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PortalForcer;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PortalLinker {
    public static final String MOD_ID = "stronghold-tester";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void setupStrongholdPortalPair(MinecraftServer server) {

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        ServerLevel nether = server.getLevel(Level.NETHER);

        if (overworld == null || nether == null) return;

        // Locate Stronghold
        BlockPos stronghold = overworld.findNearestMapFeature(
                StructureFeature.STRONGHOLD,
                new BlockPos(0, 64, 0),
                100,
                false
        );

        if (stronghold == null) return;

        // Force-load chunks
        forceLoad(overworld, stronghold);

        // Create portal frame
        PortalForcer portalForcer = overworld.getPortalForcer();
        ItemEntity dummy = new ItemEntity(overworld, stronghold.getX() + 0.5, stronghold.getY(), stronghold.getZ() + 0.5);

        LOGGER.info("Building nether portal at {}", stronghold.toShortString());
        portalForcer.createPortal(dummy);

        unforceLoad(overworld, stronghold);
    }

    private static void forceLoad(ServerLevel world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        world.getChunkSource().addRegionTicket(
                TicketType.FORCED,
                chunkPos,
                5,
                chunkPos
        );
    }

    private static void unforceLoad(ServerLevel world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        world.getChunkSource().removeRegionTicket(
                TicketType.FORCED,
                chunkPos,
                5,
                chunkPos
        );
    }
}
