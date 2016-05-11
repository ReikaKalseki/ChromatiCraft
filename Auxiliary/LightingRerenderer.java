/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.EnumSet;

import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class LightingRerenderer implements TickHandler {

	public static final LightingRerenderer instance = new LightingRerenderer();

	private LightingRerenderer() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {/*
		World world = Minecraft.getMinecraft().theWorld;
		if (world != null) {
			if (EntityFlyingLight.lightsInWorld(world)) {
				ReikaRenderHelper.rerenderAllChunks();
			}
		}*/
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "ChromatiCraft Relighter";
	}

}
