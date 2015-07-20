package Reika.ChromatiCraft.World.Dimension.Structure.DataStorage;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.StructureData;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator.MazePath;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class ShiftMazeData extends StructureData {

	private ArrayList<MazePath> paths;
	private int state = 0;

	private int width;

	public ShiftMazeData(ShiftMazeGenerator gen) {
		super(gen);
	}

	@Override
	public void load() {
		ShiftMazeGenerator shf = (ShiftMazeGenerator)generator;
		paths = shf.getPaths();
	}

	@Override
	public void onInteract(World world, int x, int y, int z, EntityPlayer ep, int s) {
		this.cycle(world);
	}

	private void cycle(World world) {
		MazePath last = paths.get(state);
		state = (state+1)%paths.size();
		MazePath active = paths.get(state);
		for (int i = 0; i < width; i++) {
			for (int k = 0; k < width; k++) {
				for (int n = 2; n < 6; n++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[n];
					if (last.isPositionOpen(i, k, dir) && !active.isPositionOpen(i, k, dir)) {
						this.toggleLock(world, i, k, dir, false);
					}
					else if (!last.isPositionOpen(i, k, dir) && active.isPositionOpen(i, k, dir)) {
						this.toggleLock(world, i, k, dir, true);
					}
				}
			}
		}
	}

	private void toggleLock(World world, int i, int k, ForgeDirection dir, boolean open) {
		for (Coordinate c : ((ShiftMazeGenerator)generator).getLocks(i, k, dir)) {
			int meta = open ? 1 : 0;
			c.setBlock(world, ChromaBlocks.SHIFTLOCK.getBlockInstance(), meta);
			c.triggerBlockUpdate(world, false);
			ReikaSoundHelper.playBreakSound(world, c.xCoord, c.yCoord, c.zCoord, Blocks.stone);
		}
	}

}
