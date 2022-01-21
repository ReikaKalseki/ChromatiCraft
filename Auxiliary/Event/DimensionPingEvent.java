/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaOverlays;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledSoundEvent;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DimensionPingEvent extends ScheduledSoundEvent {

	private final double distance;
	private final double angle;

	public DimensionPingEvent(ChromaSounds s, float p, EntityPlayer ep, double dist, double ang) {
		super(s, ep, 1, p);
		distance = dist;
		angle = ang;
	}

	@Override
	public final void fire() {
		super.fire();
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.DIMPING.ordinal(), (EntityPlayerMP)this.getEntity(), this.getDataInt(), (int)distance, (int)angle);
	}

	protected int getDataInt() {
		return -1;
	}

	public static class StructurePingEvent extends DimensionPingEvent {

		public final CrystalElement color;

		public StructurePingEvent(CrystalElement e, EntityPlayer ep, double dist, double ang) {
			super(ChromaSounds.DING, (float)CrystalMusicManager.instance.getDingPitchScale(e), ep, dist, ang);
			color = e;
		}

		@Override
		protected int getDataInt() {
			return color.ordinal();
		}

	}

	@SideOnly(Side.CLIENT)
	public static void addPing(int idx, int dist, int ang) {
		ChromaOverlays.instance.addPingOverlay(idx, dist, ang);
	}

}


