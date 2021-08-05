/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Water;

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaISBRH;
import Reika.ChromatiCraft.World.Dimension.Structure.WaterPuzzleGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
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
		this.setBlockUnbreakable();

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
		TileEntityEverFluid te = (TileEntityEverFluid)world.getTileEntity(x, y, z);
		if (te != null && te.sourceLocation != null) {
			BlockArray b = new BlockArray();
			//b.recursiveMultiAddWithBounds(world, x, y, z, x-100, y-100, z-100, x+100, y+100, z+100, this, ChromaBlocks.WATERLOCK.getBlockInstance());
			b.recursiveAdd(world, x, y, z, this);
			if (!b.hasBlock(te.sourceLocation)) {
				world.setBlock(x, y, z, Blocks.air);
				if (world.getBlock(x, y-1, z) == ChromaBlocks.WATERLOCK.getBlockInstance() && world.getBlock(x, y-2, z) == this)
					world.setBlock(x, y-2, z, Blocks.air);
			}
			else if (this.isSourceBlock(world, x, y, z) || this.isSourceBlock(world, te.sourceLocation)) {
				this.trySpread(world, x, y, z, te, rand);
			}
			else if (te.sourceLocation.getBlock(world) != this) {
				world.setBlock(x, y, z, Blocks.air);
			}
		}
		else {
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	private void trySpread(World world, int x, int y, int z, TileEntityEverFluid te, Random rand) {
		if (this.flowIntoBlock(world, x, y, z, te, ForgeDirection.DOWN)) {
			//do not flow outwards
		}
		else if (world.getBlock(x, y-1, z) != this) {
			for (int i = 2; i < 6; i++) {
				this.flowIntoBlock(world, x, y, z, te, ForgeDirection.VALID_DIRECTIONS[i]);
			}
		}
	}

	private boolean isSourceBlock(IBlockAccess world, Coordinate c) {
		return this.isSourceBlock(world, c.xCoord, c.yCoord, c.zCoord);
	}

	public boolean isSourceBlock(IBlockAccess world, int x, int y, int z) {
		return world.getBlock(x, y, z) == this && world.getBlockMetadata(x, y, z) == 0;
	}

	private boolean flowIntoBlock(World world, int x, int y, int z, TileEntityEverFluid src, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		tickRate = 3;
		boolean placeSource = false;
		if (dy > 0 && y > 0 && dir == ForgeDirection.DOWN && world.getBlock(x, y-1, z) == ChromaBlocks.WATERLOCK.getBlockInstance()) {
			dy--;
			placeSource = true;
		}
		if (this.canFlowInto(world, dx, dy, dz, src)) {
			if (placeSource) {
				placeSource(world, dx, dy, dz);
			}
			else {
				world.setBlock(dx, dy, dz, this, 1, 3);
				this.onBlockAdded(world, dx, dy, dz);
			}
			TileEntityEverFluid te = (TileEntityEverFluid)world.getTileEntity(dx, dy, dz);
			if (!placeSource) {
				te.sourceLocation = src.sourceLocation;
			}

			te.uid = src.uid;
			te.level = placeSource ? src.level+1 : src.level;
			return true;
		}
		else if (this.isBlockingBlock(world, dx, dy, dz)) {
			return true;
		}
		return false;
	}

	private boolean isBlockingBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b instanceof BlockStructureShield ||b == ChromaBlocks.WATERLOCK.getBlockInstance())
			return false;
		return true;
	}

	private boolean canFlowInto(IBlockAccess world, int x, int y, int z, TileEntityEverFluid te) {
		if (y < 0)
			return false;

		/*
		if (te != null) {
			WaterPuzzleGenerator w = te.getGenerator();
			if (w != null) {
				WaterFloor f = w.getLevel(te.level);
				if (f != null) {
					if (f.isSlotOccluded(x, y, z)) {
						return false;
					}
				}
			}
		}
		 */

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
		if (!(world.getTileEntity(x, y, z) instanceof TileEntityEverFluid))
			world.setTileEntity(x, y, z, new TileEntityEverFluid());
		world.playSoundEffect(x, y, z, "liquid.water", world.rand.nextFloat() * 0.25F + 0.75F, world.rand.nextFloat() * 1.0F + 0.5F);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		world.scheduleBlockUpdate(x, y, z, this, tickRate);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return this.canFlowInto(world, x, y, z, null);
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
		return ChromaISBRH.everfluid.getRenderID();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		super.onBlockPlacedBy(world, x, y, z, e, is);
		placeSource(world, x, y, z);
	}

	public static void placeSource(World world, int x, int y, int z) {
		Block b = ChromaBlocks.EVERFLUID.getBlockInstance();
		if (world.getBlock(x, y, z) != b)
			world.setBlock(x, y, z, b);
		b.onBlockAdded(world, x, y, z);
		((TileEntityEverFluid)world.getTileEntity(x, y, z)).sourceLocation = new Coordinate(x, y, z);
	}

	public static class TileEntityEverFluid extends StructureBlockTile<WaterPuzzleGenerator> {

		private Coordinate sourceLocation;
		private int level;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void setData(UUID id, int lvl) {
			uid = id;
			level = lvl;
		}

		public int getLevel() {
			return level;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (sourceLocation != null)
				sourceLocation.writeToNBT("loc", NBT);

			NBT.setInteger("lvl", level);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
			sourceLocation = Coordinate.readFromNBT("loc", NBT);
			level = NBT.getInteger("lvl");
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.WATER;
		}

	}

}
