/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class TileEntityLifeEmitter extends CrystalReceiverBase {

	private static final ElementTagCompound use = new ElementTagCompound();

	static {
		use.addTag(CrystalElement.MAGENTA, 60);
		use.addTag(CrystalElement.BLACK, 15);
	}

	public static ElementTagCompound getLumensPerHundredLP() {
		return use.copy();
	}

	@Override
	public int getReceiveRange() {
		return 0;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return false;
	}

	@Override
	public int maxThroughput() {
		return 0;
	}

	@Override
	public boolean canConduct() {
		return false;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 0;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LIFEEMITTER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
