/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import java.util.Collection;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeType;


public class BeeConversionRecipe extends PylonCastingRecipe {

	private static ItemStack displayDrone;
	private static long lastRedisplayTime;

	public BeeConversionRecipe() {
		super(new ItemStack(ForestryHandler.ItemEntry.PRINCESS.getItem()), new ItemStack(ForestryHandler.ItemEntry.DRONE.getItem()));

		for (int i = -4; i <= 4; i++) {
			if (i != 0) {
				this.addAuxItem(ChromaStacks.livingEssence, i, i);
				this.addAuxItem(ChromaStacks.livingEssence, i, -i);

				this.addAuxItem(ChromaStacks.lifegel, i, 0);
				this.addAuxItem(ChromaStacks.lifegel, 0, i);
			}
		}

		this.addAuxItem(ForestryHandler.ItemEntry.JELLY.getItem(), -2, -4);
		this.addAuxItem(ForestryHandler.ItemEntry.JELLY.getItem(), 2, -4);
		this.addAuxItem(ForestryHandler.ItemEntry.JELLY.getItem(), -4, -2);
		this.addAuxItem(ForestryHandler.ItemEntry.JELLY.getItem(), 4, -2);
		this.addAuxItem(ForestryHandler.ItemEntry.JELLY.getItem(), -2, 4);
		this.addAuxItem(ForestryHandler.ItemEntry.JELLY.getItem(), 2, 4);
		this.addAuxItem(ForestryHandler.ItemEntry.JELLY.getItem(), -4, 2);
		this.addAuxItem(ForestryHandler.ItemEntry.JELLY.getItem(), 4, 2);

		this.addRune(CrystalElement.GREEN, 5, 0, -3);
		this.addRune(CrystalElement.PURPLE, -5, 0, -3);
		this.addRune(CrystalElement.LIGHTGRAY, 0, 0, 5);

		this.addAuraRequirement(CrystalElement.GREEN, 36000);
		this.addAuraRequirement(CrystalElement.MAGENTA, 10000);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getOutputForDisplay() {
		return this.getOutputForDisplay(null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getOutputForDisplay(ItemStack center) {
		ItemStack ctr = center == null ? this.getArrayForDisplay()[4] : center;
		return ctr.stackTagCompound != null ? this.calcOutput(ctr.stackTagCompound) : this.getOutput();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack[] getArrayForDisplay() {
		ItemStack[] iss = new ItemStack[9];
		long time = System.currentTimeMillis();
		if (displayDrone == null || lastRedisplayTime < time-(GuiScreen.isShiftKeyDown() ? 100 : 1000)) {
			displayDrone = this.generateRandomBee();
			lastRedisplayTime = time;
		}
		iss[4] = displayDrone;
		return iss;
	}

	private ItemStack generateRandomBee() {
		ItemStack is = null;
		while (is == null) {
			String species = ReikaBeeHelper.getRandomBeeSpecies();
			try {
				is = ReikaBeeHelper.getBeeItem(species, EnumBeeType.DRONE);
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not generate bee item for bee type "+species+": "+e.toString());
			}
		}
		return is;
	}

	@Override
	public int getRequiredCentralItemCount() {
		return 16;
	}

	@Override
	protected boolean isValidCentralNBT(ItemStack is) {
		return true;
	}

	@Override
	protected void getRequiredProgress(Collection<ProgressStage> c) {
		super.getRequiredProgress(c);
		c.add(ProgressStage.HIVE);
		c.add(ProgressStage.DIMENSION);
	}

	@Override
	public NBTTagCompound getOutputTag(NBTTagCompound input) {
		return this.calcOutput(input).stackTagCompound;
	}

	/*
	@Override
	public NBTTagCompound handleNBTResult(TileEntityCastingTable te, EntityPlayer ep, NBTTagCompound originalCenter, NBTTagCompound tag) {
		return this.calcOutput(originalCenter).stackTagCompound;
	}
	 */

	private ItemStack calcOutput(NBTTagCompound in) {
		ItemStack is = this.getOutput().copy();
		is.stackTagCompound = in != null ? (NBTTagCompound)in.copy() : null;
		return is;
	}

	@Override
	public boolean isIndexed() {
		return false;
	}

}
