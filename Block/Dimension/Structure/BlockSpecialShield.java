/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;


public class BlockSpecialShield extends BlockStructureShield {

	public static final IIcon[] edgeIcons = new IIcon[4];

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
		}

		for (int i = 0; i < 4; i++) {
			edgeIcons[i] = ico.registerIcon("chromaticraft:basic/side"+i);
		}
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int dx, int dy, int dz, int s) {
		return super.shouldSideBeRendered(iba, dx, dy, dz, s) && iba.getBlock(dx, dy, dz) != this;
	}

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return Item.getItemFromBlock(ChromaBlocks.STRUCTSHIELD.getBlockInstance());
	}

	public boolean useNoLighting(IBlockAccess iba, int x, int y, int z) {
		int m = iba.getBlockMetadata(x, y, z)%8;
		return m <= 1 || m == 7;
	}

}
