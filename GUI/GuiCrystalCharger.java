/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerCrystalCharger;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalCharger;

public class GuiCrystalCharger extends GuiChromaBase {

	private final TileEntityCrystalCharger tile;

	public GuiCrystalCharger(EntityPlayer ep, TileEntityCrystalCharger te) {
		super(new ContainerCrystalCharger(ep, te), ep, te);

		tile = te;
		ySize = 191;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int max = tile.getMaxStorage();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int mx = api.getMouseRealX();
		int my = api.getMouseRealY();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			int dx = i%8 >= 4 ? 22 : 0;
			int x1 = j+6+18*(i%8)+dx;
			int x2 = x1+16;
			int y1 = k+17+40*(i/8);
			int y2 = y1+33;
			if (api.isMouseInBox(x1, x2, y1, y2)) {
				CrystalElement e = CrystalElement.elements[i];
				int level = tile.getEnergy(e);
				String s = String.format("%s: %d/%d", e.displayName, level, max);
				api.drawTooltipAt(fontRendererObj, s, mx-32, my);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		GL11.glColor4f(1, 1, 1, 1);
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int px = tile.getEnergyScaled(e, 34);
			int dy = i < 8 ? 50 : 90;
			int dx = i%8 >= 4 ? 22 : 0;
			int x1 = 6+18*(i%8)+dx;
			//api.fillBar(j+x1, k+dy-33, 18, dy, e.color.color, px, 34, false);
			api.drawRect(j+x1, k+dy-px+1, 16, px, e.getColor(), true);
		}
	}

	@Override
	public String getGuiTexture() {
		return "charger";
	}

}
