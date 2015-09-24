/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class WorldGenCrystalPit extends ChromaWorldGenerator {

	private static final CrystalElement[][] colors = new CrystalElement[4][4];

	@Override
	public float getGenerationChance(int cx, int cz) {
		return 0.00625F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int r1 = 8;
		int r2 = 4;

		int primary = rand.nextInt(4);

		if (this.canGenAt(world, x, y, z, r1, r2)) {
			for (int i = -r1; i <= r1; i++) {
				for (int k = -r1; k <= r1; k++) {
					for (int j = r2; j >= -r2; j--) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r1, r2, r1)) {
							int dx = x+i;
							int dz = z+k;
							int dy = y+j;
							if (j >= 0 || ReikaMathLibrary.isPointInsideEllipse(i, j, k, r1-1, r2-1, r1-1)) {
								world.setBlock(dx, dy, dz, Blocks.air);
							}
							else {
								world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
								if (j <= -2 && rand.nextInt(3) == 0) {
									world.setBlock(dx, dy+1, dz, ChromaBlocks.CRYSTAL.getBlockInstance(), colors[primary][rand.nextInt(4)].ordinal(), 3);
								}
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	private boolean canGenAt(World world, int x, int y, int z, int r1, int r2) {
		for (int i = -r1; i <= r1; i++) {
			for (int k = -r1; k <= r1; k++) {
				for (int j = r2; j >= -r2; j--) {
					if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r1, r2, r1)) {
						int dx = x+i;
						int dz = z+k;
						int dy = y+j;
						Block b = world.getBlock(dx, dy, dz);
						if (b instanceof BlockFluidBase || b instanceof BlockLiquid) {
							return false;
						}
						if (j >= 0 || ReikaMathLibrary.isPointInsideEllipse(i, j, k, r1-1, r2-1, r1-1)) {
							//air
						}
						else { //cloak
							Block bd = world.getBlock(dx, dy-1, dz);
							if (bd.isAir(world, dx, dy-1, dz))
								return false;
							if (bd.getMaterial() != Material.rock && bd.getMaterial() != Material.grass && bd.getMaterial() != Material.iron && bd.getMaterial() != Material.ground)
								return false;
						}
					}
				}
			}
		}
		return true;
	}

	static {
		colors[0] = new CrystalElement[]{CrystalElement.BLUE, CrystalElement.RED, CrystalElement.PURPLE, CrystalElement.MAGENTA};
		colors[1] = new CrystalElement[]{CrystalElement.GREEN, CrystalElement.YELLOW, CrystalElement.CYAN, CrystalElement.LIME};
		colors[2] = new CrystalElement[]{CrystalElement.BROWN, CrystalElement.PINK, CrystalElement.ORANGE, CrystalElement.LIGHTBLUE};
		colors[3] = new CrystalElement[]{CrystalElement.BLACK, CrystalElement.GRAY, CrystalElement.LIGHTGRAY, CrystalElement.WHITE};
	}

}
