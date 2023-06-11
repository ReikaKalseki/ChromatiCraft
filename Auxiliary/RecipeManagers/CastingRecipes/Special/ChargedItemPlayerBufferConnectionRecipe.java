/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Magic.Interfaces.PoweredItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChargedItemPlayerBufferConnectionRecipe extends PylonCastingRecipe {

	public ChargedItemPlayerBufferConnectionRecipe(ItemStack item, int costFactor) {
		super(getOutputItem(item), item);

		this.addAuraRequirement(((PoweredItem)item.getItem()).getColor(item), 6000*costFactor);

		this.addAuxItem(ChromaStacks.glowbeans, -2, -2);
		this.addAuxItem(ChromaStacks.glowbeans, 2, -2);
		this.addAuxItem(ChromaStacks.glowbeans, -2, 2);
		this.addAuxItem(ChromaStacks.glowbeans, 2, 2);

		this.addAuxItem(ChromaStacks.lumenGem, 0, -2);
		this.addAuxItem(ChromaStacks.lumenGem, 2, 0);
		this.addAuxItem(ChromaStacks.lumenGem, 0, 2);
		this.addAuxItem(ChromaStacks.lumenGem, -2, 0);

		this.addAuxItem(Items.redstone, 0, -4);
		this.addAuxItem(Items.redstone, 4, 0);
		this.addAuxItem(Items.redstone, 0, 4);
		this.addAuxItem(Items.redstone, -4, 0);

		this.addAuxItem(Items.glowstone_dust, 4, -2);
		this.addAuxItem(Items.glowstone_dust, -2, -4);
		this.addAuxItem(Items.glowstone_dust, -4, 2);
		this.addAuxItem(Items.glowstone_dust, 2, 4);

		this.addAuxItem(Items.glowstone_dust, 4, 2);
		this.addAuxItem(Items.glowstone_dust, -2, 4);
		this.addAuxItem(Items.glowstone_dust, -4, -2);
		this.addAuxItem(Items.glowstone_dust, 2, -4);
	}

	private static ItemStack getOutputItem(ItemStack item) {
		ItemStack is = item.copy();
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setBoolean("bufferlinked", true);
		return is;
	}

	@Override
	protected boolean isValidCentralNBT(ItemStack is) {
		return true;
	}

	@Override
	public NBTTagCompound getOutputTag(EntityPlayer ep, NBTTagCompound input) {
		NBTTagCompound ret = input == null ? new NBTTagCompound() : (NBTTagCompound)input.copy();
		ret.setBoolean("bufferlinked", true);
		return ret;
	}

	@Override
	public boolean isIndexed() {
		return false;
	}

	@Override
	public int getExperience() {
		return super.getExperience()*2/5;
	}

	@Override
	public int getDuration() {
		return super.getDuration()/2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDisplayName() {
		return "Lumen Buffer Connection - "+this.getOutput().getDisplayName();
	}

}
