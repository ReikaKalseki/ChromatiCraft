/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.ArrayMap;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;

public class ChromaBookData {

	private static final ArrayMap<ChromaResearch> tabMappings = new ArrayMap(2);
	private static final ReikaGuiAPI gui = ReikaGuiAPI.instance;

	private static void mapHandbook() {
		for (int i = 0; i < ChromaResearch.researchList.length; i++) {
			ChromaResearch h = ChromaResearch.researchList[i];
			tabMappings.putV(h, h.getScreen(), h.getPage());
		}
	}

	public static ChromaResearch getMapping(int screen, int page) {
		return tabMappings.getV(screen, page);
	}

	static {
		mapHandbook();
	}

	public static void drawPage(FontRenderer fr, RenderItem ri, int screen, int page, int subpage, int recipe, int posX, int posY) {
		drawPage(fr, ri, ChromaResearch.getEntry(screen, page), subpage, recipe, posX, posY);
	}

	public static void drawPage(FontRenderer fr, RenderItem ri, ChromaResearch h, int subpage, int recipe, int posX, int posY) {
		if (h.isCrafting()) {
			ArrayList<CastingRecipe> li = h.getCrafting();
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
		gui.drawItemStack(ri, fr, c.getOutput(), posX+7, posY+7);
		if (subpage == 0 || subpage == 2) {
			ItemStack[] arr = c.getArrayForDisplay();
			for (int i = 0; i < 9; i++) {
				ItemStack in = arr[i];
				if (in != null) {
					int x = subpage == 0 ? 54 : 102;
					int y = subpage == 0 ? 10 : 53;
					int dx = x+posX+i%3*18;
					int dy = y+posY+i/3*18;
					gui.drawItemStackWithTooltip(ri, fr, in, dx, dy);
				}
			}
		}
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
				int dx = 205+sx*(tx);
				int dy = 81+sy*(ty);
				ItemStack out = items.get(key);
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
}
