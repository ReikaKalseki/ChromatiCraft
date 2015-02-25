/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.NEI;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.GUI.GuiItemFabricator;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class FabricatorHandler extends TemplateRecipeHandler {

	private class FabricationRecipe extends CachedRecipe {

		private final ItemStack item;
		private final ElementTagCompound cost;

		public FabricationRecipe(ItemStack out, ElementTagCompound tag) {
			item = out;
			cost = tag;
		}

		@Override
		public PositionedStack getResult() {
			return new PositionedStack(item, 131, 24);
		}

		@Override
		public PositionedStack getIngredient()
		{
			return null;//new PositionedStack(this.getInputShard(), 74, 6);
		}

		@Override
		public List<PositionedStack> getOtherStacks()
		{
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			return stacks;
		}
	}

	@Override
	public String getRecipeName() {
		return "Item Fabrication";
	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ChromatiCraft/Textures/GUIs/fabricator.png";
	}

	@Override
	public void drawBackground(int recipe)
	{
		GL11.glColor4f(1, 1, 1, 1);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(0, 0, 5, 11, 166, 70, ReikaGuiAPI.NEI_DEPTH);
	}

	@Override
	public void drawForeground(int recipe)
	{
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
		this.drawExtras(recipe);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		ElementTagCompound tag = FabricationRecipes.recipes().getItemCost(result);
		if (tag != null) {
			arecipes.add(new FabricationRecipe(result, tag));
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiItemFabricator.class;
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public void drawExtras(int recipe)
	{

	}

}
