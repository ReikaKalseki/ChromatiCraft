/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.StructureBase;

public abstract class ChromaStructureBase extends StructureBase {

	protected static final Block crystalstone = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
	protected static final Block shield = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

	protected Random rand;

	public void setRand(Random r) {
		rand = r;
	}

	public void resetToDefaults() {
		this.setRand(DragonAPICore.rand);
	}

	public static Block getChestGen() {
		return ChromaBlocks.LOOTCHEST.getBlockInstance();//Blocks.chest;
	}

	public static int getChestMeta(ForgeDirection dir) {
		switch(dir) {
			case EAST:
				return 1+8;
			case WEST:
				return 0+8;
			case NORTH:
				return 2+8;
			case SOUTH:
				return 3+8;
			default:
				return 0;
		}
	}

}
