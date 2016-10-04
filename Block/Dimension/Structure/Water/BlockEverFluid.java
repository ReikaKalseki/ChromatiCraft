/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Water;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class BlockEverFluid extends BlockContainer {

	private int tickRate;

	public BlockEverFluid(Material mat) {
		super(mat);
		tickRate = 4;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setTickRandomly(true);
		this.setResistance(60000);
		this.disableStats();

		if (DragonAPICore.isReikasComputer())
			this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = Blocks.water.getIcon(0, 0);
	}

	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityEverFluid();
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (this.isSourceBlock(world, x, y, z)) {
			this.trySpread(world, x, y, z, rand);
		}
		else {
			TileEntityEverFluid te = (TileEntityEverFluid)world.getTileEntity(x, y, z);
			if (te != null && te.sourceLocation != null && this.isSourceBlock(world, te.sourceLocation)) {
				this.trySpread(world, x, y, z, rand);
			}
			else {
				world.setBlock(x, y, z, Blocks.air);
			}
		}
	}

	private void trySpread(World world, int x, int y, int z, Random rand) {
		if (this.flowIntoBlock(world, x, y, z, ForgeDirection.DOWN)) {
			//do not flow outwards
		}
		else if (world.getBlock(x, y-1, z) != this) {
			for (int i = 2; i < 6; i++) {
				this.flowIntoBlock(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[i]);
			}
		}
	}

	private boolean isSourceBlock(IBlockAccess world, Coordinate c) {
		return this.isSourceBlock(world, c.xCoord, c.yCoord, c.zCoord);
	}

	public boolean isSourceBlock(IBlockAccess world, int x, int y, int z) {
		return world.getBlock(x, y, z) == this && world.getBlockMetadata(x, y, z) == 0;
	}

	private boolean flowIntoBlock(World world, int x, int y, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		tickRate = 3;
		if (this.canFlowInto(world, dx, dy, dz)) {
			world.setBlock(dx, dy, dz, this, 1, 3);
			TileEntityEverFluid te = (TileEntityEverFluid)world.getTileEntity(dx, dy, dz);

			Coordinate c = this.isSourceBlock(world, x, y, z) ? new Coordinate(x, y, z) : ((TileEntityEverFluid)world.getTileEntity(x, y, z)).sourceLocation.copy();
			te.sourceLocation = c;
			return true;
		}
		return false;
	}

	private boolean canFlowInto(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y, z).isAir(world, x, y, z))
			return true;

		Block block = world.getBlock(x, y, z);
		if (block == this) {
			return false;
		}

		Material material = block.getMaterial();
		if (material.blocksMovement() || material == Material.water || material == Material.lava  || material == Material.portal) {
			return false;
		}

		return true;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.scheduleBlockUpdate(x, y, z, this, tickRate);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		world.scheduleBlockUpdate(x, y, z, this, tickRate);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return this.canFlowInto(world, x, y, z);
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3) {
		return null;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	@Override
	public int tickRate(World world) {
		return tickRate;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	public boolean canCollideCheck(int meta, boolean fullHit) {
		return fullHit && meta == 0;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return 10;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		if (block != this) {
			return !block.isOpaqueCube();
		}
		return block.getMaterial() == this.getMaterial() ? false : super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.everfluidRender;
	}

	public static void placeSource(World world, int x, int y, int z) {
		world.setBlock(x, y, z, ChromaBlocks.EVERFLUID.getBlockInstance());
		((TileEntityEverFluid)world.getTileEntity(x, y, z)).sourceLocation = new Coordinate(x, y, z);
	}

	public static class TileEntityEverFluid extends TileEntity {

		private Coordinate sourceLocation;

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (sourceLocation != null)
				sourceLocation.writeToNBT("loc", NBT);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
			sourceLocation = Coordinate.readFromNBT("loc", NBT);
		}

	}

}
