package fourers.stronghold.tester;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class PortalLinker {

    public static void setupStrongholdPortalPair(MinecraftServer server) {

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        ServerLevel nether = server.getLevel(Level.NETHER);

        if (overworld == null || nether == null) return;

        // 1️⃣ Locate Stronghold
        BlockPos stronghold = overworld.findNearestMapFeature(
                StructureFeature.STRONGHOLD,
                new BlockPos(0, 64, 0),
                100,
                false
        );

        if (stronghold == null) return;

        // Choose a position slightly offset inside stronghold
        BlockPos overworldTarget = stronghold.offset(2, 0, 2);

        // 2️⃣ Calculate Nether coords (÷8 rule reversed)
        BlockPos netherPos = new BlockPos(
                overworldTarget.getX() / 8,
                64,
                overworldTarget.getZ() / 8
        );

        // 3️⃣ Force-load chunks
        forceLoad(overworld, overworldTarget);
        forceLoad(nether, netherPos);

        // 4️⃣ Clear safe area in Nether
        clearArea(nether, netherPos);

        // 5️⃣ Build Nether portal frame
        buildPortalFrame(nether, netherPos);

        // 6️⃣ Light portal (this is what triggers proper linking)
        NetherPortalBlock.trySpawnPortal(nether, netherPos);
    }

    private static void forceLoad(ServerLevel world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        world.getChunkSource().addRegionTicket(
                TicketType.START,
                chunkPos,
                1,
                Unit.INSTANCE
        );
    }

    private static void clearArea(ServerLevel world, BlockPos center) {

        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos p = center.offset(x, y, z);
                    world.removeBlock(p, false);
                }
            }
        }
    }

    private static void buildPortalFrame(ServerLevel world, BlockPos base) {

        // Standard 4x5 vertical portal frame (X-axis facing)

        for (int y = 0; y < 5; y++) {
            world.setBlockAndUpdate(base.offset(0, y, 0), Blocks.OBSIDIAN.defaultBlockState());
            world.setBlockAndUpdate(base.offset(3, y, 0), Blocks.OBSIDIAN.defaultBlockState());
        }

        for (int x = 0; x < 4; x++) {
            world.setBlockAndUpdate(base.offset(x, 0, 0), Blocks.OBSIDIAN.defaultBlockState());
            world.setBlockAndUpdate(base.offset(x, 4, 0), Blocks.OBSIDIAN.defaultBlockState());
        }
    }
}
