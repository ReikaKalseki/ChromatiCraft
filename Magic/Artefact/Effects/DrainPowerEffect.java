/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Artefact.Effects;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Magic.Artefact.UABombingEffect;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.ModInteract.Power.ReikaRFHelper;
import cofh.api.energy.IEnergyHandler;


public class DrainPowerEffect extends UABombingEffect.BlockEffect {

	private static final int LOCATIONS = 6;
	private static final int RANGE = 8;
	private static final int RANGE_Y = 4;

	@Override
	public void trigger(IInventory inv, TileEntity te) {
		for (int i = 0; i < LOCATIONS; i++) {
			int dx = ReikaRandomHelper.getRandomPlusMinus(te.xCoord, RANGE);
			int dy = ReikaRandomHelper.getRandomPlusMinus(te.yCoord, RANGE_Y);
			int dz = ReikaRandomHelper.getRandomPlusMinus(te.zCoord, RANGE);
			TileEntity tile = te.worldObj.getTileEntity(dx, dy, dz);
			if (tile instanceof IEnergyHandler) {
				ReikaRFHelper.drainStorage((IEnergyHandler)tile, 5000000);
			}
		}
	}

}
