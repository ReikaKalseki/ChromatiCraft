/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.ModInteract.ReikaThaumHelper;

public class CrystalDyeAspectManager {

	public static void register() {
		Object[] asp = new Object[]{
				Aspect.AURA, 10, Aspect.AIR, 3, Aspect.CROP, 4, Aspect.CRYSTAL, 1, Aspect.EARTH, 4, Aspect.TREE, 10,
				Aspect.EXCHANGE, 5, Aspect.HEAL, 10, Aspect.LIFE, 10, Aspect.LIGHT, 2, Aspect.MAGIC, 10, Aspect.ORDER, 10,
				Aspect.PLANT, 10, /*Aspect.SEED, 2,*/ Aspect.VOID, 1
		};
		ReikaThaumHelper.addAspectsToBlock(ChromaBlocks.RAINBOWLEAF.getBlockInstance(), asp);
		ReikaThaumHelper.addAspectsToBlock(ChromaBlocks.RAINBOWSAPLING.getBlockInstance(), asp);

		Aspect[] flowers = {
				Aspect.DARKNESS,
				Aspect.LIFE,
				Aspect.POISON,
				Aspect.BEAST,
				Aspect.WATER,
				Aspect.TAINT,
				Aspect.MOTION,
				Aspect.SOUL,
				Aspect.ENTROPY,
				Aspect.FLESH,
				Aspect.SLIME,
				Aspect.GREED,
				Aspect.COLD,
				Aspect.MAGIC,
				Aspect.FIRE,
				Aspect.ORDER
		};
		for (int i = 0; i < 16; i++)
			ReikaThaumHelper.addAspectsToBlockMeta(ChromaBlocks.DYEFLOWER.getBlockInstance(), i, flowers[i], 1, Aspect.PLANT, 2);
	}

}
