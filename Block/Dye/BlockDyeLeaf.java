/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dye;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Base.BlockCustomLeaf;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

public class BlockDyeLeaf extends BlockCustomLeaf {

	public BlockDyeLeaf() {
		super();
	}

	@Override
	public final int getRenderColor(int dmg)
	{
		return ReikaDyeHelper.dyes[dmg].getColor();
	}

	@Override
	public final int colorMultiplier(IBlockAccess iba, int x, int y, int z)
	{
		int dmg = iba.getBlockMetadata(x, y, z);
		return ReikaDyeHelper.dyes[dmg].getJavaColor().brighter().getRGB();
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune)
	{
		return Item.getItemFromBlock(ChromaBlocks.DYESAPLING.getBlockInstance());
	}

	@Override
	public int damageDropped(int dmg)
	{
		return dmg;
	}

	@Override
	public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		float saplingChance = 0.05F;
		float appleChance = 0.005F;
		float dyeChance = 0.1F;
		float rainbowChance = 0.0001F;

		saplingChance *= (1+fortune);
		appleChance *= (1+fortune*5);
		dyeChance *= (1+fortune);
		rainbowChance *= (1+fortune)*(1+fortune);

		if (ReikaRandomHelper.doWithChance(saplingChance))
			li.add(new ItemStack(ChromaBlocks.DYESAPLING.getBlockInstance(), 1, meta));
		if (ReikaRandomHelper.doWithChance(appleChance))
			li.add(new ItemStack(Items.apple, 1, 0));
		if (ReikaRandomHelper.doWithChance(dyeChance))
			li.add(this.getDye(world, x, y, z, meta));
		if (ReikaRandomHelper.doWithChance(rainbowChance))
			li.add(new ItemStack(ChromaBlocks.RAINBOWSAPLING.getBlockInstance(), 1, 0));
		return li;
	}

	@Override
	public final void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune)
	{
		if (!world.isRemote) {
			ArrayList<ItemStack> li = this.getDrops(world, x, y, z, metadata, fortune);
			for (int i = 0; i < li.size(); i++) {
				if (chance >= 1 || ReikaRandomHelper.doWithChance(chance))
					this.dropBlockAsItem(world, x, y, z, li.get(i));
			}
		}
	}

	private final ItemStack getDye(World world, int x, int y, int z, int metadata) {
		if (ReikaRandomHelper.doWithChance(ChromaOptions.DYEFRAC.getValue())) {
			return new ItemStack(Items.dye, 1, metadata);
		}
		else {
			return ChromaItems.DYE.getStackOfMetadata(metadata);
		}
	}

	@Override
	public final ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(new ItemStack(ChromaBlocks.DYELEAF.getBlockInstance(), 1, world.getBlockMetadata(x, y, z)));
		return ret;
	}

	@Override
	protected final ItemStack createStackedBlock(int par1)
	{
		return new ItemStack(ChromaBlocks.DYELEAF.getBlockInstance(), 1, par1);
	}

	@Override
	public final void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (!ChromaOptions.BLOCKPARTICLES.getState() || rand.nextInt(12) > 0)
			return;
		double offset = 0.125;
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		Color color = dye.getJavaColor();
		double r = color.getRed()/255D;
		double g = color.getGreen()/255D;
		double b = dye.getBlue()/255D;
		ReikaParticleHelper.spawnColoredParticlesWithOutset(world, x, y, z, r, g, b, 1, offset);
	}

	@Override
	public final Item getItem(World par1World, int par2, int par3, int par4)
	{
		return Item.getItemFromBlock(ChromaBlocks.DYELEAF.getBlockInstance());
	}

	@Override
	public boolean decays() {
		return this == ChromaBlocks.DECAY.getBlockInstance();
	}

	@Override
	public boolean showInCreative() {
		return this != ChromaBlocks.DECAY.getBlockInstance();
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return ChromatiCraft.tabChroma;
	}

	@Override
	public String getFastGraphicsIcon(int meta) {
		return "ChromatiCraft:dye/leaves_opaque";
	}

	@Override
	public String getFancyGraphicsIcon(int meta) {
		return "ChromatiCraft:dye/leaves";
	}

	@Override
	public boolean shouldTryDecay(World world, int x, int y, int z, int meta) {
		return this.decays();
	}

	@Override
	public boolean shouldRandomTick() {
		return false;
	}

	@Override
	public boolean allowModDecayControl() {
		return true;
	}

}
