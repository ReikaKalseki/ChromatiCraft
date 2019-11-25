/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Render.ISBRH.CrystallineStoneRenderer;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityPowerTree;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Interfaces.Block.ConnectedTextureGlass;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPylonStructure extends Block implements ConnectedTextureGlass {

	private final ArrayList<Integer> allDirs = new ArrayList();

	private final IIcon[][] icons = new IIcon[16][16];
	private final IIcon[] edges = new IIcon[10];

	private final int[] variants = ReikaArrayHelper.getArrayOf(1, 16);

	private static IIcon redstoneTorch;
	private static IIcon redstoneTorchOff;

	public static enum StoneTypes {
		SMOOTH(),
		BEAM(),
		COLUMN(),
		GLOWCOL(),
		GLOWBEAM(),
		FOCUS(),
		CORNER(),
		ENGRAVED(),
		EMBOSSED(),
		FOCUSFRAME(),
		GROOVE1(),
		GROOVE2(),
		BRICKS(),
		MULTICHROMIC(),
		STABILIZER(),
		RESORING();

		public static final StoneTypes[] list = values();

		public BlockKey getBlock() {
			return new BlockKey(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), this.ordinal());
		}

		public boolean needsSilkTouch() {
			return this == GLOWCOL || this == GLOWBEAM || this == FOCUS;
		}

		public boolean isBeam() {
			return this == BEAM || this == GLOWBEAM;
		}

		public boolean isColumn() {
			return this == COLUMN || this == GLOWCOL;
		}

		public boolean glows() {
			return this == GLOWCOL || this == GLOWBEAM || this == FOCUS || this == RESORING || this == STABILIZER || this == MULTICHROMIC;
		}

		public boolean isConnectedTexture() {
			return this == SMOOTH && ChromaOptions.CONNECTEDCRYSTALSTONE.getState();
		}

		public int getBrightRenderPass() {
			switch(this) {
				case RESORING:
				case GLOWCOL:
				case GLOWBEAM:
				case FOCUS:
					return 1;
				default:
					return 0;
			}
		}

		public StoneTypes getGlowingVariant() {
			switch(this) {
				case BEAM:
					return GLOWBEAM;
				case COLUMN:
					return GLOWCOL;
				case FOCUSFRAME:
					return FOCUS;
				default:
					return null;
			}
		}
	}

	public BlockPylonStructure(Material mat) {
		super(mat);
		this.setHardness(3);
		this.setResistance(12);
		this.setCreativeTab(ChromatiCraft.tabChroma);

		allDirs.add(5); //5 at beginning (bottom layer)
		for (int i = 1; i < 10; i++) {
			if (i != 5)
				allDirs.add(i);
		}

		variants[StoneTypes.BEAM.ordinal()] = 3;
		variants[StoneTypes.GLOWBEAM.ordinal()] = 3;

		variants[StoneTypes.CORNER.ordinal()] = 4;
		variants[StoneTypes.RESORING.ordinal()] = 2;

		for (int i = 0; i < StoneTypes.list.length; i++) {
			StoneTypes s = StoneTypes.list[i];
			if (s.glows()) {
				variants[i] *= 2;
			}
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		int idx = this.getIconIndex(s, meta);
		if (s < 2 && meta < 6)
			return icons[0][idx];
		return icons[meta][idx];
	}

	private int getIconIndex(int s, int meta) {
		return 0;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.crystalStoneRender;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		CrystallineStoneRenderer.renderPass = pass;
		return pass <= 1;
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		int meta = this.getWrappedMeta(s, iba.getBlockMetadata(x, y, z));
		int idx = this.getIconIndex(iba, x, y, z, s, meta);
		return idx >= 0 ? icons[meta][idx] : super.getIcon(iba, x, y, z, s);
	}

	private int getWrappedMeta(int s, int meta) {
		StoneTypes m = StoneTypes.list[meta];
		if (m.isBeam() && s <= 1) {
			return StoneTypes.BEAM.ordinal();
		}
		if (m.isColumn() && s <= 1) {
			return StoneTypes.COLUMN.ordinal();
		}
		return meta;
	}

	private int getIconIndex(IBlockAccess iba, int x, int y, int z, int s, int meta) {
		StoneTypes m = StoneTypes.list[meta];
		if (m == StoneTypes.RESORING) {
			if (s <= 1) {
				if (iba.getBlock(x+1, y, z) == this && iba.getBlockMetadata(x+1, y, z) == meta)
					return 0;
				if (iba.getBlock(x-1, y, z) == this && iba.getBlockMetadata(x-1, y, z) == meta)
					return 0;
				if (iba.getBlock(x, y, z+1) == this && iba.getBlockMetadata(x, y, z+1) == meta)
					return 1;
				if (iba.getBlock(x, y, z-1) == this && iba.getBlockMetadata(x, y, z-1) == meta)
					return 1;
			}
			if (iba.getBlock(x, y+1, z) == this && iba.getBlockMetadata(x, y+1, z) == meta) {
				return 1;
			}
			if (iba.getBlock(x, y-1, z) == this && iba.getBlockMetadata(x, y-1, z) == meta) {
				return 1;
			}
			boolean flag = true;
			boolean flag2 = false;
			boolean flag3 = false;
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				if (iba.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ) == this) {
					if (iba.getBlockMetadata(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ) == meta) {
						flag = false;
					}
					if (dir.offsetY == 0) {
						flag2 = true;
						if (dir.offsetZ == 0) {
							flag3 = true;
						}
					}
				}
			}
			if (flag && flag2) {
				if (s > 1 || flag3) {
					return 1;
				}
			}
		}
		if (m.isBeam() && s <= 1) {
			if (iba.getBlock(x+1, y, z) == this && this.getWrappedMeta(s, iba.getBlockMetadata(x+1, y, z)) == meta)
				return 2;
			if (iba.getBlock(x-1, y, z) == this && this.getWrappedMeta(s, iba.getBlockMetadata(x-1, y, z)) == meta)
				return 2;

			if (iba.getBlock(x, y, z+1) == this && this.getWrappedMeta(s, iba.getBlockMetadata(x, y, z+1)) == meta)
				return 1;
			if (iba.getBlock(x, y, z-1) == this && this.getWrappedMeta(s, iba.getBlockMetadata(x, y, z-1)) == meta)
				return 1;
		}
		if (m == StoneTypes.CORNER) {
			switch(s) {
				case 0:
				case 1:
					if (iba.getBlock(x+1, y, z) == this && iba.getBlock(x, y, z+1) == this)
						return 0;
					if (iba.getBlock(x-1, y, z) == this && iba.getBlock(x, y, z+1) == this)
						return 1;
					if (iba.getBlock(x+1, y, z) == this && iba.getBlock(x, y, z-1) == this)
						return 3;
					if (iba.getBlock(x-1, y, z) == this && iba.getBlock(x, y, z-1) == this)
						return 2;
					break;
				case 2:
					if (iba.getBlock(x, y-1, z) == this && iba.getBlock(x+1, y, z) == this)
						return 1;
					if (iba.getBlock(x, y-1, z) == this && iba.getBlock(x-1, y, z) == this)
						return 0;
					if (iba.getBlock(x, y+1, z) == this && iba.getBlock(x+1, y, z) == this)
						return 2;
					if (iba.getBlock(x, y+1, z) == this && iba.getBlock(x-1, y, z) == this)
						return 3;
					break;
				case 3:
					if (iba.getBlock(x, y-1, z) == this && iba.getBlock(x+1, y, z) == this)
						return 0;
					if (iba.getBlock(x, y-1, z) == this && iba.getBlock(x-1, y, z) == this)
						return 1;
					if (iba.getBlock(x, y+1, z) == this && iba.getBlock(x+1, y, z) == this)
						return 3;
					if (iba.getBlock(x, y+1, z) == this && iba.getBlock(x-1, y, z) == this)
						return 2;
					break;
				case 4:
					if (iba.getBlock(x, y-1, z) == this && iba.getBlock(x, y, z+1) == this)
						return 0;
					if (iba.getBlock(x, y-1, z) == this && iba.getBlock(x, y, z-1) == this)
						return 1;
					if (iba.getBlock(x, y+1, z) == this && iba.getBlock(x, y, z+1) == this)
						return 3;
					if (iba.getBlock(x, y+1, z) == this && iba.getBlock(x, y, z-1) == this)
						return 2;
					break;
				case 5:
					if (iba.getBlock(x, y-1, z) == this && iba.getBlock(x, y, z+1) == this)
						return 1;
					if (iba.getBlock(x, y-1, z) == this && iba.getBlock(x, y, z-1) == this)
						return 0;
					if (iba.getBlock(x, y+1, z) == this && iba.getBlock(x, y, z+1) == this)
						return 2;
					if (iba.getBlock(x, y+1, z) == this && iba.getBlock(x, y, z-1) == this)
						return 3;
					break;
			}
		}
		return -1;
	}

	public IIcon getBrightOverlay(IBlockAccess iba, int x, int y, int z, int s) {
		int meta = iba.getBlockMetadata(x, y, z);
		switch(StoneTypes.list[meta]) {
			case FOCUS:
			case GLOWBEAM:
			case GLOWCOL:
				if (s <= 1)
					return null;
			case MULTICHROMIC:
			case RESORING:
			case STABILIZER:
				return icons[meta][variants[meta]/2+Math.max(0, this.getIconIndex(iba, x, y, z, s, meta))];
			default:
				return null;
		}
	}

	public IIcon getBrightOverlay(int meta, int s) {
		switch(StoneTypes.list[meta]) {
			case FOCUS:
			case GLOWBEAM:
			case GLOWCOL:
				if (s <= 1)
					return null;
			case MULTICHROMIC:
			case RESORING:
			case STABILIZER:
				return icons[meta][variants[meta]/2+Math.max(0, this.getIconIndex(s, meta))];
			default:
				return null;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < variants[i]; k++) {
				String suff = k > 0 ? String.valueOf(i+"-"+(k+1)) : String.valueOf(i);
				if (StoneTypes.list[i].isColumn()) {
					if (k == 0 && ChromaIcons.loadXmasTextures()) {
						suff = suff+"_xm";
					}
				}
				icons[i][k] = ico.registerIcon("chromaticraft:pylon/block_"+suff);
			}
		}

		for (int i = 0; i < 10; i++) {
			edges[i] = ico.registerIcon("chromaticraft:pylon/connected/side_"+i);
		}

		redstoneTorch = ico.registerIcon("chromaticraft:crystaltorch_on");
		redstoneTorchOff = ico.registerIcon("chromaticraft:crystaltorch_off");
	}

	@Override
	public int damageDropped(int meta) {
		switch(StoneTypes.list[meta]) {
			case GLOWCOL:
				return StoneTypes.COLUMN.ordinal();
			case GLOWBEAM:
				return StoneTypes.BEAM.ordinal();
			case FOCUS:
				return StoneTypes.FOCUSFRAME.ordinal();
			default:
				return meta;
		}
	}

	@Override
	public boolean canSilkHarvest() {
		return true;
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion e) {
		int r = 12;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					TileEntity te = world.getTileEntity(dx, dy, dz);
					if (te instanceof TileEntityCrystalPylon) {
						((TileEntityCrystalPylon)te).invalidateMultiblock();
					}
					if (te instanceof TileEntityCrystalRepeater) {
						((TileEntityCrystalRepeater)te).validateStructure();
					}
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (ChromaIcons.loadXmasTextures() && StoneTypes.list[world.getBlockMetadata(x, y, z)].isColumn()) {
			double o = 0.0625;
			int d = 2+rand.nextInt(4);
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
			double px = 0.5;
			double pz = 0.5;
			if (dir.offsetX != 0) {
				px += dir.offsetX/2D+o*dir.offsetX;
				pz = rand.nextDouble()*(1+o*2)-o;
			}
			else {
				pz += dir.offsetZ/2D+o*dir.offsetZ;
				px = rand.nextDouble()*(1+o*2)-o;
			}
			EntityBlurFX fx = new EntityBlurFX(world, x+px, y+rand.nextDouble(), z+pz);
			int rgb = ReikaColorAPI.RGBtoHex(rand.nextBoolean() ? 255 : 0, rand.nextBoolean() ? 255 : 0, rand.nextBoolean() ? 255 : 0);
			fx.setColor(rgb).setIcon(ChromaIcons.CENTER).setAlphaFading().setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldB, int oldM) {
		this.triggerBreakCheck(world, x, y, z);

		super.breakBlock(world, x, y, z, oldB, oldM);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		int dy = y;
		TileEntity te = world.getTileEntity(x, dy, z);
		while (dy-y < 5 && !(te instanceof CrystalNetworkTile)) {
			dy++;
			te = world.getTileEntity(x, dy, z);
		}
		if (!(te instanceof TileEntityBase))
			return super.getPlayerRelativeBlockHardness(ep, world, x, y, z);
		if (te instanceof CrystalSource)
			return -1;
		return ((TileEntityBase)te).isPlacer(ep) ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z) : -1;
	}

	void triggerBreakCheck(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);

		blocks.extraSpread = true;

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			blocks.recursiveAddWithBounds(world, dx, dy, dz, this, x-16, y-32, z-16, x+16, y+32, z+16);
		}

		int mx = blocks.getMidX();
		int my = blocks.getMinY(); //intentionally bottom
		int mz = blocks.getMidZ();

		for (int h = 9; h < 16; h++) {
			TileEntity te = world.getTileEntity(mx, my+h, mz);
			if (te instanceof TileEntityCrystalPylon) {
				if (!ChromaStructures.PYLON.getArray(world, mx, my+h, mz, ((TileEntityCrystalPylon)te).getColor()).matchInWorld()) {
					((TileEntityCrystalPylon)te).invalidateMultiblock();
					break;
				}
			}
		}

		for (int i = 0; i <= 23; i++) {
			TileEntity te = world.getTileEntity(mx, my+i, mz);
			if (te instanceof MultiBlockChromaTile) {
				((MultiBlockChromaTile)te).validateStructure();
			}
		}

		for (int k = 0; k < 6; k++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[k];
			for (int i = 1; i <= 5; i++) {
				TileEntity te = world.getTileEntity(x+dir.offsetX*i, y+dir.offsetY*i, z+dir.offsetZ*i);
				if (te instanceof TileEntityCrystalRepeater) {
					((TileEntityCrystalRepeater)te).validateStructure();
				}
			}
		}

		TileEntity te = world.getTileEntity(blocks.getMidX()-1, blocks.getMaxY()+1, blocks.getMidZ());
		if (te instanceof TileEntityPowerTree) {
			((TileEntityPowerTree)te).validateStructure();
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		this.triggerAddCheck(world, x, y, z);

		super.onBlockAdded(world, x, y, z);
	}

	public static void triggerAddCheck(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);

		blocks.extraSpread = true;
		blocks.recursiveAddWithBounds(world, x, y, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), x-16, y-32, z-16, x+16, y+32, z+16);

		int mx = blocks.getMidX();
		int my = blocks.getMinY(); //intentionally bottom
		int mz = blocks.getMidZ();

		for (int h = 9; h < 16; h++) {
			TileEntity te = world.getTileEntity(mx, my+h, mz);
			if (te instanceof TileEntityCrystalPylon) {
				FilledBlockArray f = ChromaStructures.PYLON.getArray(world, mx, my+h, mz, ((TileEntityCrystalPylon)te).getColor());
				if (f.matchInWorld()) {
					((TileEntityCrystalPylon)te).validateMultiblock(f);
					break;
				}
			}
		}

		for (int i = 0; i <= 23; i++) {
			TileEntity te = world.getTileEntity(mx, my+i, mz);
			if (te instanceof MultiBlockChromaTile) {
				((MultiBlockChromaTile)te).validateStructure();
			}
		}

		for (int k = 0; k < 6; k++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[k];
			for (int i = 1; i <= 5; i++) {
				TileEntity te = world.getTileEntity(x+dir.offsetX*i, y+dir.offsetY*i, z+dir.offsetZ*i);
				if (te instanceof TileEntityCrystalRepeater) {
					((TileEntityCrystalRepeater)te).validateStructure();
				}
			}
		}

		TileEntity te = world.getTileEntity(blocks.getMidX()-1, blocks.getMaxY()+1, blocks.getMidZ());
		if (te instanceof TileEntityPowerTree) {
			((TileEntityPowerTree)te).validateStructure();
		}
	}

	public HashSet<Integer> getEdgesForFace(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		HashSet<Integer> li = new HashSet();
		li.addAll(allDirs);
		int meta = world.getBlockMetadata(x, y, z);

		if (face.offsetX != 0) { //test YZ
			//sides; removed if have adjacent on side
			if (this.connectTo(world, x, y, z+1, meta))
				li.remove(2);
			if (this.connectTo(world, x, y, z-1, meta))
				li.remove(8);
			if (this.connectTo(world, x, y+1, z, meta))
				li.remove(4);
			if (this.connectTo(world, x, y-1, z, meta))
				li.remove(6);

			//Corners; only removed if have adjacent on side AND corner
			if (this.connectTo(world, x, y+1, z+1, meta) && !li.contains(4) && !li.contains(2))
				li.remove(1);
			if (this.connectTo(world, x, y-1, z-1, meta) && !li.contains(6) && !li.contains(8))
				li.remove(9);
			if (this.connectTo(world, x, y+1, z-1, meta) && !li.contains(4) && !li.contains(8))
				li.remove(7);
			if (this.connectTo(world, x, y-1, z+1, meta) && !li.contains(2) && !li.contains(6))
				li.remove(3);
		}
		if (face.offsetY != 0) { //test XZ
			//sides; removed if have adjacent on side
			if (this.connectTo(world, x, y, z+1, meta))
				li.remove(2);
			if (this.connectTo(world, x, y, z-1, meta))
				li.remove(8);
			if (this.connectTo(world, x+1, y, z, meta))
				li.remove(4);
			if (this.connectTo(world, x-1, y, z, meta))
				li.remove(6);

			//Corners; only removed if have adjacent on side AND corner
			if (this.connectTo(world, x+1, y, z+1, meta) && !li.contains(4) && !li.contains(2))
				li.remove(1);
			if (this.connectTo(world, x-1, y, z-1, meta) && !li.contains(6) && !li.contains(8))
				li.remove(9);
			if (this.connectTo(world, x+1, y, z-1, meta) && !li.contains(4) && !li.contains(8))
				li.remove(7);
			if (this.connectTo(world, x-1, y, z+1, meta) && !li.contains(2) && !li.contains(6))
				li.remove(3);
		}
		if (face.offsetZ != 0) { //test XY
			//sides; removed if have adjacent on side
			if (this.connectTo(world, x, y+1, z, meta))
				li.remove(4);
			if (this.connectTo(world, x, y-1, z, meta))
				li.remove(6);
			if (this.connectTo(world, x+1, y, z, meta))
				li.remove(2);
			if (this.connectTo(world, x-1, y, z, meta))
				li.remove(8);

			//Corners; only removed if have adjacent on side AND corner
			if (this.connectTo(world, x+1, y+1, z, meta) && !li.contains(2) && !li.contains(4))
				li.remove(1);
			if (this.connectTo(world, x-1, y-1, z, meta) && !li.contains(8) && !li.contains(6))
				li.remove(9);
			if (this.connectTo(world, x+1, y-1, z, meta) && !li.contains(2) && !li.contains(6))
				li.remove(3);
			if (this.connectTo(world, x-1, y+1, z, meta) && !li.contains(4) && !li.contains(8))
				li.remove(7);
		}
		return li;
	}

	private boolean connectTo(IBlockAccess world, int x, int y, int z, int meta) {
		if (world.getBlock(x, y, z) == this && world.getBlockMetadata(x, y, z) == meta)
			return true;
		if (world.getBlock(x, y, z) == ChromaBlocks.RUNE.getBlockInstance())
			return true;
		return false;
	}

	public IIcon getIconForEdge(IBlockAccess world, int x, int y, int z, int edge) {
		return edges[edge];
	}

	public IIcon getIconForEdge(int itemMeta, int edge) {
		return edges[edge];
	}

	@Override
	public boolean renderCentralTextureForItem(int meta) {
		return true;
	}

	public static IIcon getIconOverride(Block b) {
		return b == Blocks.redstone_torch ? redstoneTorch : redstoneTorchOff;
	}

}
