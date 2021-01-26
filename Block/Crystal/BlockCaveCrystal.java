/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Crystal;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.MinerBlock;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ProgressionTrigger;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.DimensionTuningManager;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class BlockCaveCrystal extends CrystalBlock implements ProgressionTrigger, MinerBlock {

	public BlockCaveCrystal(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return ChromaItems.SHARD.getItemInstance();
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
		int num = this.getNumberDrops(rand, fortune);
		EntityPlayer ep = harvesters.get();
		if (ep != null)
			num = DimensionTuningManager.instance.getTunedDropCount(ep, num, 1, Integer.MAX_VALUE);
		for (int i = 0; i < num; i++)
			li.add(ChromaItems.SHARD.getStackOfMetadata(meta));
		return li;
	}

	@Override
	public boolean canDropFromExplosion(Explosion e) {
		return false;
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
		int n = 1+rand.nextInt(5);
		for (int i = 0; i < n; i++)
			ReikaItemHelper.dropItem(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), ChromaStacks.crystalPowder);
		super.onBlockExploded(world, x, y, z, explosion);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return ReikaAABBHelper.getBlockAABB(x, y, z).contract(0.03125, 0, 0.03125);
	}

	/**
	 Approx equals 4+2*fortune:<br><br>
Fortune 0 average: 4.513 = 4<br>
Fortune 1 average: 6.5059 = 6<br>
Fortune 2 average: 8.5229 = 8<br>
Fortune 3 average: 10.4932 = 10<br>
Fortune 4 average: 12.5796 = 12<br>
Fortune 5 average: 14.5277 = 14<br>
Fortune 6 average: 16.5918 = 16<br>
Fortune 7 average: 18.5853 = 18<br>
Fortune 8 average: 20.449 = 20<br>
Fortune 9 average: 22.5409 = 22<br>
Fortune 10 average: 24.572 = 24<br>
Fortune 11 average: 26.6424 = 26<br>
Fortune 12 average: 28.3735 = 28<br>
Fortune 13 average: 30.7102 = 30<br>
Fortune 14 average: 32.1941 = 32<br>
Fortune 15 average: 34.6519 = 34<br>
Fortune 16 average: 36.2285 = 36<br>
Fortune 17 average: 38.3125 = 38<br>
Fortune 18 average: 40.5295 = 40<br>
Fortune 19 average: 42.1995 = 42<br>
Fortune 20 average: 44.3653 = 44<br>
	 */
	public static int getNumberDrops(Random rand, int fortune) {
		return 1+rand.nextInt(6+fortune)+(1+fortune)*rand.nextInt(3)+rand.nextInt(1+fortune);
	}

	@Override
	public boolean shouldMakeNoise() {
		return true;
	}

	@Override
	public boolean shouldGiveEffects(CrystalElement e) {
		return true;
	}

	@Override
	public boolean performEffect(CrystalElement e) {
		return e == CrystalElement.BROWN || e == CrystalElement.BLUE ? rand.nextInt(4) == 0 : true;
	}

	@Override
	public int getRange() {
		return 4;
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
	public BlockKey getBaseBlock(IBlockAccess iba, int x, int y, int z, ForgeDirection side) {
		return new BlockKey(Blocks.cobblestone, 0);
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

	@Override
	public MineralCategory getCategory() {
		return MineralCategory.MISC_UNDERGROUND_VALUABLE;
	}

	@Override
	public Block getReplacedBlock(World world, int x, int y, int z) {
		return Blocks.air;
	}

	@Override
	public boolean allowSilkTouch(int meta) {
		return true;
	}
}
