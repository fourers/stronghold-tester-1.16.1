package fourers.stronghold.tester;

import java.io.File;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
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
            setupInventory(player);
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

        LOGGER.info("Teleporting player to {}", safePos.toShortString());
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

    private static void setupInventory(ServerPlayer player) {
        ItemStack fireResistPotion = PotionUtils.setPotion(
            new ItemStack(Items.POTION),
            Potions.FIRE_RESISTANCE
        );

        player.inventory.setItem(0, new ItemStack(Items.IRON_PICKAXE)); // hotbar slot 1
        player.inventory.setItem(1, new ItemStack(Items.IRON_AXE)); // hotbar slot 2
        player.inventory.setItem(2, new ItemStack(Items.LAVA_BUCKET)); // hotbar slot 3
        player.inventory.setItem(3, new ItemStack(Items.CRYING_OBSIDIAN, 20)); // hotbar slot 4
        player.inventory.setItem(4, new ItemStack(Items.BLACKSTONE, 64)); // hotbar slot 5
        player.inventory.setItem(5, new ItemStack(Items.ENDER_EYE, 12)); // hotbar slot 6
        player.inventory.setItem(6, new ItemStack(Items.ENDER_PEARL, 12)); // hotbar slot 7
        player.inventory.setItem(7, new ItemStack(Items.COOKED_BEEF, 12)); // hotbar slot 8
        player.inventory.setItem(8, new ItemStack(Items.TORCH, 64)); // hotbar slot 9

        player.inventory.setItem(9, new ItemStack(Items.ARROW, 48)); // main inventory slot 1
        player.inventory.setItem(10, new ItemStack(Items.BOW)); // main inventory slot 2
        player.inventory.setItem(11, fireResistPotion); // main inventory slot 3
        player.inventory.setItem(12, new ItemStack(Items.OAK_PLANKS, 64)); // main inventory slot 4
        player.inventory.setItem(13, new ItemStack(Items.WHITE_WOOL, 18)); // main inventory slot 5
        player.inventory.setItem(14, new ItemStack(Items.GLOWSTONE, 6)); // main inventory slot 6
        player.inventory.setItem(15, new ItemStack(Items.DIRT, 64)); // main inventory slot 7

        player.inventory.setItem(36, new ItemStack(Items.IRON_BOOTS)); // boots
        player.inventory.setItem(37, new ItemStack(Items.IRON_LEGGINGS)); // bottom
        player.inventory.setItem(38, new ItemStack(Items.IRON_CHESTPLATE)); // top
        player.inventory.setItem(39, new ItemStack(Items.IRON_HELMET)); // hat
        player.inventory.setItem(40, new ItemStack(Items.SHIELD)); // offhand
    }
}
