package Reika.ChromatiCraft.Base;

import net.minecraft.world.gen.feature.WorldGenerator;

public abstract class ChromaWorldGenerator extends WorldGenerator {

	public abstract float getGenerationChance(int cx, int cz);

}
