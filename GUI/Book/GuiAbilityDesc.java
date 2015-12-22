/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiDescription;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiAbilityDesc extends GuiDescription {

	public GuiAbilityDesc(EntityPlayer ep, ChromaResearch r) {
		super(ChromaGuis.ABILITYDESC, ep, r, 256, 220);
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		GL11.glDisable(GL11.GL_LIGHTING);
		leftX = (width - xSize) / 2;
		topY = (height - ySize) / 2;
		String s = "Textures/Ability/"+page.getAbility().name().toLowerCase()+".png";
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
		GL11.glPushMatrix();
		double d = 50/256D;
		GL11.glColor3f(1, 1, 1);
		GL11.glScaled(d, d, 1);
		this.drawTexturedModalRect((int)((leftX+103)/d), (int)((topY+11)/d), 0, 0, 256, 256);
		GL11.glPopMatrix();
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

}
