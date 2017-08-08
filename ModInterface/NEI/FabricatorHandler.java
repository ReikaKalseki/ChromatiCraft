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
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Container.ContainerItemFabricator;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class FabricatorHandler extends TemplateRecipeHandler {

	private class FabricationRecipe extends CachedRecipe {

		private final ItemStack item;
		private final ElementTagCompound cost;

		public FabricationRecipe(ItemStack out, ElementTagCompound tag) {
			item = ReikaItemHelper.getSizedItemStack(out, 1);
			cost = tag;
		}

		@Override
		public PositionedStack getResult() {
			return new PositionedStack(item, 22, 5);
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
			for (KeyedItemStack is : li)
				arecipes.add(new FabricationRecipe(is.getItemStack(), FabricationRecipes.recipes().getItemCost(is.getItemStack())));
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
		return null;//GuiItemFabricator.class;
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

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

}
