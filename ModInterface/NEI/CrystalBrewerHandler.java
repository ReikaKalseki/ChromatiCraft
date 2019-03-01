/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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

import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiCrystalBrewer;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCrystalBrewer;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class CrystalBrewerHandler extends TemplateRecipeHandler {

	private class CrystalRecipe extends CachedRecipe {

		public final CrystalElement color;
		public final boolean boosted;

		public CrystalRecipe(int dmg) {
			color = CrystalElement.elements[dmg%16];
			boosted = dmg >= 16;
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
			return ChromaItems.SHARD.getStackOfMetadata(boosted ? 16+color.ordinal() : color.ordinal());
		}

		public Potion getOutputPotion() {
			return Potion.potionTypes[CrystalPotionController.getEffectFromColor(color, 20, 0).getPotionID()];
		}

		@Override
		public List<PositionedStack> getOtherStacks()
		{
			ItemStack in = new ItemStack(Items.potionitem); //water bottle
			if (CrystalPotionController.isPotionModifier(color)) {
				ArrayList<ItemStack> li = ReikaPotionHelper.getBasePotionItems();
				int tick = (int)((System.currentTimeMillis()/1000)%li.size());
				in = li.get(tick);
			}
			ItemStack out = TileEntityCrystalBrewer.getPotionStackFromColor(in.getItemDamage(), color, boosted);
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			stacks.add(new PositionedStack(in, 51, 35));
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
			for (int i = 0; i < 32; i++) {
				CrystalElement color = CrystalElement.elements[i%16];
				ItemStack out = TileEntityCrystalBrewer.getPotionStackFromColor(result.getItemDamage(), color, i >= 16);
				if (ReikaItemHelper.matchStacks(result, out)) {
					arecipes.add(new CrystalRecipe(i));
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (ingredient.getItem() == ChromaItems.SHARD.getItemInstance()) {
			arecipes.add(new CrystalRecipe(ingredient.getItemDamage()));
		}
		else if (ingredient.getItem() == Items.potionitem) {
			int dmg = ingredient.getItemDamage();
			if (dmg == 0) {
				for (int i = 0; i < 32; i++) {
					CrystalElement color = CrystalElement.elements[i%16];
					if (!CrystalPotionController.isPotionModifier(color))
						arecipes.add(new CrystalRecipe(i));
				}
			}
			else if (ReikaPotionHelper.getPotionValues().values().contains(dmg)) {
				for (int i = 0; i < 32; i++) {
					CrystalElement color = CrystalElement.elements[i%16];
					if (CrystalPotionController.isPotionModifier(color))
						arecipes.add(new CrystalRecipe(i));
				}
			}
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiCrystalBrewer.class;
	}

}
