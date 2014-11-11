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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaBookData;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Registry.ChromaBook;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Interfaces.HandbookEntry;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

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

	private int screen = 0;
	private int page = 0;
	private int subpage = 0;

	private static final int descX = 8;
	private static final int descY = 88;

	protected static final RenderBlocks rb = new RenderBlocks();
	protected static final RenderItem ri = new RenderItem();

	public GuiChromaBook(EntityPlayer ep) {
		player = ep;

		if (ChromaOptions.DYNAMICHANDBOOK.getState() || (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()))
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

		String file = ChromaBook.TOC.getTabImageFile();
		buttonList.add(new ImagedGuiButton(11, j-20, 17+k+135, 20, 20, 220, 20, "+", 0, false, file, ChromatiCraft.class));	//Next page
		buttonList.add(new ImagedGuiButton(10, j-20, 17+k+155, 20, 20, 220, 0, "-", 0, false, file, ChromatiCraft.class)); //Prev Page
		buttonList.add(new ImagedGuiButton(15, j-20, 17+k+175, 20, 20, 220, 20, "<<", 0, false, file, ChromatiCraft.class));	//First page
		buttonList.add(new GuiButton(12, j+xSize-27, k-2, 20, 20, "X"));	//Close gui button

		HandbookEntry h = this.getEntry();

		if (h.hasSubpages()) {
			buttonList.add(new GuiButton(13, j+xSize-27, k+32, 20, 20, ">"));
			buttonList.add(new GuiButton(14, j+xSize-27, k+52, 20, 20, "<"));
		}
		this.addTabButtons(j, k);
	}

	protected void addTabButtons(int j, int k) {
		ChromaBook.addRelevantButtons(j, k, screen, buttonList);
	}

	@Override
	public final boolean doesGuiPauseGame()
	{
		return true;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.id == 12) {
			mc.thePlayer.closeScreen();
			return;
		}
		if (buttontimer > 0)
			return;
		buttontimer = 20;
		if (button.id == 15) {
			screen = 0;
			page = 0;
			subpage = 0;
			//renderq = 22.5F;
			this.initGui();
			//this.refreshScreen();
			return;
		}
		if (button.id == 10) {
			if (screen > 0) {
				screen--;
				page = 0;
				subpage = 0;
			}
			//renderq = 22.5F;
			this.initGui();
			//this.refreshScreen();
			return;
		}
		if (button.id == 11) {
			if (screen < this.getMaxPage()) {
				screen++;
				page = 0;
				subpage = 0;
			}
			//renderq = 22.5F;
			this.initGui();
			//this.refreshScreen();
			return;
		}
		if (this.isOnTOC()) {
			screen = this.getNewScreenByTOCButton(button.id+screen*8);
			this.initGui();
			page = 0;
			subpage = 0;
			//renderq = 22.5F;
			return;
		}
		if (button.id == 13) {
			if (subpage < this.getMaxSubpage())
				subpage++;
			this.initGui();
			return;
		}
		if (button.id == 14) {
			if (subpage > 0)
				subpage--;
			this.initGui();
			return;
		}
		time = System.nanoTime();
		i = 0;
		page = button.id;
		subpage = 0;
		//renderq = 22.5F;
		this.initGui();
	}

	protected boolean isOnTOC() {
		return this.getEntry() == ChromaBook.TOC;
	}

	protected int getNewScreenByTOCButton(int id) {
		switch(id) {
		case 0:
			return ChromaBook.INTRO.getScreen();
		}
		return 0;
	}

	protected static enum PageType {
		PLAIN(""),
		MACHINE("machine"),
		CAST("cast"),
		RUNES("runes"),
		MULTICAST("multicast"),
		PYLONCAST("pyloncast"),
		STRUCT("structure");

		private final String endString;

		private PageType(String s) {
			endString = s;
		}

		public String getFileName() {
			return endString.isEmpty() ? "handbook" : "handbook_"+endString;
		}
	}

	protected PageType getGuiLayout() {
		ChromaBook h = ChromaBook.getEntry(screen, page);
		if (h.isCrafting()) {
			switch(subpage) {
			case 0:
				return PageType.CAST;
			case 1:
				return PageType.RUNES;
			case 2:
				return PageType.MULTICAST;
			case 3:
				return PageType.PYLONCAST;
			}
		}
		return PageType.PLAIN;
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
		if (System.currentTimeMillis()%10 == 0)
			Minecraft.getMinecraft().renderEngine.tick();
		ChromaBookData.drawPage(fontRendererObj, ri, screen, page, subpage, posX, posY);
	}

	private final void drawTabIcons() {
		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2;
		List<HandbookEntry> li = this.getAllTabsOnScreen();
		for (int i = 0; i < li.size(); i++) {
			ChromaBook h = (ChromaBook)li.get(i);
			if (h.playerCanSee(player)) {
				ReikaGuiAPI.instance.drawItemStack(ri, fontRendererObj, h.getTabIcon(), posX-17, posY-6+i*20);
			}
		}
	}

	private final void drawGraphics() {
		int posX = (width - xSize) / 2-2;
		int posY = (height - ySize) / 2-8;

		ReikaRenderHelper.disableLighting();
		int msx = ReikaGuiAPI.instance.getMouseRealX();
		int msy = ReikaGuiAPI.instance.getMouseRealY();



		String s = String.format("Page %d/%d", screen, this.getMaxPage());
		//ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, s, posX+xSize+23, posY+5, 0xffffff);
		ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, s, posX+24+xSize+fontRendererObj.getStringWidth(s), posY+20);
		if (ReikaGuiAPI.instance.isMouseInBox(posX-18, posX+2, posY+0, posY+220)) {
			String sg = "";
			List<HandbookEntry> li = this.getAllTabsOnScreen();
			int idx = (msy-posY)/20;

			if (idx >= 8) {
				int diff = idx-8;
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
			else if (idx < li.size()) {
				HandbookEntry h = li.get(idx);
				sg = h.getTitle();
			}
			if (!sg.isEmpty())
				ReikaGuiAPI.instance.drawTooltipAt(fontRendererObj, sg, msx+fontRendererObj.getStringWidth(sg)+30, msy);
		}



		this.drawAuxGraphics(posX, posY);
	}

	protected HandbookEntry getEntry() {
		return ChromaBook.getEntry(screen, page);
	}

	public int getMaxPage() {
		return ChromaBook.RESOURCEDESC.getScreen()+ChromaBook.RESOURCEDESC.getNumberChildren()/8;
	}

	public int getMaxSubpage() {
		ChromaBook h = ChromaBook.getEntry(screen, page);
		if (h.isCrafting()) {
			RecipeType r = h.getRecipeLevel();
			return r != null ? r.ordinal() : 0;
		}
		return 0;
	}

	public List<HandbookEntry> getAllTabsOnScreen() {
		List<ChromaBook> li = ChromaBook.getEntriesForScreen(screen);
		return new ArrayList(li);
	}

	protected void drawAuxGraphics(int posX, int posY) {
		//HandbookAuxData.drawGraphics((ChromaBook)this.getEntry(), posX, posY, xSize, ySize, fontRendererObj, ri, subpage);
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
