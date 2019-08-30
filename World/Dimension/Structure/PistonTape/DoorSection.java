package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class DoorSection extends StructurePiece<PistonTapeGenerator> {

	static final int WIDTH = 9;

	private final ForgeDirection tunnelDir;
	private final DoorKey doorData;
	private final PistonTapeLoop tape;
	private final TapeStage level;

	public DoorSection(PistonTapeGenerator s, TapeStage t, ForgeDirection dir, DoorKey d) {
		super(s);
		tunnelDir = dir;
		doorData = d;
		level = t;
		tape = new PistonTapeLoop(s, ReikaDirectionHelper.getRightBy90(dir), level);
	}

	public int getLength() {
		return Math.max(3, tape.busWidth+2);
	}

	public int getHeight() {
		return tape.dimensions.totalHeight+6;
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
				for (int h = 0; h <= 3; h++) {
					world.setBlock(x+i*left.offsetX+d*tunnelDir.offsetX, y+h, z+i*left.offsetZ+d*tunnelDir.offsetZ, Blocks.air);
				}
			}
		}
		for (int d = 0; d <= len; d++) {
			for (int i = 0; i <= WIDTH; i++) {
				world.setBlock(x+i*left.offsetX+d*tunnelDir.offsetX, y, z+i*left.offsetZ+d*tunnelDir.offsetZ, b, ms);
				world.setBlock(x+i*left.offsetX+d*tunnelDir.offsetX, y+3, z+i*left.offsetZ+d*tunnelDir.offsetZ, b, ms);
			}
			for (int h = 1; h <= 2; h++) {
				world.setBlock(x+d*tunnelDir.offsetX, y+h, z+d*tunnelDir.offsetZ, b, ms);
				world.setBlock(x+d*tunnelDir.offsetX+1*left.offsetX, y+h, z+d*tunnelDir.offsetZ+1*left.offsetZ, b, ms);
				world.setBlock(x+d*tunnelDir.offsetX+5*left.offsetX, y+h, z+d*tunnelDir.offsetZ+5*left.offsetZ, b, ms);
				world.setBlock(x+d*tunnelDir.offsetX+WIDTH*left.offsetX, y+h, z+d*tunnelDir.offsetZ+WIDTH*left.offsetZ, b, ms);
			}
		}
		for (int hw = 2; hw <= 4; hw++) {
			this.placeDoorBlock(world, x+left.offsetX*hw, y+1, z+left.offsetZ*hw);
			this.placeDoorBlock(world, x+left.offsetX*hw, y+2, z+left.offsetZ*hw);
		}

		world.setBlock(x+left.offsetX*5+2*tunnelDir.offsetX, y+1, z+left.offsetZ*5+2*tunnelDir.offsetZ, escd, esc);
		world.setBlock(x+left.offsetX*5+2*tunnelDir.offsetX, y+2, z+left.offsetZ*5+2*tunnelDir.offsetZ, escd, esc);

		world.setBlock(x+left.offsetX+2*tunnelDir.offsetX, y, z+left.offsetZ+2*tunnelDir.offsetZ, b, BlockType.LIGHT.metadata);
		parent.generateLootChest(x+left.offsetX+2*tunnelDir.offsetX, y+1, z+left.offsetZ+2*tunnelDir.offsetZ, this.getChestFacing(), ChestGenHooks.MINESHAFT_CORRIDOR, 0);
		world.setBlock(x+left.offsetX+2*tunnelDir.offsetX, y+2, z+left.offsetZ+2*tunnelDir.offsetZ, b, BlockType.CRACK.metadata);

		world.setBlock(x+left.offsetX*WIDTH+2*tunnelDir.offsetX, y+2, z+left.offsetZ*WIDTH+2*tunnelDir.offsetZ, b, BlockType.LIGHT.metadata);

		tape.generate(world, x+left.offsetX*(3+WIDTH), y, z+left.offsetZ*(3+WIDTH));
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

	private void placeDoorBlock(ChunkSplicedGenerationCache world, int x, int y, int z) {
		//world.setBlock(x, y, z, ChromaBlocks.DOOR.getBlockInstance());
		//world.setTileEntity(x, y, z, ChromaBlocks.COLORLOCK.getBlockInstance(), 0, new DoorKeySet(parent, level.ordinal(), parent.id, elements));
		doorData.addDoorLocation(x, y, z);
	}

	private static class PistonDoorCallback implements TileCallback {

		private final int[] colors;

		private PistonDoorCallback(DoorKey k) {
			colors = k.getRenderColors();
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {

		}

	}

}
