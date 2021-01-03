/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

public abstract class GuiScrollingPage extends ChromaBookGui {

	protected static int offsetX = 0;
	protected static int offsetY = 0;

	protected int maxX = 0;
	protected int maxY = 0;

	protected final int paneWidth;
	protected final int paneHeight;

	protected GuiScrollingPage(ChromaGuis g, EntityPlayer ep, int x, int y, int w, int h) {
		super(g, ep, x, y);
		paneWidth = w;
		paneHeight = h;
	}

	public static void resetOffset() {
		if (ChromaBookGui.lastGui == null) {
			offsetX = 0;
			offsetY = 0;
		}
		else {
			//goTo(ChromaBookGui.saveLocation.gui, ChromaBookGui.saveLocation.page, Minecraft.getMinecraft().thePlayer);
		}
	}

	public static void saveLocation(/*ChromaGuis g, ChromaResearch r, int s*/GuiBookSection gui) {
		ChromaBookGui.lastGui = gui;// = new GuiPosition(g, r, s);
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		leftX = (width - xSize) / 2;
		topY = (height - ySize) / 2;

		int sp = Math.max(1, 180/Math.max(1, ReikaRenderHelper.getFPS()));
		if (GuiScreen.isShiftKeyDown()) {
			sp *= 2;
		}
		else if (GuiScreen.isCtrlKeyDown()) {
			sp = Math.max(1, sp/2);
		}
		if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode()) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			offsetY -= sp;
		}
		if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode()) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			offsetX -= sp;
		}
		if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode()) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			offsetY += sp;
		}
		if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode()) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			offsetX += sp;
		}

		if (offsetX < 0) {
			offsetX = 0;
		}
		if (offsetY < 0) {
			offsetY = 0;
		}
		if (offsetX > maxX && maxX >= 0) {
			offsetX = maxX;
		}
		if (offsetY > maxY && maxY >= 0) {
			offsetY = maxY;
		}

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getScrollingTexture());
		int u = offsetX%256;
		int v = offsetY%256;
		this.drawTexturedModalRect(leftX+7, topY-1, u, v, paneWidth, paneHeight);

		super.drawScreen(x, y, f);
	}

	protected abstract String getScrollingTexture();

}
