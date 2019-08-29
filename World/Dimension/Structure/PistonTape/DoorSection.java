package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class DoorSection extends StructurePiece<PistonTapeGenerator> {

	static final int LENGTH = 3;
	static final int WIDTH = 9;

	private static final ForgeDirection DIRECTION = ForgeDirection.EAST;

	private final DoorKey doorData;

	public DoorSection(PistonTapeGenerator s, DoorKey d) {
		super(s);
		doorData = d;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(DIRECTION);
		int ms = BlockType.STONE.metadata;
		int esc = this.getEscapeMeta();
		Block escd = ChromaBlocks.SHIFTLOCK.getBlockInstance();
		for (int d = 0; d <= LENGTH; d++) {
			for (int i = 0; i <= WIDTH; i++) {
				for (int h = 0; h <= 3; h++) {
					world.setBlock(x+i*left.offsetX+d*DIRECTION.offsetX, y+h, z+i*left.offsetZ+d*DIRECTION.offsetZ, Blocks.air);
				}
			}
		}
		for (int d = 0; d <= LENGTH; d++) {
			for (int i = 0; i <= WIDTH; i++) {
				world.setBlock(x+i*left.offsetX+d*DIRECTION.offsetX, y, z+i*left.offsetZ+d*DIRECTION.offsetZ, b, ms);
				world.setBlock(x+i*left.offsetX+d*DIRECTION.offsetX, y+3, z+i*left.offsetZ+d*DIRECTION.offsetZ, b, ms);
			}
			for (int h = 1; h <= 2; h++) {
				world.setBlock(x+d*DIRECTION.offsetX, y+h, z+d*DIRECTION.offsetZ, b, ms);
				world.setBlock(x+d*DIRECTION.offsetX+1*left.offsetX, y+h, z+d*DIRECTION.offsetZ+1*left.offsetZ, b, ms);
				world.setBlock(x+d*DIRECTION.offsetX+5*left.offsetX, y+h, z+d*DIRECTION.offsetZ+5*left.offsetZ, b, ms);
				world.setBlock(x+d*DIRECTION.offsetX+WIDTH*left.offsetX, y+h, z+d*DIRECTION.offsetZ+WIDTH*left.offsetZ, b, ms);
			}
		}
		for (int hw = 2; hw <= 4; hw++) {
			this.placeDoorBlock(world, x+left.offsetX*hw, y+1, z+left.offsetZ*hw);
			this.placeDoorBlock(world, x+left.offsetX*hw, y+2, z+left.offsetZ*hw);
		}

		world.setBlock(x+left.offsetX*5+2*DIRECTION.offsetX, y+1, z+left.offsetZ*5+2*DIRECTION.offsetZ, escd, esc);
		world.setBlock(x+left.offsetX*5+2*DIRECTION.offsetX, y+2, z+left.offsetZ*5+2*DIRECTION.offsetZ, escd, esc);

		world.setBlock(x+left.offsetX+2*DIRECTION.offsetX, y, z+left.offsetZ+2*DIRECTION.offsetZ, b, BlockType.LIGHT.metadata);
		parent.generateLootChest(x+left.offsetX+2*DIRECTION.offsetX, y+1, z+left.offsetZ+2*DIRECTION.offsetZ, this.getChestFacing(), ChestGenHooks.MINESHAFT_CORRIDOR, 0);
		world.setBlock(x+left.offsetX+2*DIRECTION.offsetX, y+2, z+left.offsetZ+2*DIRECTION.offsetZ, b, BlockType.CRACK.metadata);

		world.setBlock(x+left.offsetX*WIDTH+2*DIRECTION.offsetX, y+2, z+left.offsetZ*WIDTH+2*DIRECTION.offsetZ, b, BlockType.LIGHT.metadata);
	}

	private ForgeDirection getChestFacing() {
		return ReikaDirectionHelper.getLeftBy90(DIRECTION);
	}

	private int getEscapeMeta() {
		switch(DIRECTION) {
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

	private void placeDoorBlock(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setBlock(x, y, z, ChromaBlocks.DOOR.getBlockInstance());
		doorData.addDoorLocation(x, y, z);
	}

}
