/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

public abstract class BlockDyeTypes extends Block {

	private final IIcon[][] icons = new IIcon[16][6];

	public BlockDyeTypes(Material par2Material) {
		super(par2Material);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public final IIcon getIcon(int s, int meta) {
		return icons[meta][s%this.subIconCount(meta)];
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final void registerBlockIcons(IIconRegister ico) {
		String base = "chromaticraft:";
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < this.subIconCount(i); k++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				String folder = this.getIconFolder();
				String append = this.useNamedIcons() ? dye.colorNameNoSpaces.toLowerCase(Locale.ENGLISH) : "tile"+i+"_"+k;
				String path = folder != null && !folder.isEmpty() ? base+folder+append : base+append;
				icons[i][k] = ico.registerIcon(path);
			}
		}
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {

	}

	@Override
	public final ItemStack getPickBlock(MovingObjectPosition mov, World world, int x, int y, int z) {
		return new ItemStack(this, 1, world.getBlockMetadata(x, y, z));
	}

	protected abstract String getIconFolder();

	protected int subIconCount(int meta) {
		return 1;
	}

	protected boolean useNamedIcons() {
		return true;
	}

}
