package Reika.ChromatiCraft.TileEntity.Plants;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class TileEntityCrystalFlower extends TileEntityChromaticBase {

	private int timer;
	private final CrystalElement color = CrystalElement.randomElement();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CRYSTALFLOWER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
