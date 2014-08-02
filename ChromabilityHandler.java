/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Registry.Chromabilities;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ChromabilityHandler implements ITickHandler {

	public static final ChromabilityHandler instance = new ChromabilityHandler();

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		EntityPlayer ep = (EntityPlayer) tickData[0];
		ArrayList<Chromabilities> li = Chromabilities.getFrom(ep);
		for (int i = 0; i < li.size(); i++) {
			li.get(i).apply(ep);
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "Chromabilities";
	}

}
