/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockStructureDataStorage.StructureInterfaceTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;


public class BlockSpecialShield extends BlockStructureShield {

	public static final IIcon[] edgeIcons = new IIcon[4];

	private static final int[] overlays = {0, 0, 2, 0, 0, 0, 0, 0};
	public static final IIcon[][] overlayIcons = new IIcon[16][ReikaArrayHelper.getMaxValue(overlays)];

	public BlockSpecialShield(Material mat) {
		super(mat);
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.specialShieldRender;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < BlockType.list.length; i++) {
			icons[i] = ico.registerIcon("chromaticraft:basic/specialshield_"+i);
			int n = overlays[i];
			for (int k = 0; k < n; k++) {
				overlayIcons[i][k] = ico.registerIcon("chromaticraft:basic/specialshield_"+i+ReikaStringParser.intToAlphaChar(k));
			}
		}

		for (int i = 0; i < 4; i++) {
			edgeIcons[i] = ico.registerIcon("chromaticraft:basic/side"+i);
		}
	}

	public static int getOverlayIndex(IBlockAccess iba, int x, int y, int z, int meta) {
		return overlays[meta] > 0 ? ((x+z)%overlays[meta]+overlays[meta])%overlays[meta] : -1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		int meta = iba.getBlockMetadata(x, y, z)%8;
		return icons[meta];
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int dx, int dy, int dz, int s) {
		return super.shouldSideBeRendered(iba, dx, dy, dz, s);// && iba.getBlock(dx, dy, dz) != this;
	}

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return Item.getItemFromBlock(ChromaBlocks.STRUCTSHIELD.getBlockInstance());
	}

	public boolean useNoLighting(IBlockAccess iba, int x, int y, int z) {
		int m = iba.getBlockMetadata(x, y, z)%8;
		return m <= 1 || m == 7;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		int meta = world.getBlockMetadata(x, y, z)%8;
		if (meta <= 1)
			return true;
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		super.onNeighborBlockChange(world, x, y, z, b);
		DimensionStructureGenerator gen = this.getStructure(world, x, y, z);
		if (gen != null) {
			gen.onBlockUpdate(world, x, y, z, b);
		}
	}

	public static DimensionStructureGenerator getStructure(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		int dx = x;
		int dy = y;
		int dz = z;

		Block b2 = world.getBlock(dx, dy, dz);
		int meta2 = world.getBlockMetadata(dx, dy, dz);
		while (b2 instanceof BlockStructureShield && matchMetas(meta, meta2)) {
			dy--;
			b2 = world.getBlock(dx, dy, dz);
			meta2 = world.getBlockMetadata(dx, dy, dz);
		}
		dy++;
		b2 = world.getBlock(dx, dy, dz);
		meta2 = world.getBlockMetadata(dx, dy, dz);
		while (b2 instanceof BlockStructureShield && matchMetas(meta, meta2)) {
			dx--;
			b2 = world.getBlock(dx, dy, dz);
			meta2 = world.getBlockMetadata(dx, dy, dz);
		}
		dx++;
		b2 = world.getBlock(dx, dy, dz);
		meta2 = world.getBlockMetadata(dx, dy, dz);
		while (b2 instanceof BlockStructureShield && matchMetas(meta, meta2)) {
			dz--;
			b2 = world.getBlock(dx, dy, dz);
			meta2 = world.getBlockMetadata(dx, dy, dz);
		}
		dz++;

		if (world.getBlock(dx, dy-1, dz) == ChromaBlocks.DIMDATA.getBlockInstance()) {
			TileEntity te = world.getTileEntity(dx, dy-1, dz);
			if (te instanceof StructureInterfaceTile) {
				return ((StructureInterfaceTile)te).getStructure();
			}
		}

		return null;
	}

	private static boolean matchMetas(int meta, int meta2) {
		if (true)
			return true;
		if (meta <= 1)
			return true;
		return meta == meta2;
	}

}
