package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface NBTTile {

	public NBTTagCompound getTagsToWriteToStack();

	public void setDataFromItemStackTag(ItemStack is);



}
