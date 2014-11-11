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

import java.util.Arrays;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.RotaryCraft.Registry.ConfigRegistry;

public class GuiChromaBook extends GuiScreen {

	private final EntityPlayer player;

	protected final int xSize = 256;
	protected final int ySize = 220;

	public static long time;
	private long buttontime;
	public static int i = 0;
	private int buttoni = 0;
	protected int buttontimer = 0;
	public static final long SECOND = 2000000000;

	public GuiChromaBook(EntityPlayer ep) {
		player = ep;

		if (ConfigRegistry.DYNAMICHANDBOOK.getState() || (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()))
			this.reloadXMLData();
	}

	protected void reloadXMLData() {
		ChromaDescriptions.reload();
	}

	@Override
	public final void initGui() {
		super.initGui();
		buttonList.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
	}

	@Override
	public final boolean doesGuiPauseGame()
	{
		return true;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
	}

	protected static enum PageType {
		PLAIN(""),
		RUNES("runes");

		private final String endString;

		private PageType(String s) {
			endString = s;
		}

		public String getFileName() {
			return "handbookgui"+endString;
		}
	}

	protected PageType getGuiLayout() {
		return PageType.RUNES;
	}

	public final String getBackgroundTexture() {
		PageType type = this.getGuiLayout();
		String var4 = "/Reika/ChromatiCraft/Textures/GUIs/Handbook/"+type.getFileName()+".png";
		return var4;
	}

	private final void drawRecipes() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2;
		try {
			this.drawAuxData(posX, posY);
		}
		catch (Exception e) {
			ReikaChatHelper.write(Arrays.toString(e.getStackTrace()));
			e.printStackTrace();
		}
	}

	protected void drawAuxData(int posX, int posY) {
		//HandbookAuxData.drawPage(fontRendererObj, ri, screen, page, subpage, posX, posY);
	}

	private final void drawTabIcons() {
		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2;/*
		List<HandbookEntry> li = this.getAllTabsOnScreen();
		for (int i = 0; i < li.size(); i++) {
			HandbookEntry h = li.get(i);
			ReikaGuiAPI.instance.drawItemStack(ri, fontRendererObj, h.getTabIcon(), posX-17, posY-6+i*20);
		}*/
	}

	private final void drawGraphics() {
		int posX = (width - xSize) / 2-2;
		int posY = (height - ySize) / 2-8;

		ReikaRenderHelper.disableLighting();
		int msx = ReikaGuiAPI.instance.getMouseRealX();
		int msy = ReikaGuiAPI.instance.getMouseRealY();

		/*

		String s = String.format("Page %d/%d", screen, this.getMaxPage());
		//ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, s, posX+xSize+23, posY+5, 0xffffff);
		ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, s, posX+24+xSize+fontRendererObj.getStringWidth(s), posY+20);
		if (ReikaGuiAPI.instance.isMouseInBox(posX-18, posX+2, posY+0, posY+220)) {
			String sg = "";
			List<HandbookEntry> li = this.getAllTabsOnScreen();
			int idx = (msy-posY)/20;
			if (idx >= li.size()) {
				int diff = idx-li.size();
				switch(diff) {
				case 0:
					sg = "Next";
					break;
				case 1:
					sg = "Back";
					break;
				case 2:
					sg = "Return";
					break;
				}
			}
			else {
				HandbookEntry h = li.get(idx);
				sg = h.getTitle();
			}
			ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, sg, msx+fontRendererObj.getStringWidth(sg)+30, msy);
		}

		 */

		this.drawAuxGraphics(posX, posY);
	}

	protected void drawAuxGraphics(int posX, int posY) {
		//HandbookAuxData.drawGraphics((HandbookRegistry)this.getEntry(), posX, posY, xSize, ySize, fontRendererObj, ri, subpage);
	}

	@Override
	public final void drawScreen(int x, int y, float f)
	{
		if (System.nanoTime()-buttontime > SECOND/20) {
			buttoni++;
			buttontime = System.nanoTime();
			buttontimer = 0;
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.bindTexture();

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		this.drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);

		/*
		int xo = 0;
		int yo = 0;
		HandbookEntry h = this.getEntry();
		boolean disable = h.isConfigDisabled();
		String s = h.getTitle()+(disable ? " (Disabled)" : "");
		fontRendererObj.drawString(s, posX+xo+6, posY+yo+6, disable ? 0xff0000 : 0x000000);
		int c = disable ? 0x777777 : 0xffffff;
		int px = posX+descX;
		if (subpage == 0 || h.sameTextAllSubpages()) {
			fontRendererObj.drawSplitString(String.format("%s", h.getData()), px, posY+descY, 242, c);
		}
		else {
			fontRendererObj.drawSplitString(String.format("%s", h.getNotes()), px, posY+descY, 242, c);
		}
		if (disable) {
			fontRendererObj.drawSplitString("This machine has been disabled by your server admin or modpack creator.", px, posY+descY, 242, 0xffffff);
			fontRendererObj.drawSplitString("Contact them for further information or to request that they remove this restriction.", px, posY+descY+27, 242, 0xffffff);
			fontRendererObj.drawSplitString("If you are the server admin or pack creator, use the configuration files to change this setting.", px, posY+descY+54, 242, 0xffffff);
		}
		 */

		super.drawScreen(x, y, f);

		//if (subpage == 0 && !disable)
		this.drawRecipes();

		this.drawTabIcons();

		this.drawGraphics();

		//if (subpage == 0)
		//	this.drawMachineRender(posX, posY);

		RenderHelper.disableStandardItemLighting();
	}

	protected void bindTexture() {
		String var4 = this.getBackgroundTexture();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
	}

}
