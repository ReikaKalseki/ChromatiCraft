/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.LightPanel;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanelGenerator;


public class LightPanelEntrance extends DynamicStructurePiece {

	//private int verticalDisplacement = 0;

	public LightPanelEntrance(LightPanelGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		int y = parent.getPosY()-1;
		int top = world.getTopSolidOrLiquidBlock(x, z)+1;

		/*
		int dy = y;
		int nstair = 0;

		dy += LightPanelStairBottom.HEIGHT;
		while (dy+LightPanelStairSection.HEIGHT < top) {
			dy += LightPanelStairSection.HEIGHT;
			nstair++;
		}
		int gentop = dy+LightPanelStairTop.HEIGHT;
		if (gentop > y+1) {
			verticalDisplacement = gentop-y-1;
		}

		y -= verticalDisplacement;
		 */

		new LightPanelStairBottom(parent).generate(world, x, y, z);
		y += LightPanelStairBottom.HEIGHT;
		while (y+LightPanelStairSection.HEIGHT < top) {
			new LightPanelStairSection(parent).generate(world, x, y, z);
			y += LightPanelStairSection.HEIGHT;
		}
		new LightPanelStairTop(parent).generate(world, x, y, z);
		y += LightPanelStairTop.HEIGHT;
		int over = y-top-2;
		if (over > 2) {
			this.generateMountain(world, x, z, top, over);
		}
	}
	/*
	public int getDisplacement() {
		return verticalDisplacement;
	}*/

	private void generateMountain(World world, int x, int z, int top, int over) {
		int diff = over+4;
		int min = top+over-diff;
		int r = over*8;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				if (Math.abs(i) > 2 || Math.abs(k) > 2) {
					int di = Math.max(0, Math.abs(i)-3);
					int dk = Math.max(0, Math.abs(k)-3);
					int y = min+diff-Math.max(di, dk)/2;//(int)(diff*ReikaMathLibrary.cosInterpolation(-r*r, r*r, (di*di+dk*dk)*8));
					int dx = x+i;
					int dz = z+k;
					int loctop = world.getTopSolidOrLiquidBlock(dx, dz);
					if (y > loctop) {
						for (int dy = loctop; dy <= y+12; dy++) {
							Block at = world.getBlock(dx, dy, dz);
							if (at != ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
								Block b = Blocks.stone;
								if (y == dy)
									b = Blocks.grass;
								if (y > dy)
									b = Blocks.air;
								world.setBlock(dx, dy, dz, b);
							}
						}
					}
				}
			}
		}
	}

}
