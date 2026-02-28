package fourers.stronghold.tester;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class StrongholdWorldState extends SavedData {

    private static final String NAME = "stronghold_spawn_state";
    private boolean generated = false;

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

    @Override
    public void load(CompoundTag nbt) {
        generated = nbt.getBoolean("generated");
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putBoolean("generated", generated);
        return compound;
    }
}
