/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension;

import java.util.List;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LightedTreeBlock;
import Reika.ChromatiCraft.Render.ISBRH.GlowTreeRenderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLightedLog extends BlockLog implements LightedTreeBlock {

	private IIcon overlay;

	public BlockLightedLog() {
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs cr, List li)
	{
		li.add(new ItemStack(this));
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.glowTreeRender;
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
	public IIcon getIcon(int s, int meta) {
		return Blocks.log.getIcon(s, meta-meta%4);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		overlay = ico.registerIcon("chromaticraft:dimgen/glowlog-light");
	}

	@Override
	public IIcon getOverlay(int meta) {
		return overlay;
	}

	@Override
	public boolean renderOverlayOnSide(int s, int meta) {
		return !this.getIcon(s, meta).getIconName().endsWith("_top");
	}

}
