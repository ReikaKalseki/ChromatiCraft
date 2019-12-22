package Reika.ChromatiCraft.Base.TileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;

public abstract class TileEntityPylonEnhancer extends TileEntityChromaticBase implements OwnedTile, BreakAction {


	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		this.writeOwnerData(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		this.readOwnerData(is);
	}

}
