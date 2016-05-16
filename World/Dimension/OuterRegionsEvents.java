/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.ChromaTeleporter;
import Reika.ChromatiCraft.Entity.EntityDimensionFlare;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class OuterRegionsEvents {

	public static OuterRegionsEvents instance = new OuterRegionsEvents();

	private OuterRegionsEvents() {

	}

	public void tickPlayerInOuterRegion(EntityPlayer ep) {
		if (ep.worldObj.rand.nextInt(100) == 0) {
			this.spawnFlare(ep.worldObj, ep, 256);
		}
	}

	private EntityDimensionFlare spawnFlare(World world, EntityPlayer ep, double r) {
		EntityDimensionFlare f = new EntityDimensionFlare(world, ep);
		double dx = ReikaRandomHelper.getRandomBetween(ep.posX, r);
		double dy = ReikaRandomHelper.getRandomBetween(ep.posY, r/2);
		double dz = ReikaRandomHelper.getRandomBetween(ep.posZ, r);
		f.setLocationAndAngles(dx, dy, dz, 0, 0);
		if (!world.isRemote) {
			world.spawnEntityInWorld(f);
		}
		return f;
	}

	public boolean doRejectAttack(EntityDimensionFlare e, EntityPlayer ep) {
		return this.doRejectAttack(e, ep, Math.max(3, ep.getHealth()/2F));
	}

	public boolean doRejectAttack(EntityDimensionFlare e, EntityPlayer ep, float dmg) {
		if (ep.worldObj.isRemote) {
			this.doRejectAttackFX(e, ep);
			return false;
		}
		else {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.FLAREATTACK.ordinal(), (EntityPlayerMP)ep, e.getEntityId());
			if (ep.getHealth() <= dmg) {
				if (!ep.capabilities.isCreativeMode)
					ReikaEntityHelper.transferEntityToDimension(ep, 0, new ChromaTeleporter(0));
				return true;
			}
			else {
				if (!ep.capabilities.isCreativeMode)
					ChromaAux.doPylonAttack(CrystalElement.WHITE, ep, dmg, false);
				ReikaEntityHelper.knockbackEntity(e, ep, ep.worldObj.rand.nextDouble());
				return false;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doRejectAttackFX(EntityDimensionFlare e, EntityPlayer ep) {
		ReikaSoundHelper.playClientSound(ChromaSounds.FLAREATTACK, ep, 1, e.getIdentity().soundPitch);
		int n = 4+ep.worldObj.rand.nextInt(4);
		LightningBolt b = new LightningBolt(new DecimalPosition(e), new DecimalPosition(ep).offset(0, -0.8, 0), n);
		b.variance *= 2;
		b.update();
		int clr = e.getIdentity().flareColor;
		for (int i = 0; i < b.nsteps; i++) {
			DecimalPosition pos1 = b.getPosition(i);
			DecimalPosition pos2 = b.getPosition(i+1);
			for (double r = 0; r <= 1; r += 0.03125) {
				double f = i+r;
				float s = 1.75F;//(float)(1.25+1.75*f/(2D*b.nsteps));
				int l = 20;
				int a = (int)(2*f);
				DecimalPosition dd = DecimalPosition.interpolate(pos1, pos2, r);
				EntityFX fx = new EntityBlurFX(ep.worldObj, dd.xCoord, dd.yCoord, dd.zCoord).setScale(s).setColor(clr).setLife(l).setRapidExpand().freezeLife(a);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	public void doFlareAggro(EntityPlayer ep) {
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(ep, 96);
		List<EntityDimensionFlare> li = ep.worldObj.getEntitiesWithinAABB(EntityDimensionFlare.class, box);
		for (EntityDimensionFlare e : li) {
			e.aggroTo(ep);
		}
		int n = 1+ep.worldObj.rand.nextInt(12);
		for (int i = 0; i < n; i++) {
			EntityDimensionFlare e = this.spawnFlare(ep.worldObj, ep, 64);
			e.aggroTo(ep);
		}
	}

}
