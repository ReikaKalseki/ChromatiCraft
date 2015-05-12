package Reika.ChromatiCraft.Base;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Block.Dimension.BlockColoredLock.TileEntityColorLock;
import Reika.ChromatiCraft.Block.Dimension.BlockLockKey;
import Reika.ChromatiCraft.Block.Dimension.BlockLockKey.LockChannel;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;

public abstract class LockLevel extends StructurePiece {

	public final LockChannel level;

	protected LockLevel(DimensionStructureGenerator s, BlockLockKey.LockChannel level) {
		super(s);
		this.level = level;
	}

	protected final void generateLock(int x, int y, int z, CrystalElement... colors) {
		parent.world.setTileEntity(x, y, z, ChromaBlocks.COLORLOCK.getBlockInstance(), 0, new LockColorSet(colors));
	}

	protected final void generateGate(int x, int y, int z) {
		parent.world.setTileEntity(x, y, z, ChromaBlocks.COLORLOCK.getBlockInstance(), 1, new LockColorSet());
	}

	private class LockColorSet implements TileCallback {

		private final CrystalElement[] colors;

		public LockColorSet(CrystalElement... c) {
			colors = c;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity tile) {
			if (tile instanceof TileEntityColorLock) {
				TileEntityColorLock te = (TileEntityColorLock)tile;
				for (CrystalElement e : colors)
					te.addColor(e);
				te.setChannel(level.ordinal());
			}
		}

	}

	public abstract int getWidth();
	public abstract int getLength();

}
