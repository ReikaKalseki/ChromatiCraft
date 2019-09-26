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

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;



public class ConfigRecipe {

	public static final class Basic extends CastingRecipe {

		private final int duration;
		private final int typicalCrafted;
		private final ItemStack centralLeftover;
		private final float automationCost;
		private final float experienceFactor;
		private final Collection<ProgressStage> extraProgress;

		public Basic(IRecipe ir, int dur, int typical, ItemStack leftover, float autocost, float xp, Collection<ProgressStage> p) {
			super(ir.getRecipeOutput(), ir);

			duration = dur;
			typicalCrafted = typical;
			centralLeftover = leftover;
			automationCost = autocost;
			experienceFactor = xp;
			extraProgress = p;
		}

		@Override
		public int getDuration() {
			return duration > 0 ? duration : super.getDuration();
		}

		@Override
		public int getTypicalCraftedAmount() {
			return typicalCrafted;
		}

		@Override
		public float getAutomationCostFactor(TileEntityCastingAuto ae, TileEntityCastingTable te, ItemStack is) {
			return automationCost;
		}

		@Override
		public ItemStack getCentralLeftover(ItemStack is) {
			return centralLeftover;
		}

		@Override
		public void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.addAll(extraProgress);
		}

		@Override
		public int getExperience() {
			return (int)(super.getExperience()*experienceFactor);
		}

	}

	public static final class Rune extends TempleCastingRecipe {

		private final int duration;
		private final int typicalCrafted;
		private final ItemStack centralLeftover;
		private final float automationCost;
		private final float experienceFactor;
		private final Collection<ProgressStage> extraProgress;

		public Rune(IRecipe ir, int dur, int typical, ItemStack leftover, float autocost, float xp, HashMap<Coordinate, CrystalElement> runes, Collection<ProgressStage> p) {
			super(ir.getRecipeOutput(), ir);

			for (Coordinate c : runes.keySet()) {
				this.addRune(runes.get(c).ordinal(), c.xCoord, c.yCoord, c.zCoord);
			}

			duration = dur;
			typicalCrafted = typical;
			centralLeftover = leftover;
			automationCost = autocost;
			experienceFactor = xp;
			extraProgress = p;
		}

		@Override
		public int getDuration() {
			return duration > 0 ? duration : super.getDuration();
		}

		@Override
		public int getTypicalCraftedAmount() {
			return typicalCrafted;
		}

		@Override
		public float getAutomationCostFactor(TileEntityCastingAuto ae, TileEntityCastingTable te, ItemStack is) {
			return automationCost;
		}

		@Override
		public ItemStack getCentralLeftover(ItemStack is) {
			return centralLeftover;
		}

		@Override
		public void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.addAll(extraProgress);
		}

		@Override
		public int getExperience() {
			return (int)(super.getExperience()*experienceFactor);
		}

	}

	public static final class Multi extends MultiBlockCastingRecipe {

		private final int duration;
		private final int typicalCrafted;
		private final ItemStack centralLeftover;
		private final float automationCost;
		private final float experienceFactor;
		private final Collection<ProgressStage> extraProgress;

		public Multi(ItemStack out, ItemStack center, int dur, int typical, ItemStack leftover, float autocost, float xp, HashMap<Coordinate, CrystalElement> runes, HashMap<Coordinate, Object> items, Collection<ProgressStage> p) {
			super(out, center);

			for (Coordinate c : runes.keySet()) {
				this.addRune(runes.get(c).ordinal(), c.xCoord, c.yCoord, c.zCoord);
			}

			for (Coordinate c : items.keySet()) {
				Object o = items.get(c);
				if (o instanceof String) {
					this.addAuxItem((String)o, c.xCoord, c.zCoord);
				}
				if (o instanceof ItemStack) {
					this.addAuxItem((ItemStack)o, c.xCoord, c.zCoord);
				}
			}

			duration = dur;
			typicalCrafted = typical;
			centralLeftover = leftover;
			automationCost = autocost;
			experienceFactor = xp;
			extraProgress = p;
		}

		@Override
		public int getDuration() {
			return duration > 0 ? duration : super.getDuration();
		}

		@Override
		public int getTypicalCraftedAmount() {
			return typicalCrafted;
		}

		@Override
		public float getAutomationCostFactor(TileEntityCastingAuto ae, TileEntityCastingTable te, ItemStack is) {
			return automationCost;
		}

		@Override
		public ItemStack getCentralLeftover(ItemStack is) {
			return centralLeftover;
		}

		@Override
		public void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.addAll(extraProgress);
		}

		@Override
		public int getExperience() {
			return (int)(super.getExperience()*experienceFactor);
		}

	}

	public static final class Pylon extends PylonCastingRecipe {

		private final int duration;
		private final int typicalCrafted;
		private final ItemStack centralLeftover;
		private final float automationCost;
		private final float experienceFactor;
		private final Collection<ProgressStage> extraProgress;

		public Pylon(ItemStack out, ItemStack center, int dur, int typical, ItemStack leftover, float autocost, float xp, HashMap<Coordinate, CrystalElement> runes, HashMap<Coordinate, Object> items, HashMap<CrystalElement, Integer> energy, Collection<ProgressStage> p) {
			super(out, center);

			for (Coordinate c : runes.keySet()) {
				this.addRune(runes.get(c).ordinal(), c.xCoord, c.yCoord, c.zCoord);
			}

			for (Coordinate c : items.keySet()) {
				Object o = items.get(c);
				if (o instanceof String) {
					this.addAuxItem((String)o, c.xCoord, c.zCoord);
				}
				if (o instanceof ItemStack) {
					this.addAuxItem((ItemStack)o, c.xCoord, c.zCoord);
				}
			}

			for (CrystalElement e : energy.keySet()) {
				this.addAuraRequirement(e, energy.get(e));
			}

			duration = dur;
			typicalCrafted = typical;
			centralLeftover = leftover;
			automationCost = autocost;
			experienceFactor = xp;
			extraProgress = p;
		}

		@Override
		public int getDuration() {
			return duration > 0 ? duration : super.getDuration();
		}

		@Override
		public int getTypicalCraftedAmount() {
			return typicalCrafted;
		}

		@Override
		public float getAutomationCostFactor(TileEntityCastingAuto ae, TileEntityCastingTable te, ItemStack is) {
			return automationCost;
		}

		@Override
		public ItemStack getCentralLeftover(ItemStack is) {
			return centralLeftover;
		}

		@Override
		public void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.addAll(extraProgress);
		}

		@Override
		public int getExperience() {
			return (int)(super.getExperience()*experienceFactor);
		}

	}

}
