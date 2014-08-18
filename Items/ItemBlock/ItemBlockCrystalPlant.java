/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPlant;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockCrystalPlant extends ItemBlock {

	public ItemBlockCrystalPlant(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public IIcon getIconFromDamage(int dmg) {
		return field_150939_a.getIcon(0, dmg);
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.PLANT.getMultiValuedName(is.getItemDamage());
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		boolean flag = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (flag) {
			TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
			te.grow();
			te.grow();
		}
		return flag;
	}

}
