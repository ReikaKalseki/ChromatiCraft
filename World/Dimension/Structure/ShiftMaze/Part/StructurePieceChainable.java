/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Part;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public abstract class StructurePieceChainable extends StructurePiece {

    public StructurePieceChainable(DimensionStructureGenerator s) {
        super(s);
    }

    public int generateAndMoveCursor(ChunkSplicedGenerationCache world, int x, int y, int z, int cursor) {
        this.generate(world, x, y, z);
        return cursor + getCursorStepWidth();
    }

    public abstract int getCursorStepWidth();

}
