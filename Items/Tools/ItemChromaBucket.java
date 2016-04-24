/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemChromaBucket extends ItemChromaTool {

	public ItemChromaBucket(int index) {
		super(index);
		hasSubtypes = true;
		this.setContainerItem(Items.bucket);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) //Adds the metadata blocks to the creative inventory
	{
		for (int i = 0; i < ChromaItems.BUCKET.getNumberMetadatas(); i++)
			par3List.add(new ItemStack(par1, 1, i));
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float a, float b, float c) {
		if (is.stackSize > 1)
			return false;
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
				return false;
		}
		if (world.getBlock(x, y, z) == this.getBlock(is) && world.getBlockMetadata(x, y, z) == 0)
			return false;
		world.setBlock(x, y, z, this.getBlock(is));
		/*
		if (is.getItemDamage() >= 2) {
			TileEntityChroma te = (TileEntityChroma)world.getTileEntity(x, y, z);
			te.activate(CrystalElement.elements[is.getItemDamage()-2]);
			if (is.stackTagCompound != null) {
				te.setBerries(is.stackTagCompound.getInteger("berry"));
			}
		}
		 */
		if (!ep.capabilities.isCreativeMode)
			ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
		return true;
	}

	private Block getBlock(ItemStack is) {
		//if (is.getItemDamage() >= 2)
		//	return ChromaBlocks.ACTIVECHROMA.getBlockInstance();
		switch(is.getItemDamage()) {
			case 0:
				return ChromaBlocks.CHROMA.getBlockInstance();
			case 1:
				return ChromaBlocks.ENDER.getBlockInstance();
			default:
				return Blocks.air;
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + String.valueOf(d);
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)+item.getItemDamage();
	}

}
