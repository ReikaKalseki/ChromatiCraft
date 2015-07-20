package Reika.ChromatiCraft.World.Dimension.Structure.DataStorage;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.StructureData;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockShiftLock.TileEntityShiftLock;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;

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
				if (last.isPositionOpen(i, k) && !active.isPositionOpen(i, k)) {
					this.toggleLock(world, i, k, false);
				}
				else if (!last.isPositionOpen(i, k) && active.isPositionOpen(i, k)) {
					this.toggleLock(world, i, k, true);
				}
			}
		}
	}

	private void toggleLock(World world, int i, int k, boolean open) {
		int dx = ?;
		int dy = ?;
		int dz = ?;
		((TileEntityShiftLock)world.getTileEntity(dx, dy, dz)).setState(open);
		world.markBlockForUpdate(dx, dy, dz);
	}

	public static class MazePath {

		private MazePath(LinkedList<ForgeDirection> pathCache) {

		}

		public boolean isPositionOpen(int x, int z) {

		}

	}

}
