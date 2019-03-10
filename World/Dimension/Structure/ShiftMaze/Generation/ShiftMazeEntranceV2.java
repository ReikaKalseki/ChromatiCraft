/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Generation;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class ShiftMazeEntranceV2 extends DynamicStructurePiece {

    public ShiftMazeEntranceV2(DimensionStructureGenerator s) {
        super(s);
    }

    @Override
    public void generate(World world, int x, int z) {
        int y = parent.getPosY() + 1;
        this.generatePrefab(world, x, y, z-5);
        int top = world.getTopSolidOrLiquidBlock(x, z)-1;

        y += 5;
        x += 9;
        parent.offsetEntry(9, 0);

        int dx = this.generateEntrance(world, x, y, z, top);

        x += dx;
        parent.offsetEntry(dx, 0);

        this.generateSurface(world, x-2, top, z);
    }

    private void generateSurface(World world, int x, int y, int z) {
        for (int h = 0; h <= 4; h++) {
            int r = 11-h*2;
            for (int i = -r; i <= r; i++) {
                for (int k = -r; k <= r; k++) {
                    int dx = x+i;
                    int dz = z+k;
                    int dy = y+h;
                    boolean wall = Math.abs(i) >= 2 || Math.abs(k) >= 2;
                    if (wall) {
                        world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockStructureShield.BlockType.STONE.metadata, 3);
                    }
                    else {
                        world.setBlock(dx, dy, dz, STRUCTURE_AIR);
                    }
                }
            }
        }
    }

    private int generateEntrance(World world, int x, int y, int z, int top) { //generate in +X direction
        int d = 0;
        int dy = top-y+1;
        int h = y;
        while (dy > 0) {
            int step = dy < 12 ? dy : 8;
            dy -= step;
            this.generateShaft(world, x, h, z, step+3, dy == 0);
            x += 5;
            d += 5;
            h += step;
            if (dy > 0) {
                int l = 8;
                this.generateTunnel(world, x, h, z, l);
                x += l-1;
                d += l-1;
            }
        }
        return d;
    }

    private void generateTunnel(World world, int x, int y, int z, int l) {
        for (int i = -2; i <= 2; i++) {
            int dz = z+i;
            for (int k = -2; k <= 2; k++) {
                int dy = y+k;
                boolean wall = Math.abs(i) == 2 || Math.abs(k) == 2;
                for (int d = 0; d < l; d++) {
                    int dx = x+d;
                    if (wall) {
                        world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockStructureShield.BlockType.STONE.metadata, 3);
                    }
                    else {
                        world.setBlock(dx, dy, dz, STRUCTURE_AIR);
                    }
                }
            }
        }
    }

    private void generateShaft(World world, int x, int y, int z, int h, boolean top) {
        for (int i = -2; i <= 2; i++) {
            int dx = x+i+3;
            for (int k = -2; k <= 2; k++) {
                int dz = z+k;
                for (int n = -3; n < h; n++) {
                    int dy = y+n;
                    boolean wall = Math.abs(i) == 2 || Math.abs(k) == 2 || n == -3 || (n == h-1 && !top);
                    if (wall) {
                        int m = i == 0 && k == 0 ? BlockStructureShield.BlockType.LIGHT.metadata : BlockStructureShield.BlockType.STONE.metadata;
                        world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
                    }
                    else if (n == -2) {
                        world.setBlock(dx, dy, dz, Blocks.water);
                    }
                    else {
                        world.setBlock(dx, dy, dz, STRUCTURE_AIR);
                    }
                }
            }
        }

        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k < 2; k++) {
                int dz = z+i;
                int dy = y+k;
                world.setBlock(x+1, dy, dz, STRUCTURE_AIR);
            }
        }
    }

    private void generatePrefab(World world, int x, int y, int z) {
        Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
        int ms = BlockStructureShield.BlockType.STONE.metadata;

        //HellFire modifications.
        world.setBlockToAir(x+0, y+0, z+4);
        world.setBlockToAir(x+0, y+0, z+5);
        world.setBlockToAir(x+0, y+0, z+6);
        world.setBlock(x+0, y-1, z+3, b, ms, 3);
        world.setBlock(x+0, y-1, z+4, b, ms, 3);
        world.setBlock(x+0, y-1, z+5, b, ms, 3);
        world.setBlock(x+0, y-1, z+6, b, ms, 3);
        world.setBlock(x+0, y-1, z+7, b, ms, 3);
        world.setBlock(x+0, y+0, z+3, b, ms, 3);
        world.setBlock(x+0, y+0, z+7, b, ms, 3);
        //modifications end

        world.setBlock(x+0, y+1, z+3, b, ms, 3);
        world.setBlock(x+0, y+1, z+7, b, ms, 3);
        world.setBlock(x+0, y+2, z+3, b, ms, 3);
        world.setBlock(x+0, y+2, z+7, b, ms, 3);
        world.setBlock(x+0, y+3, z+0, b, ms, 3);
        world.setBlock(x+0, y+3, z+1, b, ms, 3);
        world.setBlock(x+0, y+3, z+2, b, ms, 3);
        world.setBlock(x+0, y+3, z+3, b, ms, 3);
        world.setBlock(x+0, y+3, z+7, b, ms, 3);
        world.setBlock(x+0, y+3, z+8, b, ms, 3);
        world.setBlock(x+0, y+3, z+9, b, ms, 3);
        world.setBlock(x+0, y+3, z+10, b, ms, 3);
        world.setBlock(x+0, y+4, z+0, b, ms, 3);
        world.setBlock(x+0, y+4, z+10, b, ms, 3);
        world.setBlock(x+0, y+5, z+0, b, ms, 3);
        world.setBlock(x+0, y+5, z+10, b, ms, 3);
        world.setBlock(x+0, y+6, z+0, b, ms, 3);
        world.setBlock(x+0, y+6, z+10, b, ms, 3);
        world.setBlock(x+0, y+7, z+0, b, ms, 3);
        world.setBlock(x+0, y+7, z+1, b, ms, 3);
        world.setBlock(x+0, y+7, z+2, b, ms, 3);
        world.setBlock(x+0, y+7, z+3, b, ms, 3);
        world.setBlock(x+0, y+7, z+4, b, ms, 3);
        world.setBlock(x+0, y+7, z+5, b, ms, 3);
        world.setBlock(x+0, y+7, z+6, b, ms, 3);
        world.setBlock(x+0, y+7, z+7, b, ms, 3);
        world.setBlock(x+0, y+7, z+8, b, ms, 3);
        world.setBlock(x+0, y+7, z+9, b, ms, 3);
        world.setBlock(x+0, y+7, z+10, b, ms, 3);

        //HellFire modifications.
        world.setBlockToAir(x+1, y+0, z+4);
        world.setBlockToAir(x+1, y+0, z+5);
        world.setBlockToAir(x+1, y+0, z+6);

        world.setBlock(x+1, y-1, z+3, b, ms, 3);
        world.setBlock(x+1, y-1, z+4, b, ms, 3);
        world.setBlock(x+1, y-1, z+5, b, ms, 3);
        world.setBlock(x+1, y-1, z+6, b, ms, 3);
        world.setBlock(x+1, y-1, z+7, b, ms, 3);

        world.setBlock(x+1, y+0, z+3, b, ms, 3);
        world.setBlock(x+1, y+0, z+7, b, ms, 3);
        //modifications end

        world.setBlock(x+1, y+1, z+3, b, ms, 3);
        world.setBlock(x+1, y+1, z+7, b, ms, 3);
        world.setBlock(x+1, y+2, z+3, b, ms, 3);
        world.setBlock(x+1, y+2, z+7, b, ms, 3);
        world.setBlock(x+1, y+3, z+0, b, ms, 3);
        world.setBlock(x+1, y+3, z+1, b, ms, 3);
        world.setBlock(x+1, y+3, z+2, b, ms, 3);
        world.setBlock(x+1, y+3, z+3, b, ms, 3);
        world.setBlock(x+1, y+3, z+7, b, ms, 3);
        world.setBlock(x+1, y+3, z+8, b, ms, 3);
        world.setBlock(x+1, y+3, z+9, b, ms, 3);
        world.setBlock(x+1, y+3, z+10, b, ms, 3);
        world.setBlock(x+1, y+4, z+0, b, ms, 3);
        world.setBlock(x+1, y+4, z+10, b, ms, 3);
        world.setBlock(x+1, y+5, z+0, b, ms, 3);
        world.setBlock(x+1, y+5, z+10, b, ms, 3);
        world.setBlock(x+1, y+6, z+0, b, ms, 3);
        world.setBlock(x+1, y+6, z+10, b, ms, 3);
        world.setBlock(x+1, y+7, z+0, b, ms, 3);
        world.setBlock(x+1, y+7, z+1, b, ms, 3);
        world.setBlock(x+1, y+7, z+2, b, ms, 3);
        world.setBlock(x+1, y+7, z+3, b, ms, 3);
        world.setBlock(x+1, y+7, z+4, b, ms, 3);
        world.setBlock(x+1, y+7, z+5, b, ms, 3);
        world.setBlock(x+1, y+7, z+6, b, ms, 3);
        world.setBlock(x+1, y+7, z+7, b, ms, 3);
        world.setBlock(x+1, y+7, z+8, b, ms, 3);
        world.setBlock(x+1, y+7, z+9, b, ms, 3);
        world.setBlock(x+1, y+7, z+10, b, ms, 3);

        //HellFire modifications.
        world.setBlock(x+2, y+0, z+4, b, ms, 3);
        world.setBlock(x+2, y+0, z+5, b, ms, 3);
        world.setBlock(x+2, y+0, z+6, b, ms, 3);

        world.setBlock(x+2, y-1, z+3, b, ms, 3);
        world.setBlock(x+2, y-1, z+4, b, ms, 3);
        world.setBlock(x+2, y-1, z+5, b, ms, 3);
        world.setBlock(x+2, y-1, z+6, b, ms, 3);
        world.setBlock(x+2, y-1, z+7, b, ms, 3);

        world.setBlock(x+2, y+0, z+3, b, ms, 3);
        world.setBlock(x+2, y+0, z+7, b, ms, 3);
        //modifications end


        world.setBlock(x+2, y+1, z+3, b, ms, 3);
        world.setBlock(x+2, y+1, z+7, b, ms, 3);
        world.setBlock(x+2, y+2, z+3, b, ms, 3);
        world.setBlock(x+2, y+2, z+7, b, ms, 3);
        world.setBlock(x+2, y+3, z+0, b, ms, 3);
        world.setBlock(x+2, y+3, z+1, b, ms, 3);
        world.setBlock(x+2, y+3, z+2, b, ms, 3);
        world.setBlock(x+2, y+3, z+3, b, ms, 3);
        world.setBlock(x+2, y+3, z+7, b, ms, 3);
        world.setBlock(x+2, y+3, z+8, b, ms, 3);
        world.setBlock(x+2, y+3, z+9, b, ms, 3);
        world.setBlock(x+2, y+3, z+10, b, ms, 3);
        world.setBlock(x+2, y+4, z+0, b, ms, 3);
        world.setBlock(x+2, y+4, z+10, b, ms, 3);
        world.setBlock(x+2, y+5, z+0, b, ms, 3);
        world.setBlock(x+2, y+5, z+10, b, ms, 3);
        world.setBlock(x+2, y+6, z+0, b, ms, 3);
        world.setBlock(x+2, y+6, z+10, b, ms, 3);
        world.setBlock(x+2, y+7, z+0, b, ms, 3);
        world.setBlock(x+2, y+7, z+1, b, ms, 3);
        world.setBlock(x+2, y+7, z+2, b, ms, 3);
        world.setBlock(x+2, y+7, z+3, b, ms, 3);
        world.setBlock(x+2, y+7, z+4, b, ms, 3);
        world.setBlock(x+2, y+7, z+5, b, ms, 3);
        world.setBlock(x+2, y+7, z+6, b, ms, 3);
        world.setBlock(x+2, y+7, z+7, b, ms, 3);
        world.setBlock(x+2, y+7, z+8, b, ms, 3);
        world.setBlock(x+2, y+7, z+9, b, ms, 3);
        world.setBlock(x+2, y+7, z+10, b, ms, 3);
        world.setBlock(x+3, y+1, z+3, b, ms, 3);
        world.setBlock(x+3, y+1, z+4, b, ms, 3);
        world.setBlock(x+3, y+1, z+5, b, ms, 3);
        world.setBlock(x+3, y+1, z+6, b, ms, 3);
        world.setBlock(x+3, y+1, z+7, b, ms, 3);
        world.setBlock(x+3, y+2, z+3, b, ms, 3);
        world.setBlock(x+3, y+2, z+7, b, ms, 3);
        world.setBlock(x+3, y+3, z+0, b, ms, 3);
        world.setBlock(x+3, y+3, z+1, b, ms, 3);
        world.setBlock(x+3, y+3, z+2, b, ms, 3);
        world.setBlock(x+3, y+3, z+3, b, ms, 3);
        world.setBlock(x+3, y+3, z+7, b, ms, 3);
        world.setBlock(x+3, y+3, z+8, b, ms, 3);
        world.setBlock(x+3, y+3, z+9, b, ms, 3);
        world.setBlock(x+3, y+3, z+10, b, ms, 3);
        world.setBlock(x+3, y+4, z+0, b, ms, 3);
        world.setBlock(x+3, y+4, z+10, b, ms, 3);
        world.setBlock(x+3, y+5, z+0, b, ms, 3);
        world.setBlock(x+3, y+5, z+10, b, ms, 3);
        world.setBlock(x+3, y+6, z+0, b, ms, 3);
        world.setBlock(x+3, y+6, z+10, b, ms, 3);
        world.setBlock(x+3, y+7, z+0, b, ms, 3);
        world.setBlock(x+3, y+7, z+1, b, ms, 3);
        world.setBlock(x+3, y+7, z+2, b, ms, 3);
        world.setBlock(x+3, y+7, z+3, b, ms, 3);
        world.setBlock(x+3, y+7, z+4, b, ms, 3);
        world.setBlock(x+3, y+7, z+5, b, ms, 3);
        world.setBlock(x+3, y+7, z+6, b, ms, 3);
        world.setBlock(x+3, y+7, z+7, b, ms, 3);
        world.setBlock(x+3, y+7, z+8, b, ms, 3);
        world.setBlock(x+3, y+7, z+9, b, ms, 3);
        world.setBlock(x+3, y+7, z+10, b, ms, 3);
        world.setBlock(x+4, y+2, z+3, b, ms, 3);
        world.setBlock(x+4, y+2, z+4, b, ms, 3);
        world.setBlock(x+4, y+2, z+5, b, ms, 3);
        world.setBlock(x+4, y+2, z+6, b, ms, 3);
        world.setBlock(x+4, y+2, z+7, b, ms, 3);
        world.setBlock(x+4, y+3, z+0, b, ms, 3);
        world.setBlock(x+4, y+3, z+1, b, ms, 3);
        world.setBlock(x+4, y+3, z+2, b, ms, 3);
        world.setBlock(x+4, y+3, z+3, b, ms, 3);
        world.setBlock(x+4, y+3, z+7, b, ms, 3);
        world.setBlock(x+4, y+3, z+8, b, ms, 3);
        world.setBlock(x+4, y+3, z+9, b, ms, 3);
        world.setBlock(x+4, y+3, z+10, b, ms, 3);
        world.setBlock(x+4, y+4, z+0, b, ms, 3);
        world.setBlock(x+4, y+4, z+10, b, ms, 3);
        world.setBlock(x+4, y+5, z+0, b, ms, 3);
        world.setBlock(x+4, y+5, z+10, b, ms, 3);
        world.setBlock(x+4, y+6, z+0, b, ms, 3);
        world.setBlock(x+4, y+6, z+10, b, ms, 3);
        world.setBlock(x+4, y+7, z+0, b, ms, 3);
        world.setBlock(x+4, y+7, z+1, b, ms, 3);
        world.setBlock(x+4, y+7, z+2, b, ms, 3);
        world.setBlock(x+4, y+7, z+3, b, ms, 3);
        world.setBlock(x+4, y+7, z+4, b, ms, 3);
        world.setBlock(x+4, y+7, z+5, b, ms, 3);
        world.setBlock(x+4, y+7, z+6, b, ms, 3);
        world.setBlock(x+4, y+7, z+7, b, ms, 3);
        world.setBlock(x+4, y+7, z+8, b, ms, 3);
        world.setBlock(x+4, y+7, z+9, b, ms, 3);
        world.setBlock(x+4, y+7, z+10, b, ms, 3);
        world.setBlock(x+5, y+3, z+0, b, ms, 3);
        world.setBlock(x+5, y+3, z+1, b, ms, 3);
        world.setBlock(x+5, y+3, z+2, b, ms, 3);
        world.setBlock(x+5, y+3, z+4, b, ms, 3);
        world.setBlock(x+5, y+3, z+5, b, ms, 3);
        world.setBlock(x+5, y+3, z+6, b, ms, 3);
        world.setBlock(x+5, y+3, z+8, b, ms, 3);
        world.setBlock(x+5, y+3, z+9, b, ms, 3);
        world.setBlock(x+5, y+3, z+10, b, ms, 3);
        world.setBlock(x+5, y+4, z+0, b, ms, 3);
        world.setBlock(x+5, y+4, z+10, b, ms, 3);
        world.setBlock(x+5, y+5, z+0, b, ms, 3);
        world.setBlock(x+5, y+5, z+10, b, ms, 3);
        world.setBlock(x+5, y+6, z+0, b, ms, 3);
        world.setBlock(x+5, y+6, z+10, b, ms, 3);
        world.setBlock(x+5, y+7, z+0, b, ms, 3);
        world.setBlock(x+5, y+7, z+1, b, ms, 3);
        world.setBlock(x+5, y+7, z+2, b, ms, 3);
        world.setBlock(x+5, y+7, z+3, b, ms, 3);
        world.setBlock(x+5, y+7, z+4, b, ms, 3);
        world.setBlock(x+5, y+7, z+5, b, ms, 3);
        world.setBlock(x+5, y+7, z+6, b, ms, 3);
        world.setBlock(x+5, y+7, z+7, b, ms, 3);
        world.setBlock(x+5, y+7, z+8, b, ms, 3);
        world.setBlock(x+5, y+7, z+9, b, ms, 3);
        world.setBlock(x+5, y+7, z+10, b, ms, 3);
        world.setBlock(x+6, y+3, z+0, b, ms, 3);
        world.setBlock(x+6, y+3, z+1, b, ms, 3);
        world.setBlock(x+6, y+3, z+2, b, ms, 3);
        world.setBlock(x+6, y+3, z+3, b, ms, 3);
        world.setBlock(x+6, y+3, z+4, b, ms, 3);
        world.setBlock(x+6, y+3, z+5, b, ms, 3);
        world.setBlock(x+6, y+3, z+6, b, ms, 3);
        world.setBlock(x+6, y+3, z+7, b, ms, 3);
        world.setBlock(x+6, y+3, z+8, b, ms, 3);
        world.setBlock(x+6, y+3, z+9, b, ms, 3);
        world.setBlock(x+6, y+3, z+10, b, ms, 3);
        world.setBlock(x+6, y+4, z+0, b, ms, 3);
        world.setBlock(x+6, y+4, z+1, b, ms, 3);
        world.setBlock(x+6, y+4, z+9, b, ms, 3);
        world.setBlock(x+6, y+4, z+10, b, ms, 3);
        world.setBlock(x+6, y+5, z+0, b, ms, 3);
        world.setBlock(x+6, y+5, z+1, b, ms, 3);
        world.setBlock(x+6, y+5, z+9, b, ms, 3);
        world.setBlock(x+6, y+5, z+10, b, ms, 3);
        world.setBlock(x+6, y+6, z+0, b, ms, 3);
        world.setBlock(x+6, y+6, z+1, b, ms, 3);
        world.setBlock(x+6, y+6, z+9, b, ms, 3);
        world.setBlock(x+6, y+6, z+10, b, ms, 3);
        world.setBlock(x+6, y+7, z+0, b, ms, 3);
        world.setBlock(x+6, y+7, z+1, b, ms, 3);
        world.setBlock(x+6, y+7, z+2, b, ms, 3);
        world.setBlock(x+6, y+7, z+3, b, ms, 3);
        world.setBlock(x+6, y+7, z+4, b, ms, 3);
        world.setBlock(x+6, y+7, z+5, b, ms, 3);
        world.setBlock(x+6, y+7, z+6, b, ms, 3);
        world.setBlock(x+6, y+7, z+7, b, ms, 3);
        world.setBlock(x+6, y+7, z+8, b, ms, 3);
        world.setBlock(x+6, y+7, z+9, b, ms, 3);
        world.setBlock(x+6, y+7, z+10, b, ms, 3);
        world.setBlock(x+7, y+3, z+1, b, ms, 3);
        world.setBlock(x+7, y+3, z+2, b, ms, 3);
        world.setBlock(x+7, y+3, z+3, b, ms, 3);
        world.setBlock(x+7, y+3, z+4, b, ms, 3);
        world.setBlock(x+7, y+3, z+5, b, ms, 3);
        world.setBlock(x+7, y+3, z+6, b, ms, 3);
        world.setBlock(x+7, y+3, z+7, b, ms, 3);
        world.setBlock(x+7, y+3, z+8, b, ms, 3);
        world.setBlock(x+7, y+3, z+9, b, ms, 3);
        world.setBlock(x+7, y+4, z+1, b, ms, 3);
        world.setBlock(x+7, y+4, z+2, b, ms, 3);
        world.setBlock(x+7, y+4, z+8, b, ms, 3);
        world.setBlock(x+7, y+4, z+9, b, ms, 3);
        world.setBlock(x+7, y+5, z+1, b, ms, 3);
        world.setBlock(x+7, y+5, z+2, b, ms, 3);
        world.setBlock(x+7, y+5, z+8, b, ms, 3);
        world.setBlock(x+7, y+5, z+9, b, ms, 3);
        world.setBlock(x+7, y+6, z+1, b, ms, 3);
        world.setBlock(x+7, y+6, z+2, b, ms, 3);
        world.setBlock(x+7, y+6, z+8, b, ms, 3);
        world.setBlock(x+7, y+6, z+9, b, ms, 3);
        world.setBlock(x+7, y+7, z+1, b, ms, 3);
        world.setBlock(x+7, y+7, z+2, b, ms, 3);
        world.setBlock(x+7, y+7, z+3, b, ms, 3);
        world.setBlock(x+7, y+7, z+4, b, ms, 3);
        world.setBlock(x+7, y+7, z+5, b, ms, 3);
        world.setBlock(x+7, y+7, z+6, b, ms, 3);
        world.setBlock(x+7, y+7, z+7, b, ms, 3);
        world.setBlock(x+7, y+7, z+8, b, ms, 3);
        world.setBlock(x+7, y+7, z+9, b, ms, 3);
        world.setBlock(x+8, y+3, z+2, b, ms, 3);
        world.setBlock(x+8, y+3, z+3, b, ms, 3);
        world.setBlock(x+8, y+3, z+4, b, ms, 3);
        world.setBlock(x+8, y+3, z+5, b, ms, 3);
        world.setBlock(x+8, y+3, z+6, b, ms, 3);
        world.setBlock(x+8, y+3, z+7, b, ms, 3);
        world.setBlock(x+8, y+3, z+8, b, ms, 3);
        world.setBlock(x+8, y+4, z+2, b, ms, 3);
        world.setBlock(x+8, y+4, z+3, b, ms, 3);
        world.setBlock(x+8, y+4, z+7, b, ms, 3);
        world.setBlock(x+8, y+4, z+8, b, ms, 3);
        world.setBlock(x+8, y+5, z+2, b, ms, 3);
        world.setBlock(x+8, y+5, z+3, b, ms, 3);
        world.setBlock(x+8, y+5, z+7, b, ms, 3);
        world.setBlock(x+8, y+5, z+8, b, ms, 3);
        world.setBlock(x+8, y+6, z+2, b, ms, 3);
        world.setBlock(x+8, y+6, z+3, b, ms, 3);
        world.setBlock(x+8, y+6, z+7, b, ms, 3);
        world.setBlock(x+8, y+6, z+8, b, ms, 3);
        world.setBlock(x+8, y+7, z+2, b, ms, 3);
        world.setBlock(x+8, y+7, z+3, b, ms, 3);
        world.setBlock(x+8, y+7, z+4, b, ms, 3);
        world.setBlock(x+8, y+7, z+5, b, ms, 3);
        world.setBlock(x+8, y+7, z+6, b, ms, 3);
        world.setBlock(x+8, y+7, z+7, b, ms, 3);
        world.setBlock(x+8, y+7, z+8, b, ms, 3);
        world.setBlock(x+9, y+3, z+3, b, ms, 3);
        world.setBlock(x+9, y+3, z+4, b, ms, 3);
        world.setBlock(x+9, y+3, z+5, b, ms, 3);
        world.setBlock(x+9, y+3, z+6, b, ms, 3);
        world.setBlock(x+9, y+3, z+7, b, ms, 3);
        world.setBlock(x+9, y+4, z+3, b, ms, 3);
        world.setBlock(x+9, y+4, z+7, b, ms, 3);
        world.setBlock(x+9, y+5, z+3, b, ms, 3);
        world.setBlock(x+9, y+5, z+7, b, ms, 3);
        world.setBlock(x+9, y+6, z+3, b, ms, 3);
        world.setBlock(x+9, y+6, z+7, b, ms, 3);
        world.setBlock(x+9, y+7, z+3, b, ms, 3);
        world.setBlock(x+9, y+7, z+4, b, ms, 3);
        world.setBlock(x+9, y+7, z+5, b, ms, 3);
        world.setBlock(x+9, y+7, z+6, b, ms, 3);
        world.setBlock(x+9, y+7, z+7, b, ms, 3);

        world.setBlock(x+5, y+3, z+3, b, BlockStructureShield.BlockType.LIGHT.metadata, 3);
        world.setBlock(x+5, y+3, z+7, b, BlockStructureShield.BlockType.LIGHT.metadata, 3);

        Block d = ChromaBlocks.DOOR.getBlockInstance();
        world.setBlock(x+4, y+4, z+3, d);
        world.setBlock(x+4, y+4, z+7, d);
        world.setBlock(x+3, y+4, z+3, d);
        world.setBlock(x+3, y+4, z+7, d);
        world.setBlock(x+2, y+4, z+3, d);
        world.setBlock(x+2, y+4, z+7, d);
        world.setBlock(x+1, y+4, z+3, d);
        world.setBlock(x+1, y+4, z+7, d);
        world.setBlock(x+0, y+4, z+3, d);
        world.setBlock(x+0, y+4, z+7, d);

        world.setBlock(x+0, y+1, z+4, STRUCTURE_AIR);
        world.setBlock(x+0, y+1, z+5, STRUCTURE_AIR);
        world.setBlock(x+0, y+1, z+6, STRUCTURE_AIR);
        world.setBlock(x+0, y+2, z+4, STRUCTURE_AIR);
        world.setBlock(x+0, y+2, z+5, STRUCTURE_AIR);
        world.setBlock(x+0, y+2, z+6, STRUCTURE_AIR);
        world.setBlock(x+0, y+3, z+4, STRUCTURE_AIR);
        world.setBlock(x+0, y+3, z+5, STRUCTURE_AIR);
        world.setBlock(x+0, y+3, z+6, STRUCTURE_AIR);
        world.setBlock(x+0, y+4, z+1, STRUCTURE_AIR);
        world.setBlock(x+0, y+4, z+2, STRUCTURE_AIR);
        world.setBlock(x+0, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+0, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+0, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+0, y+4, z+8, STRUCTURE_AIR);
        world.setBlock(x+0, y+4, z+9, STRUCTURE_AIR);
        world.setBlock(x+0, y+5, z+1, STRUCTURE_AIR);
        world.setBlock(x+0, y+5, z+2, STRUCTURE_AIR);
        world.setBlock(x+0, y+5, z+3, STRUCTURE_AIR);
        world.setBlock(x+0, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+0, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+0, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+0, y+5, z+7, STRUCTURE_AIR);
        world.setBlock(x+0, y+5, z+8, STRUCTURE_AIR);
        world.setBlock(x+0, y+5, z+9, STRUCTURE_AIR);
        world.setBlock(x+0, y+6, z+1, STRUCTURE_AIR);
        world.setBlock(x+0, y+6, z+2, STRUCTURE_AIR);
        world.setBlock(x+0, y+6, z+3, STRUCTURE_AIR);
        world.setBlock(x+0, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+0, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+0, y+6, z+6, STRUCTURE_AIR);
        world.setBlock(x+0, y+6, z+7, STRUCTURE_AIR);
        world.setBlock(x+0, y+6, z+8, STRUCTURE_AIR);
        world.setBlock(x+0, y+6, z+9, STRUCTURE_AIR);
        world.setBlock(x+1, y+1, z+4, STRUCTURE_AIR);
        world.setBlock(x+1, y+1, z+5, STRUCTURE_AIR);
        world.setBlock(x+1, y+1, z+6, STRUCTURE_AIR);
        world.setBlock(x+1, y+2, z+4, STRUCTURE_AIR);
        world.setBlock(x+1, y+2, z+5, STRUCTURE_AIR);
        world.setBlock(x+1, y+2, z+6, STRUCTURE_AIR);
        world.setBlock(x+1, y+3, z+4, STRUCTURE_AIR);
        world.setBlock(x+1, y+3, z+5, STRUCTURE_AIR);
        world.setBlock(x+1, y+3, z+6, STRUCTURE_AIR);
        world.setBlock(x+1, y+4, z+1, STRUCTURE_AIR);
        world.setBlock(x+1, y+4, z+2, STRUCTURE_AIR);
        world.setBlock(x+1, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+1, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+1, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+1, y+4, z+8, STRUCTURE_AIR);
        world.setBlock(x+1, y+4, z+9, STRUCTURE_AIR);
        world.setBlock(x+1, y+5, z+1, STRUCTURE_AIR);
        world.setBlock(x+1, y+5, z+2, STRUCTURE_AIR);
        world.setBlock(x+1, y+5, z+3, STRUCTURE_AIR);
        world.setBlock(x+1, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+1, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+1, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+1, y+5, z+7, STRUCTURE_AIR);
        world.setBlock(x+1, y+5, z+8, STRUCTURE_AIR);
        world.setBlock(x+1, y+5, z+9, STRUCTURE_AIR);
        world.setBlock(x+1, y+6, z+1, STRUCTURE_AIR);
        world.setBlock(x+1, y+6, z+2, STRUCTURE_AIR);
        world.setBlock(x+1, y+6, z+3, STRUCTURE_AIR);
        world.setBlock(x+1, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+1, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+1, y+6, z+6, STRUCTURE_AIR);
        world.setBlock(x+1, y+6, z+7, STRUCTURE_AIR);
        world.setBlock(x+1, y+6, z+8, STRUCTURE_AIR);
        world.setBlock(x+1, y+6, z+9, STRUCTURE_AIR);
        world.setBlock(x+2, y+1, z+4, STRUCTURE_AIR);
        world.setBlock(x+2, y+1, z+5, STRUCTURE_AIR);
        world.setBlock(x+2, y+1, z+6, STRUCTURE_AIR);
        world.setBlock(x+2, y+2, z+4, STRUCTURE_AIR);
        world.setBlock(x+2, y+2, z+5, STRUCTURE_AIR);
        world.setBlock(x+2, y+2, z+6, STRUCTURE_AIR);
        world.setBlock(x+2, y+3, z+4, STRUCTURE_AIR);
        world.setBlock(x+2, y+3, z+5, STRUCTURE_AIR);
        world.setBlock(x+2, y+3, z+6, STRUCTURE_AIR);
        world.setBlock(x+2, y+4, z+1, STRUCTURE_AIR);
        world.setBlock(x+2, y+4, z+2, STRUCTURE_AIR);
        world.setBlock(x+2, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+2, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+2, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+2, y+4, z+8, STRUCTURE_AIR);
        world.setBlock(x+2, y+4, z+9, STRUCTURE_AIR);
        world.setBlock(x+2, y+5, z+1, STRUCTURE_AIR);
        world.setBlock(x+2, y+5, z+2, STRUCTURE_AIR);
        world.setBlock(x+2, y+5, z+3, STRUCTURE_AIR);
        world.setBlock(x+2, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+2, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+2, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+2, y+5, z+7, STRUCTURE_AIR);
        world.setBlock(x+2, y+5, z+8, STRUCTURE_AIR);
        world.setBlock(x+2, y+5, z+9, STRUCTURE_AIR);
        world.setBlock(x+2, y+6, z+1, STRUCTURE_AIR);
        world.setBlock(x+2, y+6, z+2, STRUCTURE_AIR);
        world.setBlock(x+2, y+6, z+3, STRUCTURE_AIR);
        world.setBlock(x+2, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+2, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+2, y+6, z+6, STRUCTURE_AIR);
        world.setBlock(x+2, y+6, z+7, STRUCTURE_AIR);
        world.setBlock(x+2, y+6, z+8, STRUCTURE_AIR);
        world.setBlock(x+2, y+6, z+9, STRUCTURE_AIR);
        world.setBlock(x+3, y+2, z+4, STRUCTURE_AIR);
        world.setBlock(x+3, y+2, z+5, STRUCTURE_AIR);
        world.setBlock(x+3, y+2, z+6, STRUCTURE_AIR);
        world.setBlock(x+3, y+3, z+4, STRUCTURE_AIR);
        world.setBlock(x+3, y+3, z+5, STRUCTURE_AIR);
        world.setBlock(x+3, y+3, z+6, STRUCTURE_AIR);
        world.setBlock(x+3, y+4, z+1, STRUCTURE_AIR);
        world.setBlock(x+3, y+4, z+2, STRUCTURE_AIR);
        world.setBlock(x+3, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+3, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+3, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+3, y+4, z+8, STRUCTURE_AIR);
        world.setBlock(x+3, y+4, z+9, STRUCTURE_AIR);
        world.setBlock(x+3, y+5, z+1, STRUCTURE_AIR);
        world.setBlock(x+3, y+5, z+2, STRUCTURE_AIR);
        world.setBlock(x+3, y+5, z+3, STRUCTURE_AIR);
        world.setBlock(x+3, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+3, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+3, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+3, y+5, z+7, STRUCTURE_AIR);
        world.setBlock(x+3, y+5, z+8, STRUCTURE_AIR);
        world.setBlock(x+3, y+5, z+9, STRUCTURE_AIR);
        world.setBlock(x+3, y+6, z+1, STRUCTURE_AIR);
        world.setBlock(x+3, y+6, z+2, STRUCTURE_AIR);
        world.setBlock(x+3, y+6, z+3, STRUCTURE_AIR);
        world.setBlock(x+3, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+3, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+3, y+6, z+6, STRUCTURE_AIR);
        world.setBlock(x+3, y+6, z+7, STRUCTURE_AIR);
        world.setBlock(x+3, y+6, z+8, STRUCTURE_AIR);
        world.setBlock(x+3, y+6, z+9, STRUCTURE_AIR);
        world.setBlock(x+4, y+3, z+4, STRUCTURE_AIR);
        world.setBlock(x+4, y+3, z+5, STRUCTURE_AIR);
        world.setBlock(x+4, y+3, z+6, STRUCTURE_AIR);
        world.setBlock(x+4, y+4, z+1, STRUCTURE_AIR);
        world.setBlock(x+4, y+4, z+2, STRUCTURE_AIR);
        world.setBlock(x+4, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+4, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+4, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+4, y+4, z+8, STRUCTURE_AIR);
        world.setBlock(x+4, y+4, z+9, STRUCTURE_AIR);
        world.setBlock(x+4, y+5, z+1, STRUCTURE_AIR);
        world.setBlock(x+4, y+5, z+2, STRUCTURE_AIR);
        world.setBlock(x+4, y+5, z+3, STRUCTURE_AIR);
        world.setBlock(x+4, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+4, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+4, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+4, y+5, z+7, STRUCTURE_AIR);
        world.setBlock(x+4, y+5, z+8, STRUCTURE_AIR);
        world.setBlock(x+4, y+5, z+9, STRUCTURE_AIR);
        world.setBlock(x+4, y+6, z+1, STRUCTURE_AIR);
        world.setBlock(x+4, y+6, z+2, STRUCTURE_AIR);
        world.setBlock(x+4, y+6, z+3, STRUCTURE_AIR);
        world.setBlock(x+4, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+4, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+4, y+6, z+6, STRUCTURE_AIR);
        world.setBlock(x+4, y+6, z+7, STRUCTURE_AIR);
        world.setBlock(x+4, y+6, z+8, STRUCTURE_AIR);
        world.setBlock(x+4, y+6, z+9, STRUCTURE_AIR);
        world.setBlock(x+5, y+4, z+1, STRUCTURE_AIR);
        world.setBlock(x+5, y+4, z+2, STRUCTURE_AIR);
        world.setBlock(x+5, y+4, z+3, STRUCTURE_AIR);
        world.setBlock(x+5, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+5, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+5, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+5, y+4, z+7, STRUCTURE_AIR);
        world.setBlock(x+5, y+4, z+8, STRUCTURE_AIR);
        world.setBlock(x+5, y+4, z+9, STRUCTURE_AIR);
        world.setBlock(x+5, y+5, z+1, STRUCTURE_AIR);
        world.setBlock(x+5, y+5, z+2, STRUCTURE_AIR);
        world.setBlock(x+5, y+5, z+3, STRUCTURE_AIR);
        world.setBlock(x+5, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+5, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+5, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+5, y+5, z+7, STRUCTURE_AIR);
        world.setBlock(x+5, y+5, z+8, STRUCTURE_AIR);
        world.setBlock(x+5, y+5, z+9, STRUCTURE_AIR);
        world.setBlock(x+5, y+6, z+1, STRUCTURE_AIR);
        world.setBlock(x+5, y+6, z+2, STRUCTURE_AIR);
        world.setBlock(x+5, y+6, z+3, STRUCTURE_AIR);
        world.setBlock(x+5, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+5, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+5, y+6, z+6, STRUCTURE_AIR);
        world.setBlock(x+5, y+6, z+7, STRUCTURE_AIR);
        world.setBlock(x+5, y+6, z+8, STRUCTURE_AIR);
        world.setBlock(x+5, y+6, z+9, STRUCTURE_AIR);
        world.setBlock(x+6, y+4, z+2, STRUCTURE_AIR);
        world.setBlock(x+6, y+4, z+3, STRUCTURE_AIR);
        world.setBlock(x+6, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+6, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+6, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+6, y+4, z+7, STRUCTURE_AIR);
        world.setBlock(x+6, y+4, z+8, STRUCTURE_AIR);
        world.setBlock(x+6, y+5, z+2, STRUCTURE_AIR);
        world.setBlock(x+6, y+5, z+3, STRUCTURE_AIR);
        world.setBlock(x+6, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+6, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+6, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+6, y+5, z+7, STRUCTURE_AIR);
        world.setBlock(x+6, y+5, z+8, STRUCTURE_AIR);
        world.setBlock(x+6, y+6, z+2, STRUCTURE_AIR);
        world.setBlock(x+6, y+6, z+3, STRUCTURE_AIR);
        world.setBlock(x+6, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+6, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+6, y+6, z+6, STRUCTURE_AIR);
        world.setBlock(x+6, y+6, z+7, STRUCTURE_AIR);
        world.setBlock(x+6, y+6, z+8, STRUCTURE_AIR);
        world.setBlock(x+7, y+4, z+3, STRUCTURE_AIR);
        world.setBlock(x+7, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+7, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+7, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+7, y+4, z+7, STRUCTURE_AIR);
        world.setBlock(x+7, y+5, z+3, STRUCTURE_AIR);
        world.setBlock(x+7, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+7, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+7, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+7, y+5, z+7, STRUCTURE_AIR);
        world.setBlock(x+7, y+6, z+3, STRUCTURE_AIR);
        world.setBlock(x+7, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+7, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+7, y+6, z+6, STRUCTURE_AIR);
        world.setBlock(x+7, y+6, z+7, STRUCTURE_AIR);
        world.setBlock(x+8, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+8, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+8, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+8, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+8, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+8, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+8, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+8, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+8, y+6, z+6, STRUCTURE_AIR);
        world.setBlock(x+9, y+4, z+4, STRUCTURE_AIR);
        world.setBlock(x+9, y+4, z+5, STRUCTURE_AIR);
        world.setBlock(x+9, y+4, z+6, STRUCTURE_AIR);
        world.setBlock(x+9, y+5, z+4, STRUCTURE_AIR);
        world.setBlock(x+9, y+5, z+5, STRUCTURE_AIR);
        world.setBlock(x+9, y+5, z+6, STRUCTURE_AIR);
        world.setBlock(x+9, y+6, z+4, STRUCTURE_AIR);
        world.setBlock(x+9, y+6, z+5, STRUCTURE_AIR);
        world.setBlock(x+9, y+6, z+6, STRUCTURE_AIR);
    }

}
