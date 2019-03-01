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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes.FabricationRecipe;
import Reika.ChromatiCraft.Container.ContainerItemFabricator;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class FabricatorHandler extends TemplateRecipeHandler {

	private static final RenderItem itemRender = new RenderItem();

	private class FabricationRecipeDisplay extends CachedRecipe {

		private final FabricationRecipe recipe;

		public FabricationRecipeDisplay(FabricationRecipe r) {
			recipe = r;
		}

		@Override
		public PositionedStack getResult() {
			return new PositionedStack(recipe.getDisplay(), 22, 5);
		}

		@Override
		public PositionedStack getIngredient()
		{
			return null;
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
		return "/Reika/ChromatiCraft/Textures/GUIs/itemvalue.png";
	}

	@Override
	public void drawBackground(int recipe)
	{
		GL11.glColor4f(1, 1, 1, 1);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(0, 0, 5, 11, 166, 172, ReikaGuiAPI.NEI_DEPTH);
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
	public void loadTransferRects() {
		if (!(Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerItemFabricator))
			transferRects.add(new RecipeTransferRect(new Rectangle(32, -12, 105, 10), "ccfabric"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId != null && outputId.equals("ccfabric")) {
			Collection<KeyedItemStack> li = FabricationRecipes.recipes().getFabricableItems();
			for (KeyedItemStack is : li) {
				FabricationRecipe f = FabricationRecipes.recipes().getItemRecipe(is.getItemStack());
				if (f.hasProgress(Minecraft.getMinecraft().thePlayer))
					arecipes.add(new FabricationRecipeDisplay(f));
			}
		}
		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId != null && inputId.equals("ccfabric")) {
			this.loadCraftingRecipes(inputId, ingredients);
		}
		super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		FabricationRecipe tag = FabricationRecipes.recipes().getItemRecipe(result);
		if (tag != null && tag.hasProgress(Minecraft.getMinecraft().thePlayer)) {
			arecipes.add(new FabricationRecipeDisplay(tag));
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return null;//GuiItemFabricator.class;
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public void drawExtras(int recipe) {
		FontRenderer f = Minecraft.getMinecraft().fontRenderer;
		FabricationRecipeDisplay fr = (FabricationRecipeDisplay)arecipes.get(recipe);
		f.drawString(fr.recipe.getDisplay().getDisplayName(), 46, 9, 0xffffff);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int dx = 3+(i/8)*84;
			int dy = 28+(i%8)*18;
			IIcon ico = e.getGlowRune();
			GL11.glColor4f(1, 1, 1, 1);
			ReikaTextureHelper.bindTerrainTexture();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(dx, dy, ico, 16, 16);
			int c = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.75F);
			f.drawString(String.valueOf(fr.recipe.getCost().getValue(e)), dx+24, dy+4, c);
		}
		/*
		int dy = -2;
		String s = "Progress:";
		f.drawString(s, -f.getStringWidth(s)-8, dy+18, 0xffffff);
		for (ProgressStage p : fr.recipe.getProgress()) {
			dy += 30;
			p.renderIcon(itemRender, Minecraft.getMinecraft().fontRenderer, -25, dy);
		}*/

	}

}
