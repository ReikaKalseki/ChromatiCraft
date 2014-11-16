/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.Arrays;

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
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public class GuiCastingRecipe extends GuiScreen {

	private final EntityPlayer player;

	protected final int xSize = 256;
	protected final int ySize = 220;

	public static long time;
	private long buttontime;
	public static int i = 0;
	private int buttoni = 0;
	protected int buttontimer = 0;
	public static final long SECOND = 2000000000;

	private ChromaBook page;

	private int subpage = 0;
	private int recipe = 0;

	private static final int descX = 8;
	private static final int descY = 88;

	protected static final RenderBlocks rb = new RenderBlocks();
	protected static final RenderItem ri = new RenderItem();

	public GuiCastingRecipe(EntityPlayer ep) {
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
		buttonList.add(new GuiButton(12, j+xSize-27, k-2, 20, 20, "X"));	//Close gui button

		if (page.hasSubpages()) {
			buttonList.add(new GuiButton(13, j+xSize-27, k+32, 20, 20, ">"));
			buttonList.add(new GuiButton(14, j+xSize-27, k+52, 20, 20, "<"));
		}

		if (page.isCrafting() && page.getRecipeCount() > 1) {
			String f2 = "Textures/GUIs/Handbook/buttons.png";
			buttonList.add(new ImagedGuiButton(16, j+146, k-4, 36, 10, 126, 6, "Prev", 0, false, f2, ChromatiCraft.class));
			buttonList.add(new ImagedGuiButton(17, j+187, k-4, 36, 10, 126, 6, "Next", 0, false, f2, ChromatiCraft.class));
		}
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
		if (button.id == 16) { //prev recipe
			if (recipe > 0)
				recipe--;
			subpage = Math.min(subpage, page.getRecipeLevel(recipe).ordinal());
			this.initGui();
			return;
		}
		if (button.id == 17) { //next recipe
			if (recipe < page.getRecipeCount()-1)
				recipe++;
			subpage = Math.min(subpage, page.getRecipeLevel(recipe).ordinal());
			this.initGui();
			return;
		}
		time = System.nanoTime();
		i = 0;
		subpage = 0;
		//renderq = 22.5F;
		this.initGui();
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
		if (page.isCrafting()) {
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
		ChromaBookData.drawPage(fontRendererObj, ri, page, subpage, recipe, posX, posY);
	}

	private final void drawGraphics() {
		int posX = (width - xSize) / 2-2;
		int posY = (height - ySize) / 2-8;

		ReikaRenderHelper.disableLighting();
		int msx = ReikaGuiAPI.instance.getMouseRealX();
		int msy = ReikaGuiAPI.instance.getMouseRealY();

		this.drawAuxGraphics(posX, posY);
	}

	public int getMaxPage() {
		return ChromaBook.RESOURCEDESC.getScreen()+ChromaBook.RESOURCEDESC.getNumberChildren()/8;
	}

	public int getMaxSubpage() {
		RecipeType r = page.getRecipeLevel(recipe);
		return r != null ? r.ordinal() : 0;
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

		boolean disable = page.isConfigDisabled();
		String s = page.getTitle()+(disable ? " (Disabled)" : "");
		fontRendererObj.drawString(s, posX+xo+6, posY+yo+6, disable ? 0xff0000 : 0x000000);
		int c = disable ? 0x777777 : 0xffffff;
		int px = posX+descX;
		if (subpage == 0 || page.sameTextAllSubpages()) {
			fontRendererObj.drawSplitString(String.format("%s", page.getData()), px, posY+descY, 242, c);
		}
		else {
			fontRendererObj.drawSplitString(String.format("%s", page.getNotes()), px, posY+descY, 242, c);
		}
		if (disable) {
			fontRendererObj.drawSplitString("This machine has been disabled by your server admin or modpack creator.", px, posY+descY, 242, 0xffffff);
			fontRendererObj.drawSplitString("Contact them for further information or to request that they remove this restriction.", px, posY+descY+27, 242, 0xffffff);
			fontRendererObj.drawSplitString("If you are the server admin or pack creator, use the configuration files to change this setting.", px, posY+descY+54, 242, 0xffffff);
		}


		super.drawScreen(x, y, f);

		//if (subpage == 0 && !disable)
		this.drawRecipes();

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
