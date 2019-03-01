/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant.Modifier;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class ItemCrystalSeeds extends ItemCrystalBasic {

	public ItemCrystalSeeds(int tex) {
		super(tex);
	}

	@Override
	public int getNumberTypes() {
		return ChromaItems.getEntryByID(this).getNumberMetadatas();
	}

	@Override
	protected boolean isMetaInCreative(int meta) {
		meta = meta & 0xfffffff0;
		if (meta == 0)
			return true;
		return ReikaMathLibrary.isPowerOfTwo(meta) && (DragonAPICore.isReikasComputer() || Modifier.getFromFlag(meta).showsInCreative());
	}

	/*
	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ReikaDyeHelper.dyes[is.getItemDamage()%16].colorName+" Crystal Seeds";
	}*/

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)-item.getItemDamage()+item.getItemDamage()%16;
	}

	@Override
	public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z)) {
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
		}
		Block idbelow = world.getBlock(x, y-1, z);
		if ((!ReikaWorldHelper.softBlocks(world, x, y, z)) || !ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z))
			return false;
		if (!player.canPlayerEdit(x, y, z, 0, item))
			return false;
		else
		{
			if (!player.capabilities.isCreativeMode)
				--item.stackSize;
			world.setBlock(x, y, z, ChromaBlocks.PLANT.getBlockInstance(), item.getItemDamage()%16, 3);
			TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
			te.setStates(item);
			ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.grass);
			return true;
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		for (int i = 0; i < Modifier.list.length; i++) {
			Modifier m = Modifier.list[i];
			if (m.present(is.getItemDamage())) {
				li.add(m.displayName);
			}
		}
		if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("sterile")) {
			li.add("Will plant a sterile plant, for decoration only");
		}
	}

}
