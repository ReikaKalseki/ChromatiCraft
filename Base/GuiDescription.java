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

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public abstract class GuiDescription extends GuiBookSection {

	protected GuiDescription(ChromaGuis g, EntityPlayer ep, ChromaResearch r, int x, int y) {
		super(g, ep, r, x, y, false);
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.PLAIN;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		super.drawScreen(x, y, f);

		int px = posX+descX;
		String s = String.format("%s", page.getData());
		boolean flag = page.isUnloadable();
		if (flag) {
			int c1 = 0x6c6c6c; //7a7a7a avg
			int c2 = 0x828282;
			float mix = (float)(0.5+0.5*Math.sin(this.getGuiTick()/16D));
			fontRendererObj.drawSplitString(s, px, posY+descY, 242, ReikaColorAPI.mixColors(c1, c2, mix));
			ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawSplitString(s, px, posY+descY, 242, ReikaColorAPI.mixColors(c1, c2, 1-mix));
			String err = "Something is wrong with the fabric of the world; this entry seems to be illegible, and whatever it pertains to is likely unavailable.";
			err += "\n\nPerhaps someone else might have influenced this, and perhaps they could be of assistance to you.";
			fontRendererObj.drawSplitString(err, px, posY+descY, 242, 0xffffff);
		}
		else {
			boolean disable = page.isConfigDisabled();
			int c = disable ? 0xff7777 : 0xffffff;
			if (subpage == 0 || page.sameTextAllSubpages()) {
				fontRendererObj.drawSplitString(s, px, posY+descY, 242, c);
			}
			else {
				fontRendererObj.drawSplitString(String.format("%s", page.getNotes(subpage)), px, posY+descY, 242, c);
			}
			if (disable) {
				fontRendererObj.drawSplitString("This item has been disabled by your server admin or modpack creator.", px, posY+descY, 242, 0xffffff);
				fontRendererObj.drawSplitString("Contact them for further information or to request that they remove this restriction.", px, posY+descY+27, 242, 0xffffff);
				fontRendererObj.drawSplitString("If you are the server admin or pack creator, use the configuration files to change this setting.", px, posY+descY+54, 242, 0xffffff);
			}
		}
	}

	@Override
	protected int getMaxSubpage() {
		return 1;
	}

	@Override
	public final String getPageTitle() {
		return page.isConfigDisabled() ? page.getTitle()+" (Disabled)" : super.getPageTitle();
	}

	@Override
	public final int getTitleColor() {
		return page.isConfigDisabled() ? 0xffffff : super.getTitleColor();
	}

}
