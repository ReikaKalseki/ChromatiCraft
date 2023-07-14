package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.HashSet;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureTileCallback;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonTarget.PistonDoorTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.DoorKey.DoorValue;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class DoorSection extends StructurePiece<PistonTapeGenerator> {

	static final int WIDTH = 10;
	static final int HEIGHT = 4;

	private final ForgeDirection tunnelDir;
	final DoorKey doorData;
	private final TapeStage level;
	private final boolean isClosed;
	private final int index;

	private final HashSet<Coordinate> doors = new HashSet();

	public DoorSection(PistonTapeGenerator s, TapeStage t, ForgeDirection dir, DoorKey d, int idx, boolean closed) {
		super(s);
		tunnelDir = dir;
		doorData = d;
		level = t;
		isClosed = closed;
		index = idx;
	}

	public int getLength() {
		return Math.max(3, level.bitsPerDoor+2);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(tunnelDir);
		int ms = BlockType.STONE.metadata;
		int esc = this.getEscapeMeta();
		Block escd = ChromaBlocks.SHIFTLOCK.getBlockInstance();
		int len = this.getLength();
		for (int d = 0; d <= len; d++) {
			for (int i = 0; i <= WIDTH; i++) {
				for (int h = 0; h < HEIGHT; h++) {
					if (isClosed && d == len && i > 4)
						world.setBlock(x+i*left.offsetX+d*tunnelDir.offsetX, y+h, z+i*left.offsetZ+d*tunnelDir.offsetZ, b, ms);
					else
						world.setBlock(x+i*left.offsetX+d*tunnelDir.offsetX, y+h, z+i*left.offsetZ+d*tunnelDir.offsetZ, Blocks.air);
				}
			}
			for (int h = 1; h < HEIGHT; h++) {
				world.setBlock(x+left.offsetX*WIDTH+d*tunnelDir.offsetX, y+h, z+left.offsetZ*WIDTH+d*tunnelDir.offsetZ, b, ms);
			}
		}
		for (int d = 0; d <= len; d++) {
			for (int i = 0; i <= WIDTH; i++) {
				world.setBlock(x+i*left.offsetX+d*tunnelDir.offsetX, y, z+i*left.offsetZ+d*tunnelDir.offsetZ, b, ms);
				world.setBlock(x+i*left.offsetX+d*tunnelDir.offsetX, y+HEIGHT, z+i*left.offsetZ+d*tunnelDir.offsetZ, b, ms);
			}
			for (int h = 1; h < HEIGHT; h++) {
				world.setBlock(x+d*tunnelDir.offsetX, y+h, z+d*tunnelDir.offsetZ, b, ms);
				world.setBlock(x+d*tunnelDir.offsetX+1*left.offsetX, y+h, z+d*tunnelDir.offsetZ+1*left.offsetZ, b, ms);
				world.setBlock(x+d*tunnelDir.offsetX+5*left.offsetX, y+h, z+d*tunnelDir.offsetZ+5*left.offsetZ, b, ms);
				//world.setBlock(x+d*tunnelDir.offsetX+WIDTH*left.offsetX, y+h, z+d*tunnelDir.offsetZ+WIDTH*left.offsetZ, b, ms);
			}
		}
		for (int hw = 2; hw <= 4; hw++) {
			for (int i = 1; i < HEIGHT; i++) {
				this.placeDoorBlock(world, x+left.offsetX*hw, y+i, z+left.offsetZ*hw);
			}
		}

		for (int i = 1; i < HEIGHT; i++) {
			int a = level.bitsPerDoor > 2 ? 1 : 0;
			int m = i == 2 ? BlockType.LIGHT.metadata : ms;
			world.setBlock(x+left.offsetX*6+(a+1)*tunnelDir.offsetX, y+i, z+left.offsetZ*6+(a+1)*tunnelDir.offsetZ, b, m);
			world.setBlock(x+left.offsetX*6+(a+3)*tunnelDir.offsetX, y+i, z+left.offsetZ*6+(a+3)*tunnelDir.offsetZ, b, m);

			world.setBlock(x+left.offsetX*5+(a+2)*tunnelDir.offsetX, y+i, z+left.offsetZ*5+(a+2)*tunnelDir.offsetZ, escd, esc);
		}

		world.setBlock(x+left.offsetX+2*tunnelDir.offsetX, y, z+left.offsetZ+2*tunnelDir.offsetZ, b, BlockType.LIGHT.metadata);
		parent.generateLootChest(x+left.offsetX+2*tunnelDir.offsetX, y+1, z+left.offsetZ+2*tunnelDir.offsetZ, this.getChestFacing(), ChestGenHooks.MINESHAFT_CORRIDOR, 0);
		world.setBlock(x+left.offsetX+2*tunnelDir.offsetX, y+2, z+left.offsetZ+2*tunnelDir.offsetZ, b, BlockType.CRACK.metadata);

		//world.setBlock(x+left.offsetX*WIDTH+2*tunnelDir.offsetX, y+2, z+left.offsetZ*WIDTH+2*tunnelDir.offsetZ, b, BlockType.LIGHT.metadata);

		Coordinate door = new Coordinate(x+left.offsetX*3, y+1, z+left.offsetZ*3);
		for (int i = 0; i < level.bitsPerDoor; i++) {
			this.placeTarget(world, x+left.offsetX*5+(i-level.bitsPerDoor/2)*tunnelDir.offsetX, y+2, z+left.offsetZ*5+(i-level.bitsPerDoor/2)*tunnelDir.offsetZ, i, door);
		}
	}

	private ForgeDirection getChestFacing() {
		return ReikaDirectionHelper.getLeftBy90(tunnelDir);
	}

	private int getEscapeMeta() {
		switch(tunnelDir) {
			case WEST:
				return 9;
			case EAST:
				return 7;
			case NORTH:
				return 3;
			case SOUTH:
				return 5;
			default:
				return 1;
		}
	}

	private void placeTarget(ChunkSplicedGenerationCache world, int x, int y, int z, int idx, Coordinate door) {
		doorData.setTarget(idx, new Coordinate(x, y, z));
		world.setTileEntity(x, y, z, ChromaBlocks.PISTONTARGET.getBlockInstance(), 1, new DoorTargetCallback(doorData.getValue(idx), level, index, ReikaDirectionHelper.getRightBy90(tunnelDir), door, parent.id));
	}

	private void placeDoorBlock(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setBlock(x, y, z, ChromaBlocks.DOOR.getBlockInstance());
		//world.setTileEntity(x, y, z, ChromaBlocks.COLORLOCK.getBlockInstance(), 0, new DoorKeySet(parent, level.ordinal(), parent.id, elements));
		doors.add(new Coordinate(x, y, z));
	}

	public void forceOpenDoor(World world) {
		for (Coordinate c : doors) {
			BlockChromaDoor.setOpen(world, c.xCoord, c.yCoord, c.zCoord, true);
		}
	}

	private static class DoorTargetCallback extends DimensionStructureTileCallback {

		private final UUID id;
		private final int stage;
		private final int doorIndex;
		private final DoorValue data;
		private final Coordinate door;
		private final ForgeDirection direction;

		private DoorTargetCallback(DoorValue d, TapeStage s, int step, ForgeDirection dir, Coordinate c, UUID uid) {
			id = uid;
			data = d;
			door = c;
			direction = dir;
			doorIndex = step;
			stage = s.index;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			((PistonDoorTile)te).uid = id;
			((PistonDoorTile)te).setColor(data.getColor());
			((PistonDoorTile)te).setData(direction, stage, doorIndex, data.index, data.getParent().colorCount);
			((PistonDoorTile)te).setTarget(door);
		}

	}

}
