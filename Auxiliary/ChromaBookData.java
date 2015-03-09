/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Items.PoolRecipes.PoolRecipe;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ChromaBookData {

	private static final ReikaGuiAPI gui = ReikaGuiAPI.instance;

	private static final int[][] permuOffset = new int[5][5];

	public static void drawPage(FontRenderer fr, RenderItem ri, ChromaResearch h, int subpage, int recipe, int posX, int posY) {
		if (h.isCrafting()) {
			ArrayList<CastingRecipe> li = h.getCraftingRecipes();
			if (!li.isEmpty()) {
				CastingRecipe c = li.get(recipe);
				drawCastingRecipe(fr, ri, c, subpage, posX, posY);
				if (h.getTitle().isEmpty()) {
					fr.drawString(c.getOutput().getDisplayName(), posX+6, posY-2, 0);
				}
			}
		}
	}

	public static void drawCastingRecipe(FontRenderer fr, RenderItem ri, CastingRecipe c, int subpage, int posX, int posY) {
		gui.drawItemStack(ri, fr, c.getOutput(), posX+7, posY+5);
		if (subpage == 0 || subpage == 2) {
			ItemStack[] arr = c.getArrayForDisplay();
			for (int i = 0; i < 9; i++) {
				ItemStack in = arr[i];
				if (in != null) {
					int x = subpage == 0 ? 54 : 102;
					int y = subpage == 0 ? 10 : 76;
					int dx = x+posX+i%3*18;
					int dy = y+posY+i/3*18;
					//FontRenderer fr2 = in.getDisplayName().contains(FontType.OBFUSCATED.id) ? FontType.OBFUSCATED.renderer : fr;
					gui.drawItemStackWithTooltip(ri, fr, in, dx, dy);
				}
			}
		}
		GL11.glColor4f(1, 1, 1, 1);
		if (subpage == 1) {
			RuneShapeRenderer.instance.render(((TempleCastingRecipe)c).getRunes(), posX+128, posY+110);
		}
		if (subpage == 2) {
			Map<List<Integer>, ItemStack> items = ((MultiBlockCastingRecipe)c).getAuxItems();
			for (List<Integer> key : items.keySet()) {
				int i = key.get(0);
				int k = key.get(1);
				int sx = i == 0 ? 0 : i < 0 ? -1 : 1;
				int sy = k == 0 ? 0 : k < 0 ? -1 : 1;
				int tx = Math.abs(i) == 2 ? 38 : 64;
				int ty = Math.abs(k) == 2 ? 38 : 63;
				int dx = posX+120+sx*tx;
				int dy = posY+94+sy*ty;
				ItemStack out = items.get(key).copy();
				if (out.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					List<ItemStack> dmg = ReikaItemHelper.getAllMetadataPermutations(out.getItem());
					if (System.currentTimeMillis()%1000 == 0) {
						for (int f = 0; f < permuOffset.length; f++) {
							for (int g = 0; g < permuOffset[f].length; g++) {
								permuOffset[f][g] = ReikaRandomHelper.getRandomPlusMinus(0, 16);
							}
						}
					}
					out = dmg.get((int)((System.currentTimeMillis()/1000+permuOffset[i/2+2][k/2+2])%dmg.size()));
				}
				gui.drawItemStackWithTooltip(ri, fr, out, dx, dy);
			}
		}
		if (subpage == 3) {
			ElementTagCompound tag = ((PylonRecipe)c).getRequiredAura();
			int max = tag.getMaximumValue();
			for (CrystalElement e : tag.elementSet()) {
				int color = e.getColor();
				//int dx = posX+24*e.ordinal();
				//int dy = posY+20;

				int energy = (int)((System.currentTimeMillis())%max);
				int w = 10;
				int h2 = 48;
				int x = posX+7+e.ordinal()*(w+4);
				int ht = energy*h2/tag.getValue(e);
				int dy = Math.max(h2-ht, 0); //prevent gui overflow
				int y1 = posY+26;
				int y = posY+26+h2;
				gui.drawRect(x, y1, x+w, y, e.getJavaColor().darker().darker().getRGB());
				gui.drawRect(x, y1+dy, x+w, y, e.getColor());
			}
		}
	}

	public static void drawPoolRecipe(FontRenderer fr, RenderItem ri, PoolRecipe r, int subpage, int posX, int posY) {
		gui.drawItemStackWithTooltip(ri, fr, r.getOutput(), posX+120, posY+128);
		gui.drawItemStackWithTooltip(ri, fr, r.getMainInput(), posX+34, posY+91);
		int i = 0;
		for (ItemStack is : r.getInputs()) {
			int dx = posX+103+(i%3)*17;
			int dy = posY+29+(i/3)*17;
			gui.drawItemStackWithTooltip(ri, fr, is, dx, dy);
			i++;
		}
		ReikaTextureHelper.bindTerrainTexture();
		GL11.glDisable(GL11.GL_BLEND);
		gui.drawTexturedModelRectFromIcon(posX+196, posY+81, ChromatiCraft.chroma.getIcon(), 36, 36);
		GL11.glEnable(GL11.GL_BLEND);
	}
}
