/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Orbit;
import Reika.DragonAPI.Instantiable.ParticleController.OrbitMotionController;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemEfficiencyCrystal extends ItemChromaTool {

	private static final String TAG = "last_efficiency";

	public ItemEfficiencyCrystal(int index) {
		super(index);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (e instanceof EntityPlayer) {
			e.getEntityData().setLong(TAG, world.getTotalWorldTime());
			if (world.isRemote)
				this.doHeldFX(is, e);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doHeldFX(ItemStack is, Entity e) {
		if (itemRand.nextInt(4*4) == 0) {
			double r = ReikaRandomHelper.getRandomBetween(1D, 3D);
			double ec = itemRand.nextDouble()*0.75;
			double i = ReikaRandomHelper.getRandomPlusMinus(0D, 90D);
			double raan = itemRand.nextDouble()*360;
			Orbit o = new Orbit(r, ec, i, raan, 0, 0);
			OrbitMotionController p = new OrbitMotionController(o, e.posX, e.posY, e.posZ).trackEntity(e);
			p.thetaSpeed = ReikaRandomHelper.getRandomBetween(1.5, 5);
			int l = 60+itemRand.nextInt(120);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.25);
			EntityFX fx = new EntityBlurFX(e.worldObj, e.posX, e.posY, e.posZ).setPositionController(p).setLife(l).setIcon(ChromaIcons.CHROMA).setBasicBlend().setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public static boolean isActive(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong(TAG) < 20;
	}

}
