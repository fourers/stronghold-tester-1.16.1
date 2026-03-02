package fourers.stronghold.tester.core;

import fourers.stronghold.tester.utils.CustomPortalForcer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class StrongholdInitialiser {
    public static void register() {

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            initialize(server);
        });
    }

    private static void initialize(MinecraftServer server) {

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        StrongholdWorldState state = overworld.getDataStorage()
            .computeIfAbsent(
                StrongholdWorldState::new,
                "stronghold_spawn_state"
            );

        if (state.isGenerated()) {
            return; // Already done
        }

        // Setup nether portal at stronghold
        BlockPos portal = createPortalAtStronghold(overworld);
        state.setPortal(portal);

        // Setup world spawn
        setDefaultSpawn(server);

        // Mark complete
        state.setGenerated();
    }

    private static BlockPos createPortalAtStronghold(ServerLevel level) {
        CustomPortalForcer portalForcer = new CustomPortalForcer(level);
        BlockPos stronghold = level.findNearestMapFeature(
                StructureFeature.STRONGHOLD,
                new BlockPos(0, 64, 0),
                100,
                false
        );
        return portalForcer.createPortal(stronghold);
    }

    private static void setDefaultSpawn(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        BlockPos stronghold = overworld.findNearestMapFeature(
            StructureFeature.STRONGHOLD,
            new BlockPos(0, 64, 0),
            100,
            false
        );
        overworld.setDefaultSpawnPos(stronghold);
    }
}
