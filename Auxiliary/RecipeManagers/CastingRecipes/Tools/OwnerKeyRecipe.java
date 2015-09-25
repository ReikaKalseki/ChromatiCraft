package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;


public class OwnerKeyRecipe extends CastingRecipe {

	public OwnerKeyRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public int getExperience() {
		return 0;
	}

	@Override
	public NBTTagCompound handleNBTResult(TileEntityCastingTable te, EntityPlayer ep, NBTTagCompound tag) {
		EntityPlayer plc = te.getPlacer();
		if (plc != null && ep == plc) {
			if (tag == null)
				tag = new NBTTagCompound();
			tag.setString("owner", ep.getUniqueID().toString());
		}
		return tag;
	}

}
