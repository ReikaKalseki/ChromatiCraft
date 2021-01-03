/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public abstract class ChromaPotion extends Potion {

	public final int icon;

	protected ChromaPotion(int id, boolean bad, int clr, int idx) {
		super(id, bad, clr);
		icon = idx;
	}

	@Override
	public int getStatusIconIndex() {
		return icon;
	}

	@Override
	public boolean hasStatusIcon() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/potions.png");
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		int u = 16*(icon%16);
		int v = 16*(icon/16);
		ReikaGuiAPI.instance.drawTexturedModalRect(x+8, y+8, u, v, 16, 16);
		GL11.glPopAttrib();
	}

}
