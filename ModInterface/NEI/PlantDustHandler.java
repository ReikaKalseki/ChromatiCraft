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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PlantDropManager;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class PlantDustHandler extends TemplateRecipeHandler {

	private class PlantDustRecipe extends CachedRecipe {

		private final ArrayList<BlockKey> source;
		private final ItemStack item;
		private final int index;

		public PlantDustRecipe(Collection<BlockKey> plants, ItemStack out) {
			item = ReikaItemHelper.getSizedItemStack(out, 1);
			source = new ArrayList(plants);
			index = arecipes.size();
		}

		@Override
		public PositionedStack getResult() {
			return new PositionedStack(item, 117, 12-this.yOffset());
		}

		private int yOffset() {
			return (index%PlantDustHandler.this.recipiesPerPage())*24;
		}

		@Override
		public PositionedStack getIngredient()
		{
			return new PositionedStack(source.get(this.getIndex()).asItemStack(), 32, 12-this.yOffset());
		}

		private int getIndex() {
			return (int)((System.currentTimeMillis()/1000)%source.size());
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
		return "Chromaflora";
	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ChromatiCraft/Textures/GUIs/plantdust.png";
	}

	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1, 1, 1, 1);
		int n = recipe%this.recipiesPerPage();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(8, 0-n*24, 5, 11, 166, 40, ReikaGuiAPI.NEI_DEPTH);
	}

	@Override
	public void drawForeground(int recipe) {
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
		//this.drawExtras(recipe);
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(57, 10, 51, 20), "ccplants"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId != null && outputId.equals("ccplants")) {
			Collection<ItemStack> li = PlantDropManager.instance.getAllDrops();
			for (ItemStack is : li) {
				Collection<BlockKey> c = PlantDropManager.instance.getPlantForDrops(is, Minecraft.getMinecraft().thePlayer);
				if (c != null && !c.isEmpty())
					arecipes.add(new PlantDustRecipe(c, is));
			}
		}
		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId != null && inputId.equals("ccplants")) {
			this.loadCraftingRecipes(inputId, ingredients);
		}
		super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		Collection<BlockKey> plants = PlantDropManager.instance.getPlantForDrops(result, Minecraft.getMinecraft().thePlayer);
		if (plants != null && !plants.isEmpty()) {
			arecipes.add(new PlantDustRecipe(plants, result));
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
		return 3;
	}
	/*
	@Override
	public void drawExtras(int recipe) {
		FabricationRecipe fr = (FabricationRecipe)arecipes.get(recipe);
		Minecraft.getMinecraft().fontRenderer.drawString(fr.item.getDisplayName(), 46, 9, 0xffffff);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int dx = 3+(i/8)*84;
			int dy = 28+(i%8)*18;
			IIcon ico = e.getGlowRune();
			GL11.glColor4f(1, 1, 1, 1);
			ReikaTextureHelper.bindTerrainTexture();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(dx, dy, ico, 16, 16);
			int c = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.75F);
			Minecraft.getMinecraft().fontRenderer.drawString(String.valueOf(fr.cost.getValue(e)), dx+24, dy+4, c);
		}

	}
	 */
}
