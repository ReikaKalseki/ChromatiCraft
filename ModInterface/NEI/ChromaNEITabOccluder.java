/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.NEI;

import net.minecraft.client.gui.inventory.GuiContainer;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiCastingTable;

import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiAdapter;

public class ChromaNEITabOccluder extends INEIGuiAdapter {

	@Override
	public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData data)
	{
		if (gui instanceof GuiCastingTable) {
			data.showItemSection = false;
		}
		return data;
	}

	@Override
	public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h)
	{
		if (gui instanceof GuiChromaBase) {
			GuiChromaBase gm = (GuiChromaBase)gui;
			Rectangle4i item = new Rectangle4i(x, y, w, h);
			//Rectangle4i help = new Rectangle4i(gm.getGuiLeft()-10, gm.getGuiTop(), gm.getGuiLeft(), gm.getYSize());
			Rectangle4i tabs = this.getTabBox(gm);
			//if (help.intersects(item)) {
			//	return true;
			//}
			if (tabs != null && tabs.intersects(item)) {
				return true;
			}
		}
		return false;
	}

	private Rectangle4i getTabBox(GuiChromaBase gm) {
		if (gm instanceof GuiCastingTable) {
			return new Rectangle4i(gm.getGuiLeft()+gm.getXSize(), gm.getGuiTop(), 43, gm.getYSize());
		}
		else {
			return null;
		}
	}

}
