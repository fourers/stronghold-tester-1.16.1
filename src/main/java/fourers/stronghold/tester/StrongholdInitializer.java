package fourers.stronghold.tester;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrongholdInitializer {
    public static final String MOD_ID = "stronghold-tester";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

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

        // Setup linked portal pair
        PortalLinker.setupStrongholdPortalPair(server);

        // Mark complete
        state.setGenerated();
    }
}
