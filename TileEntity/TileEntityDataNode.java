package Reika.ChromatiCraft.TileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;


public class TileEntityDataNode extends TileEntityChromaticBase {

	private double extension1;
	private double extension2;

	private static final double EXTENSION_SPEED = 0.03125;

	private static final double EXTENSION_LIMIT_1 = 2.5;
	private static final double EXTENSION_LIMIT_2 = 1.5;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.DATANODE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		EntityPlayer ep = world.getClosestPlayer(x+0.5, y+0.5, z+0.5, 24);
		if (ep != null) {
			if (extension1 < EXTENSION_LIMIT_1)
				extension1 = Math.min(extension1+EXTENSION_SPEED, EXTENSION_LIMIT_1);
			else
				extension2 = Math.min(extension2+EXTENSION_SPEED, EXTENSION_LIMIT_2);
		}
		else {
			if (extension2 == 0)
				extension1 = Math.max(extension1-EXTENSION_SPEED, 0);
			else
				extension2 = Math.max(extension2-EXTENSION_SPEED, 0);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public double getExtension1() {
		return extension1;
	}

	public double getExtension2() {
		return extension2;
	}

}
