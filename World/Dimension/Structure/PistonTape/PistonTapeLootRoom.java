package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureTileCallback;
import Reika.ChromatiCraft.Base.StructureLootRoom;
import Reika.ChromatiCraft.Block.BlockEncrustedCrystal.CrystalGrowth;
import Reika.ChromatiCraft.Block.BlockEncrustedCrystal.TileCrystalEncrusted;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class PistonTapeLootRoom extends StructureLootRoom<PistonTapeGenerator> {

	private final ForgeDirection facing;
	private final Random rand;

	public PistonTapeLootRoom(PistonTapeGenerator s, ForgeDirection dir, Random r) {
		super(s);
		facing = dir;
		rand = r;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		int d = 7;
		int h = 2;
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(facing);
		for (int i = 0; i <= d; i++) {
			for (int j = -h; j <= h; j++) {
				int w = i > 2 && i < d-1 ? 3 : 2;
				for (int k = -w; k <= w; k++) {
					int dx = x+i*facing.offsetX+k*left.offsetX;
					int dy = y+j;
					int dz = z+i*facing.offsetZ+k*left.offsetZ;
					boolean wall = j == -h || j == h || k == w || k == -w || i == d;
					Block b = Blocks.air;
					int m = 0;
					if (wall) {
						b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
						m = BlockType.STONE.metadata;
						if (j == 0 && (i == 1 || i == 4)) {
							m = BlockType.LIGHT.metadata;
						}
					}
					else if (i == 1) {
						b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
						m = j == 0 && k == 0 ? BlockType.CRACKS.metadata : BlockType.GLASS.metadata;
					}
					if (wall && i > 2 && (j == -h || j == h) && (i == 2 || i == d-1 || k == w-1 || k == -w+1)) {
						m = BlockType.COBBLE.metadata;
					}
					if (b == Blocks.air && i > 1) {
						world.setTileEntity(dx, dy, dz, ChromaBlocks.ENCRUSTED.getBlockInstance(), parent.getCoreColor().ordinal(), new EncrustedCrystalCallback(parent, rand));
					}
					else {
						world.setBlock(dx, dy, dz, b, m);
					}
				}
			}
		}
		int l = 4;
		this.placeCore(x+facing.offsetX*l, y, z+facing.offsetZ*l);
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				int dx = x+facing.offsetX*(i+l)+left.offsetX*k;
				int dz = z+facing.offsetZ*(i+l)+left.offsetZ*k;
				parent.addBreakable(dx, y-h, dz);
				parent.addBreakable(dx, y+h, dz);
			}
		}
		for (int i = -1; i <= 1; i++) {
			for (int k = 4; k <= 6; k++) {
				int dx = x+facing.offsetX*-1+left.offsetX*k;
				int dz = z+facing.offsetZ*-1+left.offsetZ*k;
				int dy = y+i;
				parent.addBreakable(dx, dy, dz);
			}
		}
	}

	private static class EncrustedCrystalCallback extends DimensionStructureTileCallback {

		private final CrystalElement color;
		private final Random rand;

		private EncrustedCrystalCallback(DimensionStructureGenerator p, Random r) {
			color = p.getCoreColor();
			rand = r;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity tile) {
			TileCrystalEncrusted te = (TileCrystalEncrusted)tile;
			if (te == null) {
				ReikaJavaLibrary.pConsole("No tile @ "+new Coordinate(x, y, z)+"?!");
				return;
			}
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				if (CrystalGrowth.canExist(world, x, y, z, dir)) {
					te.addGrowth(dir, 3+rand.nextInt(7));
				}
			}
			world.scheduleBlockUpdate(x, y, z, te.getBlockType(), 100);
		}

	}

}
