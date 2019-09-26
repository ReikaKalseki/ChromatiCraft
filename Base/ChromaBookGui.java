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

import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Maps.RegionMap;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
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

	protected final ChromaGuis guiType;

	public static ChromaBookGui lastGui = null;

	private final RegionMap<AuxButton> auxButtons = new RegionMap();

	protected ChromaBookGui(ChromaGuis t, EntityPlayer ep, int x, int y) {
		player = ep;
		xSize = x;
		ySize = y;

		guiType = t;

		cacheMouse = true;
	}

	protected final ItemStack getBook() {
		return player.getCurrentEquippedItem();
	}

	protected final boolean isFragmentPresent(ChromaResearch r) {
		return ItemChromaBook.hasPage(this.getBook(), r);
	}

	protected final boolean shouldDisplayFragment(ChromaResearch r) {
		return ChromaResearchManager.instance.playerHasFragment(player, r) && this.isFragmentPresent(r);
	}

	public final void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.33F, 1);
	}

	public final void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.67F, 1);
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

	protected final void addAuxButton(GuiButton b, String name) {
		buttonList.add(b);
		auxButtons.addRegionByWH(b.xPosition-1, b.yPosition-1, b.width, b.height, new AuxButton(b, name));
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

		AuxButton ab = auxButtons.getRegion(api.getMouseRealX(), api.getMouseRealY());
		if (ab != null) { //api.isMouseInBox(posX+xSize-1, posX+xSize-1+22, posY+13, posY+13+39)
			//String sg = "Save & Exit";
			int bw = ab.button.width;
			int sp = 4;
			String sg = ab.text;
			int sw = fontRendererObj.getStringWidth(sg);//20+fontRendererObj.getStringWidth(sg)/2;
			int dx = posX+12-bw-sp;//Math.min(api.getMouseRealX(), posX-22)-sw+16;
			if (api.getMouseRealX() > posX+xSize-2) {
				//ReikaJavaLibrary.pConsole((posX+xSize+22)+","+(api.getMouseRealX()+sw+32));
				dx = posX+xSize+sw+20+bw+sp;//Math.max(posX+xSize+22*0, api.getMouseRealX())+sw+32;
			}
			api.drawTooltipAt(fontRendererObj, sg, dx, api.getMouseRealY());
		}

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
		goTo(next, to, player);
	}

	protected static final void goTo(ChromaGuis next, ChromaResearch to, EntityPlayer player) {
		//Minecraft.getMinecraft().thePlayer.playSound("random.click", 2, 1);
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.33F, 1);
		preMouseX = Mouse.getX();
		preMouseY = Mouse.getY();
		player.closeScreen();
		player.openGui(ChromatiCraft.instance, next.ordinal(), null, to != null ? to.ordinal() : -1, 0, 0);
	}

	private static class AuxButton {

		private final GuiButton button;
		private final String text;

		private AuxButton(GuiButton b, String s) {
			button = b;
			text = s;
		}

	}
	/*
	public static class GuiPosition {

		public final ChromaGuis gui;
		public final ChromaResearch page;
		public final int subpage;

		public GuiPosition(ChromaGuis g, ChromaResearch r, int s) {
			gui = g;
			page = r;
			subpage = s;
		}

	}
	 */
}
