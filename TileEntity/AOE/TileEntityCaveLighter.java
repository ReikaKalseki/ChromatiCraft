package Reika.ChromatiCraft.TileEntity.AOE;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockEtherealLight.Flags;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockSpiral;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityCaveLighter extends TileEntityChromaticBase {

	public static final int RANGE = 128;
	private static final int ZONE_SIZE = 8;

	private final BlockSpiral[] spiral = new BlockSpiral[128/ZONE_SIZE];

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LIGHTER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}
		else {
			this.placeLights(world, x, y, z);
		}
	}

	private void placeLights(World world, int x, int y, int z) {
		boolean flag = false;
		for (int m = 0; m < 16; m++) {
			for (int i = 0; i < spiral.length; i++) {
				if (spiral[i].getSize() > 0) {
					Coordinate c = spiral[i].getNextAndMoveOn();
					int cx = c.xCoord+rand.nextInt(ZONE_SIZE);
					int cy = 4+c.yCoord+rand.nextInt(ZONE_SIZE);
					int cz = c.zCoord+rand.nextInt(ZONE_SIZE);
					if (this.placeBlockAt(world, cx, cy, cz)) {
						world.setBlock(cx, cy, cz, ChromaBlocks.LIGHT.getBlockInstance(), Flags.PARTICLES.getFlag(), 3);
						world.markBlockForUpdate(cx, cy, cz);
						//ReikaJavaLibrary.pConsole("Lighting "+new Coordinate(cx, cy, cz));
					}
					//else {
					//	Coordinate cp = new Coordinate(cx, cy, cz);
					//	ReikaJavaLibrary.pConsole("Not lighting "+cp+", "+cp.getBlock(world).getLocalizedName());
					//}
					flag = true;
					break;
				}
			}
		}
		if (!flag) {
			//this.initSpirals(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {

	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.initSpirals(world, x, y, z);
	}

	private void initSpirals(World world, int x, int y, int z) {
		for (int i = 0; i < spiral.length; i++) {
			spiral[i] = new BlockSpiral(x, i*ZONE_SIZE, z, RANGE/ZONE_SIZE).setRightHanded().setGridSize(ZONE_SIZE).calculate();
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private boolean placeBlockAt(World world, int x, int y, int z) {
		return !world.canBlockSeeTheSky(x, y+1, z) && world.getBlockLightValue(x, y, z) <= 7 && world.getBlock(x, y, z).isAir(world, x, y, z);
	}

}
