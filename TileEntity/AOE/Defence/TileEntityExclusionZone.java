package Reika.ChromatiCraft.TileEntity.AOE.Defence;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.Perimeter;


public class TileEntityExclusionZone extends TileEntityChromaticBase {

	private final Perimeter boundary = new Perimeter().disallowVertical();

	@Override
	public ChromaTiles getTile() {
		return null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
