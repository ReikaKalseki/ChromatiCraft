/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public abstract class GuiBookSection extends ChromaBookGui {

	protected int subpage = 0;

	protected final ChromaResearch page;

	protected GuiBookSection(EntityPlayer ep, ChromaResearch r, int x, int y) {
		super(ep, x, y);

		page = r;

		if (ChromaOptions.DYNAMICHANDBOOK.getState() || (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()))
			this.reloadXMLData();
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		buttonList.add(new GuiButton(50, j+xSize-27, k-2, 20, 20, "R"));	//back to main navigation

		buttonList.add(new GuiButton(13, j+xSize-27, k+32, 20, 20, ">"));
		buttonList.add(new GuiButton(14, j+xSize-27, k+52, 20, 20, "<"));
	}

	protected final void reloadXMLData() {
		ChromaDescriptions.reload();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button.id == 50) {
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
		if (buttontimer > 0)
			return;
		//Do things
	}

	protected abstract int getMaxSubpage();

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		fontRendererObj.drawString(this.getPageTitle(), posX+6, posY+6, this.getTitleColor());
	}

	@Override
	public final String getBackgroundTexture() {
		PageType type = this.getGuiLayout();
		String var4 = "/Reika/ChromatiCraft/Textures/GUIs/Handbook/"+type.getFileName()+".png";
		return var4;
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

	protected abstract PageType getGuiLayout();

	public String getPageTitle() {
		return page.getTitle();
	}

	protected int getTitleColor() {
		return 0x333333;
	}

}
