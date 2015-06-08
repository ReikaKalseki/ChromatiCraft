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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.MinerBlock;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeType;

public class BlockCrystalHive extends Block implements MinerBlock {

	private static final Random rand = new Random();

	private final IIcon[][] icons = new IIcon[16][6];

	public BlockCrystalHive(Material par2Material) {
		super(par2Material);
		this.setHardness(3);
		this.setResistance(5);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity e)
	{
		return !(e instanceof EntityDragon) && super.canEntityDestroy(world, x, y, z, e);
	}

	@Override
	public boolean canSilkHarvest() {
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		if (willHarvest)
			ProgressStage.HIVE.stepPlayerTo(player);
		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		if (!ModList.FORESTRY.isLoaded())
			return li;
		BeeSpecies bee = this.getBeeForMeta(metadata);
		if (bee != null) {
			float chance = Math.min(0.95F, (1+fortune)*0.25F);
			int drones = ReikaRandomHelper.doWithChance(chance) ? 2 : 1;
			for (int i = 0; i < drones; i++) {
				li.add(bee.getBeeItem(world, EnumBeeType.DRONE));
			}
			li.add(bee.getBeeItem(world, EnumBeeType.PRINCESS));
		}
		return li;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return new ItemStack(ChromaBlocks.HIVE.getBlockInstance(), 1, meta);
	}

	@ModDependent(ModList.FORESTRY)
	private BeeSpecies getBeeForMeta(int meta) {
		switch(meta) {
		case 0:
			return CrystalBees.getCrystalBee();
		case 1:
			return CrystalBees.getPureBee();
		default:
			return null;
		}
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		int meta = world.getBlockMetadata(x, y, z);
		ReikaParticleHelper p = meta == 0 ? ReikaParticleHelper.AMBIENTMOBSPELL : ReikaParticleHelper.ENCHANTMENT;
		int dy = meta == 0 ? y : y+1;
		p.spawnAroundBlock(world, x, dy, z, 8);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ico)
	{
		for (int i = 0; i < 2; i++) {
			icons[0][i] = ico.registerIcon("chromaticraft:hives/crystal_top"); //make crystal hive translucent?
		}
		for (int i = 2; i < 6; i++) {
			icons[0][i] = ico.registerIcon("chromaticraft:hives/crystal_side");
		}

		for (int i = 0; i < 2; i++) {
			icons[1][i] = ico.registerIcon("chromaticraft:hives/pure_top");
		}
		for (int i = 2; i < 6; i++) {
			icons[1][i] = ico.registerIcon("chromaticraft:hives/pure_side");
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta][s];
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
