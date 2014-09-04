package Reika.ChromatiCraft.TileEntity;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;

public class TileEntityCrystalFence extends TileEntityChromaticBase {

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FENCE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
