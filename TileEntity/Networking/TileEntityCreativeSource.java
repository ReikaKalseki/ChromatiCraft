/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class TileEntityCreativeSource extends CrystalTransmitterBase implements CrystalSource {

	@Override
	public int getSendRange() {
		return 48;
	}

	@Override
	public boolean needsLineOfSight() {
		return true;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true;
	}

	@Override
	public int maxThroughput() {
		return 10000;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getEnergy(CrystalElement e) {
		return 1000000;
	}

	@Override
	public ElementTagCompound getEnergy() {
		return ElementTagCompound.getUniformTag(1000000);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 1000000;
	}

	@Override
	public int getTransmissionStrength() {
		return 500;
	}

	@Override
	public boolean drain(CrystalElement e, int amt) {
		return true;
	}

	@Override
	public int getSourcePriority() {
		return 0;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CREATIVEPYLON;
	}

}
