/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public abstract class GuiDescription extends GuiBookSection {

	private int textOffset = 0;

	protected GuiDescription(ChromaGuis g, EntityPlayer ep, ChromaResearch r, int x, int y) {
		super(g, ep, r, x, y, false);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (System.currentTimeMillis()-buttoncooldown >= 50) {
			if (button.id == 2 && textOffset > 0) {
				textOffset--;
			}
			else if (button.id == 3 && textOffset < CrystalBees.getBeeDescription(CrystalBees.getBeeByIndex(subpage-1)).size()-11) {
				textOffset++;
			}
			else {
				textOffset = 0;
			}
		}
		//renderq = 22.5F;
		super.actionPerformed(button);
		this.initGui();
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";

		if (page == ChromaResearch.BEES && subpage > 0) {
			buttonList.add(new CustomSoundImagedGuiButton(2, j+205, k+50, 12, 10, 100, 6, file, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(3, j+205, k+60, 12, 10, 112, 6, file, ChromatiCraft.class, this));
		}
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
				if (page == ChromaResearch.BEES && subpage > 0) {
					ArrayList<String> li = CrystalBees.getBeeDescription(CrystalBees.getBeeByIndex(subpage-1));
					for (int i = textOffset; i < li.size(); i++) {
						fontRendererObj.drawString(li.get(i), px, posY+descY+(fontRendererObj.FONT_HEIGHT+2)*(i-textOffset), c);
						if (i-textOffset > 9)
							break;
					}
				}
				else {
					fontRendererObj.drawSplitString(String.format("%s", page.getNotes(subpage)), px, posY+descY, 242, c);
				}
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
		if (page == ChromaResearch.ACCEL && subpage > 1)
			return AdjacencyUpgrades.upgrades[subpage-2].getName();
		return page.isConfigDisabled() ? page.getTitle()+" (Disabled)" : super.getPageTitle();
	}

	@Override
	public final int getTitleColor() {
		return page.isConfigDisabled() ? 0xffffff : super.getTitleColor();
	}

}
