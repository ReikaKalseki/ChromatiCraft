/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import java.util.ArrayList;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiAbilitySelect extends GuiScreen {

	protected final EntityPlayer player;

	private int dx = 0;
	private double offset = 0;
	private Ability ability;
	private int data = 0;

	private final ArrayList<Ability> abilities = new ArrayList();

	public GuiAbilitySelect(EntityPlayer ep) {
		player = ep;
		abilities.addAll(Chromabilities.getAbilitiesAvailableToPlayer(ep));
	}

	@Override
	public final void drawScreen(int x, int y, float f)
	{
		super.drawScreen(x, y, f);

		int dy = 51;

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/abilityselect.png");
		int tx = width/2-128;
		int ty = height/2-128+dy+15;
		GL11.glEnable(GL11.GL_BLEND);
		this.drawTexturedModalRect(tx, ty, 0, 79, 256, 177);
		GL11.glDisable(GL11.GL_BLEND);
		if (ability != null && ability.getMaxPower() > 0) {
			int sc = data*64/ability.getMaxPower();
			this.drawTexturedModalRect(width/2-12, height/2+5-sc, 116, 76-sc, 24, sc);
		}

		int sep = 22;
		int w = 50;
		int step = w+sep;
		int move = Mouse.getDWheel();
		boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		if (move > 0) {
			if (ability != null && ctrl) {
				if (data < Chromabilities.maxPower(player, ability) && Chromabilities.playerHasAbility(player, ability))
					data++;
			}
			else {
				dx += step;
				data = 0;
			}
		}
		if (move < 0) {
			if (ctrl) {
				if (data > 0)
					data--;
			}
			else {
				dx -= step;
				data = 0;
			}
		}

		if (dx > 0)
			dx = 0;
		if (dx < -(abilities.size()-1)*step)
			dx = -(abilities.size()-1)*step;

		ability = abilities.get(-dx/step);

		double sp = 270D/Math.max(1, ReikaRenderHelper.getFPS())*Math.max(1, Math.abs(offset-dx)/4)/5D;
		if (offset > dx) {
			offset -= sp;
		}
		if (offset < dx) {
			offset += sp;
		}
		if (Math.abs(offset-dx) < 1) {
			offset = dx;
		}

		for (int i = 0; i < abilities.size(); i++) {
			Ability c = abilities.get(i);
			int w2 = c == ability ? Math.max(50, 75-(int)Math.abs(dx-offset)) : w;
			String s = this.getTextureName(c);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
			GL11.glPushMatrix();
			double d = w2/256D;
			GL11.glScaled(d, d, d);
			int ax = (int)offset+width/2-w2/2+i*step;
			int ay = height/2-w2/2+dy;
			if (c == ability) {
				ay -= 2;
				ax--;
			}
			this.drawTexturedModalRect((int)(ax/d), (int)(ay/d), 0, 0, 256, 256);
			GL11.glPopMatrix();
		}

		//fontRendererObj.drawString(String.valueOf(data), 8, 8, 0xffffff);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int control) { //actually a mouse release

	}

	private String getTextureName(Ability c) {
		return c.getTexturePath(!Chromabilities.playerHasAbility(player, c));
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		if (button == 0 && ReikaGuiAPI.instance.isMouseInBox(width/2-37, width/2+37, height/2-37, height/2+37)) {
			if (ability != null && Chromabilities.playerHasAbility(player, ability)) {
				if (Chromabilities.canPlayerExecuteAt(player, ability)) {
					mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
					player.closeScreen();
					Chromabilities.triggerAbility(player, ability, data);
				}
				else {
					ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, player, 1, 1);
					ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, player, 1, 2);
				}
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed() {

	}

	@SubscribeEvent
	public void mouseEvent(MouseEvent evt) {

	}

}
