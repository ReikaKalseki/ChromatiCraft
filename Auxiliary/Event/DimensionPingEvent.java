/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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

	public final CrystalElement color;
	private final double distance;
	private final double angle;

	public DimensionPingEvent(CrystalElement e, EntityPlayer ep, double dist, double ang) {
		super(ChromaSounds.DING, ep, 1, (float)CrystalMusicManager.instance.getDingPitchScale(e));
		distance = dist;
		angle = ang;
		color = e;
	}

	@Override
	public void fire() {
		super.fire();
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.DIMPING.ordinal(), (EntityPlayerMP)this.getEntity(), color.ordinal(), (int)distance, (int)angle);
	}

	@SideOnly(Side.CLIENT)
	public static void addPing(int color, int dist, int ang) {
		ChromaOverlays.instance.addPingOverlay(CrystalElement.elements[color], dist, ang);
	}

}
