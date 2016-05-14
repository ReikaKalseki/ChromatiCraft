/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
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
		int meta = iba.getBlockMetadata(x, y, z);
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

}
