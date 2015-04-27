/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.Altar.AltarCenter;
import Reika.ChromatiCraft.World.Dimension.Structure.Altar.AltarNode;
import Reika.ChromatiCraft.World.Dimension.Structure.Altar.AltarTunnel;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class AltarGenerator extends DimensionStructureGenerator {

	private final AltarCenter center = new AltarCenter();

	int posY = 0;
	int lastPosY;

	@Override
	public void generate(World world, int chunkX, int chunkZ, CrystalElement e, Random rand) {

		posY = 10+rand.nextInt(30);
		posY = 80;
		center.generate(world, chunkX, posY, chunkZ);

		ArrayList<ForgeDirection> li = new ArrayList();
		for (int i = 2; i < 6; i++) {
			if (rand.nextInt(3) > 0)
				li.add(ForgeDirection.VALID_DIRECTIONS[i]);
		}
		this.recursiveGeneratePaths(world, chunkX, posY, chunkZ, rand, li, 0, 17);

	}

	private void recursiveGeneratePaths(World world, int x, int y, int z, Random rand, ArrayList<ForgeDirection> li, int depth, int step) {
		if (depth > 2)
			return;
		for (ForgeDirection dir : li) {
			int len = 8+rand.nextInt(32);
			AltarTunnel at = new AltarTunnel(dir, len);
			int dx = x+dir.offsetX*step;
			int dz = z+dir.offsetZ*step;
			at.generate(world, dx, y, dz);
			AltarNode node = new AltarNode(rand.nextBoolean());
			int dx2 = dx+dir.offsetX*(len+5);
			int dz2 = dz+dir.offsetZ*(len+5);
			node.generate(world, dx2, y, dz2);

			for (int i = 2; i < 6; i++) {
				ForgeDirection dir2 = ForgeDirection.VALID_DIRECTIONS[i];
				if (dir2 != dir.getOpposite() && rand.nextInt(1+step*0) == 0) {
					this.recursiveGeneratePaths(world, dx2, y, dz2, rand, ReikaJavaLibrary.makeListFrom(dir2), depth+1, 5);
				}
			}

			if (node.Yshunt) {
				for (int i = 2; i < 6; i++) {
					ForgeDirection dir2 = ForgeDirection.VALID_DIRECTIONS[i];
					if (rand.nextInt(1+step*0) == 0) {
						this.recursiveGeneratePaths(world, dx2, y+11, dz2, rand, ReikaJavaLibrary.makeListFrom(dir2), depth+1, 5);
					}
				}
			}
		}
	}

}
