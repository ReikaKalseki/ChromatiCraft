package Reika.ChromatiCraft.Base;

import net.minecraft.world.World;

public abstract class DynamicStructurePiece extends StructureElement {

	protected final int posY;

	protected DynamicStructurePiece(DimensionStructureGenerator s, int y) {
		super(s);
		posY = y;
	}

	public abstract void generate(World world, int x, int z);

}
