/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Models;

import java.awt.Color;

import net.minecraft.client.model.ModelSlime;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ColorizableSlimeModel extends ModelSlime
{
	private static final String texture = "/Reika/DragonAPI/Resources/slime.png";

	private final float alpha;

	public ColorizableSlimeModel(int par1)
	{
		super(par1);
		alpha = par1 == 16 ? 1F : 0.8125F;
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(Entity e, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		int x = MathHelper.floor_double(e.posX);
		int z = MathHelper.floor_double(e.posZ);
		World world = e.worldObj;
		if (ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
			ReikaTextureHelper.bindFinalTexture(DragonAPICore.class, texture);
			int dmg = e.getEntityId()%16;
			Color c = ReikaDyeHelper.getColorFromDamage(dmg).getJavaColor();
			GL11.glColor4f(c.getRed()/255F, c.getGreen()/255F, c.getBlue()/255F, alpha);
		}
		super.render(e, par2, par3, par4, par5, par6, par7);
	}
}
