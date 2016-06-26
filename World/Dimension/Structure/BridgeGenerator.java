package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Dimension.Structure.Bridge.BlockDynamicBridge.TileDynamicBridge;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.Bridge.BridgeEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.Bridge.BridgeEntryPlatform;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class BridgeGenerator extends DimensionStructureGenerator {

	private final CountMap<UUID> totalSwitches = new CountMap();
	private final CountMap<UUID> activeSwitches = new CountMap();
	private final HashMap<UUID, ControlType> modes = new HashMap();
	private final MultiMap<UUID, Coordinate> bridgeLocations = new MultiMap();
	private static final int SIZE = getSize();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		posY = 30+rand.nextInt(40);
		int x = chunkX;
		int z = chunkZ;
		int y = posY;
		for (int i = -SIZE; i <= SIZE; i++) {
			for (int j = -SIZE; j <= SIZE; j++) {
				for (int k = -SIZE; k <= SIZE; k++) {
					if (ReikaMathLibrary.isPointInsidePowerEllipse(i, j, k, SIZE, SIZE/4D, SIZE/2D, 1.5)) {
						if (ReikaMathLibrary.isPointInsidePowerEllipse(i, j, k, SIZE-1.5, SIZE/4D-1.5, SIZE/2D-1.5, 1.5)) {
							world.setBlock(x+i, y+j, z+k, j < -1 ? Blocks.water : Blocks.air);
						}
						else {
							world.setBlock(x+i, y+j, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
						}
					}
				}
			}
		}

		new BridgeEntryPlatform(this).connect(ForgeDirection.EAST).generate(world, x, y, z);

		this.addDynamicStructure(new BridgeEntrance(this), x, z);
	}

	private static int getSize() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return 32;
			case 2:
				return 64;
			case 3:
			default:
				return 128;
		}
	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	@Override
	public boolean hasBeenSolved(World world) {
		return false;
	}

	public boolean isKeyActive(UUID uid) {
		return modes.get(uid).compare(activeSwitches.get(uid), totalSwitches.get(uid));
	}

	@Override
	protected void clearCaches() {
		totalSwitches.clear();
		activeSwitches.clear();
		modes.clear();
		bridgeLocations.clear();
	}

	public void addBridge(Coordinate c, UUID... uids) {
		for (int i = 0; i < uids.length; i++) {
			bridgeLocations.addValue(uids[i], c);
		}
	}

	public void addKey(UUID uid) {
		totalSwitches.increment(uid);
	}

	@Override
	public void tickPlayer(EntityPlayer ep) { //need to stop block placement too
		while(!ep.onGround && !ep.handleWaterMovement())
			ep.moveEntity(0, -0.05F, 0);
	}

	public void updateControl(World world, UUID id, boolean active) {
		activeSwitches.increment(id, active ? 1 : -1);
		for (Coordinate c : bridgeLocations.get(id)) {
			TileDynamicBridge te = (TileDynamicBridge)c.getTileEntity(world);
			if (te != null) {
				te.checkState();
			}
		}
	}

	public static enum ControlType {
		AND(),
		OR(),
		XOR();

		public boolean compare(int num, int total) {
			switch(this) {
				case AND:
					return num == total;
				case OR:
					return num > 0;
				case XOR:
					return num == 1;
			}
			return false;
		}
	}

}
