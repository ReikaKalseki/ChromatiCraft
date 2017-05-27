/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Potions;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import Reika.ChromatiCraft.Base.ChromaPotion;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PotionLumarhea extends ChromaPotion {

	private static final Random rand = new Random();

	private static final long BLOWOUT_TIMER = 600; //30s
	private static final long BLOWOUT_CHARGE = 100; //5s

	public PotionLumarhea(int id) {
		super(id, true, /*0x4A7CD3*/0x2391FF, 0);
	}

	@Override
	public String getName() {
		return StatCollector.translateToLocal("chromapotion.lumarhea");
	}

	@Override
	public void affectEntity(EntityLivingBase src, EntityLivingBase e, int level, double c) {

	}

	@Override
	public void performEffect(EntityLivingBase e, int level) {
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			long id = ((e.worldObj.getTotalWorldTime()+ep.getUniqueID().getLeastSignificantBits())%BLOWOUT_TIMER+BLOWOUT_TIMER)%BLOWOUT_TIMER;
			if (id == 0) {
				if (e.worldObj.isRemote) {
					for (int i = 0; i < 32; i++) {
						this.spawnParticle(e, CrystalElement.randomElement());
					}
					ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, ep, 1, 0.5F);
				}
				else {
					PlayerElementBuffer.instance.removeFromPlayer(ep, ElementTagCompound.getUniformTag(10*this.getRemovedEnergy(ep, level)));
					ChromaSounds.ERROR.playSound(ep, 1, 0.5F);
				}
			}
			else if (BLOWOUT_TIMER-id < BLOWOUT_CHARGE || id < 10) {
				//do nothing
			}
			else {
				CrystalElement c = CrystalElement.randomElement();
				if (e.worldObj.isRemote) {
					this.spawnParticle(e, c);
					if (rand.nextInt(5) == 0)
						ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, ep, 0.125F, 2F);
				}
				else
					PlayerElementBuffer.instance.removeFromPlayer(ep, c, this.getRemovedEnergy(ep, level));
			}
		}
	}

	private int getRemovedEnergy(EntityPlayer ep, int level) {
		return Math.max(20, PlayerElementBuffer.instance.getElementCap(ep)/(10*Math.max(1, 100-level*2)));
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticle(EntityLivingBase e, CrystalElement c) {
		EntityFloatingSeedsFX fx = new EntityFloatingSeedsFX(e.worldObj, e.posX, e.posY-0.52, e.posZ, 270+e.rotationYaw, -15);
		fx.setColor(c.getColor());
		fx.setIcon(ChromaIcons.FADE_GENTLE);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public boolean isReady(int tick, int level) {
		return true;
	}

}
