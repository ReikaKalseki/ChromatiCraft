/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer.FontType;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

public class GuiChromability extends GuiScreen implements CustomSoundGui {

	protected final EntityPlayer player;
	protected int xSize;
	protected int ySize;

	protected final ArrayList<Ability> abilities = new ArrayList();
	private int index = 0;

	private int dx = 0;

	public GuiChromability(EntityPlayer ep) {
		player = ep;
		xSize = 232;
		ySize = 188;

		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())
			ChromaDescriptions.reload();

		abilities.addAll(Chromabilities.getAbilitiesAvailableToPlayer(ep));
	}

	public final void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
	}

	public final void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.8F, 1);
	}

	protected final Ability getActiveAbility() {
		return abilities.isEmpty() ? null : abilities.get(index);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();

		int midx = width/2;
		int midy = height/2;
		int w = 12;
		int h = ySize-14;
		int out = xSize/2;//+60;

		if (!abilities.isEmpty()) {
			String tex = this.getButtonTexture();
			buttonList.add(new CustomSoundImagedGuiButton(0, midx-w-out, midy-h/2, w, h, 244, 0, tex, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(1, midx+out, midy-h/2, w, h, 232, 0, tex, ChromatiCraft.class, this));
		}

		/*
		for (int i = 0; i < available.size(); i++) {
			Chromabilities c = available.get(i);
			String name = c.name();
			buttonList.add(new GuiButton(i, 30, 30+i*20, 40, 20, name));
		}*/
	}

	protected String getButtonTexture() {
		return "Textures/GUIs/ability.png";
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		switch(b.id) {
			case 0:
				this.scrollRight(1);
				break;
			case 1:
				this.scrollLeft(1);
				break;
		}
		//this.initGui();
	}

	protected final void scrollLeft(int n) {
		if (dx == 0 && index < abilities.size()-1) {
			for (int i = 0; i < n-1 && index < abilities.size()-2; i++)
				index++;
			//index++;
			dx--;
			this.markButtons(false);
		}
	}

	protected final void scrollRight(int n) {
		if (dx == 0 && index > 0) {
			for (int i = 0; i < n-1 && index > 1; i++)
				index--;
			//index--;
			dx++;
			this.markButtons(false);
		}
	}

	private void markButtons(boolean on) {
		this.initGui();
		for (int i = 0; i < buttonList.size(); i++) {
			GuiButton b2 = (GuiButton)buttonList.get(i);
			b2.visible = on;
		}
	}

	@Override
	public final void drawScreen(int x, int y, float f)
	{
		super.drawScreen(x, y, f);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (abilities.isEmpty()) {
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/ability2.png");
			this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/Ability/unknown.png");
			GL11.glPushMatrix();
			double d = 0.1953125;
			GL11.glScaled(d, d, d);
			int x2 = j+8+dx;
			int y2 = k+8;
			this.drawTexturedModalRect((int)(x2/d), (int)(y2/d), 0, 0, 256, 256);
			GL11.glPopMatrix();
		}
		else {
			Ability c = abilities.get(index);
			boolean has = Chromabilities.playerHasAbility(player, c);

			double px = 2D*Math.abs(dx)/width;
			int sp = Math.max(1, (int)(360D/Math.max(1, ReikaRenderHelper.getFPS())*Math.max(1, 6*Math.abs(-(px*px)+2*px))));
			if (dx > 0) {
				dx += sp;
			}
			if (dx < 0) {
				dx -= sp;
			}
			if (dx >= width) {
				dx = 0;
				index--;
				this.markButtons(true);
			}
			if (dx <= -width) {
				dx = 0;
				index++;
				this.markButtons(true);
			}
			int m = dx != 0 ? 1 : 0;
			int min = index > 0 ? -m : 0;
			int max = index < abilities.size()-1 ? m : 0;
			for (int i = min; i <= max; i++) {
				int a = j+i*width+dx;
				Ability ca = abilities.get(index+i);
				String s = this.getBackTexture(ca);
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
				//GL11.glEnable(GL11.GL_BLEND);
				this.drawTexturedModalRect(a, k, 0, 0, xSize, ySize);
				//GL11.glDisable(GL11.GL_BLEND);
			}

			this.drawPreview(c, j, k);

			fontRendererObj.drawString(c.getDisplayName(), j+63+dx, k+9, 0xffffff);
			if (dx != 0) { //performance boost
				if (index > 0) {
					c = abilities.get(index-1);
					fontRendererObj.drawString(c.getDisplayName(), j+63+dx-width, k+9, 0xffffff);
				}

				if (index < abilities.size()-1) {
					c = abilities.get(index+1);
					fontRendererObj.drawString(c.getDisplayName(), j+63+dx+width, k+9, 0xffffff);
				}
			}

			int fx = 9;
			int fy = 64;
			if (!has)
				;//return;
			String desc = c.getDescription();
			if (!this.hasFragment(c))
				desc = FontType.OBFUSCATED.id+desc;
			fontRendererObj.drawSplitString(desc, j+dx+fx, k+fy, xSize-fx*2, 0xffffff);

			if (dx != 0) { //performance boost
				if (index > 0) {
					c = abilities.get(index-1);
					desc = c.getDescription();
					if (!this.hasFragment(c))
						desc = FontType.OBFUSCATED.id+desc;
					fontRendererObj.drawSplitString(desc, j+dx+fx-width, k+fy, xSize-fx*2, 0xffffff);
				}

				if (index < abilities.size()-1) {
					c = abilities.get(index+1);
					desc = c.getDescription();
					if (!this.hasFragment(c))
						desc = FontType.OBFUSCATED.id+desc;
					fontRendererObj.drawSplitString(desc, j+dx+fx+width, k+fy, xSize-fx*2, 0xffffff);
				}
			}
		}
	}

	private boolean hasFragment(Ability c) {
		return !(c instanceof Chromabilities) || ChromaResearchManager.instance.playerHasFragment(player, ChromaResearch.getPageFor((Chromabilities)c));
	}

	protected String getBackTexture(Ability a) {
		return Chromabilities.playerHasAbility(player, a) ? "Textures/GUIs/ability.png" : "Textures/GUIs/ability2.png";
	}

	private void drawPreview(Ability c, int j, int k) {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getTextureName(c));
		GL11.glPushMatrix();
		double d = 0.1953125;
		GL11.glScaled(d, d, d);
		int x = j+8+dx;
		int y = k+8;
		this.drawTexturedModalRect((int)(x/d), (int)(y/d), 0, 0, 256, 256);

		if (dx != 0) { //performance boost
			if (index > 0) {
				c = abilities.get(index-1);
				ReikaTextureHelper.bindTexture(c.getTextureReferenceClass(), this.getTextureName(c));
				x = j+8+dx-width;
				this.drawTexturedModalRect((int)(x/d), (int)(y/d), 0, 0, 256, 256);
			}

			if (index < abilities.size()-1) {
				c = abilities.get(index+1);
				ReikaTextureHelper.bindTexture(c.getTextureReferenceClass(), this.getTextureName(c));
				x = j+8+dx+width;
				this.drawTexturedModalRect((int)(x/d), (int)(y/d), 0, 0, 256, 256);
			}
		}
		GL11.glPopMatrix();
	}

	private String getTextureName(Ability c) {
		return this.hasFragment(c) ? c.getTexturePath(!Chromabilities.playerHasAbility(player, c)) : "Textures/Ability/unknown.png";
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed() {

	}

}
