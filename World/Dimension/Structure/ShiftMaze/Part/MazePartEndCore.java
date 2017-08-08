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
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import net.minecraft.block.Block;

public class MazePartEndCore extends StructurePiece {

    public MazePartEndCore(DimensionStructureGenerator s) {
        super(s);
    }

    @Override
    public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
        Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
        int ms = BlockStructureShield.BlockType.STONE.metadata;
        int ml = BlockStructureShield.BlockType.LIGHT.metadata;
        int mg = BlockStructureShield.BlockType.GLASS.metadata;

        //Layered layout ordered by decrementing X
        //Special blocks are set last.

        world.setBlock(x, y,     z,     sh, ms);
        world.setBlock(x, y + 4, z,     sh, ms);
        world.setBlock(x, y,     z + 1, sh, ms);
        world.setBlock(x, y,     z + 2, sh, ms);
        world.setBlock(x, y + 1, z + 2, sh, ms);
        world.setBlock(x, y + 2, z + 2, sh, ms);
        world.setBlock(x, y + 3, z + 2, sh, ms);
        world.setBlock(x, y + 4, z + 2, sh, ms);
        world.setBlock(x, y + 4, z + 1, sh, ms);
        world.setBlock(x, y,     z - 1, sh, ms);
        world.setBlock(x, y,     z - 2, sh, ms);
        world.setBlock(x, y + 1, z - 2, sh, ms);
        world.setBlock(x, y + 2, z - 2, sh, ms);
        world.setBlock(x, y + 3, z - 2, sh, ms);
        world.setBlock(x, y + 4, z - 2, sh, ms);
        world.setBlock(x, y + 4, z - 1, sh, ms);

        world.setAir(x, y + 1, z    );
        world.setAir(x, y + 1, z + 1);
        world.setAir(x, y + 1, z - 1);
        world.setAir(x, y + 2, z    );
        world.setAir(x, y + 2, z + 1);
        world.setAir(x, y + 2, z - 1);
        world.setAir(x, y + 3, z    );
        world.setAir(x, y + 3, z + 1);
        world.setAir(x, y + 3, z - 1);

        world.setBlock(x - 1, y,     z,     sh, ms);
        world.setBlock(x - 1, y + 5, z,     sh, ms);
        world.setBlock(x - 1, y,     z + 1, sh, ms);
        world.setBlock(x - 1, y,     z + 2, sh, ms);
        world.setBlock(x - 1, y + 1, z + 2, sh, ms);
        world.setBlock(x - 1, y + 2, z + 2, sh, ms);
        world.setBlock(x - 1, y + 3, z + 2, sh, ms);
        world.setBlock(x - 1, y + 4, z + 2, sh, ms);
        world.setBlock(x - 1, y + 5, z + 2, sh, ms);
        world.setBlock(x - 1, y + 5, z + 1, sh, ms);
        world.setBlock(x - 1, y,     z - 1, sh, ms);
        world.setBlock(x - 1, y,     z - 2, sh, ms);
        world.setBlock(x - 1, y + 1, z - 2, sh, ms);
        world.setBlock(x - 1, y + 2, z - 2, sh, ms);
        world.setBlock(x - 1, y + 3, z - 2, sh, ms);
        world.setBlock(x - 1, y + 4, z - 2, sh, ms);
        world.setBlock(x - 1, y + 5, z - 2, sh, ms);
        world.setBlock(x - 1, y + 5, z - 1, sh, ms);

        world.setAir(x - 1, y + 1, z    );
        world.setAir(x - 1, y + 1, z + 1);
        world.setAir(x - 1, y + 1, z - 1);
        world.setAir(x - 1, y + 2, z    );
        world.setAir(x - 1, y + 2, z + 1);
        world.setAir(x - 1, y + 2, z - 1);
        world.setAir(x - 1, y + 3, z    );
        world.setAir(x - 1, y + 3, z + 1);
        world.setAir(x - 1, y + 3, z - 1);
        world.setAir(x - 1, y + 4, z    );
        world.setAir(x - 1, y + 4, z + 1);
        world.setAir(x - 1, y + 4, z - 1);

        world.setBlock(x - 2, y,     z,     sh, ms);
        world.setBlock(x - 2, y + 5, z,     sh, ms);
        world.setBlock(x - 2, y,     z + 1, sh, ms);
        world.setBlock(x - 2, y,     z + 2, sh, ms);
        world.setBlock(x - 2, y,     z + 3, sh, ms);
        world.setBlock(x - 2, y + 1, z + 3, sh, ms);
        world.setBlock(x - 2, y + 2, z + 3, sh, ms);
        world.setBlock(x - 2, y + 3, z + 3, sh, ms);
        world.setBlock(x - 2, y + 4, z + 3, sh, ms);
        world.setBlock(x - 2, y + 5, z + 3, sh, ms);
        world.setBlock(x - 2, y + 5, z + 2, sh, ms);
        world.setBlock(x - 2, y + 5, z + 1, sh, ms);
        world.setBlock(x - 2, y,     z - 1, sh, ms);
        world.setBlock(x - 2, y,     z - 2, sh, ms);
        world.setBlock(x - 2, y,     z - 3, sh, ms);
        world.setBlock(x - 2, y + 1, z - 3, sh, ms);
        world.setBlock(x - 2, y + 2, z - 3, sh, ms);
        world.setBlock(x - 2, y + 3, z - 3, sh, ms);
        world.setBlock(x - 2, y + 4, z - 3, sh, ms);
        world.setBlock(x - 2, y + 5, z - 3, sh, ms);
        world.setBlock(x - 2, y + 5, z - 2, sh, ms);
        world.setBlock(x - 2, y + 5, z - 1, sh, ms);

        world.setAir(x - 2, y + 1, z    );
        world.setAir(x - 2, y + 1, z + 1);
        world.setAir(x - 2, y + 1, z - 1);
        world.setAir(x - 2, y + 1, z + 2);
        world.setAir(x - 2, y + 1, z - 2);
        world.setAir(x - 2, y + 2, z    );
        world.setAir(x - 2, y + 2, z + 1);
        world.setAir(x - 2, y + 2, z - 1);
        world.setAir(x - 2, y + 2, z + 2);
        world.setAir(x - 2, y + 2, z - 2);
        world.setAir(x - 2, y + 3, z    );
        world.setAir(x - 2, y + 3, z + 1);
        world.setAir(x - 2, y + 3, z - 1);
        world.setAir(x - 2, y + 3, z + 2);
        world.setAir(x - 2, y + 3, z - 2);
        world.setAir(x - 2, y + 4, z    );
        world.setAir(x - 2, y + 4, z + 1);
        world.setAir(x - 2, y + 4, z - 1);
        world.setAir(x - 2, y + 4, z + 2);
        world.setAir(x - 2, y + 4, z - 2);

        world.setBlock(x - 3, y,     z,     sh, ms);
        world.setBlock(x - 3, y + 5, z,     sh, ml);
        world.setBlock(x - 3, y,     z + 1, sh, ms);
        world.setBlock(x - 3, y,     z + 2, sh, ms);
        world.setBlock(x - 3, y,     z + 3, sh, ms);
        world.setBlock(x - 3, y,     z + 4, sh, ms);
        world.setBlock(x - 3, y + 1, z + 4, sh, ms);
        world.setBlock(x - 3, y + 2, z + 4, sh, ms);
        world.setBlock(x - 3, y + 3, z + 4, sh, ms);
        world.setBlock(x - 3, y + 4, z + 4, sh, ms);
        world.setBlock(x - 3, y + 5, z + 4, sh, ms);
        world.setBlock(x - 3, y + 5, z + 3, sh, ms);
        world.setBlock(x - 3, y + 5, z + 2, sh, ms);
        world.setBlock(x - 3, y + 5, z + 1, sh, ms);
        world.setBlock(x - 3, y,     z - 1, sh, ms);
        world.setBlock(x - 3, y,     z - 2, sh, ms);
        world.setBlock(x - 3, y,     z - 3, sh, ms);
        world.setBlock(x - 3, y,     z - 4, sh, ms);
        world.setBlock(x - 3, y + 1, z - 4, sh, ms);
        world.setBlock(x - 3, y + 2, z - 4, sh, ms);
        world.setBlock(x - 3, y + 3, z - 4, sh, ms);
        world.setBlock(x - 3, y + 4, z - 4, sh, ms);
        world.setBlock(x - 3, y + 5, z - 4, sh, ms);
        world.setBlock(x - 3, y + 5, z - 3, sh, ms);
        world.setBlock(x - 3, y + 5, z - 2, sh, ms);
        world.setBlock(x - 3, y + 5, z - 1, sh, ms);

        world.setAir(x - 3, y + 1, z    );
        world.setAir(x - 3, y + 1, z + 1);
        world.setAir(x - 3, y + 1, z - 1);
        world.setAir(x - 3, y + 1, z + 2);
        world.setAir(x - 3, y + 1, z - 2);
        world.setAir(x - 3, y + 1, z + 3);
        world.setAir(x - 3, y + 1, z - 3);
        world.setAir(x - 3, y + 2, z    );
        world.setAir(x - 3, y + 2, z + 1);
        world.setAir(x - 3, y + 2, z - 1);
        world.setAir(x - 3, y + 2, z + 2);
        world.setAir(x - 3, y + 2, z - 2);
        world.setAir(x - 3, y + 2, z + 3);
        world.setAir(x - 3, y + 2, z - 3);
        world.setAir(x - 3, y + 3, z    );
        world.setAir(x - 3, y + 3, z + 1);
        world.setAir(x - 3, y + 3, z - 1);
        world.setAir(x - 3, y + 3, z + 2);
        world.setAir(x - 3, y + 3, z - 2);
        world.setAir(x - 3, y + 3, z + 3);
        world.setAir(x - 3, y + 3, z - 3);
        world.setAir(x - 3, y + 4, z    );
        world.setAir(x - 3, y + 4, z + 1);
        world.setAir(x - 3, y + 4, z - 1);
        world.setAir(x - 3, y + 4, z + 2);
        world.setAir(x - 3, y + 4, z - 2);
        world.setAir(x - 3, y + 4, z + 3);
        world.setAir(x - 3, y + 4, z - 3);


        world.setBlock(x - 4, y,     z,     sh, ms);
        world.setBlock(x - 4, y + 5, z,     sh, ms);

        world.setBlock(x - 4, y,     z + 1, sh, ms);
        world.setBlock(x - 4, y,     z + 2, sh, ms);
        world.setBlock(x - 4, y,     z + 3, sh, ms);
        world.setBlock(x - 4, y,     z + 4, sh, ms);
        world.setBlock(x - 4, y + 1, z + 4, sh, ms);
        world.setBlock(x - 4, y + 2, z + 4, sh, ms);
        world.setBlock(x - 4, y + 3, z + 4, sh, ms);
        world.setBlock(x - 4, y + 4, z + 4, sh, ms);
        world.setBlock(x - 4, y + 5, z + 4, sh, ms);
        world.setBlock(x - 4, y + 5, z + 3, sh, ms);
        world.setBlock(x - 4, y + 5, z + 2, sh, ms);
        world.setBlock(x - 4, y + 5, z + 1, sh, ms);

        world.setBlock(x - 4, y,     z - 1, sh, ms);
        world.setBlock(x - 4, y,     z - 2, sh, ms);
        world.setBlock(x - 4, y,     z - 3, sh, ms);
        world.setBlock(x - 4, y,     z - 4, sh, ms);
        world.setBlock(x - 4, y + 1, z - 4, sh, ms);
        world.setBlock(x - 4, y + 2, z - 4, sh, ms);
        world.setBlock(x - 4, y + 3, z - 4, sh, ms);
        world.setBlock(x - 4, y + 4, z - 4, sh, ms);
        world.setBlock(x - 4, y + 5, z - 4, sh, ms);
        world.setBlock(x - 4, y + 5, z - 3, sh, ms);
        world.setBlock(x - 4, y + 5, z - 2, sh, ms);
        world.setBlock(x - 4, y + 5, z - 1, sh, ms);

        world.setAir(x - 4, y + 1, z    );
        world.setAir(x - 4, y + 1, z + 1);
        world.setAir(x - 4, y + 1, z - 1);
        world.setAir(x - 4, y + 1, z + 2);
        world.setAir(x - 4, y + 1, z - 2);
        world.setAir(x - 4, y + 1, z + 3);
        world.setAir(x - 4, y + 1, z - 3);
        world.setAir(x - 4, y + 2, z    );
        world.setAir(x - 4, y + 2, z + 1);
        world.setAir(x - 4, y + 2, z - 1);
        world.setAir(x - 4, y + 2, z + 2);
        world.setAir(x - 4, y + 2, z - 2);
        world.setAir(x - 4, y + 2, z + 3);
        world.setAir(x - 4, y + 2, z - 3);
        world.setAir(x - 4, y + 3, z    );
        world.setAir(x - 4, y + 3, z + 1);
        world.setAir(x - 4, y + 3, z - 1);
        world.setAir(x - 4, y + 3, z + 2);
        world.setAir(x - 4, y + 3, z - 2);
        world.setAir(x - 4, y + 3, z + 3);
        world.setAir(x - 4, y + 3, z - 3);
        world.setAir(x - 4, y + 4, z    );
        world.setAir(x - 4, y + 4, z + 1);
        world.setAir(x - 4, y + 4, z - 1);
        world.setAir(x - 4, y + 4, z + 2);
        world.setAir(x - 4, y + 4, z - 2);
        world.setAir(x - 4, y + 4, z + 3);
        world.setAir(x - 4, y + 4, z - 3);

        world.setBlock(x - 5, y,     z,     sh, ms);

        world.setBlock(x - 5, y,     z + 1, sh, ms);
        world.setBlock(x - 5, y,     z + 2, sh, ms);
        world.setBlock(x - 5, y,     z + 3, sh, ms);
        world.setBlock(x - 5, y,     z + 4, sh, ms);
        world.setBlock(x - 5, y,     z + 5, sh, ms);
        world.setBlock(x - 5, y,     z - 1, sh, ms);
        world.setBlock(x - 5, y,     z - 2, sh, ms);
        world.setBlock(x - 5, y,     z - 3, sh, ms);
        world.setBlock(x - 5, y,     z - 4, sh, ms);
        world.setBlock(x - 5, y,     z - 5, sh, ms);

        world.setBlock(x - 5, y + 4, z + 1, sh, ms);
        world.setBlock(x - 5, y + 4, z + 2, sh, ms);
        world.setBlock(x - 5, y + 4, z + 3, sh, ms);
        world.setBlock(x - 5, y + 4, z + 4, sh, ms);
        world.setBlock(x - 5, y + 4, z + 5, sh, ms);
        world.setBlock(x - 5, y + 4, z - 1, sh, ms);
        world.setBlock(x - 5, y + 4, z - 2, sh, ms);
        world.setBlock(x - 5, y + 4, z - 3, sh, ms);
        world.setBlock(x - 5, y + 4, z - 4, sh, ms);
        world.setBlock(x - 5, y + 4, z - 5, sh, ms);

        world.setBlock(x - 5, y + 1, z,     sh, mg);
        world.setBlock(x - 5, y + 2, z,     sh, mg);
        world.setBlock(x - 5, y + 3, z,     sh, mg);
        world.setBlock(x - 5, y + 4, z,     sh, ms);
        world.setBlock(x - 5, y + 1, z + 1, sh, ms);
        world.setBlock(x - 5, y + 2, z + 1, sh, ms);
        world.setBlock(x - 5, y + 3, z + 1, sh, ms);
        world.setBlock(x - 5, y + 1, z - 1, sh, ms);
        world.setBlock(x - 5, y + 2, z - 1, sh, ms);
        world.setBlock(x - 5, y + 3, z - 1, sh, ms);

        setEndBreakableStone(world, x - 5, y + 1, z + 5);
        setEndBreakableStone(world, x - 5, y + 2, z + 5);
        setEndBreakableStone(world, x - 5, y + 3, z + 5);
        setEndBreakableStone(world, x - 5, y + 1, z - 5);
        setEndBreakableStone(world, x - 5, y + 2, z - 5);
        setEndBreakableStone(world, x - 5, y + 3, z - 5);
        world.setAir(x - 5, y + 1, z + 2);
        world.setAir(x - 5, y + 1, z - 2);
        world.setAir(x - 5, y + 1, z + 3);
        world.setAir(x - 5, y + 1, z - 3);
        world.setAir(x - 5, y + 1, z + 4);
        world.setAir(x - 5, y + 1, z - 4);
        world.setAir(x - 5, y + 2, z + 2);
        world.setAir(x - 5, y + 2, z - 2);
        world.setAir(x - 5, y + 2, z + 3);
        world.setAir(x - 5, y + 2, z - 3);
        world.setAir(x - 5, y + 2, z + 4);
        world.setAir(x - 5, y + 2, z - 4);
        world.setAir(x - 5, y + 3, z + 2);
        world.setAir(x - 5, y + 3, z - 2);
        world.setAir(x - 5, y + 3, z + 3);
        world.setAir(x - 5, y + 3, z - 3);
        world.setAir(x - 5, y + 3, z + 4);
        world.setAir(x - 5, y + 3, z - 4);


        world.setBlock(x - 6, y,     z,     sh, ms);
        world.setBlock(x - 6, y + 5, z,     sh, ms);
        world.setBlock(x - 6, y,     z + 1, sh, ms);
        world.setBlock(x - 6, y,     z + 2, sh, ms);
        world.setBlock(x - 6, y,     z + 3, sh, ms);
        world.setBlock(x - 6, y,     z + 4, sh, ms);
        world.setBlock(x - 6, y,     z + 5, sh, ms);
        world.setBlock(x - 6, y,     z - 1, sh, ms);
        world.setBlock(x - 6, y,     z - 2, sh, ms);
        world.setBlock(x - 6, y,     z - 3, sh, ms);
        world.setBlock(x - 6, y,     z - 4, sh, ms);
        world.setBlock(x - 6, y,     z - 5, sh, ms);
        world.setBlock(x - 6, y + 4, z + 4, sh, ms);
        world.setBlock(x - 6, y + 4, z + 5, sh, ms);
        world.setBlock(x - 6, y + 4, z - 4, sh, ms);
        world.setBlock(x - 6, y + 4, z - 5, sh, ms);
        world.setBlock(x - 6, y + 5, z + 1, sh, ms);
        world.setBlock(x - 6, y + 5, z + 2, sh, ms);
        world.setBlock(x - 6, y + 5, z + 3, sh, ms);
        world.setBlock(x - 6, y + 5, z - 1, sh, ms);
        world.setBlock(x - 6, y + 5, z - 2, sh, ms);
        world.setBlock(x - 6, y + 5, z - 3, sh, ms);
        world.setBlock(x - 6, y + 1, z + 2, sh, ms);
        world.setBlock(x - 6, y + 2, z + 2, sh, ms);
        world.setBlock(x - 6, y + 3, z + 2, sh, ms);
        world.setBlock(x - 6, y + 4, z + 2, sh, ms);
        world.setBlock(x - 6, y + 1, z - 2, sh, ms);
        world.setBlock(x - 6, y + 2, z - 2, sh, ms);
        world.setBlock(x - 6, y + 3, z - 2, sh, ms);
        world.setBlock(x - 6, y + 4, z - 2, sh, ms);

        setEndBreakableStone(world, x - 6, y + 1, z + 5);
        setEndBreakableStone(world, x - 6, y + 2, z + 5);
        setEndBreakableStone(world, x - 6, y + 3, z + 5);
        setEndBreakableStone(world, x - 6, y + 1, z - 5);
        setEndBreakableStone(world, x - 6, y + 2, z - 5);
        setEndBreakableStone(world, x - 6, y + 3, z - 5);

        world.setAir(x - 6, y + 1, z + 3);
        world.setAir(x - 6, y + 1, z - 3);
        world.setAir(x - 6, y + 1, z + 4);
        world.setAir(x - 6, y + 1, z - 4);
        world.setAir(x - 6, y + 2, z + 3);
        world.setAir(x - 6, y + 2, z - 3);
        world.setAir(x - 6, y + 2, z + 4);
        world.setAir(x - 6, y + 2, z - 4);
        world.setAir(x - 6, y + 3, z + 3);
        world.setAir(x - 6, y + 3, z - 3);
        world.setAir(x - 6, y + 3, z + 4);
        world.setAir(x - 6, y + 3, z - 4);
        world.setAir(x - 6, y + 4, z + 3);
        world.setAir(x - 6, y + 4, z - 3);

        world.setAir(x - 6, y + 1, z    );
        world.setAir(x - 6, y + 1, z + 1);
        world.setAir(x - 6, y + 1, z - 1);
        world.setAir(x - 6, y + 2, z    );
        world.setAir(x - 6, y + 2, z + 1);
        world.setAir(x - 6, y + 2, z - 1);
        world.setAir(x - 6, y + 3, z    );
        world.setAir(x - 6, y + 3, z + 1);
        world.setAir(x - 6, y + 3, z - 1);
        world.setAir(x - 6, y + 4, z    );
        world.setAir(x - 6, y + 4, z + 1);
        world.setAir(x - 6, y + 4, z - 1);

        world.setBlock(x - 7, y,     z,     sh, ms);
        world.setBlock(x - 7, y + 5, z,     sh, ms);
        world.setBlock(x - 7, y,     z + 1, sh, ms);
        world.setBlock(x - 7, y,     z + 2, sh, ms);
        world.setBlock(x - 7, y,     z + 3, sh, ms);
        world.setBlock(x - 7, y,     z + 4, sh, ms);
        world.setBlock(x - 7, y,     z + 5, sh, ms);
        world.setBlock(x - 7, y,     z - 1, sh, ms);
        world.setBlock(x - 7, y,     z - 2, sh, ms);
        world.setBlock(x - 7, y,     z - 3, sh, ms);
        world.setBlock(x - 7, y,     z - 4, sh, ms);
        world.setBlock(x - 7, y,     z - 5, sh, ms);

        setEndBreakableStone(world, x - 7, y + 1, z + 5);
        setEndBreakableStone(world, x - 7, y + 2, z + 5);
        setEndBreakableStone(world, x - 7, y + 3, z + 5);
        setEndBreakableStone(world, x - 7, y + 1, z - 5);
        setEndBreakableStone(world, x - 7, y + 2, z - 5);
        setEndBreakableStone(world, x - 7, y + 3, z - 5);

        world.setBlock(x - 7, y + 5, z + 1, sh, ms);
        world.setBlock(x - 7, y + 5, z + 2, sh, ms);
        world.setBlock(x - 7, y + 5, z + 3, sh, ms);
        world.setBlock(x - 7, y + 5, z - 1, sh, ms);
        world.setBlock(x - 7, y + 5, z - 2, sh, ms);
        world.setBlock(x - 7, y + 5, z - 3, sh, ms);
        world.setBlock(x - 7, y + 4, z + 4, sh, ms);
        world.setBlock(x - 7, y + 4, z + 5, sh, ms);
        world.setBlock(x - 7, y + 4, z - 4, sh, ms);
        world.setBlock(x - 7, y + 4, z - 5, sh, ms);

        world.setAir(x - 7, y + 1, z    );
        world.setAir(x - 7, y + 1, z + 1);
        world.setAir(x - 7, y + 1, z - 1);
        world.setAir(x - 7, y + 1, z + 2);
        world.setAir(x - 7, y + 1, z - 2);
        world.setAir(x - 7, y + 1, z + 3);
        world.setAir(x - 7, y + 1, z - 3);
        world.setAir(x - 7, y + 1, z + 4);
        world.setAir(x - 7, y + 1, z - 4);
        world.setAir(x - 7, y + 2, z    );
        world.setAir(x - 7, y + 2, z + 1);
        world.setAir(x - 7, y + 2, z - 1);
        world.setAir(x - 7, y + 2, z + 2);
        world.setAir(x - 7, y + 2, z - 2);
        world.setAir(x - 7, y + 2, z + 3);
        world.setAir(x - 7, y + 2, z - 3);
        world.setAir(x - 7, y + 2, z + 4);
        world.setAir(x - 7, y + 2, z - 4);
        world.setAir(x - 7, y + 3, z    );
        world.setAir(x - 7, y + 3, z + 1);
        world.setAir(x - 7, y + 3, z - 1);
        world.setAir(x - 7, y + 3, z + 2);
        world.setAir(x - 7, y + 3, z - 2);
        world.setAir(x - 7, y + 3, z + 3);
        world.setAir(x - 7, y + 3, z - 3);
        world.setAir(x - 7, y + 3, z + 4);
        world.setAir(x - 7, y + 3, z - 4);
        world.setAir(x - 7, y + 4, z    );
        world.setAir(x - 7, y + 4, z + 1);
        world.setAir(x - 7, y + 4, z - 1);
        world.setAir(x - 7, y + 4, z + 2);
        world.setAir(x - 7, y + 4, z - 2);
        world.setAir(x - 7, y + 4, z + 3);
        world.setAir(x - 7, y + 4, z - 3);

        world.setBlock(x - 8, y,     z,     sh, ml);
        world.setBlock(x - 8, y + 5, z,     sh, ml);
        world.setBlock(x - 8, y,     z + 1, sh, ms);
        world.setBlock(x - 8, y,     z + 2, sh, ms);
        world.setBlock(x - 8, y,     z + 3, sh, ms);
        world.setBlock(x - 8, y,     z + 4, sh, ms);
        world.setBlock(x - 8, y,     z - 1, sh, ms);
        world.setBlock(x - 8, y,     z - 2, sh, ms);
        world.setBlock(x - 8, y,     z - 3, sh, ms);
        world.setBlock(x - 8, y,     z - 4, sh, ms);
        world.setBlock(x - 8, y + 1, z + 4, sh, ms);
        world.setBlock(x - 8, y + 2, z + 4, sh, ms);
        world.setBlock(x - 8, y + 3, z + 4, sh, ms);
        world.setBlock(x - 8, y + 1, z - 4, sh, ms);
        world.setBlock(x - 8, y + 2, z - 4, sh, ms);
        world.setBlock(x - 8, y + 3, z - 4, sh, ms);
        world.setBlock(x - 8, y + 5, z + 1, sh, ms);
        world.setBlock(x - 8, y + 5, z + 2, sh, ms);
        world.setBlock(x - 8, y + 5, z - 1, sh, ms);
        world.setBlock(x - 8, y + 5, z - 2, sh, ms);
        world.setBlock(x - 8, y + 4, z + 3, sh, ms);
        world.setBlock(x - 8, y + 4, z - 3, sh, ms);

        world.setAir(x - 8, y + 1, z    );
        world.setAir(x - 8, y + 1, z + 1);
        world.setAir(x - 8, y + 1, z - 1);
        world.setAir(x - 8, y + 1, z + 2);
        world.setAir(x - 8, y + 1, z - 2);
        world.setAir(x - 8, y + 1, z + 3);
        world.setAir(x - 8, y + 1, z - 3);
        world.setAir(x - 8, y + 2, z + 1);
        world.setAir(x - 8, y + 2, z - 1);
        world.setAir(x - 8, y + 2, z + 2);
        world.setAir(x - 8, y + 2, z - 2);
        world.setAir(x - 8, y + 2, z + 3);
        world.setAir(x - 8, y + 2, z - 3);
        world.setAir(x - 8, y + 3, z    );
        world.setAir(x - 8, y + 3, z + 1);
        world.setAir(x - 8, y + 3, z - 1);
        world.setAir(x - 8, y + 3, z + 2);
        world.setAir(x - 8, y + 3, z - 2);
        world.setAir(x - 8, y + 3, z + 3);
        world.setAir(x - 8, y + 3, z - 3);
        world.setAir(x - 8, y + 4, z    );
        world.setAir(x - 8, y + 4, z + 1);
        world.setAir(x - 8, y + 4, z - 1);
        world.setAir(x - 8, y + 4, z + 2);
        world.setAir(x - 8, y + 4, z - 2);

        world.setBlock(x - 9, y,     z,     sh, ms);
        world.setBlock(x - 9, y + 5, z,     sh, ms);
        world.setBlock(x - 9, y,     z + 1, sh, ms);
        world.setBlock(x - 9, y,     z + 2, sh, ms);
        world.setBlock(x - 9, y,     z + 3, sh, ms);
        world.setBlock(x - 9, y,     z + 4, sh, ms);
        world.setBlock(x - 9, y,     z - 1, sh, ms);
        world.setBlock(x - 9, y,     z - 2, sh, ms);
        world.setBlock(x - 9, y,     z - 3, sh, ms);
        world.setBlock(x - 9, y,     z - 4, sh, ms);
        world.setBlock(x - 9, y + 1, z + 4, sh, ms);
        world.setBlock(x - 9, y + 2, z + 4, sh, ms);
        world.setBlock(x - 9, y + 3, z + 4, sh, ms);
        world.setBlock(x - 9, y + 1, z - 4, sh, ms);
        world.setBlock(x - 9, y + 2, z - 4, sh, ms);
        world.setBlock(x - 9, y + 3, z - 4, sh, ms);
        world.setBlock(x - 9, y + 5, z + 1, sh, ms);
        world.setBlock(x - 9, y + 5, z + 2, sh, ms);
        world.setBlock(x - 9, y + 5, z - 1, sh, ms);
        world.setBlock(x - 9, y + 5, z - 2, sh, ms);
        world.setBlock(x - 9, y + 4, z + 3, sh, ms);
        world.setBlock(x - 9, y + 4, z - 3, sh, ms);

        world.setAir(x - 9, y + 1, z    );
        world.setAir(x - 9, y + 1, z + 1);
        world.setAir(x - 9, y + 1, z - 1);
        world.setAir(x - 9, y + 1, z + 2);
        world.setAir(x - 9, y + 1, z - 2);
        world.setAir(x - 9, y + 1, z + 3);
        world.setAir(x - 9, y + 1, z - 3);
        world.setAir(x - 9, y + 2, z    );
        world.setAir(x - 9, y + 2, z + 1);
        world.setAir(x - 9, y + 2, z - 1);
        world.setAir(x - 9, y + 2, z + 2);
        world.setAir(x - 9, y + 2, z - 2);
        world.setAir(x - 9, y + 2, z + 3);
        world.setAir(x - 9, y + 2, z - 3);
        world.setAir(x - 9, y + 3, z    );
        world.setAir(x - 9, y + 3, z + 1);
        world.setAir(x - 9, y + 3, z - 1);
        world.setAir(x - 9, y + 3, z + 2);
        world.setAir(x - 9, y + 3, z - 2);
        world.setAir(x - 9, y + 3, z + 3);
        world.setAir(x - 9, y + 3, z - 3);
        world.setAir(x - 9, y + 4, z    );
        world.setAir(x - 9, y + 4, z + 1);
        world.setAir(x - 9, y + 4, z - 1);
        world.setAir(x - 9, y + 4, z + 2);
        world.setAir(x - 9, y + 4, z - 2);

        world.setBlock(x - 10, y,     z,     sh, ms);
        world.setBlock(x - 10, y + 5, z,     sh, ms);
        world.setBlock(x - 10, y,     z + 1, sh, ms);
        world.setBlock(x - 10, y,     z + 2, sh, ms);
        world.setBlock(x - 10, y,     z + 3, sh, ms);
        world.setBlock(x - 10, y,     z - 1, sh, ms);
        world.setBlock(x - 10, y,     z - 2, sh, ms);
        world.setBlock(x - 10, y,     z - 3, sh, ms);
        world.setBlock(x - 10, y + 1, z + 3, sh, ms);
        world.setBlock(x - 10, y + 2, z + 3, sh, ms);
        world.setBlock(x - 10, y + 3, z + 3, sh, ms);
        world.setBlock(x - 10, y + 1, z - 3, sh, ms);
        world.setBlock(x - 10, y + 2, z - 3, sh, ms);
        world.setBlock(x - 10, y + 3, z - 3, sh, ms);
        world.setBlock(x - 10, y + 4, z + 2, sh, ms);
        world.setBlock(x - 10, y + 4, z - 2, sh, ms);
        world.setBlock(x - 10, y + 5, z + 1, sh, ms);
        world.setBlock(x - 10, y + 5, z - 1, sh, ms);

        world.setAir(x - 10, y + 1, z    );
        world.setAir(x - 10, y + 1, z + 1);
        world.setAir(x - 10, y + 1, z - 1);
        world.setAir(x - 10, y + 1, z + 2);
        world.setAir(x - 10, y + 1, z - 2);
        world.setAir(x - 10, y + 2, z    );
        world.setAir(x - 10, y + 2, z + 1);
        world.setAir(x - 10, y + 2, z - 1);
        world.setAir(x - 10, y + 2, z + 2);
        world.setAir(x - 10, y + 2, z - 2);
        world.setAir(x - 10, y + 3, z    );
        world.setAir(x - 10, y + 3, z + 1);
        world.setAir(x - 10, y + 3, z - 1);
        world.setAir(x - 10, y + 3, z + 2);
        world.setAir(x - 10, y + 3, z - 2);
        world.setAir(x - 10, y + 4, z    );
        world.setAir(x - 10, y + 4, z + 1);
        world.setAir(x - 10, y + 4, z - 1);

        world.setBlock(x - 11, y,     z,     sh, ms);
        world.setBlock(x - 11, y + 4, z,     sh, ms);
        world.setBlock(x - 11, y,     z + 1, sh, ms);
        world.setBlock(x - 11, y,     z + 2, sh, ms);
        world.setBlock(x - 11, y + 1, z + 2, sh, ms);
        world.setBlock(x - 11, y + 2, z + 2, sh, ms);
        world.setBlock(x - 11, y + 3, z + 2, sh, ms);
        world.setBlock(x - 11, y + 4, z + 2, sh, ms);
        world.setBlock(x - 11, y + 4, z + 1, sh, ms);
        world.setBlock(x - 11, y,     z - 1, sh, ms);
        world.setBlock(x - 11, y,     z - 2, sh, ms);
        world.setBlock(x - 11, y + 1, z - 2, sh, ms);
        world.setBlock(x - 11, y + 2, z - 2, sh, ms);
        world.setBlock(x - 11, y + 3, z - 2, sh, ms);
        world.setBlock(x - 11, y + 4, z - 2, sh, ms);
        world.setBlock(x - 11, y + 4, z - 1, sh, ms);
        world.setAir(x - 11, y + 1, z    );
        world.setAir(x - 11, y + 1, z + 1);
        world.setAir(x - 11, y + 1, z - 1);
        world.setAir(x - 11, y + 2, z    );
        world.setAir(x - 11, y + 2, z + 1);
        world.setAir(x - 11, y + 2, z - 1);
        world.setAir(x - 11, y + 3, z    );
        world.setAir(x - 11, y + 3, z + 1);
        world.setAir(x - 11, y + 3, z - 1);

        world.setBlock(x - 12, y,     z,     sh, ms);
        world.setBlock(x - 12, y,     z + 1, sh, ms);
        world.setBlock(x - 12, y,     z - 1, sh, ms);

        setEndBreakableStone(world, x - 12, y + 1, z    );
        setEndBreakableStone(world, x - 12, y + 1, z + 1);
        setEndBreakableStone(world, x - 12, y + 1, z - 1);
        setEndBreakableStone(world, x - 12, y + 2, z    );
        setEndBreakableStone(world, x - 12, y + 2, z + 1);
        setEndBreakableStone(world, x - 12, y + 2, z - 1);
        setEndBreakableStone(world, x - 12, y + 3, z    );
        setEndBreakableStone(world, x - 12, y + 3, z + 1);
        setEndBreakableStone(world, x - 12, y + 3, z - 1);


        //System.out.println("Core at: " + (x - 8) + ", " + (y + 2) + ", " + z);
        placeCore(x - 8, y + 2, z);
    }

    private void setEndBreakableStone(ChunkSplicedGenerationCache world, int x, int y, int z) {
        parent.addBreakable(x, y, z);
        world.setBlock(x, y, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockStructureShield.BlockType.STONE.metadata);
    }

}
