/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiDescription;
import Reika.ChromatiCraft.Block.BlockHeatLamp.HeatLampEffect;
import Reika.ChromatiCraft.Block.BlockHeatLamp.TileEntityHeatLamp;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies;

import forestry.api.apiculture.EnumBeeType;

public class GuiCraftableDesc extends GuiDescription {

	public GuiCraftableDesc(EntityPlayer ep, ChromaResearch r) {
		super(ChromaGuis.BASICDESC, ep, r, 256, 220);
	}

	@Override
	protected final int getMaxSubpage() {
		if (page == ChromaResearch.BEES) {
			return CrystalBees.beeCount();
		}
		else if (page == ChromaResearch.HEATLAMP) {
			return 2;
		}
		return 0;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		if (page.getParent() == ChromaResearch.BLOCKS) {
			this.renderBlock(posX, posY);
		}
		if (page.getParent() == ChromaResearch.RESOURCEDESC) {
			GL11.glPushMatrix();
			double s = 4;
			GL11.glScaled(s, s, 1);
			GL11.glTranslated(33, 3, 0);
			ItemStack is = null;//page.getTabIcon();
			ArrayList<ItemStack> li = page.getItemStacks();
			if (li != null && !li.isEmpty()) {
				int tick = (int)((System.currentTimeMillis()/1000)%li.size());
				is = li.get(tick);
			}
			if (page == ChromaResearch.BEES && subpage > 0) {
				BeeSpecies bs = CrystalBees.getBeeByIndex(subpage-1);
				int idx = (int)((System.currentTimeMillis()/1000)%3);
				is = bs.getBeeItem(Minecraft.getMinecraft().theWorld, EnumBeeType.VALUES[idx]);
			}
			if (is != null)
				api.drawItemStack(itemRender, is, (int)(posX/s), (int)(posY/s));
			GL11.glPopMatrix();
		}
		if (page == ChromaResearch.HEATLAMP && subpage > 0) {
			Collection<HeatLampEffect> li = TileEntityHeatLamp.getEffects(subpage == 2);
			int oy = posY+107;
			int dy = 0;
			for (HeatLampEffect s : li) {
				List<GuiItemDisplay> items = s.isActive() ? s.getRelevantItems() : null;
				if (items != null && !items.isEmpty()) {
					int ox = posX+12;
					int dx = 0;
					for (GuiItemDisplay g : items) {
						int dx2 = dx+ox;
						int dy2 = dy+oy-textOffset*17;
						if (dx2 >= 0 && dy2 >= oy && dy2 <= oy+90) {
							g.draw(fontRendererObj, dx2, dy2);
							if (api.isMouseInBox(dx2, dx2+16, dy2, dy2+16)) {
								String sg = s.getDescription();
								api.drawTooltipAt(fontRendererObj, sg, api.getMouseRealX()+fontRendererObj.getStringWidth(sg)+22, api.getMouseRealY()+15);
							}
						}
						dx += 18;
						if (dx >= 220) {
							dx = 0;
							dy += 17;
						}
					}
					dy += 22;
				}
			}
		}
	}

	@Override
	protected boolean hasScroll() {
		return super.hasScroll() || (page == ChromaResearch.HEATLAMP && subpage > 0);
	}

	@Override
	protected int getMaxScroll() {
		return page == ChromaResearch.HEATLAMP && subpage > 0 ? 50 : super.getMaxScroll();
	}

	private void renderBlock(int posX, int posY) {
		if (page == ChromaResearch.WARPNODE) {
			GL11.glPushMatrix();
			double s = 4;
			GL11.glScaled(s, s, 1);
			GL11.glTranslated(-50, -8, 0);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/warpnode-small.png");
			int idx = (int)(System.currentTimeMillis()/20%64);
			double u = idx%8/8D;
			double v = idx/8/8D;
			double du = u+1/8D;
			double dv = v+1/8D;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			int d = 2;
			int w = 16;
			int h = 16;
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.addVertexWithUV((posX + 0 - d), (posY + h + d), 0, u, dv);
			v5.addVertexWithUV((posX + w + d), (posY + h + d), 0, du, dv);
			v5.addVertexWithUV((posX + w + d), (posY + 0 - d), 0, du, v);
			v5.addVertexWithUV((posX + 0 - d), (posY + 0 - d), 0, u, v);
			v5.draw();
			GL11.glPopAttrib();
			GL11.glPopMatrix();
			return;
		}
		else {
			int mod = 2000;
			int metas = page.getBlock().getNumberMetadatas();
			int meta = (int)((System.currentTimeMillis()/mod)%metas);
			if (page == ChromaResearch.HEATLAMP) {
				switch(subpage) {
					case 0:
						meta = (int)(((System.currentTimeMillis()/mod)%2)*8);
						break;
					case 1:
						meta = 0;
						break;
					case 2:
						meta = 8;
						break;
				}
			}
			GuiMachineDescription.runningRender = true;
			this.drawBlockRender(posX, posY, page.getBlock().getBlockInstance(), meta);
			GuiMachineDescription.runningRender = false;
		}
	}
}
