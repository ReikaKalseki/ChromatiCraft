/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface NBTTile {

	public void getTagsToWriteToStack(NBTTagCompound NBT);

	public void setDataFromItemStackTag(ItemStack is);

	public void addTooltipInfo(List li, ItemStack is, boolean shift);



}
