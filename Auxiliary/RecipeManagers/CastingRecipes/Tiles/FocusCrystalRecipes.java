/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal.CrystalTier;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public class FocusCrystalRecipes {

	private static final boolean exponentialCosts = true;

	private static final ItemStack basicCrystal = CrystalTier.FLAWED.getCraftedItem();
	private static final ItemStack defaultCrystal = CrystalTier.DEFAULT.getCraftedItem();
	private static final ItemStack refinedCrystal = CrystalTier.REFINED.getCraftedItem();
	private static final ItemStack exquisiteCrystal = CrystalTier.EXQUISITE.getCraftedItem();

	private static final ItemStack defaultSlab = ReikaItemHelper.stoneSlab;
	private static final ItemStack refinedSlab = ChromaBlocks.PYLONSTRUCT.getStackOf();
	private static final ItemStack exquisiteSlab = ReikaItemHelper.chiseledQuartz;

	private static final ItemStack defaultDust = ChromaStacks.focusDust;
	private static final ItemStack refinedDust = ChromaStacks.purityDust;
	private static final ItemStack exquisiteDust = ChromaStacks.lumaDust;

	@SideOnly(Side.CLIENT)
	public static IIcon getBaseRenderIcon(CrystalTier tier) {
		ItemStack is = null;
		switch(tier) {
			case DEFAULT:
				is = defaultSlab;
				break;
			case EXQUISITE:
				is = exquisiteSlab;
				break;
			case REFINED:
				is = refinedSlab;
				break;
			default:
				break;
		}
		return is != null ? Block.getBlockFromItem(is.getItem()).getIcon(1, is.getItemDamage()) : null;
	}

	public static class FlawedFocusCrystalRecipe extends CastingRecipe {

		public FlawedFocusCrystalRecipe() {
			super(basicCrystal, getBasicRecipe());
		}

		@Override
		public int getTypicalCraftedAmount() {
			return exponentialCosts ? 2*8*ReikaMathLibrary.intpow2(2, 3) : 16;
		}

		@Override
		public void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.add(ProgressStage.FOCUSCRYSTAL);
		}

	}

	public static class DefaultFocusCrystalRecipe extends TempleCastingRecipe {

		public DefaultFocusCrystalRecipe() {
			super(defaultCrystal, getDefaultRecipe());

			this.addRune(CrystalElement.BLACK, 3, -1, -3);
			this.addRune(CrystalElement.WHITE, 2, -1, 4);
			this.addRune(CrystalElement.LIGHTBLUE, -3, -1, -2);
		}

		@Override
		public int getTypicalCraftedAmount() {
			return exponentialCosts ? 2*8*ReikaMathLibrary.intpow2(2, 2) : 16;
		}

		@Override
		public void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.add(ProgressStage.FOCUSCRYSTAL);
			c.add(ProgressStage.RUNEUSE);
		}

	}

	public static class RefinedFocusCrystalRecipe extends MultiBlockCastingRecipe {

		public RefinedFocusCrystalRecipe(TempleCastingRecipe r) {
			super(refinedCrystal, exponentialCosts ? ChromaStacks.bindingCrystal : defaultCrystal);
			this.addRunes(r.getRunes());

			this.addAuxItem(exponentialCosts ? defaultCrystal : refinedDust, 0, -2);
			this.addAuxItem(exponentialCosts ? defaultCrystal : refinedDust, 0, 2);

			this.addAuxItem(refinedDust, -2, 0);
			this.addAuxItem(refinedDust, 2, 0);
			this.addAuxItem(refinedDust, 0, -4);
			this.addAuxItem(refinedDust, -4, 0);
			this.addAuxItem(refinedDust, 4, 0);

			this.addAuxItem(refinedSlab, -2, 4);
			this.addAuxItem(refinedSlab, 0, 4);
			this.addAuxItem(refinedSlab, 2, 4);

			this.addAuxItem(ChromaStacks.avolite, 2, -2);
			this.addAuxItem(ChromaStacks.avolite, -2, -2); //to gate explicitly as well as internally
		}

		@Override
		public int getTypicalCraftedAmount() {
			return exponentialCosts ? 2*8*ReikaMathLibrary.intpow2(2, 1) : 16;
		}

		@Override
		public void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.add(ProgressStage.FOCUSCRYSTAL);
			c.add(ProgressStage.RUNEUSE);
			c.add(ProgressStage.LINK);
			c.add(ProgressStage.POWERCRYSTAL); //to justify avolite
		}

	}

	public static class ExquisiteFocusCrystalRecipe extends PylonCastingRecipe {

		public ExquisiteFocusCrystalRecipe(TempleCastingRecipe r) {
			super(exquisiteCrystal, exponentialCosts ? ChromaStacks.bindingCrystal : refinedCrystal);
			this.addRunes(r.getRunes());

			this.addAuxItem(exponentialCosts ? refinedCrystal : exquisiteDust, 0, -2);
			this.addAuxItem(exponentialCosts ? refinedCrystal : exquisiteDust, 0, 2);

			this.addAuxItem(exquisiteDust, -2, 0);
			this.addAuxItem(exquisiteDust, 2, 0);
			this.addAuxItem(exquisiteDust, 0, -4);
			this.addAuxItem(exquisiteDust, -4, 0);
			this.addAuxItem(exquisiteDust, 4, 0);

			this.addAuxItem(exquisiteSlab, -2, 4);
			this.addAuxItem(exquisiteSlab, 0, 4);
			this.addAuxItem(exquisiteSlab, 2, 4);

			this.addAuraRequirement(CrystalElement.WHITE, 1000);
			this.addAuraRequirement(CrystalElement.BLACK, 5000);
			this.addAuraRequirement(CrystalElement.LIGHTBLUE, 10000);
		}

		@Override
		public int getTypicalCraftedAmount() {
			return 2*8;
		}

		@Override
		public float getConsecutiveStackingTimeFactor(TileEntityCastingTable te) {
			return 0.8F;
		}

		@Override
		public boolean canBeStacked() {
			return true;
		}

		@Override
		public void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.add(ProgressStage.FOCUSCRYSTAL);
			c.add(ProgressStage.RUNEUSE);
			c.add(ProgressStage.LINK);
			c.add(ProgressStage.DIMENSION);
		}

	}

	private static IRecipe getBasicRecipe() {
		return new ShapedOreRecipe(basicCrystal, " S ", " E ", " s ", 'S', "stone", 'E', Items.emerald, 's', Blocks.stone_slab);
	}

	private static IRecipe getDefaultRecipe() {
		return ReikaRecipeHelper.getShapedRecipeFor(defaultCrystal, " A ", "BCB", "SAS", 'S', defaultSlab, 'A', exponentialCosts ? basicCrystal : defaultDust, 'B', defaultDust, 'C', exponentialCosts ? ChromaStacks.bindingCrystal : basicCrystal);
	}

}
