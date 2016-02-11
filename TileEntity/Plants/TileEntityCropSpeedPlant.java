package Reika.ChromatiCraft.TileEntity.Plants;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMagicPlant;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent.UpdateFlags;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaCropHelper;
import Reika.DragonAPI.ModRegistry.ModCropList;


public class TileEntityCropSpeedPlant extends TileEntityMagicPlant {

	private static double[][] randomDistrib = {
		{0, 0, 4, 0, 0},
		{0, 0, 8, 0, 0},
		{4, 8, 0, 8, 4},
		{0, 0, 8, 0, 0},
		{0, 0, 4, 0, 0},
	};

	private static final WeightedRandom<Coordinate> coordinateRand = new WeightedRandom();

	static {
		for (int i = 0; i < randomDistrib.length; i++) {
			for (int k = 0; k < randomDistrib[i].length; k++) {
				Coordinate c = new Coordinate(i-2, 0, k-2);
				if (randomDistrib[i][k] > 0) {
					coordinateRand.addEntry(c, randomDistrib[i][k]);
				}
			}
		}
	}

	@Override
	public ForgeDirection getGrowthDirection() {
		return ForgeDirection.UP;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CROPSPEED;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote)
			return;
		double n = 0.1;
		n *= Math.min(4, 1+this.getAccelerationPlants());
		while (n >= 1) {
			this.growCrop(world, x, y, z);
			n -= 1;
		}
		if (ReikaRandomHelper.doWithChance(n))
			this.growCrop(world, x, y, z);
	}

	private void growCrop(World world, int x, int y, int z) {
		Coordinate c = coordinateRand.getRandomEntry().offset(x, y, z);
		Block b = c.getBlock(world);
		int meta = c.getBlockMetadata(world);
		boolean flag = b instanceof BlockSapling || b == ChromaBlocks.DECOFLOWER.getBlockInstance() || b instanceof BlockReed || b instanceof BlockCactus || b == Blocks.vine;
		flag |= ReikaCropHelper.getCrop(b) != null;
		flag |= ModCropList.getModCrop(b, meta) != null;
		if (flag) {
			c.updateTick(world);
			BlockTickEvent.fire(world, x, y, z, b, UpdateFlags.FORCED.flag);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
