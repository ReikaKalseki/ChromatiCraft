/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LightedTreeBlock;
import Reika.ChromatiCraft.Render.ISBRH.GlowTreeRenderer;
import Reika.DragonAPI.Base.BlockCustomLeaf;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLightedLeaf extends BlockCustomLeaf implements LightedTreeBlock {

	private final IIcon[] overlay = new IIcon[5];

	public BlockLightedLeaf() {
		super();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs cr, List li)
	{
		for (int i = 0; i < overlay.length; i++)
			li.add(new ItemStack(this, 1, i));
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		return 12;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		GlowTreeRenderer.renderPass = pass;
		return pass <= 1;
	}

	@Override
	protected void onRandomUpdate(World world, int x, int y, int z, Random r) {
		world.setBlockMetadataWithNotify(x, y, z, rand.nextInt(overlay.length), 3);
		world.scheduleBlockUpdate(x, y, z, this, 200+rand.nextInt(1000));
	}

	@Override
	public boolean shouldRandomTick() {
		return true;
	}

	@Override
	public boolean decays() {
		return false;
	}

	@Override
	public boolean allowModDecayControl() {
		return false;
	}

	@Override
	public boolean showInCreative() {
		return true;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return ChromatiCraft.tabChromaGen;
	}

	@Override
	public boolean shouldTryDecay(World world, int x, int y, int z, int meta) {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico)
	{
		blockIcon = Blocks.leaves.getIcon(0, 0);
		for (int i = 0; i < overlay.length; i++) {
			overlay[i] = ico.registerIcon("chromaticraft:dimgen/glowleaf-light_"+i);
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return Blocks.leaves.field_150129_M[this.getOpacityIndex()][0];
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.glowTreeRender;
	}

	@Override
	public String getFastGraphicsIcon(int meta) {
		return "";//Blocks.leaves.field_150129_M[1][0];
	}

	@Override
	public String getFancyGraphicsIcon(int meta) {
		return "";//Blocks.leaves.field_150129_M[0][0];
	}

	public IIcon getOverlay(int meta) {
		return overlay[meta];
	}

	@Override
	public boolean renderOverlayOnSide(int s, int meta) {
		return true;
	}

}
