/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockDimensionStructure;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityChromaCrafter;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Interfaces.Block.CustomSnowAccumulation;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Interfaces.Block.Submergeable;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class BlockStructureShield extends Block implements SemiUnbreakable, Submergeable, CustomSnowAccumulation {

	public static enum BlockType {
		CLOAK("Cloak"),
		STONE("Stone"),
		COBBLE("Cobble"),
		CRACK("Crack"),
		MOSS("Moss"),
		GLASS("Glass"),
		LIGHT("Light"),
		CRACKS("Cracks");

		public final String name;
		public final int metadata;

		public static final BlockType[] list = values();

		private BlockType(String s) {
			name = s;
			metadata = this.ordinal()+8;
		}

		public boolean isOpaque(ForgeDirection side) {
			return this == STONE && side == ForgeDirection.UP;
		}

		public boolean isTransparent(ForgeDirection side) {
			return this == CRACK || this == GLASS || this == CRACKS;
		}

		public boolean isTransparentToLight() {
			return this == GLASS;
		}

		public int getLightValue() {
			return this == LIGHT ? 15 : 0;
		}

		public boolean isMineable() {
			return this == CRACK || this == CRACKS;
		}
	}

	protected final IIcon[] icons = new IIcon[BlockType.list.length];
	public static IIcon upperConnectedIcon;
	public static IIcon centerConnectedIcon;
	public static IIcon lowerConnectedIcon;

	public BlockStructureShield(Material mat) {
		super(mat);
		this.setHardness(2);
		this.setResistance(6000);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		stepSound = soundTypeStone;
	}

	@Override
	public int getMobilityFlag() {
		return 0;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return BlockType.list[meta%8].getLightValue();
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return BlockType.list[meta%8].isTransparentToLight() ? 0 : 255;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean func_149730_j() { //prevents leaf and TCon berry overwrite
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta >= 8 && !BlockType.list[meta%8].isMineable() ? -1 : super.getBlockHardness(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if (!this.isSideSolid(world, x, y, z, dir)) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				Block b2 = world.getBlock(dx, dy, dz);
				if (b == b2 && this.canBreakLitBlockAt(world, dx, dy, dz, b2, world.getBlockMetadata(dx, dy, dz))) {
					if (b.getLightValue(world, dx, dy, dz) > 3) {
						ReikaSoundHelper.playBreakSound(world, dx, dy, dz, b2);
						ReikaWorldHelper.dropBlockAt(world, dx, dy, dz, null);
						world.setBlock(dx, dy, dz, Blocks.air);
						ReikaSoundHelper.playSoundAtBlock(world, dx, dy, dz, "random.fizz");
					}
				}
			}
		}
	}

	private boolean canBreakLitBlockAt(World world, int x, int y, int z, Block b, int meta) {
		if (ReikaBlockHelper.isLiquid(b))
			return false;
		if (b == ChromaBlocks.DOOR.getBlockInstance())
			return false;
		if (b instanceof BlockStructureShield || b instanceof BlockDimensionStructure || b instanceof BlockDimensionStructureTile)
			return false;
		if (ChromaBlocks.getEntryByID(b) != null && ChromaBlocks.getEntryByID(b).isDimensionStructureBlock())
			return false;
		if (b == ChromaBlocks.HEATLAMP.getBlockInstance())
			return false;
		if (b instanceof BlockFurnace)
			return false;
		if (ChromaTiles.getTileFromIDandMetadata(b, meta) == ChromaTiles.STRUCTCONTROL)
			return false;
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue() || (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())) {
			if (b == ChromaBlocks.CRYSTAL.getBlockInstance())
				return false;
			if (b == ChromaBlocks.CHUNKLOADER.getBlockInstance())
				return false;
			int mx = ChunkProviderChroma.getMonumentGenerator().getPosX();
			int mz = ChunkProviderChroma.getMonumentGenerator().getPosZ();
			if (ReikaMathLibrary.py3d(x-mx, 0, z-mz) < 100)
				return false;
		}
		return true;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta%8];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		int meta = iba.getBlockMetadata(x, y, z);
		if (meta%8 == BlockType.GLASS.ordinal() && s > 1) {
			if (ChromaTiles.getTile(iba, x, y-2, z) == ChromaTiles.CHROMACRAFTER && ((TileEntityChromaCrafter)iba.getTileEntity(x, y-2, z)).hasStructure()) {
				return ChromaIcons.TRANSPARENT.getIcon();//upperConnectedIcon;
			}
			else if (ChromaTiles.getTile(iba, x, y-1, z) == ChromaTiles.CHROMACRAFTER && ((TileEntityChromaCrafter)iba.getTileEntity(x, y-1, z)).hasStructure()) {
				return ChromaIcons.TRANSPARENT.getIcon();//centerConnectedIcon;
			}
		}
		return this.getIcon(s, meta);
	}

	@Override
	public int damageDropped(int meta) {
		return meta%8;
	}

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return super.getItemDropped(meta, r, fortune);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < BlockType.list.length; i++) {
			icons[i] = ico.registerIcon("chromaticraft:basic/shield_"+i);
		}
		upperConnectedIcon = ico.registerIcon("chromaticraft:basic/shield_5b");
		centerConnectedIcon = ico.registerIcon("chromaticraft:basic/shield_5c");
		lowerConnectedIcon = ico.registerIcon("chromaticraft:basic/shield_5a");
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (BlockType.list[meta%8].isOpaque(side))
			return true;
		if (BlockType.list[meta%8].isTransparent(side))
			return false;
		return meta < 8;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int dx, int dy, int dz, int s) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		Block b = world.getBlock(dx, dy, dz);
		int meta = world.getBlockMetadata(dx-dir.offsetX, dy-dir.offsetY, dz-dir.offsetZ);
		if (b.isOpaqueCube())
			return false;
		if (b == this && world.getBlockMetadata(dx, dy, dz) == meta)
			return false;
		if (ChromaTiles.getTile(world, dx, dy, dz) == ChromaTiles.CHROMACRAFTER)
			return false;
		switch(dir) {
			case EAST:
			case WEST:
			case SOUTH:
			case NORTH:
			case UP:
			case DOWN:
			default:
				return true;
		}
	}

	@Override
	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		return meta >= 8 && !BlockType.list[meta%8].isMineable();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		super.breakBlock(world, x, y, z, b, meta);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.getBlock(dx, dy, dz) instanceof BlockTNT) {
				world.getBlock(dx, dy, dz).onBlockDestroyedByPlayer(world, dx, dy, dz, 1);
				world.setBlockToAir(dx, dy, dz);
			}
		}
	}

	@Override
	public boolean isSubmergeable(IBlockAccess iba, int x, int y, int z) {
		return true;
	}

	@Override
	public final boolean renderLiquid(int meta) {
		return false;
	}

	@Override
	public boolean canSnowAccumulate(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (BlockType.list[meta%8] == BlockType.GLASS)
			return meta >= 8;
			return true;
	}

}
