/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.API.MinerBlock;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ProgressionTrigger;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class BlockCaveCrystal extends CrystalBlock implements ProgressionTrigger, MinerBlock {

	private static final Random rand = new Random();

	public BlockCaveCrystal(Material mat) {
		super(mat);
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return ChromaItems.SHARD.getItemInstance();
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1+r.nextInt(6)+r.nextInt(3);
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer ep, int x, int y, int z, int meta)
	{
		return true;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		int num = this.getNumberDrops(meta, fortune);
		for (int i = 0; i < num; i++)
			li.add(ChromaItems.SHARD.getStackOfMetadata(meta));
		return li;
	}

	private int getNumberDrops(int meta, int fortune) {
		return 1+rand.nextInt(6+fortune)+(1+fortune)*rand.nextInt(3)+rand.nextInt(1+fortune);
	}

	@Override
	public boolean shouldMakeNoise() {
		return true;
	}

	@Override
	public boolean shouldGiveEffects(CrystalElement e) {
		return e == CrystalElement.BROWN || e == CrystalElement.BLUE ? rand.nextInt(4) == 0 : true;
	}

	@Override
	public int getRange() {
		return 3;
	}

	@Override
	public int getDuration(CrystalElement e) {
		return e == CrystalElement.BROWN ? 5 : 200;
	}

	@Override
	public boolean renderBase() {
		return false;
	}

	@Override
	public Block getBaseBlock(ForgeDirection side) {
		return Blocks.cobblestone;
	}

	@Override
	public int getPotionLevel(CrystalElement e) {
		return 0;
	}

	@Override
	public int getBrightness(IBlockAccess iba, int x, int y, int z) {
		return this.isDarkStructure(iba, x, y, z) ? 0 : 10;
	}

	private boolean isDarkStructure(IBlockAccess iba, int x, int y, int z) {
		return iba.getBlock(x, y-1, z) == ChromaBlocks.RUNE.getBlockInstance() && iba.getBlock(x, y+1, z) == ChromaBlocks.STRUCTSHIELD.getBlockInstance();
	}

	@Override
	public ProgressStage[] getTriggers(EntityPlayer ep, World world, int x, int y, int z) {
		return new ProgressStage[]{ProgressStage.CRYSTALS};
	}

	@Override
	public boolean isMineable(int meta) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getHarvestItems(World world, int x, int y, int z, int meta, int fortune) {
		return this.getDrops(world, x, y, z, meta, fortune);
	}
}
