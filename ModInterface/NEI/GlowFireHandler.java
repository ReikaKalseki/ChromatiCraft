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
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityGlowFire;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class GlowFireHandler extends TemplateRecipeHandler {

	private class GlowFireRecipe extends CachedRecipe {

		private final ItemStack item;
		private final ElementTagCompound cost;
		private final boolean output;

		public GlowFireRecipe(ItemStack out, ElementTagCompound tag, boolean output) {
			item = ReikaItemHelper.getSizedItemStack(out, 1);
			cost = tag;
			this.output = output;
		}

		@Override
		public PositionedStack getResult() {
			return new PositionedStack(item, 73, 59);
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
		return "Lumen Transmutation";
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
		//ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(0, 0, 5, 11, 166, 172, ReikaGuiAPI.NEI_DEPTH);
		this.drawExtras(recipe);
	}

	@Override
	public void drawForeground(int recipe)
	{
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(32, -12, 105, 10), "ccglowfire"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId != null && outputId.equals("ccglowfire")) {
			Collection<KeyedItemStack> li = FabricationRecipes.recipes().getFabricableItems();
			for (KeyedItemStack is : li)
				arecipes.add(new GlowFireRecipe(is.getItemStack(), TileEntityGlowFire.getCost(is.getItemStack()), true));
		}
		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId != null && inputId.equals("ccglowfire")) {
			this.loadCraftingRecipes(inputId, ingredients);
		}
		super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		ElementTagCompound tag = TileEntityGlowFire.getCost(result);
		if (tag != null) {
			arecipes.add(new GlowFireRecipe(result, tag, false));
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		ElementTagCompound tag = TileEntityGlowFire.getDecompositionValue(ingredient);
		if (tag != null) {
			arecipes.add(new GlowFireRecipe(ingredient, tag, true));
		}
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
		GlowFireRecipe fr = (GlowFireRecipe)arecipes.get(recipe);
		if (fr == null)
			return;
		if (fr.cost == null) {
			throw new IllegalStateException(this.getRecipeName()+" recipe "+fr.item+" has null cost?!");
		}
		//Minecraft.getMinecraft().fontRenderer.drawString(fr.item.getDisplayName(), 46, 9, 0xffffff);
		int r = 60-6;
		int ox = 81;
		int oy = 70-2;
		int ir = 12;
		int o = 2;
		double max = Math.log(fr.cost.getMaximumValue()+o);

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		/*
		Tessellator.instance.startDrawing(GL11.GL_TRIANGLE_FAN);
		Tessellator.instance.setColorOpaque_I(/*!fr.output ? *//*0x303030/* : 0xc0c0c0*//*);
		Tessellator.instance.addVertex(ox, oy, 0);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[15-i];
			double val = Math.log(fr.cost.getValue(e)+o);
			double a = Math.toRadians(e.ordinal()*22.5-90);
			int dx = ox+(int)((r+2)*Math.cos(a));
			int dy = oy+(int)((r+2)*Math.sin(a));
			if (i == 0) {
				Tessellator.instance.addVertex(ox, oy-r-2, 0);
			}
			Tessellator.instance.addVertex(dx, dy, 0);
			//IIcon ico = e.getGlowRune();
			//ReikaTextureHelper.bindTerrainTexture();
			//ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(dx, dy, ico, 8, 8);
			//int c = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.75F);
			//Minecraft.getMinecraft().fontRenderer.drawString(String.valueOf(val), dx+10, dy+0, c);
		}
		Tessellator.instance.draw();
		 */

		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setColorOpaque_I(0x303030);
		Tessellator.instance.addVertex(-2, 133, 0);
		Tessellator.instance.addVertex(168, 133, 0);
		Tessellator.instance.addVertex(168, 0, 0);
		Tessellator.instance.addVertex(-2, 0, 0);
		Tessellator.instance.draw();

		Tessellator.instance.startDrawing(GL11.GL_LINES);
		Tessellator.instance.setColorOpaque_I(/*!fr.output ? */0x535353/* : 0x303030*/);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			double a = Math.toRadians(e.ordinal()*22.5-90);
			int dx = ox+(int)(r*Math.cos(a));
			int dy = oy+(int)(r*Math.sin(a));
			int dx2 = ox+(int)(r*Math.cos(a+Math.toRadians(22.5)));
			int dy2 = oy+(int)(r*Math.sin(a+Math.toRadians(22.5)));
			Tessellator.instance.addVertex(ox, oy, 0);
			Tessellator.instance.addVertex(dx, dy, 0);

			Tessellator.instance.addVertex(dx, dy, 0);
			Tessellator.instance.addVertex(dx2, dy2, 0);
		}
		Tessellator.instance.draw();

		GL11.glColor4f(1, 1, 1, 1);
		int er2 = Math.max(ir, (int)(r*Math.log(fr.cost.getValue(CrystalElement.BLACK)+o)/max));
		Tessellator.instance.startDrawing(GL11.GL_TRIANGLE_FAN);
		Tessellator.instance.setColorOpaque_I(!fr.output ? 0xffffff : 0x202020);
		Tessellator.instance.addVertex(ox, oy, 0);
		Tessellator.instance.setColorOpaque_I(fr.output ? 0xffffff : 0x6a6a6a);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[15-i];
			double val = Math.log(fr.cost.getValue(e)+o);
			double a = Math.toRadians(e.ordinal()*22.5-90);
			int er = Math.max(ir, (int)(r*val/max));
			int dx = ox+(int)(er*Math.cos(a));
			int dy = oy+(int)(er*Math.sin(a));
			Tessellator.instance.setBrightness(240);
			if (i == 0) {
				Tessellator.instance.addVertex(ox, oy-er2, 0);
			}
			Tessellator.instance.addVertex(dx, dy, 0);
			//IIcon ico = e.getGlowRune();
			//ReikaTextureHelper.bindTerrainTexture();
			//ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(dx, dy, ico, 8, 8);
			//int c = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.75F);
			//Minecraft.getMinecraft().fontRenderer.drawString(String.valueOf(val), dx+10, dy+0, c);
		}
		Tessellator.instance.draw();

		Tessellator.instance.startDrawing(GL11.GL_LINE_LOOP);
		Tessellator.instance.setColorOpaque_I(0);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			double val = Math.log(fr.cost.getValue(e)+o);
			//ReikaJavaLibrary.pConsole(e+":"+val+"/"+max);
			double a = Math.toRadians(e.ordinal()*22.5-90);
			int er = Math.max(ir, (int)(r*val/max));
			int dx = ox+(int)(er*Math.cos(a));
			int dy = oy+(int)(er*Math.sin(a));
			Tessellator.instance.setColorOpaque_I(e.getColor());
			Tessellator.instance.addVertex(dx, dy, 0);
			//IIcon ico = e.getGlowRune();
			//GL11.glColor4f(1, 1, 1, 1);
			//ReikaTextureHelper.bindTerrainTexture();
			//ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(dx, dy, ico, 8, 8);
			//int c = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.75F);
			//Minecraft.getMinecraft().fontRenderer.drawString(String.valueOf(val), dx+10, dy+0, c);
		}
		Tessellator.instance.draw();

		GL11.glPopAttrib();

		int r2 = r+6;
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			double val = Math.log(fr.cost.getValue(e)+o);
			double a = Math.toRadians(e.ordinal()*22.5-90);
			int dx = ox+(int)(r2*Math.cos(a))-4;
			int dy = oy+(int)(r2*Math.sin(a))-4;
			Tessellator.instance.setColorOpaque_I(e.getColor());
			Tessellator.instance.addVertex(dx, dy, 0);
			IIcon ico = e.getGlowRune();
			GL11.glColor4f(1, 1, 1, 1);
			ReikaTextureHelper.bindTerrainTexture();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(dx, dy, ico, 8, 8);

			if (fr.cost.getValue(e) > 0) {
				int c = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.5F);
				String s = String.valueOf(fr.cost.getValue(e));
				int tx = dx > ox ? dx+9 : dx-Minecraft.getMinecraft().fontRenderer.getStringWidth(s);
				int ty = dy+1;
				Minecraft.getMinecraft().fontRenderer.drawString(s, tx, ty, c);
			}
		}
	}

}
