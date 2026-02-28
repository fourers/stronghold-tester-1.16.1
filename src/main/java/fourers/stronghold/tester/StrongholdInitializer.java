package fourers.stronghold.tester;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class StrongholdInitializer {

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

        // 1️⃣ Locate Stronghold
        BlockPos stronghold = overworld.findNearestMapFeature(
                StructureFeature.STRONGHOLD,
                new BlockPos(0, 64, 0),
                100,
                false
        );

        if (stronghold == null) return;

        BlockPos spawnPos = stronghold.offset(2, 1, 2);

        // 2️⃣ Set world spawn
        overworld.setDefaultSpawnPos(spawnPos);

        // 3️⃣ Setup linked portal pair
        PortalLinker.setupStrongholdPortalPair(server);

        // 4️⃣ Mark complete
        state.setGenerated();
    }
}
