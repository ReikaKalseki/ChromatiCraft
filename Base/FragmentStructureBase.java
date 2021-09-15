package Reika.ChromatiCraft.Base;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public abstract class FragmentStructureBase extends GeneratedStructureBase {

	public abstract Coordinate getControllerRelativeLocation();

	protected final void addLootChest(FilledBlockArray array, int x, int y, int z, ForgeDirection dir) {
		this.addLootChest(array, x, y, z, getChestMeta(dir));
	}

	protected final void addLootChest(FilledBlockArray array, int x, int y, int z, int meta) {
		array.setBlock(x, y, z, getChestGen(), meta);
		this.cache(x, y, z, getChestGen());
	}

	public void modifyLootSet(ArrayList<WeightedRandomChestContent> li) {

	}

	public void setRNG(Random r) {

	}

	public void onPlace(World world, TileEntityStructControl te) {

	}

}
