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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Base.BlockTieredResource;

public class BlockTieredOre extends BlockTieredResource {

	private final IIcon[] overlay = new IIcon[16];
	private final IIcon[] back = new IIcon[16];

	public BlockTieredOre(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(4);
		this.setResistance(5);
	}

	public static enum TieredOres {

		INFUSED(ProgressStage.RUNEUSE);

		public final ProgressStage level;

		public static final TieredOres[] list = values();

		private TieredOres(ProgressStage lvl) {
			level = lvl;
		}

		public boolean generate(World world, int x, int z, Random r) {
			int y = r.nextBoolean() ? r.nextInt(32) : r.nextInt(64);
			new WorldGenMinable(ChromaBlocks.TIEREDORE.getBlockInstance(), this.ordinal()).generate(world, r, x, y, z);
			return false;
		}

		public int getGenerationCount() {
			return 3;
		}

		public int getGenerationChance() {
			return 5;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs c, List li) {
		for (int i = 0; i < ProgressStage.values().length; i++) {
			li.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	protected Collection<ItemStack> getHarvestResources(World world, int x, int y, int z, int fortune) {
		int meta = world.getBlockMetadata(x, y, z);
		ArrayList li = new ArrayList();
		switch (meta) {
		case 0:
			int n = 1+rand.nextInt(5)*(1+rand.nextInt(1+fortune));
			for (int i = 0; i < n; i++)
				li.add(ChromaStacks.chromaDust.copy());
			break;
		}
		return li;
	}

	private ProgressStage getProgressStage(IBlockAccess world, int x, int y, int z) {
		return ProgressStage.PYLON;
	}

	@Override
	public boolean isPlayerSufficientTier(IBlockAccess world, int x, int y, int z, EntityPlayer ep) {
		return ProgressionManager.instance.isPlayerAtStage(ep, this.getProgressStage(world, x, y, z));
	}

	@Override
	public Collection<ItemStack> getNoHarvestResources(World world, int x, int y, int z, int fortune) {
		ArrayList li = new ArrayList();
		li.add(new ItemStack(Blocks.stone));
		return li;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < back.length; i++) {
			String s = "chromaticraft:ore/tier_"+i;
			back[i] = ico.registerIcon(s+"_underlay");
			overlay[i] = ico.registerIcon(s+"_overlay");
		}
	}

	public IIcon getOverlay(int meta) {
		return overlay[meta];
	}

	public IIcon getBacking(int meta) {
		return back[meta];
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return this.getBacking(meta);
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.oreRender;
	}

	public Block getDisguise() {
		return Blocks.stone;
	}



}
