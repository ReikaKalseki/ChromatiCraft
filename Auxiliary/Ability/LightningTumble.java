package Reika.ChromatiCraft.Auxiliary.Ability;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.EntityTumblingBlock;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion.TumbleCreator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public final class LightningTumble implements TumbleCreator {

	private final World world;
	private final int posX;
	private final int posY;
	private final int posZ;
	private final int radius;

	public LightningTumble(World world, int x, int y, int z, int r) {
		this.world = world;
		posX = x;
		posY = y;
		posZ = z;
		radius = r;
	}

	@Override
	public EntityTumblingBlock createBlock(World world, int x, int y, int z, Block b, int meta) {
		double dx = x-posX;
		double dz = z-posZ;
		double v = ReikaRandomHelper.getRandomPlusMinus(0D, 15D);
		return new EntityTumblingBlock(world, x, y, z, b, meta).setRotationSpeed(v*dx/radius, 0, v*dz/radius);
	}

}
