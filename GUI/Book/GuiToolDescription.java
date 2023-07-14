/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.core.helpers.Strings;
import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.Interfaces.DynamicallyGeneratedSubpage;
import Reika.ChromatiCraft.Base.GuiDescription;
import Reika.ChromatiCraft.Items.Tools.ItemAuraPouch;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Instantiable.ItemSpecificEffectDescription;

public class GuiToolDescription extends GuiDescription {

	public GuiToolDescription(EntityPlayer ep, ChromaResearch i) {
		super(ChromaGuis.TOOLDESC, ep, i, 256, 220);
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		GL11.glPushMatrix();
		double s = 4;
		GL11.glScaled(s, s, 1);
		GL11.glTranslated(33, 3, 0);
		ArrayList<ItemStack> li = page.getItemStacks();
		if (!li.isEmpty()) {
			int idx = (int)((System.currentTimeMillis()/2000)%li.size());
			ItemStack is = li.get(idx);
			api.drawItemStack(itemRender, is, (int)(posX/s), (int)(posY/s));
		}
		GL11.glPopMatrix();

		Collection<? extends ItemSpecificEffectDescription> lie = this.getEffectList();
		if (lie != null)
			this.drawEffectDescriptions(posX, posY+47, lie);
	}

	@Override
	protected int getMaxSubpage() {
		if (page.getItem().getItemInstance() instanceof DynamicallyGeneratedSubpage)
			return ((DynamicallyGeneratedSubpage)page.getItem().getItemInstance()).getMaxSubpage();
		return !Strings.isEmpty(page.getNotes(0)) ? 1 : 0;
	}

	@Override
	protected boolean hasScroll() {
		if (this.getEffectList() != null)
			return true;
		return super.hasScroll();
	}

	@Override
	protected int getMaxScroll() {
		if (this.getEffectList() != null)
			return 50;
		return super.getMaxScroll();
	}

	private Collection<? extends ItemSpecificEffectDescription> getEffectList() {
		if (page == ChromaResearch.AURAPOUCH && subpage > 0)
			return ItemAuraPouch.getEffects();
		return null;
	}

}
