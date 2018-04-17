package Reika.ChromatiCraft.TileEntity.AOE;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;


public class TileEntityDeathFogEmitter extends TileEntityChromaticBase {

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.DEATHFOG;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
