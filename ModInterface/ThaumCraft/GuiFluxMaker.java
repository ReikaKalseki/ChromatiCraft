/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class GuiFluxMaker extends GuiChromaBase {

	private final TileEntityFluxMaker tile;

	public GuiFluxMaker(EntityPlayer ep, TileEntityFluxMaker te) {
		super(new ContainerFluxMaker(ep, te), ep, te);
		tile = te;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int mx = api.getMouseRealX();
		int my = api.getMouseRealY();
		int i = 0;
		for (CrystalElement e : tile.getTags().elementSet()) {
			int max = tile.getMaxStorage(e);
			int dx = 17;
			int x1 = j+i*18+dx;
			int x2 = x1+11;
			int y1 = k+16;
			int y2 = y1+53;
			if (api.isMouseInBox(x1, x2, y1, y2)) {
				int level = tile.getEnergy(e);
				String s = String.format("%s: %d/%d", e.displayName, level, max);
				api.drawTooltipAt(fontRendererObj, s, mx-32, my);
			}
			i++;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		int i1 = tile.getCookProgressScaled(255);
		GL11.glColor4f(1, 1, 1, i1/255F);
		this.drawTexturedModalRect(j + 62, k + 21, 180, 47, 45, 42);
		this.drawTexturedModalRect(j + 107, k + 14, 179, 99, 52, 56);

		GL11.glColor4f(1, 1, 1, 1);
		int i = 0;
		for (CrystalElement e : tile.getTags().elementSet()) {
			int px = tile.getEnergyScaled(e, 56);
			int dy = 70;
			int dx = 19;
			int x1 = i*18+dx;
			//api.fillBar(j+x1, k+dy-33, 18, dy, e.color.color, px, 34, false);
			drawRect(j+x1, k+dy-px+1, j+x1+10, k+dy+1, e.getColor());
			i++;
		}
	}

	@Override
	public String getGuiTexture() {
		return "fluxmaker";
	}

}
