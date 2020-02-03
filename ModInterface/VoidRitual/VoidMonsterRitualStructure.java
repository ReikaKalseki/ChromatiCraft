package Reika.ChromatiCraft.ModInterface.VoidRitual;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.BlockCheck;
import Reika.DragonAPI.Interfaces.BlockCheck.TileEntityCheck;


public class VoidMonsterRitualStructure extends ChromaStructureBase {

	private static final TileEntityLumenWire[] directions = new TileEntityLumenWire[4];
	private static final HashMap<Coordinate, ForgeDirection> wires = new HashMap();

	static {
		wires.put(new Coordinate(0, 3, 3), ForgeDirection.SOUTH);
		wires.put(new Coordinate(0, 3, 9), ForgeDirection.NORTH);
		wires.put(new Coordinate(0, 3, 11), ForgeDirection.SOUTH);
		wires.put(new Coordinate(0, 3, 17), ForgeDirection.NORTH);
		wires.put(new Coordinate(0, 3, 19), ForgeDirection.SOUTH);
		wires.put(new Coordinate(0, 3, 25), ForgeDirection.NORTH);
		wires.put(new Coordinate(3, 3, 0), ForgeDirection.EAST);
		wires.put(new Coordinate(3, 3, 28), ForgeDirection.EAST);
		wires.put(new Coordinate(9, 3, 0), ForgeDirection.WEST);
		wires.put(new Coordinate(9, 3, 28), ForgeDirection.WEST);
		wires.put(new Coordinate(11, 3, 0), ForgeDirection.EAST);
		wires.put(new Coordinate(11, 3, 28), ForgeDirection.EAST);
		wires.put(new Coordinate(17, 3, 0), ForgeDirection.WEST);
		wires.put(new Coordinate(17, 3, 28), ForgeDirection.WEST);
		wires.put(new Coordinate(19, 3, 0), ForgeDirection.EAST);
		wires.put(new Coordinate(19, 3, 28), ForgeDirection.EAST);
		wires.put(new Coordinate(25, 3, 0), ForgeDirection.WEST);
		wires.put(new Coordinate(25, 3, 28), ForgeDirection.WEST);
		wires.put(new Coordinate(28, 3, 3), ForgeDirection.SOUTH);
		wires.put(new Coordinate(28, 3, 9), ForgeDirection.NORTH);
		wires.put(new Coordinate(28, 3, 11), ForgeDirection.SOUTH);
		wires.put(new Coordinate(28, 3, 17), ForgeDirection.NORTH);
		wires.put(new Coordinate(28, 3, 19), ForgeDirection.SOUTH);
		wires.put(new Coordinate(28, 3, 25), ForgeDirection.NORTH);

		wires.put(new Coordinate(10, 7, 11), ForgeDirection.SOUTH);
		wires.put(new Coordinate(10, 7, 17), ForgeDirection.NORTH);
		wires.put(new Coordinate(11, 7, 10), ForgeDirection.EAST);
		wires.put(new Coordinate(11, 7, 18), ForgeDirection.EAST);
		wires.put(new Coordinate(17, 7, 10), ForgeDirection.WEST);
		wires.put(new Coordinate(17, 7, 18), ForgeDirection.WEST);
		wires.put(new Coordinate(18, 7, 11), ForgeDirection.SOUTH);
		wires.put(new Coordinate(18, 7, 17), ForgeDirection.NORTH);
	}

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block altar = ChromaBlocks.COLORALTAR.getBlockInstance();

		x -= 14;
		z -= 14;
		y -= 8;

		for (int i = 0; i <= 28; i++) {
			for (int k = 0; k <= 28; k++) {
				for (int j = 2; j <= 8; j++) {
					array.setEmpty(x+i, y+j, z+k, false, true);
				}
			}
		}

		array.setBlock(x + 0, y + 2, z + 0, crystalstone, 8);
		array.setBlock(x + 0, y + 2, z + 2, crystalstone, 12);
		array.setBlock(x + 0, y + 2, z + 10, crystalstone, 2);
		array.setBlock(x + 0, y + 2, z + 18, crystalstone, 2);
		array.setBlock(x + 0, y + 2, z + 26, crystalstone, 12);
		array.setBlock(x + 0, y + 2, z + 28, crystalstone, 8);
		array.setBlock(x + 0, y + 3, z + 0, crystalstone, 2);
		array.setBlock(x + 0, y + 3, z + 1, crystalstone, 1);
		array.setBlock(x + 0, y + 3, z + 2, crystalstone, 7);
		array.setBlock(x + 0, y + 3, z + 10, crystalstone, 7);
		array.setBlock(x + 0, y + 3, z + 18, crystalstone, 7);
		array.setBlock(x + 0, y + 3, z + 26, crystalstone, 7);
		array.setBlock(x + 0, y + 3, z + 27, crystalstone, 1);
		array.setBlock(x + 0, y + 3, z + 28, crystalstone, 2);
		array.setBlock(x + 0, y + 4, z + 0, crystalstone, 2);
		array.setBlock(x + 0, y + 4, z + 28, crystalstone, 2);
		array.setBlock(x + 0, y + 5, z + 0, crystalstone, 13);
		array.setBlock(x + 0, y + 5, z + 28, crystalstone, 13);
		array.setBlock(x + 1, y + 3, z + 0, crystalstone, 1);
		array.setBlock(x + 1, y + 3, z + 28, crystalstone, 1);
		array.setBlock(x + 2, y + 2, z + 0, crystalstone, 12);
		array.setBlock(x + 2, y + 2, z + 28, crystalstone, 12);
		array.setBlock(x + 2, y + 3, z + 0, crystalstone, 7);
		array.setBlock(x + 2, y + 3, z + 28, crystalstone, 7);
		array.setBlock(x + 4, y + 1, z + 12, crystalstone, 0);
		array.setBlock(x + 4, y + 1, z + 13, shield, 1);
		array.setBlock(x + 4, y + 1, z + 14, shield, 1);
		array.setBlock(x + 4, y + 1, z + 15, shield, 1);
		array.setBlock(x + 4, y + 1, z + 16, crystalstone, 0);
		array.setBlock(x + 4, y + 2, z + 12, crystalstone, 2);
		array.setBlock(x + 4, y + 2, z + 16, crystalstone, 2);
		array.setBlock(x + 4, y + 3, z + 12, crystalstone, 5);
		array.setBlock(x + 4, y + 3, z + 16, crystalstone, 5);
		array.setBlock(x + 5, y + 0, z + 13, shield, 0);
		array.setBlock(x + 5, y + 0, z + 14, shield, 0);
		array.setBlock(x + 5, y + 0, z + 15, shield, 0);
		array.setBlock(x + 5, y + 1, z + 12, shield, 1);
		array.setBlock(x + 5, y + 1, z + 16, shield, 1);
		array.setBlock(x + 6, y + 0, z + 13, shield, 0);
		array.setBlock(x + 6, y + 0, z + 14, crystalstone, 13);
		array.setBlock(x + 6, y + 0, z + 15, shield, 0);
		array.setBlock(x + 6, y + 1, z + 12, shield, 1);
		array.setBlock(x + 6, y + 1, z + 16, shield, 1);
		array.setBlock(x + 7, y + 0, z + 13, shield, 0);
		array.setBlock(x + 7, y + 0, z + 14, shield, 0);
		array.setBlock(x + 7, y + 0, z + 15, shield, 0);
		array.setBlock(x + 7, y + 1, z + 12, shield, 1);
		array.setBlock(x + 7, y + 1, z + 16, shield, 1);
		array.setBlock(x + 8, y + 1, z + 8, crystalstone, 0);
		array.setBlock(x + 8, y + 1, z + 9, shield, 1);
		array.setBlock(x + 8, y + 1, z + 10, shield, 1);
		array.setBlock(x + 8, y + 1, z + 11, shield, 1);
		array.setBlock(x + 8, y + 1, z + 12, shield, 1);
		array.setBlock(x + 8, y + 1, z + 13, shield, 1);
		array.setBlock(x + 8, y + 1, z + 14, crystalstone, 6);
		array.setBlock(x + 8, y + 1, z + 15, crystalstone, 15);
		array.setBlock(x + 8, y + 1, z + 16, crystalstone, 15);
		array.setBlock(x + 8, y + 1, z + 17, shield, 1);
		array.setBlock(x + 8, y + 1, z + 18, shield, 1);
		array.setBlock(x + 8, y + 1, z + 19, shield, 1);
		array.setBlock(x + 8, y + 1, z + 20, crystalstone, 0);
		array.setBlock(x + 8, y + 2, z + 8, crystalstone, 6);
		array.setBlock(x + 8, y + 2, z + 9, crystalstone, 1);
		array.setBlock(x + 8, y + 2, z + 10, crystalstone, 1);
		array.setBlock(x + 8, y + 2, z + 11, crystalstone, 1);
		array.setBlock(x + 8, y + 2, z + 12, crystalstone, 1);
		array.setBlock(x + 8, y + 2, z + 13, crystalstone, 1);
		array.setBlock(x + 8, y + 2, z + 14, crystalstone, 6);
		array.setBlock(x + 8, y + 2, z + 16, crystalstone, 6);
		array.setBlock(x + 8, y + 2, z + 17, crystalstone, 1);
		array.setBlock(x + 8, y + 2, z + 18, crystalstone, 1);
		array.setBlock(x + 8, y + 2, z + 19, crystalstone, 1);
		array.setBlock(x + 8, y + 2, z + 20, crystalstone, 6);
		array.setBlock(x + 9, y + 1, z + 8, shield, 1);
		array.setBlock(x + 9, y + 1, z + 9, shield, 0);
		array.setBlock(x + 9, y + 1, z + 10, shield, 0);
		array.setBlock(x + 9, y + 1, z + 11, shield, 0);
		array.setBlock(x + 9, y + 1, z + 12, shield, 0);
		array.setBlock(x + 9, y + 1, z + 13, shield, 0);
		array.setBlock(x + 9, y + 1, z + 14, shield, 0);
		array.setBlock(x + 9, y + 1, z + 15, shield, 0);
		array.setBlock(x + 9, y + 1, z + 16, shield, 0);
		array.setBlock(x + 9, y + 1, z + 17, shield, 0);
		array.setBlock(x + 9, y + 1, z + 18, shield, 0);
		array.setBlock(x + 9, y + 1, z + 19, shield, 0);
		array.setBlock(x + 9, y + 1, z + 20, shield, 1);
		array.setBlock(x + 9, y + 2, z + 8, crystalstone, 1);
		array.setBlock(x + 9, y + 2, z + 14, crystalstone, 1);
		array.setBlock(x + 9, y + 2, z + 16, crystalstone, 1);
		array.setBlock(x + 9, y + 2, z + 20, crystalstone, 1);
		array.setBlock(x + 10, y + 1, z + 8, shield, 1);
		array.setBlock(x + 10, y + 1, z + 9, shield, 0);
		array.setBlock(x + 10, y + 1, z + 10, shield, 0);
		array.setBlock(x + 10, y + 1, z + 11, shield, 0);
		array.setBlock(x + 10, y + 1, z + 12, shield, 0);
		array.setBlock(x + 10, y + 1, z + 13, shield, 0);
		array.setBlock(x + 10, y + 1, z + 14, shield, 0);
		array.setBlock(x + 10, y + 1, z + 15, shield, 0);
		array.setBlock(x + 10, y + 1, z + 16, shield, 0);
		array.setBlock(x + 10, y + 1, z + 17, shield, 0);
		array.setBlock(x + 10, y + 1, z + 18, shield, 0);
		array.setBlock(x + 10, y + 1, z + 19, shield, 0);
		array.setBlock(x + 10, y + 1, z + 20, shield, 1);
		array.setBlock(x + 10, y + 2, z + 0, crystalstone, 2);
		array.setBlock(x + 10, y + 2, z + 8, crystalstone, 1);
		array.setBlock(x + 10, y + 2, z + 10, crystalstone, 8);
		array.setBlock(x + 10, y + 2, z + 11, crystalstone, 12);
		array.setBlock(x + 10, y + 2, z + 12, crystalstone, 0);
		array.setBlock(x + 10, y + 2, z + 14, crystalstone, 1);
		array.setBlock(x + 10, y + 2, z + 16, crystalstone, 1);
		array.setBlock(x + 10, y + 2, z + 18, crystalstone, 8);
		array.setBlock(x + 10, y + 2, z + 20, crystalstone, 1);
		array.setBlock(x + 10, y + 2, z + 28, crystalstone, 2);
		array.setBlock(x + 10, y + 3, z + 0, crystalstone, 7);
		array.setBlock(x + 10, y + 3, z + 10, crystalstone, 2);
		array.setBlock(x + 10, y + 3, z + 18, crystalstone, 2);
		array.setBlock(x + 10, y + 3, z + 28, crystalstone, 7);
		array.setBlock(x + 10, y + 4, z + 10, crystalstone, 2);
		array.setBlock(x + 10, y + 4, z + 18, crystalstone, 2);
		array.setBlock(x + 10, y + 5, z + 10, crystalstone, 3);
		array.setBlock(x + 10, y + 5, z + 18, crystalstone, 3);
		array.setBlock(x + 10, y + 6, z + 10, crystalstone, 2);
		array.setBlock(x + 10, y + 6, z + 18, crystalstone, 2);
		array.setBlock(x + 10, y + 7, z + 10, crystalstone, 5);
		array.setBlock(x + 10, y + 7, z + 18, crystalstone, 5);
		array.setBlock(x + 11, y + 1, z + 8, shield, 1);
		array.setBlock(x + 11, y + 1, z + 9, shield, 0);
		array.setBlock(x + 11, y + 1, z + 10, shield, 0);
		array.setBlock(x + 11, y + 1, z + 11, shield, 0);
		array.setBlock(x + 11, y + 1, z + 12, shield, 0);
		array.setBlock(x + 11, y + 1, z + 13, shield, 0);
		array.setBlock(x + 11, y + 1, z + 14, shield, 0);
		array.setBlock(x + 11, y + 1, z + 15, shield, 0);
		array.setBlock(x + 11, y + 1, z + 16, shield, 0);
		array.setBlock(x + 11, y + 1, z + 17, shield, 0);
		array.setBlock(x + 11, y + 1, z + 18, shield, 0);
		array.setBlock(x + 11, y + 1, z + 19, shield, 0);
		array.setBlock(x + 11, y + 1, z + 20, shield, 1);
		array.setBlock(x + 11, y + 2, z + 8, crystalstone, 1);
		array.setBlock(x + 11, y + 2, z + 16, crystalstone, 0);
		array.setBlock(x + 11, y + 2, z + 17, crystalstone, 1);
		array.setBlock(x + 11, y + 2, z + 11, crystalstone, 1);
		array.setBlock(x + 11, y + 2, z + 14, crystalstone, 12);
		array.setBlock(x + 11, y + 2, z + 18, crystalstone, 12);
		array.setBlock(x + 11, y + 2, z + 20, crystalstone, 1);
		array.setBlock(x + 11, y + 3, z + 14, crystalstone, 6);
		array.setBlock(x + 11, y + 3, z + 15, crystalstone, 4);
		array.setBlock(x + 11, y + 3, z + 16, crystalstone, 6);
		array.setBlock(x + 12, y + 1, z + 4, crystalstone, 0);
		array.setBlock(x + 12, y + 1, z + 5, shield, 1);
		array.setBlock(x + 12, y + 1, z + 6, shield, 1);
		array.setBlock(x + 12, y + 1, z + 7, shield, 1);
		array.setBlock(x + 12, y + 1, z + 8, crystalstone, 15);
		array.setBlock(x + 12, y + 1, z + 9, shield, 0);
		array.setBlock(x + 12, y + 1, z + 10, shield, 0);
		array.setBlock(x + 12, y + 1, z + 11, shield, 0);
		array.setBlock(x + 12, y + 1, z + 12, shield, 0);
		array.setBlock(x + 12, y + 1, z + 13, shield, 0);
		array.setBlock(x + 12, y + 1, z + 14, shield, 0);
		array.setBlock(x + 12, y + 1, z + 15, shield, 0);
		array.setBlock(x + 12, y + 1, z + 16, shield, 0);
		array.setBlock(x + 12, y + 1, z + 17, shield, 0);
		array.setBlock(x + 12, y + 1, z + 18, shield, 0);
		array.setBlock(x + 12, y + 1, z + 19, shield, 0);
		array.setBlock(x + 12, y + 1, z + 20, shield, 1);
		array.setBlock(x + 12, y + 1, z + 21, shield, 1);
		array.setBlock(x + 12, y + 1, z + 22, shield, 1);
		array.setBlock(x + 12, y + 1, z + 23, shield, 1);
		array.setBlock(x + 12, y + 1, z + 24, crystalstone, 0);
		array.setBlock(x + 12, y + 2, z + 4, crystalstone, 2);
		array.setBlock(x + 12, y + 2, z + 8, crystalstone, 6);
		array.setBlock(x + 12, y + 2, z + 9, crystalstone, 1);
		array.setBlock(x + 12, y + 2, z + 10, crystalstone, 1);
		array.setBlock(x + 12, y + 2, z + 11, crystalstone, 0);
		array.setBlock(x + 12, y + 2, z + 13, crystalstone, 12);
		array.setBlock(x + 12, y + 2, z + 14, crystalstone, 0);
		array.setBlock(x + 12, y + 2, z + 18, crystalstone, 0);
		array.setBlock(x + 12, y + 2, z + 20, crystalstone, 1);
		array.setBlock(x + 12, y + 2, z + 24, crystalstone, 2);
		array.setBlock(x + 12, y + 3, z + 4, crystalstone, 5);
		array.setBlock(x + 12, y + 3, z + 11, crystalstone, 6);
		array.setBlock(x + 12, y + 3, z + 12, crystalstone, 1);
		array.setBlock(x + 12, y + 3, z + 13, crystalstone, 1);
		array.setBlock(x + 12, y + 3, z + 14, crystalstone, 12);
		array.setBlock(x + 12, y + 3, z + 16, crystalstone, 1);
		array.setBlock(x + 12, y + 3, z + 24, crystalstone, 5);
		array.setBlock(x + 12, y + 4, z + 14, crystalstone, 7);
		array.setBlock(x + 13, y + 0, z + 5, shield, 0);
		array.setBlock(x + 13, y + 0, z + 6, shield, 0);
		array.setBlock(x + 13, y + 0, z + 7, shield, 0);
		array.setBlock(x + 13, y + 0, z + 21, shield, 0);
		array.setBlock(x + 13, y + 0, z + 22, shield, 0);
		array.setBlock(x + 13, y + 0, z + 23, shield, 0);
		array.setBlock(x + 13, y + 1, z + 4, shield, 1);
		array.setBlock(x + 13, y + 1, z + 8, crystalstone, 15);
		array.setBlock(x + 13, y + 1, z + 9, shield, 0);
		array.setBlock(x + 13, y + 1, z + 10, shield, 0);
		array.setBlock(x + 13, y + 1, z + 11, shield, 0);
		array.setBlock(x + 13, y + 1, z + 12, shield, 0);
		array.setBlock(x + 13, y + 1, z + 13, shield, 0);
		array.setBlock(x + 13, y + 1, z + 14, shield, 0);
		array.setBlock(x + 13, y + 1, z + 15, shield, 0);
		array.setBlock(x + 13, y + 1, z + 16, shield, 0);
		array.setBlock(x + 13, y + 1, z + 17, shield, 0);
		array.setBlock(x + 13, y + 1, z + 18, shield, 0);
		array.setBlock(x + 13, y + 1, z + 19, shield, 0);
		array.setBlock(x + 13, y + 1, z + 20, shield, 1);
		array.setBlock(x + 13, y + 1, z + 24, shield, 1);
		array.setBlock(x + 13, y + 2, z + 13, crystalstone, 0);
		array.setBlock(x + 13, y + 2, z + 14, shield, 1);
		array.setBlock(x + 13, y + 2, z + 15, crystalstone, 0);
		array.setBlock(x + 13, y + 2, z + 16, crystalstone, 12);
		array.setBlock(x + 13, y + 2, z + 20, crystalstone, 1);
		array.setBlock(x + 13, y + 3, z + 11, crystalstone, 4);
		array.setBlock(x + 13, y + 3, z + 14, crystalstone, 15);
		array.setBlock(x + 13, y + 3, z + 16, crystalstone, 1);
		array.setBlock(x + 14, y + 0, z + 5, shield, 0);
		array.setBlock(x + 14, y + 0, z + 6, crystalstone, 13);
		array.setBlock(x + 14, y + 0, z + 7, shield, 0);
		array.setBlock(x + 14, y + 0, z + 21, shield, 0);
		array.setBlock(x + 14, y + 0, z + 22, crystalstone, 13);
		array.setBlock(x + 14, y + 0, z + 23, shield, 0);
		array.setBlock(x + 14, y + 1, z + 4, shield, 1);
		array.setBlock(x + 14, y + 1, z + 8, crystalstone, 6);
		array.setBlock(x + 14, y + 1, z + 9, shield, 0);
		array.setBlock(x + 14, y + 1, z + 10, shield, 0);
		array.setBlock(x + 14, y + 1, z + 11, shield, 0);
		array.setBlock(x + 14, y + 1, z + 12, shield, 0);
		array.setBlock(x + 14, y + 1, z + 13, shield, 0);
		array.setBlock(x + 14, y + 1, z + 14, shield, 0);
		array.setBlock(x + 14, y + 1, z + 15, shield, 0);
		array.setBlock(x + 14, y + 1, z + 16, shield, 0);
		array.setBlock(x + 14, y + 1, z + 17, shield, 0);
		array.setBlock(x + 14, y + 1, z + 18, shield, 0);
		array.setBlock(x + 14, y + 1, z + 19, shield, 0);
		array.setBlock(x + 14, y + 1, z + 20, crystalstone, 6);
		array.setBlock(x + 14, y + 1, z + 24, shield, 1);
		array.setBlock(x + 14, y + 2, z + 8, crystalstone, 6);
		array.setBlock(x + 14, y + 2, z + 9, crystalstone, 1);
		array.setBlock(x + 14, y + 2, z + 10, crystalstone, 1);
		array.setBlock(x + 14, y + 2, z + 11, crystalstone, 12);
		array.setBlock(x + 14, y + 2, z + 12, crystalstone, 0);
		array.setBlock(x + 14, y + 2, z + 13, crystalstone, 0);
		array.setBlock(x + 14, y + 2, z + 14, shield, 1);
		array.setBlock(x + 14, y + 2, z + 15, crystalstone, 0);
		array.setBlock(x + 14, y + 2, z + 16, crystalstone, 0);
		array.setBlock(x + 14, y + 2, z + 17, crystalstone, 12);
		array.setBlock(x + 14, y + 2, z + 18, crystalstone, 1);
		array.setBlock(x + 14, y + 2, z + 19, crystalstone, 1);
		array.setBlock(x + 14, y + 2, z + 20, crystalstone, 6);
		array.setBlock(x + 14, y + 3, z + 11, crystalstone, 6);
		array.setBlock(x + 14, y + 3, z + 12, crystalstone, 12);
		array.setBlock(x + 14, y + 3, z + 13, crystalstone, 15);
		array.setBlock(x + 14, y + 3, z + 14, crystalstone, 14);
		array.setBlock(x + 14, y + 3, z + 15, crystalstone, 15);
		array.setBlock(x + 14, y + 3, z + 16, crystalstone, 12);
		array.setBlock(x + 14, y + 3, z + 17, crystalstone, 6);
		array.setBlock(x + 14, y + 4, z + 12, crystalstone, 7);
		array.setBlock(x + 14, y + 4, z + 16, crystalstone, 7);
		array.setBlock(x + 15, y + 0, z + 5, shield, 0);
		array.setBlock(x + 15, y + 0, z + 6, shield, 0);
		array.setBlock(x + 15, y + 0, z + 7, shield, 0);
		array.setBlock(x + 15, y + 0, z + 21, shield, 0);
		array.setBlock(x + 15, y + 0, z + 22, shield, 0);
		array.setBlock(x + 15, y + 0, z + 23, shield, 0);
		array.setBlock(x + 15, y + 1, z + 4, shield, 1);
		array.setBlock(x + 15, y + 1, z + 8, shield, 1);
		array.setBlock(x + 15, y + 1, z + 9, shield, 0);
		array.setBlock(x + 15, y + 1, z + 10, shield, 0);
		array.setBlock(x + 15, y + 1, z + 11, shield, 0);
		array.setBlock(x + 15, y + 1, z + 12, shield, 0);
		array.setBlock(x + 15, y + 1, z + 13, shield, 0);
		array.setBlock(x + 15, y + 1, z + 14, shield, 0);
		array.setBlock(x + 15, y + 1, z + 15, shield, 0);
		array.setBlock(x + 15, y + 1, z + 16, shield, 0);
		array.setBlock(x + 15, y + 1, z + 17, shield, 0);
		array.setBlock(x + 15, y + 1, z + 18, shield, 0);
		array.setBlock(x + 15, y + 1, z + 19, shield, 0);
		array.setBlock(x + 15, y + 1, z + 20, crystalstone, 15);
		array.setBlock(x + 15, y + 1, z + 24, shield, 1);
		array.setBlock(x + 15, y + 2, z + 8, crystalstone, 1);
		array.setBlock(x + 15, y + 2, z + 12, crystalstone, 12);
		array.setBlock(x + 15, y + 2, z + 13, crystalstone, 0);
		array.setBlock(x + 15, y + 2, z + 14, crystalstone, 0);
		array.setBlock(x + 15, y + 2, z + 15, crystalstone, 0);
		array.setBlock(x + 15, y + 3, z + 12, crystalstone, 1);
		array.setBlock(x + 15, y + 3, z + 14, crystalstone, 15);
		array.setBlock(x + 15, y + 3, z + 17, crystalstone, 4);
		array.setBlock(x + 16, y + 1, z + 4, crystalstone, 0);
		array.setBlock(x + 16, y + 1, z + 5, shield, 1);
		array.setBlock(x + 16, y + 1, z + 6, shield, 1);
		array.setBlock(x + 16, y + 1, z + 7, shield, 1);
		array.setBlock(x + 16, y + 1, z + 8, shield, 1);
		array.setBlock(x + 16, y + 1, z + 9, shield, 0);
		array.setBlock(x + 16, y + 1, z + 10, shield, 0);
		array.setBlock(x + 16, y + 1, z + 11, shield, 0);
		array.setBlock(x + 16, y + 1, z + 12, shield, 0);
		array.setBlock(x + 16, y + 1, z + 13, shield, 0);
		array.setBlock(x + 16, y + 1, z + 14, shield, 0);
		array.setBlock(x + 16, y + 1, z + 15, shield, 0);
		array.setBlock(x + 16, y + 1, z + 16, shield, 0);
		array.setBlock(x + 16, y + 1, z + 17, shield, 0);
		array.setBlock(x + 16, y + 1, z + 18, shield, 0);
		array.setBlock(x + 16, y + 1, z + 19, shield, 0);
		array.setBlock(x + 16, y + 1, z + 20, crystalstone, 15);
		array.setBlock(x + 16, y + 1, z + 21, shield, 1);
		array.setBlock(x + 16, y + 1, z + 22, shield, 1);
		array.setBlock(x + 16, y + 1, z + 23, shield, 1);
		array.setBlock(x + 16, y + 1, z + 24, crystalstone, 0);
		array.setBlock(x + 16, y + 2, z + 4, crystalstone, 2);
		array.setBlock(x + 16, y + 2, z + 8, crystalstone, 1);
		array.setBlock(x + 16, y + 2, z + 10, crystalstone, 0);
		array.setBlock(x + 16, y + 2, z + 14, crystalstone, 0);
		array.setBlock(x + 16, y + 2, z + 15, crystalstone, 12);
		array.setBlock(x + 16, y + 2, z + 17, crystalstone, 0);
		array.setBlock(x + 16, y + 2, z + 18, crystalstone, 1);
		array.setBlock(x + 16, y + 2, z + 19, crystalstone, 1);
		array.setBlock(x + 16, y + 2, z + 20, crystalstone, 6);
		array.setBlock(x + 16, y + 2, z + 24, crystalstone, 2);
		array.setBlock(x + 16, y + 3, z + 4, crystalstone, 5);
		array.setBlock(x + 16, y + 3, z + 12, crystalstone, 1);
		array.setBlock(x + 16, y + 3, z + 14, crystalstone, 12);
		array.setBlock(x + 16, y + 3, z + 15, crystalstone, 1);
		array.setBlock(x + 16, y + 3, z + 16, crystalstone, 1);
		array.setBlock(x + 16, y + 3, z + 17, crystalstone, 6);
		array.setBlock(x + 16, y + 3, z + 24, crystalstone, 5);
		array.setBlock(x + 16, y + 4, z + 14, crystalstone, 7);
		array.setBlock(x + 17, y + 1, z + 8, shield, 1);
		array.setBlock(x + 17, y + 1, z + 9, shield, 0);
		array.setBlock(x + 17, y + 1, z + 10, shield, 0);
		array.setBlock(x + 17, y + 1, z + 11, shield, 0);
		array.setBlock(x + 17, y + 1, z + 12, shield, 0);
		array.setBlock(x + 17, y + 1, z + 13, shield, 0);
		array.setBlock(x + 17, y + 1, z + 14, shield, 0);
		array.setBlock(x + 17, y + 1, z + 15, shield, 0);
		array.setBlock(x + 17, y + 1, z + 16, shield, 0);
		array.setBlock(x + 17, y + 1, z + 17, shield, 0);
		array.setBlock(x + 17, y + 1, z + 18, shield, 0);
		array.setBlock(x + 17, y + 1, z + 19, shield, 0);
		array.setBlock(x + 17, y + 1, z + 20, shield, 1);
		array.setBlock(x + 17, y + 2, z + 8, crystalstone, 1);
		array.setBlock(x + 17, y + 2, z + 10, crystalstone, 12);
		array.setBlock(x + 17, y + 2, z + 11, crystalstone, 1);
		array.setBlock(x + 17, y + 2, z + 12, crystalstone, 0);
		array.setBlock(x + 17, y + 2, z + 14, crystalstone, 12);
		array.setBlock(x + 17, y + 2, z + 17, crystalstone, 1);
		array.setBlock(x + 17, y + 2, z + 20, crystalstone, 1);
		array.setBlock(x + 17, y + 3, z + 12, crystalstone, 6);
		array.setBlock(x + 17, y + 3, z + 13, crystalstone, 4);
		array.setBlock(x + 17, y + 3, z + 14, crystalstone, 6);
		array.setBlock(x + 18, y + 1, z + 8, shield, 1);
		array.setBlock(x + 18, y + 1, z + 9, shield, 0);
		array.setBlock(x + 18, y + 1, z + 10, shield, 0);
		array.setBlock(x + 18, y + 1, z + 11, shield, 0);
		array.setBlock(x + 18, y + 1, z + 12, shield, 0);
		array.setBlock(x + 18, y + 1, z + 13, shield, 0);
		array.setBlock(x + 18, y + 1, z + 14, shield, 0);
		array.setBlock(x + 18, y + 1, z + 15, shield, 0);
		array.setBlock(x + 18, y + 1, z + 16, shield, 0);
		array.setBlock(x + 18, y + 1, z + 17, shield, 0);
		array.setBlock(x + 18, y + 1, z + 18, shield, 0);
		array.setBlock(x + 18, y + 1, z + 19, shield, 0);
		array.setBlock(x + 18, y + 1, z + 20, shield, 1);
		array.setBlock(x + 18, y + 2, z + 0, crystalstone, 2);
		array.setBlock(x + 18, y + 2, z + 8, crystalstone, 1);
		array.setBlock(x + 18, y + 2, z + 10, crystalstone, 8);
		array.setBlock(x + 18, y + 2, z + 12, crystalstone, 1);
		array.setBlock(x + 18, y + 2, z + 14, crystalstone, 1);
		array.setBlock(x + 18, y + 2, z + 16, crystalstone, 0);
		array.setBlock(x + 18, y + 2, z + 17, crystalstone, 12);
		array.setBlock(x + 18, y + 2, z + 18, crystalstone, 8);
		array.setBlock(x + 18, y + 2, z + 20, crystalstone, 1);
		array.setBlock(x + 18, y + 2, z + 28, crystalstone, 2);
		array.setBlock(x + 18, y + 3, z + 0, crystalstone, 7);
		array.setBlock(x + 18, y + 3, z + 10, crystalstone, 2);
		array.setBlock(x + 18, y + 3, z + 18, crystalstone, 2);
		array.setBlock(x + 18, y + 3, z + 28, crystalstone, 7);
		array.setBlock(x + 18, y + 4, z + 10, crystalstone, 2);
		array.setBlock(x + 18, y + 4, z + 18, crystalstone, 2);
		array.setBlock(x + 18, y + 5, z + 10, crystalstone, 3);
		array.setBlock(x + 18, y + 5, z + 18, crystalstone, 3);
		array.setBlock(x + 18, y + 6, z + 10, crystalstone, 2);
		array.setBlock(x + 18, y + 6, z + 18, crystalstone, 2);
		array.setBlock(x + 18, y + 7, z + 10, crystalstone, 5);
		array.setBlock(x + 18, y + 7, z + 18, crystalstone, 5);
		array.setBlock(x + 19, y + 1, z + 8, shield, 1);
		array.setBlock(x + 19, y + 1, z + 9, shield, 0);
		array.setBlock(x + 19, y + 1, z + 10, shield, 0);
		array.setBlock(x + 19, y + 1, z + 11, shield, 0);
		array.setBlock(x + 19, y + 1, z + 12, shield, 0);
		array.setBlock(x + 19, y + 1, z + 13, shield, 0);
		array.setBlock(x + 19, y + 1, z + 14, shield, 0);
		array.setBlock(x + 19, y + 1, z + 15, shield, 0);
		array.setBlock(x + 19, y + 1, z + 16, shield, 0);
		array.setBlock(x + 19, y + 1, z + 17, shield, 0);
		array.setBlock(x + 19, y + 1, z + 18, shield, 0);
		array.setBlock(x + 19, y + 1, z + 19, shield, 0);
		array.setBlock(x + 19, y + 1, z + 20, shield, 1);
		array.setBlock(x + 19, y + 2, z + 8, crystalstone, 1);
		array.setBlock(x + 19, y + 2, z + 12, crystalstone, 1);
		array.setBlock(x + 19, y + 2, z + 14, crystalstone, 1);
		array.setBlock(x + 19, y + 2, z + 20, crystalstone, 1);
		array.setBlock(x + 20, y + 1, z + 8, crystalstone, 0);
		array.setBlock(x + 20, y + 1, z + 9, shield, 1);
		array.setBlock(x + 20, y + 1, z + 10, shield, 1);
		array.setBlock(x + 20, y + 1, z + 11, shield, 1);
		array.setBlock(x + 20, y + 1, z + 12, crystalstone, 15);
		array.setBlock(x + 20, y + 1, z + 13, crystalstone, 15);
		array.setBlock(x + 20, y + 1, z + 14, crystalstone, 6);
		array.setBlock(x + 20, y + 1, z + 15, shield, 1);
		array.setBlock(x + 20, y + 1, z + 16, shield, 1);
		array.setBlock(x + 20, y + 1, z + 17, shield, 1);
		array.setBlock(x + 20, y + 1, z + 18, shield, 1);
		array.setBlock(x + 20, y + 1, z + 19, shield, 1);
		array.setBlock(x + 20, y + 1, z + 20, crystalstone, 0);
		array.setBlock(x + 20, y + 2, z + 8, crystalstone, 6);
		array.setBlock(x + 20, y + 2, z + 9, crystalstone, 1);
		array.setBlock(x + 20, y + 2, z + 10, crystalstone, 1);
		array.setBlock(x + 20, y + 2, z + 11, crystalstone, 1);
		array.setBlock(x + 20, y + 2, z + 12, crystalstone, 6);
		array.setBlock(x + 20, y + 2, z + 14, crystalstone, 6);
		array.setBlock(x + 20, y + 2, z + 15, crystalstone, 1);
		array.setBlock(x + 20, y + 2, z + 16, crystalstone, 1);
		array.setBlock(x + 20, y + 2, z + 17, crystalstone, 1);
		array.setBlock(x + 20, y + 2, z + 18, crystalstone, 1);
		array.setBlock(x + 20, y + 2, z + 19, crystalstone, 1);
		array.setBlock(x + 20, y + 2, z + 20, crystalstone, 6);
		array.setBlock(x + 21, y + 0, z + 13, shield, 0);
		array.setBlock(x + 21, y + 0, z + 14, shield, 0);
		array.setBlock(x + 21, y + 0, z + 15, shield, 0);
		array.setBlock(x + 21, y + 1, z + 12, shield, 1);
		array.setBlock(x + 21, y + 1, z + 16, shield, 1);
		array.setBlock(x + 22, y + 0, z + 13, shield, 0);
		array.setBlock(x + 22, y + 0, z + 14, crystalstone, 13);
		array.setBlock(x + 22, y + 0, z + 15, shield, 0);
		array.setBlock(x + 22, y + 1, z + 12, shield, 1);
		array.setBlock(x + 22, y + 1, z + 16, shield, 1);
		array.setBlock(x + 23, y + 0, z + 13, shield, 0);
		array.setBlock(x + 23, y + 0, z + 14, shield, 0);
		array.setBlock(x + 23, y + 0, z + 15, shield, 0);
		array.setBlock(x + 23, y + 1, z + 12, shield, 1);
		array.setBlock(x + 23, y + 1, z + 16, shield, 1);
		array.setBlock(x + 24, y + 1, z + 12, crystalstone, 0);
		array.setBlock(x + 24, y + 1, z + 13, shield, 1);
		array.setBlock(x + 24, y + 1, z + 14, shield, 1);
		array.setBlock(x + 24, y + 1, z + 15, shield, 1);
		array.setBlock(x + 24, y + 1, z + 16, crystalstone, 0);
		array.setBlock(x + 24, y + 2, z + 12, crystalstone, 2);
		array.setBlock(x + 24, y + 2, z + 16, crystalstone, 2);
		array.setBlock(x + 24, y + 3, z + 12, crystalstone, 5);
		array.setBlock(x + 24, y + 3, z + 16, crystalstone, 5);
		array.setBlock(x + 26, y + 2, z + 0, crystalstone, 12);
		array.setBlock(x + 26, y + 2, z + 28, crystalstone, 12);
		array.setBlock(x + 26, y + 3, z + 0, crystalstone, 7);
		array.setBlock(x + 26, y + 3, z + 28, crystalstone, 7);
		array.setBlock(x + 27, y + 3, z + 0, crystalstone, 1);
		array.setBlock(x + 27, y + 3, z + 28, crystalstone, 1);
		array.setBlock(x + 28, y + 2, z + 0, crystalstone, 8);
		array.setBlock(x + 28, y + 2, z + 2, crystalstone, 12);
		array.setBlock(x + 28, y + 2, z + 10, crystalstone, 2);
		array.setBlock(x + 28, y + 2, z + 18, crystalstone, 2);
		array.setBlock(x + 28, y + 2, z + 26, crystalstone, 12);
		array.setBlock(x + 28, y + 2, z + 28, crystalstone, 8);
		array.setBlock(x + 28, y + 3, z + 0, crystalstone, 2);
		array.setBlock(x + 28, y + 3, z + 1, crystalstone, 1);
		array.setBlock(x + 28, y + 3, z + 2, crystalstone, 7);
		array.setBlock(x + 28, y + 3, z + 10, crystalstone, 7);
		array.setBlock(x + 28, y + 3, z + 18, crystalstone, 7);
		array.setBlock(x + 28, y + 3, z + 26, crystalstone, 7);
		array.setBlock(x + 28, y + 3, z + 27, crystalstone, 1);
		array.setBlock(x + 28, y + 3, z + 28, crystalstone, 2);
		array.setBlock(x + 28, y + 4, z + 0, crystalstone, 2);
		array.setBlock(x + 28, y + 4, z + 28, crystalstone, 2);
		array.setBlock(x + 28, y + 5, z + 0, crystalstone, 13);
		array.setBlock(x + 28, y + 5, z + 28, crystalstone, 13);

		array.setFluid(x + 14, y + 4, z + 14, ChromatiCraft.luma);

		array.setFluid(x + 16, y + 2, z + 9, ChromatiCraft.luma, false, false);
		array.setFluid(x + 16, y + 2, z + 11, ChromatiCraft.luma, false, false);
		array.setFluid(x + 16, y + 2, z + 12, ChromatiCraft.luma, false, false);
		array.setFluid(x + 16, y + 2, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 16, y + 2, z + 16, ChromatiCraft.luma, false, false);
		array.setFluid(x + 16, y + 3, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 12, y + 2, z + 12, ChromatiCraft.luma, false, false);
		array.setFluid(x + 12, y + 2, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 12, y + 2, z + 16, ChromatiCraft.luma, false, false);
		array.setFluid(x + 12, y + 2, z + 17, ChromatiCraft.luma, false, false);
		array.setFluid(x + 12, y + 2, z + 19, ChromatiCraft.luma, false, false);
		array.setFluid(x + 12, y + 3, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 1, z + 5, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 1, z + 6, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 1, z + 7, ChromatiCraft.luma, false, false);
		array.setFluid(x + 11, y + 2, z + 9, ChromatiCraft.luma, false, false);
		array.setFluid(x + 11, y + 2, z + 10, ChromatiCraft.luma, false, false);
		array.setFluid(x + 11, y + 2, z + 12, ChromatiCraft.luma, false, false);
		array.setFluid(x + 11, y + 2, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 11, y + 2, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 11, y + 2, z + 19, ChromatiCraft.luma, false, false);
		array.setFluid(x + 10, y + 2, z + 9, ChromatiCraft.luma, false, false);
		array.setFluid(x + 10, y + 2, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 10, y + 2, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 10, y + 2, z + 17, ChromatiCraft.luma, false, false);
		array.setFluid(x + 10, y + 2, z + 19, ChromatiCraft.luma, false, false);
		array.setFluid(x + 8, y + 2, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 9, y + 2, z + 9, ChromatiCraft.luma, false, false);
		array.setFluid(x + 9, y + 2, z + 10, ChromatiCraft.luma, false, false);
		array.setFluid(x + 9, y + 2, z + 11, ChromatiCraft.luma, false, false);
		array.setFluid(x + 9, y + 2, z + 12, ChromatiCraft.luma, false, false);
		array.setFluid(x + 9, y + 2, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 7, y + 1, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 7, y + 1, z + 14, ChromatiCraft.luma, false, false);
		array.setFluid(x + 7, y + 1, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 5, y + 1, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 5, y + 1, z + 14, ChromatiCraft.luma, false, false);
		array.setFluid(x + 5, y + 1, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 6, y + 1, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 6, y + 1, z + 14, ChromatiCraft.luma, false, false);
		array.setFluid(x + 6, y + 1, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 7, y + 2, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 9, y + 2, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 9, y + 2, z + 17, ChromatiCraft.luma, false, false);
		array.setFluid(x + 9, y + 2, z + 18, ChromatiCraft.luma, false, false);
		array.setFluid(x + 9, y + 2, z + 19, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 1, z + 21, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 1, z + 22, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 1, z + 23, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 2, z + 7, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 2, z + 8, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 2, z + 9, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 2, z + 10, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 2, z + 11, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 2, z + 12, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 2, z + 17, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 2, z + 18, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 2, z + 19, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 3, z + 12, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 3, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 3, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 4, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 4, z + 14, ChromatiCraft.luma, false, false);
		array.setFluid(x + 13, y + 4, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 14, y + 1, z + 5, ChromatiCraft.luma, false, false);
		array.setFluid(x + 14, y + 1, z + 6, ChromatiCraft.luma, false, false);
		array.setFluid(x + 14, y + 1, z + 7, ChromatiCraft.luma, false, false);
		array.setFluid(x + 14, y + 1, z + 21, ChromatiCraft.luma, false, false);
		array.setFluid(x + 14, y + 1, z + 22, ChromatiCraft.luma, false, false);
		array.setFluid(x + 14, y + 1, z + 23, ChromatiCraft.luma, false, false);
		array.setFluid(x + 14, y + 4, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 14, y + 4, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 1, z + 5, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 1, z + 6, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 1, z + 7, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 1, z + 21, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 1, z + 22, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 1, z + 23, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 9, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 10, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 11, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 16, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 17, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 18, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 19, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 20, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 21, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 2, z + 22, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 3, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 3, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 3, z + 16, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 4, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 4, z + 14, ChromatiCraft.luma, false, false);
		array.setFluid(x + 15, y + 4, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 17, y + 2, z + 9, ChromatiCraft.luma, false, false);
		array.setFluid(x + 17, y + 2, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 17, y + 2, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 17, y + 2, z + 16, ChromatiCraft.luma, false, false);
		array.setFluid(x + 17, y + 2, z + 18, ChromatiCraft.luma, false, false);
		array.setFluid(x + 17, y + 2, z + 19, ChromatiCraft.luma, false, false);
		array.setFluid(x + 18, y + 2, z + 9, ChromatiCraft.luma, false, false);
		array.setFluid(x + 18, y + 2, z + 11, ChromatiCraft.luma, false, false);
		array.setFluid(x + 18, y + 2, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 18, y + 2, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 18, y + 2, z + 19, ChromatiCraft.luma, false, false);
		array.setFluid(x + 19, y + 2, z + 9, ChromatiCraft.luma, false, false);
		array.setFluid(x + 19, y + 2, z + 10, ChromatiCraft.luma, false, false);
		array.setFluid(x + 19, y + 2, z + 11, ChromatiCraft.luma, false, false);
		array.setFluid(x + 19, y + 2, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 19, y + 2, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 19, y + 2, z + 16, ChromatiCraft.luma, false, false);
		array.setFluid(x + 19, y + 2, z + 17, ChromatiCraft.luma, false, false);
		array.setFluid(x + 19, y + 2, z + 18, ChromatiCraft.luma, false, false);
		array.setFluid(x + 19, y + 2, z + 19, ChromatiCraft.luma, false, false);
		array.setFluid(x + 21, y + 1, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 21, y + 1, z + 14, ChromatiCraft.luma, false, false);
		array.setFluid(x + 21, y + 1, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 22, y + 1, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 22, y + 1, z + 14, ChromatiCraft.luma, false, false);
		array.setFluid(x + 22, y + 1, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 23, y + 1, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 23, y + 1, z + 14, ChromatiCraft.luma, false, false);
		array.setFluid(x + 23, y + 1, z + 15, ChromatiCraft.luma, false, false);
		array.setFluid(x + 21, y + 2, z + 13, ChromatiCraft.luma, false, false);
		array.setFluid(x + 20, y + 2, z + 13, ChromatiCraft.luma, false, false);

		array.setBlock(x + 0, y + 6, z + 0, altar, CrystalElement.WHITE.ordinal());
		array.setBlock(x + 0, y + 6, z + 28, altar, CrystalElement.WHITE.ordinal());
		array.setBlock(x + 28, y + 6, z + 0, altar, CrystalElement.WHITE.ordinal());
		array.setBlock(x + 28, y + 6, z + 28, altar, CrystalElement.WHITE.ordinal());

		for (Coordinate c : wires.keySet()) {
			array.setBlock(x+c.xCoord, y+c.yCoord, z+c.zCoord, this.getLumenWire(wires.get(c)));
		}

		array.setBlock(x + 0, y + 4, z + 10, Blocks.redstone_torch, 5);
		array.setBlock(x + 0, y + 4, z + 18, Blocks.redstone_torch, 5);
		array.setBlock(x + 10, y + 4, z + 0, Blocks.redstone_torch, 5);
		array.setBlock(x + 10, y + 4, z + 28, Blocks.redstone_torch, 5);
		array.setBlock(x + 10, y + 8, z + 10, Blocks.redstone_torch, 5);
		array.setBlock(x + 10, y + 8, z + 18, Blocks.redstone_torch, 5);
		array.setBlock(x + 18, y + 4, z + 0, Blocks.redstone_torch, 5);
		array.setBlock(x + 18, y + 4, z + 28, Blocks.redstone_torch, 5);
		array.setBlock(x + 18, y + 8, z + 10, Blocks.redstone_torch, 5);
		array.setBlock(x + 18, y + 8, z + 18, Blocks.redstone_torch, 5);
		array.setBlock(x + 28, y + 4, z + 10, Blocks.redstone_torch, 5);
		array.setBlock(x + 28, y + 4, z + 18, Blocks.redstone_torch, 5);

		this.setTile(array, x + 14, y + 8, z + 14, ChromaTiles.VOIDTRAP);

		return array;
	}

	public static Collection<Coordinate> getWireLocations() {
		return Collections.unmodifiableCollection(wires.keySet());
	}

	private LumenWireCheck getLumenWire(ForgeDirection dir) {
		return new LumenWireCheck(dir);
	}

	private static class LumenWireCheck implements TileEntityCheck {

		private final ForgeDirection facing;
		private final BlockKey block;

		private LumenWireCheck(ForgeDirection dir) {
			facing = dir;
			block = new BlockKey(ChromaTiles.LUMENWIRE);
		}

		@Override
		public boolean matchInWorld(World world, int x, int y, int z) {
			if (block.matchInWorld(world, x, y, z)) {
				TileEntityLumenWire te = (TileEntityLumenWire)world.getTileEntity(x, y, z);
				return te.getFacing() == facing;
			}
			return false;
		}

		@Override
		public boolean match(Block b, int meta) {
			return block.match(b, meta);
		}

		@Override
		public boolean match(BlockCheck bc) {
			return block.match(bc);
		}

		@Override
		public void place(World world, int x, int y, int z, int flags) {
			block.place(world, x, y, z);
			TileEntityLumenWire te = (TileEntityLumenWire)world.getTileEntity(x, y, z);
			te.placeOnSide(facing.ordinal());
		}

		@Override
		public ItemStack asItemStack() {
			return block.asItemStack();
		}

		@Override
		public ItemStack getDisplay() {
			return ChromaTiles.LUMENWIRE.getCraftedProduct();
		}

		@Override
		public BlockKey asBlockKey() {
			return block;
		}

		@Override
		public TileEntity getTileEntity() {
			return getOrCreateInstanceFor(facing);
		}

	}

	private static TileEntity getOrCreateInstanceFor(ForgeDirection dir) {
		TileEntityLumenWire te = directions[dir.ordinal()-2];
		if (te == null) {
			te = new TileEntityLumenWire();
			te.placeOnSide(dir.ordinal());
			directions[dir.ordinal()-2] = te;
		}
		return te;
	}

}
