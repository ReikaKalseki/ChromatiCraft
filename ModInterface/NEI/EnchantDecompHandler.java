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
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Container.ContainerEnchantDecomposer;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiEnchantDecomposer;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityEnchantDecomposer;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class EnchantDecompHandler extends TemplateRecipeHandler {

	private class EnchantDecomposerRecipe extends CachedRecipe {

		private final ItemStack input;
		private final int output;
		private final ElementTagCompound cost;

		public EnchantDecomposerRecipe(ItemStack in) {
			input = in;
			output = TileEntityEnchantDecomposer.getChromaValue(in);
			cost = TileEntityEnchantDecomposer.getTags();
		}

		@Override
		public PositionedStack getResult() {
			return null;
		}

		@Override
		public PositionedStack getIngredient()
		{
			return new PositionedStack(input, 66, 19);
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
		return "Enchantment Decomposition";
	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ChromatiCraft/Textures/GUIs/enchantdecomp.png";
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
		if (!(Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerEnchantDecomposer))
			transferRects.add(new RecipeTransferRect(new Rectangle(100, 13, 24, 29), "ccenchantdecomp"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		boolean flag = false;
		if (outputId != null && outputId.equals("ccenchantdecomp")) {
			flag = true;
		}
		else if (outputId != null && outputId.equals("liquid")) {
			FluidStack fs = (FluidStack)results[0];
			flag |= fs != null && fs.getFluid() == ChromatiCraft.chroma;
		}
		if (flag) {
			for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
				Enchantment e = Enchantment.enchantmentsList[i];
				if (e != null) {
					ItemStack is = ReikaEnchantmentHelper.getBasicItemForEnchant(e);
					for (int k = 1; k <= e.getMaxLevel(); k++) {
						ItemStack is2 = is.copy();
						is2.addEnchantment(e, k);
						arecipes.add(new EnchantDecomposerRecipe(is2));
					}
				}
			}
		}
		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId != null && inputId.equals("ccenchantdecomp")) {
			this.loadCraftingRecipes(inputId, ingredients);
		}
		super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(result);
		if (fs != null && fs.getFluid() == FluidRegistry.getFluid("chroma"))
			this.loadCraftingRecipes("ccenchantdecomp", result);
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (TileEntityEnchantDecomposer.isItemValid(ingredient)) {
			arecipes.add(new EnchantDecomposerRecipe(ingredient));
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiEnchantDecomposer.class;
	}

	@Override
	public int recipiesPerPage() {
		return 2;
	}

	@Override
	public void drawExtras(int recipe) {
		EnchantDecomposerRecipe fr = (EnchantDecomposerRecipe)arecipes.get(recipe);
		ElementTagCompound tag = fr.cost;
		int dy = 15;
		int dx = 14;
		int max = tag.getMaximumValue();
		int hmax = 52;
		for (CrystalElement e : tag.elementSet()) {
			int val = tag.getValue(e);
			String s = String.valueOf(val);//String.format("%s: %d", e.displayName, val);

			int h = val*hmax/max;
			int yh = 55-h+1;
			Gui.drawRect(dx, yh, dx+10, yh+h, e.getColor() | 0xff000000);

			dx += 18;
		}

		int h = (int)(48*Math.log(fr.output)/Math.log(6000));
		ReikaTextureHelper.bindTerrainTexture();
		GL11.glColor4f(1, 1, 1, 1);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(120, 51-h, ChromatiCraft.chroma.getIcon(), 32, h);
		FontRenderer f = ChromaFontRenderer.FontType.GUI.renderer;
		String s = String.valueOf(fr.output)+"mB";
		f.drawString(s, 136-f.getStringWidth(s)/2, 44, 0x000000);
	}

}
