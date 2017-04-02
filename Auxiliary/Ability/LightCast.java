package Reika.ChromatiCraft.Auxiliary.Ability;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;

public class LightCast implements BreakerCallback {

	private final EntityPlayer player;

	public LightCast(EntityPlayer ep) {
		player = ep;
	}

	@Override
	public boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		return id.isAir(world, x, y, z) && !world.canBlockSeeTheSky(x, y+1, z) && world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) == 0;
	}

	@Override
	public void onPreBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {

	}

	@Override
	public void onPostBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		if (world.getBlockLightValue(x, y, z) < 2) {
			world.setBlock(x, y, z, ChromaBlocks.LIGHT.getBlockInstance(), 0, 3);
			world.updateLightByType(EnumSkyBlock.Block, x, y, z);
		}
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {

	}

}
