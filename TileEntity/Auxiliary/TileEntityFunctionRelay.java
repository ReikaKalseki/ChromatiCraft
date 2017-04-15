package Reika.ChromatiCraft.TileEntity.Auxiliary;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Registry.ReikaCropHelper;
import Reika.DragonAPI.ModRegistry.ModCropList;


public class TileEntityFunctionRelay extends TileEntityChromaticBase {

	private RelayFunctions function = RelayFunctions.HARVEST;

	private final StepTimer scanTimer = new StepTimer(50);

	private final ArrayList<Coordinate> activeCoords = new ArrayList();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FUNCTIONRELAY;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		scanTimer.update();
		if (scanTimer.checkCap()) {
			this.doScan(world, x, y, z);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.doScan(world, x, y, z);
	}

	private void doScan(World world, int x, int y, int z) {
		activeCoords.clear();
		for (int i = -4; i <= 4; i++) {
			for (int k = -4; k <= 4; k++) {
				if (Math.abs(i)+Math.abs(k) <= 6) {
					int dx = x+i;
					int dz = z+k;
					for (int j = -6; j <= 2; j++) {
						int dy = y+j;
						Block b = world.getBlock(dx, dy, dz);
						if (!b.isAir(world, dx, dy, dz)) {
							int meta = world.getBlockMetadata(dx, dy, dz);
							if (function.isCoordinateSignificant(world, dx, dy, dz, b, meta)) {
								activeCoords.add(new Coordinate(dx, dy, dz));
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public RelayFunctions getFunctionType() {
		return function;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("function", function.ordinal());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		function = RelayFunctions.list[NBT.getInteger("function")];
	}

	public Coordinate getRandomCoordinate() {
		return activeCoords.isEmpty() ? null : activeCoords.get(rand.nextInt(activeCoords.size()));
	}

	public static enum RelayFunctions {
		HARVEST();

		private static final RelayFunctions[] list = values();

		private boolean isCoordinateSignificant(World world, int x, int y, int z, Block b, int meta) {
			switch(this) {
				case HARVEST:
					return ReikaCropHelper.getCrop(b) != null || ModCropList.getModCrop(b, meta) != null;
			}
			return false;
		}
	}

}
