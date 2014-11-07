/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Magic.FiberNetwork;
import Reika.ChromatiCraft.Registry.CrystalElement;

public interface FiberIO {

	public void setNetwork(FiberNetwork net);
	//public FiberNetwork getNetwork();

	public boolean canNetworkOnSide(ForgeDirection dir);

	public CrystalElement getColor();

	public void onBroken();

	public void setColor(CrystalElement e);

}
