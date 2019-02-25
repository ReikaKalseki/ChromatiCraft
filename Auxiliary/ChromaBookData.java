/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.PoolRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.RuneShapeRenderer;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

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
					fr.drawString(c.getOutputForDisplay().getDisplayName(), posX+6, posY-2, 0);
				}
			}
		}
	}

	public static void drawCastingRecipe(FontRenderer fr, RenderItem ri, CastingRecipe c, int subpage, int posX, int posY) {
		ItemStack isout = c.getOutputForDisplay();
		ItemStack ctr = c instanceof MultiBlockCastingRecipe ? ((MultiBlockCastingRecipe)c).getMainInput() : c.getArrayForDisplay()[4];
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		if (ctr != null && isout.stackTagCompound != null)
			ReikaNBTHelper.combineNBT(isout.stackTagCompound, c.getOutputTag(Minecraft.getMinecraft().thePlayer, ctr.stackTagCompound));
		gui.drawItemStack(ri, fr, isout, posX+7, posY+5);
		GL11.glDisable(GL11.GL_LIGHTING);
		if (subpage == 0 || subpage == 2) {
			ItemStack[] arr = c.getArrayForDisplay();
			for (int i = 0; i < 9; i++) {
				ItemStack in = arr[i];
				if (in != null) {
					if (i == 4 && c instanceof MultiBlockCastingRecipe)
						in = ReikaItemHelper.getSizedItemStack(in, ((MultiBlockCastingRecipe)c).getRequiredCentralItemCount());
					int x = subpage == 0 ? 54 : 102;
					int y = subpage == 0 ? 10 : 76;
					int dx = x+posX+i%3*18;
					int dy = y+posY+i/3*18;
					//FontRenderer fr2 = in.getDisplayName().contains(FontType.OBFUSCATED.id) ? FontType.OBFUSCATED.renderer : fr;
					gui.drawItemStackWithTooltip(ri, fr, in, dx, dy);
				}
			}
		}
		GL11.glPopAttrib();
		GL11.glColor4f(1, 1, 1, 1);
		if (subpage == 1) {
			RuneShapeRenderer.instance.render(((TempleCastingRecipe)c).getRunes(), posX+128, posY+110);
		}
		if (subpage == 2) {
			Map<List<Integer>, ItemMatch> items = ((MultiBlockCastingRecipe)c).getAuxItems();
			for (List<Integer> key : items.keySet()) {
				int i = key.get(0);
				int k = key.get(1);
				int sx = i == 0 ? 0 : i < 0 ? -1 : 1;
				int sy = k == 0 ? 0 : k < 0 ? -1 : 1;
				int tx = Math.abs(i) == 2 ? 38 : 64;
				int ty = Math.abs(k) == 2 ? 38 : 63;
				int dx = posX+120+sx*tx;
				int dy = posY+94+sy*ty;
				ItemStack out = items.get(key).getCycledItem();
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
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		if (subpage == 3) {
			ElementTagCompound tag = ((PylonCastingRecipe)c).getRequiredAura();
			int max = tag.getMaximumValue();
			for (CrystalElement e : tag.elementSet()) {
				int color = e.getColor();
				//int dx = posX+24*e.ordinal();
				//int dy = posY+20;

				int energy = (int)((System.currentTimeMillis())%max);
				int w = 10;
				int h2 = 48;
				int x = posX+7+e.ordinal()*(w+4);
				//int ht = energy*h2/tag.getValue(e);
				//int dy = Math.max(h2-ht, 0); //prevent gui overflow
				int y1 = posY+26;
				//int y = posY+26+h2;
				//gui.drawRect(x, y1, x+w, y, e.getJavaColor().darker().darker().getRGB());
				//gui.drawRect(x, y1+dy, x+w, y, e.getColor());

				ChromaFX.drawFillBar(e, x, y1, w, h2, energy/(float)max);
			}

			/*
			if (tag.tagCount() <= 8) {
				int r = 40;
				int dx = posX+192;
				int dy = posY+140;
				p.renderAsPie(dx, dy, r, 0, colors);
				ReikaGuiAPI.instance.drawCircle(dx, dy, r+0.25, 0x000000);
			}
			 */

			Proportionality<CrystalElement> p = tag.getProportionality();
			FontRenderer f = ChromaFontRenderer.FontType.LEXICON.renderer;

			int px = posX+8;
			int py = posY+88;

			double r = 57.5;
			int dx = px+184;
			int dy = py+56;
			double zang = System.identityHashCode(c);
			p.renderAsPie(dx, dy, r, zang, CrystalElement.getColorMap());

			ReikaTextureHelper.bindTerrainTexture();
			double ba = zang;
			double ir = r*0.625;
			int si = 8;
			for (CrystalElement e : p.getElements()) {
				double ang = 360D*p.getFraction(e);
				double a = ba+ang/2D;
				int ix = (int)Math.round(dx+ir*Math.cos(Math.toRadians(a)));
				int iy = (int)Math.round(dy+ir*Math.sin(Math.toRadians(a)));
				if (p.getElements().size() == 1) {
					ix = dx;
					iy = dy;
				}
				ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(ix-si/2, iy-si/2, e.getOutlineRune(), si, si);
				ba += ang;
			}

			//ReikaGuiAPI.instance.drawCircle(dx, dy, r+0.25, 0x000000);
			int tot = tag.getTotalEnergy();
			int h = f.FONT_HEIGHT*3/2;
			f.drawSplitString("Total Energy:", px+3+40, py-5+3, 80, 0xffffff);
			f.drawSplitString(String.valueOf(tot), px+3+40, py-5+3+h, 80, 0xffffff);
			f.drawSplitString("Lumens", px+3+40, py-5+3+h*2, 80, 0xffffff);

			int frac = (int)(117*Math.pow((double)tot/RecipesCastingTable.instance.getMaxRecipeTotalEnergyCost(), 0.5));

			ReikaTextureHelper.bindTerrainTexture();
			BlendMode.ADDITIVEDARK.apply();
			for (int i = 0; i < 4; i++)
				ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(px+11, py+108-frac+3, ChromaIcons.RIFT.getIcon(), 16, frac+6);
			BlendMode.DEFAULT.apply();

			String s = "Textures/GUIs/Handbook/misc.png";
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
			ReikaGuiAPI.instance.drawTexturedModalRect(dx-(int)Math.ceil(r), dy-(int)Math.ceil(r), 0, 0, (int)(r*2), (int)(r*2));

			ReikaGuiAPI.instance.drawTexturedModalRect(px+1, py+108-frac, 0, 118, 36, 7);
		}
	}

	public static void drawCompressedCastingRecipe(FontRenderer fr, RenderItem ri, CastingRecipe c, int posX, int posY) {
		GL11.glDisable(GL11.GL_LIGHTING);

		ItemStack[] arr = c.getArrayForDisplay();
		for (int i = 0; i < 9; i++) {
			ItemStack in = arr[i];
			if (in != null) {
				if (i == 4 && c instanceof MultiBlockCastingRecipe)
					in = ReikaItemHelper.getSizedItemStack(in, ((MultiBlockCastingRecipe)c).getRequiredCentralItemCount());
				int x = 47;
				int y = 96;
				int dx = x+posX+i%3*18;
				int dy = y+posY+i/3*18;
				//FontRenderer fr2 = in.getDisplayName().contains(FontType.OBFUSCATED.id) ? FontType.OBFUSCATED.renderer : fr;
				gui.drawItemStackWithTooltip(ri, fr, in, dx, dy);
			}
		}

		GL11.glColor4f(1, 1, 1, 1);

		if (c instanceof MultiBlockCastingRecipe) {
			Map<List<Integer>, ItemMatch> items = ((MultiBlockCastingRecipe)c).getAuxItems();
			for (List<Integer> key : items.keySet()) {
				int i = key.get(0);
				int k = key.get(1);
				int sx = i == 0 ? 0 : i < 0 ? -1 : 1;
				int sy = k == 0 ? 0 : k < 0 ? -1 : 1;
				int tx = Math.abs(i) == 2 ? 37 : 57;
				int ty = Math.abs(k) == 2 ? 37 : 57;
				int dx = posX+65+sx*tx;
				int dy = posY+114+sy*ty;
				ItemStack out = items.get(key).getCycledItem().copy();
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

		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		if (c instanceof PylonCastingRecipe) {
			ElementTagCompound tag = ((PylonCastingRecipe)c).getRequiredAura();
			int max = tag.getMaximumValue();
			for (CrystalElement e : tag.elementSet()) {
				int color = e.getColor();
				//int dx = posX+24*e.ordinal();
				//int dy = posY+20;

				int energy = (int)((System.currentTimeMillis())%max);
				int h = 3;
				int w = 30;
				int x = posX+7+(e.ordinal()/4)*(w+4);
				//int ht = Math.min(h2, energy*h2/tag.getValue(e)); //prevent gui overflow
				int y = posY+193+(e.ordinal()%4)*7;
				//int y1 = posY+26+h2;
				//gui.drawRect(x, y, x+h2, y+w, e.getJavaColor().darker().darker().getRGB());
				//gui.drawRect(x, y, x+ht, y+w, e.getColor());

				float f = energy/(float)tag.getValue(e);
				ChromaFX.drawHorizontalFillBar(e, x, y, w, h, f);
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
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		gui.drawTexturedModelRectFromIcon(posX+196, posY+81, ChromatiCraft.chroma.getIcon(), 36, 36);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
	}
}
