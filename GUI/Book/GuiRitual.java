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

import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

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
		String s = "Textures/Ability/"+ability.name().toLowerCase(Locale.ENGLISH)+".png";
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
		GL11.glPushMatrix();
		double d = 50/256D;
		GL11.glColor3f(1, 1, 1);
		GL11.glScaled(d, d, 1);
		this.drawTexturedModalRect((int)((leftX+103)/d), (int)((topY+11)/d), 0, 0, 256, 256);
		GL11.glPopMatrix();


		ElementTagCompound tag = AbilityHelper.instance.getElementsFor(ability);
		Proportionality<CrystalElement> p = tag.getProportionality();
		/*
		int i = 0;
		for (CrystalElement e : tag.elementSet()) {
			String s1 = String.format("%s:", e.displayName);
			String s2 = String.format("%d Lumens", tag.getValue(e));
			int color = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.5F);
			p.addValue(e, tag.getValue(e));
			colors.put(e, e.getColor());
			fontRendererObj.drawString(s1, leftX+descX+3, topY+descY-5+(fontRendererObj.FONT_HEIGHT+2)*i, color);
			fontRendererObj.drawString(s2, leftX+descX+3+56, topY+descY-5+(fontRendererObj.FONT_HEIGHT+2)*i, color);
			i++;
		}

		if (tag.tagCount() <= 8) {
			int r = 40;
			int dx = leftX+descX+192;
			int dy = topY+descY+38;
			p.renderAsPie(dx, dy, r, 0, colors);
			ReikaGuiAPI.instance.drawCircle(dx, dy, r+0.25, 0x000000);
		}
		 */
		double r = 57.5;
		int dx = leftX+descX+184;
		int dy = topY+descY+52;
		double zang = System.identityHashCode(ability);
		p.setGeometry(dx, dy, r, zang);
		p.render(CrystalElement.getColorMap());

		ReikaTextureHelper.bindTerrainTexture();
		double ba = zang;
		double ir = r*0.625;
		int si = 8;
		for (CrystalElement e : p.getElements()) {
			double ang = 360D*p.getFraction(e);
			double a = ba+ang/2D;
			int ix = (int)Math.round(dx+ir*Math.cos(Math.toRadians(a)));
			int iy = (int)Math.round(dy+ir*Math.sin(Math.toRadians(a)));
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(ix-si/2, iy-si/2, e.getOutlineRune(), si, si);
			ba += ang;
		}

		//ReikaGuiAPI.instance.drawCircle(dx, dy, r+0.25, 0x000000);
		int tot = tag.getTotalEnergy();
		int h = fontRendererObj.FONT_HEIGHT*3/2;
		fontRendererObj.drawSplitString("Total Energy:", leftX+descX+3+40, topY+descY-5-9, 80, 0xffffff);
		fontRendererObj.drawSplitString(String.valueOf(tot), leftX+descX+3+40, topY+descY-5-9+h, 80, 0xffffff);
		fontRendererObj.drawSplitString("Lumens", leftX+descX+3+40, topY+descY-5-9+h*2, 80, 0xffffff);

		int frac = (int)(125*Math.pow((double)tot/AbilityRituals.instance.getMaxAbilityTotalCost(), 0.5));

		ReikaTextureHelper.bindTerrainTexture();
		BlendMode.ADDITIVEDARK.apply();
		for (int i = 0; i < 4; i++)
			api.drawTexturedModelRectFromIcon(leftX+descX+11, topY+descY+108-frac+3, ChromaIcons.RIFT.getIcon(), 16, frac+6);
		BlendMode.DEFAULT.apply();

		s = "Textures/GUIs/Handbook/misc.png";
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
		api.drawTexturedModalRect(dx-(int)Math.ceil(r), dy-(int)Math.ceil(r), 0, 0, (int)(r*2), (int)(r*2));

		api.drawTexturedModalRect(leftX+descX+1, topY+descY+108-frac, 0, 118, 36, 7);
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
