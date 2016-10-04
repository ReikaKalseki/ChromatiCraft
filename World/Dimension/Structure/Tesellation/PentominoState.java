/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Tesellation;


public class PentominoState {

	public final Pentomino shape;

	private int rotation;

	public PentominoState(Pentomino p, int r) {
		shape = p;
		rotation = r;
	}

	public void rotate() {
		rotation += 90;
	}

	public int getRotation() {
		return rotation;
	}

}
