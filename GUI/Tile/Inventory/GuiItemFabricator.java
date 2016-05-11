/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile.Inventory;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerItemFabricator;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityItemFabricator;

public class GuiItemFabricator extends GuiChromaBase {

	private final TileEntityItemFabricator tile;

	public GuiItemFabricator(EntityPlayer ep, TileEntityItemFabricator te) {
		super(new ContainerItemFabricator(ep, te), ep, te);
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
		ElementTagCompound tag = tile.getCurrentRequirements();
		if (tag != null) {
			for (CrystalElement e : tag.elementSet()) {
				int max = tag.getValue(e);
				int dx = j+10+e.ordinal()%4*14;
				int dy = k+16+e.ordinal()/4*14;
				if (api.isMouseInBox(dx, dx+12, dy, dy+12)) {
					int level = tile.getEnergy(e);
					String s = String.format("%s: %d/%d", e.displayName, level, max);
					api.drawTooltipAt(fontRendererObj, s, mx-32, my);
				}
				i++;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int i1 = tile.getProgressScaled(69);
		this.drawTexturedModalRect(j + 66, k + 16, 176, 31, i1 + 1, 54);

		GL11.glColor4f(1, 1, 1, 1);
		int i = 0;
		ElementTagCompound tag = tile.getCurrentRequirements();
		if (tag != null) {
			for (CrystalElement e : tag.elementSet()) {
				int px = Math.min(10*tile.getEnergy(e)/tag.getValue(e), 10);
				//ReikaJavaLibrary.pConsole(e+":"+px+" of "+tag+" by "+tile.getEnergy(e));
				int d = (10-px);
				int dx = 1+j+10+e.ordinal()%4*14;
				int dy = d+1+k+16+e.ordinal()/4*14;
				//api.fillBar(j+x1, k+dy-33, 18, dy, e.color.color, px, 34, false);
				api.drawRect(dx, dy-d, 10, 10, e.getJavaColor().darker().darker().getRGB(), true);
				api.drawRect(dx, dy, 10, px, e.getColor(), true);
				i++;
			}
		}
	}

	@Override
	public String getGuiTexture() {
		return "fabricator";
	}

}
