/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile.Inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerMiner;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;

public class GuiMiner extends GuiChromaBase {

	private final TileEntityMiner tile;

	private int offset = 0;
	private int cooldown = 0;

	private static final int COLS = 10;
	private static final int ROWS = 6;

	public GuiMiner(EntityPlayer ep, TileEntityMiner te) {
		super(new ContainerMiner(ep, te), ep, te);
		tile = te;
		ySize = 206;
	}

	private int getMaxOffset() {
		return Math.max(0, tile.getFound().size()/COLS-ROWS);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		List<ItemStack> c = tile.getFound();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int max = Math.min(c.size(), COLS*ROWS);
		for (int i = 0; i < max; i++) {
			ItemStack is = c.get(i+COLS*offset);
			double s = 0.75;
			int dx = (int)((1/s)*(j*s+10+(i%COLS)*10))-3;
			int dy = (int)((1/s)*(k*s+22+(i/COLS)*10))-6;
			if (api.isMouseInBox(dx, dx+12, dy, dy+12)) {
				String sg = is.getDisplayName()+": "+tile.getNumberFound(is);
				api.drawTooltipAt(fontRendererObj, sg, dx-j+fontRendererObj.getStringWidth(sg)+36, dy-k);
			}
		}

		String s = String.format("Scan: %.2f%s done", 100*tile.getDigCompletion(), "%");
		fontRendererObj.drawString(s, 4, 111, 0xffffff);

		if (tile.isReady()) {
			int g = 160+(int)(95*Math.sin(Math.toRadians(System.currentTimeMillis()/3%360)));
			int color = 0xff000000 | (g << 8);
			this.drawRect(156, 40, 164, 48, color);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);

		if (cooldown > 0)
			cooldown--;
		else {
			if (offset > 0 && Keyboard.isKeyDown(Keyboard.KEY_W)) {
				offset--;
				cooldown = 20;
			}
			else if (offset < this.getMaxOffset() && Keyboard.isKeyDown(Keyboard.KEY_S)) {
				offset++;
				cooldown = 20;
			}
		}

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		List<ItemStack> c = tile.getFound();
		GL11.glPushMatrix();
		double s = 0.75;
		GL11.glScaled(s, s, s);
		int max = Math.min(c.size(), COLS*ROWS);
		for (int i = 0; i < max; i++) {
			ItemStack is = c.get(i+COLS*offset);
			int dx = (int)((1/s)*(j+10+(i%COLS)*18*s));
			int dy = (int)((1/s)*(k+22+(i/COLS)*18*s));
			api.drawItemStack(itemRender, is, dx, dy);
		}
		GL11.glPopMatrix();

		if (offset < this.getMaxOffset()) {
			api.drawLine(j+70, k+103, j+84, k+103, 0xffffff);
			api.drawLine(j+70, k+103, j+77, k+105, 0xffffff);
			api.drawLine(j+84, k+103, j+77, k+105, 0xffffff);
		}
		if (offset > 0) {
			api.drawLine(j+70, k+20, j+84, k+20, 0xffffff);
			api.drawLine(j+70, k+20, j+77, k+17, 0xffffff);
			api.drawLine(j+84, k+20, j+77, k+17, 0xffffff);
		}
	}

	@Override
	public String getGuiTexture() {
		return "miner";
	}

}
