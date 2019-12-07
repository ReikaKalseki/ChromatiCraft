/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public interface MultiBlockChromaTile {

	public void validateStructure();

	public ChromaStructures getPrimaryStructure();

	public Coordinate getStructureOffset();

	public boolean canStructureBeInspected();

}
