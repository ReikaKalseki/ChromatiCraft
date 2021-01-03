/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile.Inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerEnchantDecomposer;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityEnchantDecomposer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

public class GuiEnchantDecomposer extends GuiChromaBase {

	private final TileEntityEnchantDecomposer tile;

	public GuiEnchantDecomposer(EntityPlayer ep, TileEntityEnchantDecomposer te) {
		super(new ContainerEnchantDecomposer(ep, te), ep, te);
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

		if (api.isMouseInBox(j+125, j+157, k+18, k+66)) {
			int level = tile.getChromaLevel();
			Fluid f = ChromatiCraft.chroma;
			String s = String.format("%s: %d/%d", f.getLocalizedName(), level, 6000);
			api.drawTooltipAt(fontRendererObj, s, mx-32, my);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		BlendMode.DEFAULT.apply();
		int h = tile.getFluidScaled(48);
		ReikaTextureHelper.bindTerrainTexture();
		GL11.glColor4f(1, 1, 1, 1);
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(125, 66-h, ChromatiCraft.chroma.getIcon(), 32, h);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int i1 = tile.getCookProgressScaled(71);
		this.drawTexturedModalRect(j + 54, k + 16, 179, 7, i1, 52);

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
		return "enchantdecomp";
	}

}
