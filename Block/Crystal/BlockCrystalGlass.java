/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Crystal;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class BlockCrystalGlass extends CrystalTypeBlock {

	private static IIcon overlay;

	public BlockCrystalGlass(Material mat) {
		super(mat);
		this.setHardness(0.125F);
		this.setResistance(2);
		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
	}

	@Override
	public int getBrightness(IBlockAccess iba, int x, int y, int z) {
		return 12;
	}

	@Override
	public final int getRenderType() {
		return ChromatiCraft.proxy.glassRender;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/glass");
		overlay = ico.registerIcon("chromaticraft:basic/glass_overlay");
	}

	@Override
	public final int getRenderColor(int dmg)
	{
		return CrystalElement.elements[dmg].getColor();
	}

	@Override
	public final int colorMultiplier(IBlockAccess iba, int x, int y, int z)
	{
		int dmg = iba.getBlockMetadata(x, y, z);
		return CrystalElement.elements[dmg].getJavaColor().brighter().getRGB();
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int s) {
		return super.shouldSideBeRendered(iba, x, y, z, s) && iba.getBlock(x, y, z) != this;
	}

	public static IIcon getOverlay() {
		return overlay;
	}

}
