package fourers.stronghold.tester.core;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class StrongholdWorldState extends SavedData {

    private static final String NAME = "stronghold_spawn_state";
    private boolean generated = false;
    private int[] portal = new int[0];

    public StrongholdWorldState() {
        super(NAME);
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated() {
        this.generated = true;
        setDirty();
    }

    public BlockPos getPortal() {
        if (portal.length != 0) {
            return new BlockPos(portal[0], portal[1], portal[2]);
        }
        return null;
    }

    public void setPortal(BlockPos pos) {
        this.portal = new int[]{pos.getX(), pos.getY(), pos.getZ()};
        setDirty();
    }

    @Override
    public void load(CompoundTag nbt) {
        generated = nbt.getBoolean("generated");
        portal = nbt.getIntArray("portal");
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putBoolean("generated", generated);
        compound.putIntArray("portal", portal);
        return compound;
    }
}
