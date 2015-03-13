/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundGui;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public abstract class ChromaBookGui extends GuiScreen implements CustomSoundGui {

	protected static final Random rand = new Random();

	protected int xSize;
	protected int ySize;

	protected final EntityPlayer player;

	protected static final int descX = 8;
	protected static final int descY = 88;

	protected static final RenderBlocks rb = new RenderBlocks();
	protected static final RenderItem ri = new RenderItem();

	protected static final ReikaGuiAPI api = ReikaGuiAPI.instance;

	protected long buttoncooldown = 0;
	public static final long SECOND = 2000000000;

	protected static int leftX;
	protected static int topY;

	private static int preMouseX;
	private static int preMouseY;
	private boolean cacheMouse;

	private int guiTick = 0;

	protected ChromaBookGui(EntityPlayer ep, int x, int y) {
		player = ep;
		xSize = x;
		ySize = y;

		cacheMouse = true;
	}

	public final void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
	}

	public final void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 1, 1);
	}

	@Override
	public final void setWorldAndResolution(Minecraft mc, int x, int y) {
		super.setWorldAndResolution(mc, x, y);
		fontRendererObj = ChromaFontRenderer.FontType.LEXICON.renderer;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		if (cacheMouse) {
			Mouse.setCursorPosition(preMouseX, preMouseY);
			cacheMouse = false;
		}

		//buttonList.add(new GuiButton(12, j+xSize-27, k-4, 20, 20, "X"));	//Close gui button
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		buttoncooldown = System.currentTimeMillis();
		if (button.id == 12) {
			mc.thePlayer.closeScreen();
			return;
		}
		//renderq = 22.5F;
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		if (System.currentTimeMillis()-buttoncooldown >= 50)
			super.mouseClicked(x, y, b);
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		if (System.currentTimeMillis()-buttoncooldown >= 50)
			buttoncooldown--;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.bindTexture();

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		this.drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);
		guiTick++;
		super.drawScreen(x, y, f);
	}

	protected final int getGuiTick() {
		return guiTick;
	}

	protected final void bindTexture() {
		String var4 = this.getBackgroundTexture();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
	}

	public abstract String getBackgroundTexture();

	@Override
	public final boolean doesGuiPauseGame() {
		return false;
	}

	protected final void goTo(ChromaGuis next, ChromaResearch to) {
		//Minecraft.getMinecraft().thePlayer.playSound("random.click", 2, 1);
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
		preMouseX = Mouse.getX();
		preMouseY = Mouse.getY();
		player.closeScreen();
		player.openGui(ChromatiCraft.instance, next.ordinal(), null, to != null ? to.ordinal() : -1, 0, 0);
	}

}
