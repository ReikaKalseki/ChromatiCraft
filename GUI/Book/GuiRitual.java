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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiRitual extends GuiBookSection {

	private final Chromabilities ability;

	public GuiRitual(EntityPlayer ep, Chromabilities r) {
		super(ChromaGuis.RITUAL, ep, null, 256, 220, false);
		ability = r;
	}

	private ElementTagCompound getEnergy() {
		return AbilityHelper.instance.getElementsFor(ability);
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.RITUAL;
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		leftX = (width - xSize) / 2;
		topY = (height - ySize) / 2;
		String s = "Textures/Ability/"+ability.name().toLowerCase()+".png";
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
		GL11.glPushMatrix();
		double d = 50/256D;
		GL11.glColor3f(1, 1, 1);
		GL11.glScaled(d, d, 1);
		this.drawTexturedModalRect((int)((leftX+103)/d), (int)((topY+11)/d), 0, 0, 256, 256);
		GL11.glPopMatrix();

		ElementTagCompound tag = AbilityHelper.instance.getElementsFor(ability);
		int i = 0;
		for (CrystalElement e : tag.elementSet()) {
			String s1 = String.format("%s:", e.displayName);
			String s2 = String.format("%d Lumens", tag.getValue(e));
			int color = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.5F);
			fontRendererObj.drawString(s1, leftX+descX+3, topY+descY-5+(fontRendererObj.FONT_HEIGHT+2)*i, color);
			fontRendererObj.drawString(s2, leftX+descX+3+56, topY+descY-5+(fontRendererObj.FONT_HEIGHT+2)*i, color);
			i++;
		}
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

	@Override
	public String getPageTitle() {
		return ability.getDisplayName()+" Ritual";
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		subpage = 0; //?
		//renderq = 22.5F;
		this.initGui();
	}

}
