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
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.BlockChromaTiered;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.RotaryCraft.API.ItemFetcher;

public class BlockTieredOre extends BlockChromaTiered {

	private final IIcon[] overlay = new IIcon[16];
	private final IIcon[] back = new IIcon[16];

	public BlockTieredOre(Material mat) {
		super(mat);
		this.setHardness(4);
		this.setResistance(5);
	}

	public static enum TieredOres {

		INFUSED(ProgressStage.CRYSTALS),
		STONES(ProgressStage.RUNEUSE),
		BINDING(ProgressStage.CHARGE),
		FOCAL(ProgressStage.MULTIBLOCK),
		PLACEHOLDER4(ProgressStage.LINK),
		PLACEHOLDER5(ProgressStage.LINK),
		PLACEHOLDER6(ProgressStage.LINK),
		PLACEHOLDER7(ProgressStage.LINK),
		PLACEHOLDER8(ProgressStage.LINK);

		public final ProgressStage level;

		public static final TieredOres[] list = values();

		private TieredOres(ProgressStage lvl) {
			level = lvl;
		}

		public boolean generate(World world, int x, int z, Random r) {
			int y = r.nextBoolean() ? r.nextInt(32) : r.nextInt(64);
			return new WorldGenMinable(ChromaBlocks.TIEREDORE.getBlockInstance(), this.ordinal(), 8, Blocks.stone).generate(world, r, x, y, z);
		}

		public int getGenerationCount() {
			return this == PLACEHOLDER8 ? 1 : this.ordinal() < PLACEHOLDER4.ordinal() ? 4 : 2;
		}

		public int getGenerationChance() {
			return this == PLACEHOLDER8 ? 2 : 1;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs c, List li) {
		for (int i = 0; i < TieredOres.list.length; i++) {
			li.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public Collection<ItemStack> getHarvestResources(World world, int x, int y, int z, int fortune, EntityPlayer player) {
		ArrayList li = new ArrayList();
		int n = 1;
		if (ItemFetcher.isPlayerHoldingBedrockPick(player))
			fortune = 5;
		switch(TieredOres.list[world.getBlockMetadata(x, y, z)]) {
		case INFUSED:
			n = 1+rand.nextInt(5)*(1+rand.nextInt(1+fortune));
			for (int i = 0; i < n; i++)
				li.add(ChromaStacks.chromaDust.copy());
			break;
		case STONES:
			li.add(ChromaItems.ELEMENTAL.getStackOfMetadata(rand.nextInt(16)));
			break;
		case BINDING:
			n = 1+rand.nextInt(3)*(1+rand.nextInt(1+fortune/2));
			for (int i = 0; i < n; i++)
				li.add(ChromaStacks.bindingCrystal.copy());
			break;
		case FOCAL:
			n = 1+rand.nextInt(8)*(1+rand.nextInt(1+fortune*2));
			for (int i = 0; i < n; i++)
				li.add(ChromaStacks.focusDust.copy());
			break;
		case PLACEHOLDER4:
			break;
		case PLACEHOLDER5:
			break;
		case PLACEHOLDER6:
			break;
		case PLACEHOLDER7:
			break;
		case PLACEHOLDER8:
			break;
		}
		return li;
	}

	@Override
	public ProgressStage getProgressStage(int meta) {
		return TieredOres.list[meta].level;
	}

	@Override
	public Collection<ItemStack> getNoHarvestResources(World world, int x, int y, int z, int fortune, EntityPlayer player) {
		ArrayList li = new ArrayList();
		li.addAll(Blocks.stone.getDrops(world, x, y, z, 0, fortune));
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

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int dx, int dy, int dz, int s) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		Block b = world.getBlock(dx, dy, dz);
		if (b.isOpaqueCube())
			return false;
		switch(dir) {
		case EAST:
		case WEST:
		case SOUTH:
		case NORTH:
		case UP:
		case DOWN:
		default:
			return true;
		}
	}



}
