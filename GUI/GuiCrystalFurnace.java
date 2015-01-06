/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerCrystalFurnace;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityCrystalFurnace;

public class GuiCrystalFurnace extends GuiChromaBase {

	private final TileEntityCrystalFurnace tile;

	public GuiCrystalFurnace(EntityPlayer ep, TileEntityCrystalFurnace te) {
		super(new ContainerCrystalFurnace(ep, te), ep, te);
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
		for (CrystalElement e : tile.smeltTags().elementSet()) {
			int max = tile.getMaxStorage(e);
			int dx = 10;
			int x1 = j+i*13+dx;
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

		int i1 = tile.getCookProgressScaled(24);
		this.drawTexturedModalRect(j + 79, k + 34, 176, 14, i1 + 1, 16);

		GL11.glColor4f(1, 1, 1, 1);
		int i = 0;
		for (CrystalElement e : tile.smeltTags().elementSet()) {
			int px = tile.getEnergyScaled(e, 52);
			int dy = 68;
			int dx = 11;
			int x1 = i*13+dx;
			//api.fillBar(j+x1, k+dy-33, 18, dy, e.color.color, px, 34, false);
			api.drawRect(j+x1, k+dy-px+1, 10, px, e.getColor(), true);
			i++;
		}
	}

	@Override
	public String getGuiTexture() {
		return "furnace";
	}

}
