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

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CrystalRenderedBlock;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public abstract class GuiBookSection extends ChromaBookGui {

	protected int subpage = 0;

	protected final ChromaResearch page;

	protected final boolean NEItrigger;

	protected GuiBookSection(EntityPlayer ep, ChromaResearch r, int x, int y, boolean nei) {
		super(ep, x, y);

		page = r;
		NEItrigger = nei;

		if (ChromaOptions.DYNAMICHANDBOOK.getState() || (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()))
			this.reloadXMLData();
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		buttonList.add(new GuiButton(50, j+xSize-27, k-2, 20, 20, "X"));	//back to main navigation

		if (this.getMaxSubpage() > 0) {
			buttonList.add(new GuiButton(13, j+xSize-27, k+32, 20, 20, ">"));
			buttonList.add(new GuiButton(14, j+xSize-27, k+52, 20, 20, "<"));
		}
	}

	protected final void reloadXMLData() {
		ChromaDescriptions.reload();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button.id == 50) {
			if (NEItrigger)
				player.closeScreen();
			else
				this.goTo(ChromaGuis.BOOKNAV, null);
			return;
		}
		else if (button.id == 13) {
			if (subpage < this.getMaxSubpage())
				subpage++;
			this.initGui();
			return;
		}
		else if (button.id == 14) {
			if (subpage > 0)
				subpage--;
			this.initGui();
			return;
		}
	}

	protected abstract int getMaxSubpage();

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		fontRendererObj.drawString(this.getPageTitle(), posX+this.getTitleOffset(), posY+6, this.getTitleColor());
	}

	@Override
	public final String getBackgroundTexture() {
		PageType type = this.getGuiLayout();
		String var4 = "/Reika/ChromatiCraft/Textures/GUIs/Handbook/"+type.getFileName()+".png";
		return var4;
	}

	protected static enum PageType {
		PLAIN(""),
		CRAFTING("cast"), //reuse
		MACHINE("machine"),
		CAST("cast"),
		RUNES("runes"),
		MULTICAST("multicast"),
		PYLONCAST("pyloncast"),
		STRUCT("structure"),
		RITUAL("ritual"),
		ELEMENT("element");

		private final String endString;

		private PageType(String s) {
			endString = s;
		}

		public String getFileName() {
			return endString.isEmpty() ? "handbook" : "handbook_"+endString;
		}
	}

	protected abstract PageType getGuiLayout();

	public String getPageTitle() {
		return page.getTitle();
	}

	protected int getTitleColor() {
		return 0xffffff;
	}

	protected int getTitleOffset() {
		return 6;
	}

	protected final void drawBlockRender(int posX, int posY, Block b, int meta) {
		GL11.glTranslated(0, 0, 32);
		GL11.glColor4f(1, 1, 1, 1);
		double x = posX+167;
		double y = posY+44;
		//float q = 12.5F + fscale*(float)Math.sin(System.nanoTime()/1000000000D); //wobble
		//ReikaJavaLibrary.pConsole(y-ReikaGuiAPI.instance.getMouseScreenY(height));
		int range = 64;
		boolean rotate = ReikaGuiAPI.instance.isMouseInBox((int)x-range/2, (int)x+range/2, (int)y-range, (int)y+range);

		y -= 8*Math.sin(Math.abs(Math.toRadians(22.5F)));

		GL11.glEnable(GL11.GL_BLEND);

		RenderHelper.enableGUIStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		double sc = 48;
		GL11.glPushMatrix();
		int r = (int)(System.nanoTime()/20000000)%360;
		double dx = x;
		double dy = y;
		double dz = 0;
		GL11.glPushMatrix();
		GL11.glTranslated(dx, dy, dz);
		GL11.glScaled(sc, -sc, sc);
		GL11.glRotatef(22.5F, 1, 0, 0);
		GL11.glRotatef(r, 0, 1, 0);
		ReikaTextureHelper.bindTerrainTexture();
		if (b instanceof CrystalRenderedBlock) {
			CrystalRenderer.renderAllArmsInInventory = true;
			GL11.glTranslated(-0.5, -0.33, -0.5);
		}
		rb.renderBlockAsItem(b, meta, 1);
		CrystalRenderer.renderAllArmsInInventory = false;
		GL11.glPopMatrix();
		GL11.glPopMatrix();

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glTranslated(0, 0, -32);
	}

}
