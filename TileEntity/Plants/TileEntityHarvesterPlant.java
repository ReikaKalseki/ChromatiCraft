/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Plants;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMagicPlant;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class TileEntityHarvesterPlant extends TileEntityMagicPlant {

	private static double[][] randomDistrib = {
		{3, 2, 1, 1, 2, 3, 2, 1, 1, 2, 3},
		{1, 6, 4, 3, 4, 5, 4, 3, 4, 6, 2},
		{1, 4, 7, 5, 4, 6, 4, 5, 7, 4, 1},
		{1, 3, 5, 8, 6, 7, 6, 8, 5, 3, 1},
		{2, 4, 4, 6, 9, 0, 9, 6, 4, 4, 2},
		{3, 5, 6, 7, 0, 0, 0, 7, 6, 5, 3},
		{2, 4, 4, 6, 9, 0, 9, 6, 4, 4, 2},
		{1, 3, 5, 8, 6, 7, 6, 8, 5, 3, 1},
		{1, 4, 7, 5, 4, 6, 4, 5, 7, 4, 1},
		{2, 6, 4, 3, 3, 5, 3, 3, 4, 6, 2},
		{3, 2, 1, 1, 2, 3, 2, 1, 1, 2, 3},
	};

	private static double[] heightDistrib = {
		10, 8, 5, 2
	};

	private static final WeightedRandom<Coordinate> coordinateRand = WeightedRandom.fromArray(randomDistrib);
	private static final WeightedRandom<Integer> verticalRand = new WeightedRandom();

	private final HashSet<BlockKey> flowerCache = new HashSet();
	private final StepTimer cacheTimer = new StepTimer(20);

	static {
		for (int i = 0; i < heightDistrib.length; i++) {
			verticalRand.addEntry(-i, heightDistrib[i]);
		}
	}

	@Override
	public ForgeDirection getGrowthDirection() {
		return ForgeDirection.UP;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.HARVESTPLANT;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			cacheTimer.update();
			if (cacheTimer.checkCap())
				this.loadCache(world, x, y, z);
			if (this.isActive(world, x, y, z)) {
				int n = 1+this.getAccelerationPlants();
				for (int i = 0; i < n; i++) {
					int dy = verticalRand.getRandomEntry();
					Coordinate c = coordinateRand.getRandomEntry().offset(x, y+dy, z);
					Block b = c.getBlock(world);
					int bmeta = c.getBlockMetadata(world);
					if (c.getTaxicabDistanceTo(new Coordinate(this)) > 1 && this.canHarvest(b, bmeta, world, c.xCoord, c.yCoord, c.zCoord)) {
						this.harvest(world, c.xCoord, c.yCoord, c.zCoord, b, bmeta);
					}
				}
			}
		}
	}

	private boolean isActive(World world, int x, int y, int z) {
		return world.canBlockSeeTheSky(x, y+1, z) && world.isDaytime() && !world.isBlockIndirectlyGettingPowered(x, y-1, z);
	}

	private void harvest(World world, int x, int y, int z, Block b, int meta) {
		ItemStack in = new ItemStack(Items.shears);
		ArrayList<ItemStack> li = b.getDrops(world, x, y, z, meta, 0);
		if (b instanceof IShearable && ((IShearable)b).isShearable(in, world, x, y, z)) {
			li = ((IShearable)b).onSheared(in, world, x, y, z, 0);
		}
		ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, li);
		ReikaSoundHelper.playBreakSound(world, x, y, z, Blocks.grass);
		ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, x, y, z, Block.getIdFromBlock(b), meta);
		world.setBlock(x, y, z, Blocks.air);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.loadCache(world, x, y, z);
	}

	private void loadCache(World world, int x, int y, int z) {
		flowerCache.clear();
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			BlockKey bk = BlockKey.getAt(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			if (bk.blockID.getMaterial() == Material.plants) {
				flowerCache.add(bk);
			}
		}
	}

	private boolean canHarvest(Block b, int meta, World world, int x, int y, int z) {
		return flowerCache.contains(new BlockKey(b, meta)) && ReikaWorldHelper.checkForAdjBlock(world, x, y, z, b, meta) != null && ReikaWorldHelper.checkForAdjBlock(world, x, y, z, this.getTile().getBlock(), this.getTile().getBlockMetadata()) == null;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean isPlantable(World world, int x, int y, int z) {
		return ReikaPlantHelper.FLOWER.canPlantAt(world, x, y, z) || ReikaBlockHelper.isLeaf(world, x, y-1, z) || ChromaTiles.getTile(world, x, y-1, z) == ChromaTiles.PLANTACCEL;
	}

}
