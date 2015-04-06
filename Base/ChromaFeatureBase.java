package Reika.ChromatiCraft.Base;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

public abstract class ChromaFeatureBase extends MapGenBase {

	@Override
	protected final void func_151538_a(World world, int local_chunkX, int local_chunkZ, int chunkX, int chunkZ, Block[] data) {
		this.generate(world, local_chunkX, local_chunkZ, chunkX, chunkZ, data);
	}

	protected abstract void generate(World world, int local_chunkX, int local_chunkZ, int chunkX, int chunkZ, Block[] data);

	protected int getIndex(int x, int y, int z) {
		return (x * 16 + z) * 256 + y;
	}



}
