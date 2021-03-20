/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dye;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaISBRH;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//commonly hangs down from under the leaves, and rarely on worldgen can have a pod that drops the shards
public class BlockDyeVine extends BlockTallGrass {

	private final boolean isFertile;

	private IIcon glowIcon;
	private final IIcon[] baseIcon = new IIcon[4];

	public BlockDyeVine(boolean fertile) {
		super();
		isFertile = fertile;
		this.setStepSound(soundTypeGrass);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public final int getRenderColor(int dmg) {
		return ReikaDyeHelper.dyes[dmg].getColor();
	}

	@Override
	public final int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return ReikaColorAPI.mixColors(ChromaBlocks.DYELEAF.getBlockInstance().colorMultiplier(iba, x, y, z), 0x404040, 0.7F);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return this.canBlockStay(world, x, y, z, world.getBlockMetadata(x, y, z));
	}

	public boolean canBlockStay(World world, int x, int y, int z, int meta) {
		Block at = world.getBlock(x, y+1, z);
		return (at instanceof BlockDyeLeaf || at instanceof BlockDyeVine) && world.getBlockMetadata(x, y+1, z) == meta;
	}

	@SideOnly(Side.CLIENT)
	public void render(IBlockAccess world, int x, int y, int z, RenderBlocks rb, Tessellator v5, Random rand) {
		IIcon ico = this.getIcon(world, x, y, z, rand.nextInt(baseIcon.length));
		float fc = (float)ReikaRandomHelper.getRandomBetween(0.675, 0.875, rand);//0.7F;
		int c = ReikaColorAPI.mixColors(ChromaBlocks.DYELEAF.getBlockInstance().colorMultiplier(world, x, y, z), 0x404040, fc);
		v5.setColorOpaque_I(c);
		v5.setBrightness(this.getMixedBrightnessForBlock(world, x, y, z));
		float dx = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.03125, rand);
		float dz = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.03125, rand);
		double w = ReikaRandomHelper.getRandomBetween(0, 0.125, rand);
		v5.addTranslation(dx, 0, dz);
		ReikaRenderHelper.renderCropTypeTex(world, x, y, z, ico, v5, rb, w, 1);
		if (isFertile) {
			ico = glowIcon;
			v5.setColorOpaque_I(CrystalElement.elements[world.getBlockMetadata(x, y, z)].getColor());
			v5.setBrightness(240);
			ReikaRenderHelper.renderCropTypeTex(world, x, y, z, ico, v5, rb, w, 1);
		}
		v5.addTranslation(-dx, 0, -dz);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < baseIcon.length; i++)
			baseIcon[i] = ico.registerIcon("chromaticraft:dye/vine/"+i);
		glowIcon = ico.registerIcon("chromaticraft:dye/vine/glow");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int meta) {
		return baseIcon[s%baseIcon.length];
	}

	@Override
	public void getSubBlocks(Item it, CreativeTabs cr, List li) {
		for (int i = 0; i < 16; i++) {
			li.add(new ItemStack(it, 1, i));
		}
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Plains;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float f = 0.4F; //from parent
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1, 0.5F + f);
	}

	@Override
	public int getRenderType() {
		return ChromaISBRH.dyeVine.getRenderID();
	}

	@Override
	public boolean canReplace(World world, int x, int y, int z, int side, ItemStack is) {
		return world.getBlock(x, y, z).isReplaceable(world, x, y, z) && this.canBlockStay(world, x, y, z, is.getItemDamage());
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList();

		if (isFertile) {
			ret.add(ChromaItems.SHARD.getCraftedMetadataProduct(1+world.rand.nextInt(1+fortune/2), meta));
		}

		if (world.rand.nextInt(Math.max(1, 10-fortune*2)) == 0)
			ret.add(ChromaBlocks.DYESAPLING.getStackOfMetadata(meta));
		if (world.rand.nextInt(Math.max(1, 3-fortune)) == 0)
			ret.add(ChromaItems.BERRY.getCraftedMetadataProduct(1+world.rand.nextInt(1+fortune), meta));

		return ret;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return false;
	}

}
