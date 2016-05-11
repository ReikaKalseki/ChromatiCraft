/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.NEI;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Container.ContainerCrystalFurnace;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiCrystalFurnace;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityCrystalFurnace;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class CrystalFurnaceHandler extends TemplateRecipeHandler {

	private class CrystalFurnaceRecipe extends CachedRecipe {

		private final ItemStack input;
		private final ItemStack output;
		private final ElementTagCompound cost;

		public CrystalFurnaceRecipe(ItemStack in, ItemStack out) {
			in = ReikaItemHelper.getSizedItemStack(in, 1);
			input = in;
			output = ReikaItemHelper.getSizedItemStack(out, TileEntityCrystalFurnace.getMultiplyRate(in, out)*out.stackSize);
			cost = TileEntityCrystalFurnace.getSmeltingCost(in, out);
		}

		@Override
		public PositionedStack getResult() {
			return new PositionedStack(output, 110, 20);
		}

		@Override
		public PositionedStack getIngredient()
		{
			return new PositionedStack(input, 51, 20);
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
		return "Crystal Smelting";
	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ChromatiCraft/Textures/GUIs/furnace.png";
	}

	@Override
	public void drawBackground(int recipe)
	{
		GL11.glColor4f(1, 1, 1, 1);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(0, 0, 5, 15, 166, 56, ReikaGuiAPI.NEI_DEPTH);
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
		if (!(Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerCrystalFurnace))
			transferRects.add(new RecipeTransferRect(new Rectangle(70, 13, 34, 29), "ccfurnace"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId != null && outputId.equals("ccfurnace")) {
			Map<ItemStack, ItemStack> map = FurnaceRecipes.smelting().getSmeltingList();
			for (ItemStack in : map.keySet())
				arecipes.add(new CrystalFurnaceRecipe(in, map.get(in)));
		}
		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId != null && inputId.equals("ccfurnace")) {
			this.loadCraftingRecipes(inputId, ingredients);
		}
		super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		Map<ItemStack, ItemStack> map = FurnaceRecipes.smelting().getSmeltingList();
		for (Entry<ItemStack, ItemStack> e : map.entrySet()) {
			if (ReikaItemHelper.matchStacks(result, e.getValue())) {
				arecipes.add(new CrystalFurnaceRecipe(e.getKey(), e.getValue()));
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		ItemStack out = FurnaceRecipes.smelting().getSmeltingResult(ingredient);
		if (out != null) {
			arecipes.add(new CrystalFurnaceRecipe(ingredient, out));
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiCrystalFurnace.class;
	}

	@Override
	public int recipiesPerPage() {
		return 2;
	}

	@Override
	public void drawExtras(int recipe) {
		CrystalFurnaceRecipe fr = (CrystalFurnaceRecipe)arecipes.get(recipe);
		ElementTagCompound tag = fr.cost;
		int dy = 13;
		int dx = 2;
		int max = tag.getMaximumValue();
		int hmax = 52;
		for (CrystalElement e : tag.elementSet()) {
			int val = tag.getValue(e);
			String s = String.valueOf(val);//String.format("%s: %d", e.displayName, val);
			ChromaFontRenderer.FontType.GUI.drawString(s, 137, dy, e.getColor());
			dy += ChromaFontRenderer.FontType.GUI.renderer.FONT_HEIGHT+2;

			int h = val*hmax/max;
			int yh = 53-h+1;
			Gui.drawRect(dx, yh, dx+10, yh+h, e.getColor() | 0xff000000);

			dx += 15;
		}
	}

}
