/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Tesellation;

import java.util.ArrayList;
import java.util.Collection;


public class Hole {

	public final int pieceCount;

	private final Collection<PentominoState> solution = new ArrayList();

	public Hole(int size) {
		pieceCount = size;
	}

	public void generate() {

	}

}
