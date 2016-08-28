package Reika.ChromatiCraft.Base;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.DragonAPICore;


public abstract class BlockDimensionStructureTile extends BlockContainer {

	protected BlockDimensionStructureTile(Material mat) {
		super(mat);
		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(DragonAPICore.isReikasComputer() ? ChromatiCraft.tabChromaGen : null);
	}

}
