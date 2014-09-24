/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Base.BlockTieredResource;

public final class BlockChromaTiered extends BlockTieredResource {

	private final IIcon[] icons = new IIcon[16];

	public BlockChromaTiered(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs c, List li) {
		for (int i = 0; i < ProgressStage.values().length; i++) {
			li.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	protected Collection<ItemStack> getHarvestResources(World world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		return li;
	}

	private ProgressStage getProgressStage(IBlockAccess world, int x, int y, int z) {
		return ProgressStage.values()[world.getBlockMetadata(x, y, z)]; //Testing
	}

	@Override
	public boolean isPlayerSufficientTier(IBlockAccess world, int x, int y, int z, EntityPlayer ep) {
		return this.getProgressStage(world, x, y, z).ordinal() <= ProgressionManager.instance.getPlayerProgressionStage(ep).ordinal();
	}

	@Override
	protected boolean isFullyUndetectable() {
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public int getRenderType() {
		return -1; --need a tesr --
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return super.canRenderInPass(pass);
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta];
	}/*

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int s) {
		return this.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer) ? this.getIcon(s, world.getBlockMetadata(x, y, z)) : ChromaIcons.TRANSPARENT.getIcon();
	}*/

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < icons.length; i++) {
			icons[i] = ico.registerIcon("chromaticraft:plant/tierplant_"+i);
		}
	}

}
