/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.NEI;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import Reika.ChromatiCraft.GUI.GuiCrystalBrewer;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalBrewer;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class CrystalBrewerHandler extends TemplateRecipeHandler {

	private class CrystalRecipe extends CachedRecipe {

		public final ReikaDyeHelper dye;

		public CrystalRecipe(int color) {
			this(ReikaDyeHelper.getColorFromDamage(color));
		}

		public CrystalRecipe(ReikaDyeHelper color) {
			dye = color;
		}

		@Override
		public PositionedStack getResult() {
			return null;//new PositionedStack(new ItemStack(Items.potionitem.getItem, 1, this.getPotionDamage()), 131, 24);
		}

		@Override
		public PositionedStack getIngredient()
		{
			return new PositionedStack(this.getInputShard(), 74, 6);
		}

		public ItemStack getInputShard() {
			return ChromaItems.SHARD.getStackOfMetadata(dye.ordinal());
		}

		public Potion getOutputPotion() {
			return Potion.potionTypes[CrystalPotionController.getEffectFromColor(dye, 20, 0).getPotionID()];
		}

		@Override
		public List<PositionedStack> getOtherStacks()
		{
			ItemStack out = TileEntityCrystalBrewer.getPotionStackFromColor(dye);
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			stacks.add(new PositionedStack(new ItemStack(Items.potionitem), 51, 35));
			//stacks.add(new PositionedStack(out, 74, 42));
			stacks.add(new PositionedStack(out, 97, 35));
			return stacks;
		}
	}

	@Override
	public String getRecipeName() {
		return "Crystal Brewing";
	}

	@Override
	public String getGuiTexture() {
		return "textures/gui/container/brewing_stand.png";
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		if (result.getItem() == ChromaItems.POTION.getItemInstance()) {
			arecipes.add(new CrystalRecipe(result.getItemDamage()%16));
		}
		else if (result.getItem() == Items.potionitem) {
			int id = ReikaPotionHelper.getPotionID(result.getItemDamage());
			for (int i = 0; i < 16; i++) {
				ReikaDyeHelper color = ReikaDyeHelper.getColorFromDamage(i);
				PotionEffect eff = CrystalPotionController.getEffectFromColor(color, 200, 0);
				if (eff != null) {
					int potid = eff.getPotionID();
					if (potid == id) {
						arecipes.add(new CrystalRecipe(color));
						return;
					}
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (ingredient.getItem() == ChromaItems.SHARD.getItemInstance()) {
			ReikaDyeHelper color = ReikaDyeHelper.getColorFromDamage(ingredient.getItemDamage());
			if (!CrystalPotionController.isPotionModifier(color))
				arecipes.add(new CrystalRecipe(color));
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiCrystalBrewer.class;
	}

}
