/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.TDMaze;

import net.minecraftforge.common.util.ForgeDirection;

public class TunnelPiece {

	public boolean[] connections = new boolean[6];

	public TunnelPiece() {

	}

	public TunnelPiece connect(ForgeDirection dir) {
		connections[dir.ordinal()] = true;
		return this;
	}

	public TunnelPiece disconnect(ForgeDirection dir) {
		connections[dir.ordinal()] = false;
		return this;
	}

	public static TunnelPiece omni() {
		TunnelPiece tp = new TunnelPiece();
		for (int i = 0; i < 6; i++)
			tp.connect(ForgeDirection.VALID_DIRECTIONS[i]);
		return tp;
	}

}
