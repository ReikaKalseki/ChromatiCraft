package Reika.ChromatiCraft.Magic;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityCrystalMusic;
import Reika.DragonAPI.Instantiable.MusicScore.Note;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

public class CrystalMusicTemple {

	private Coordinate tileLocation;
	private boolean isStructureComplete;

	private static final LinkedList<MusicKey>[] melody = new LinkedList[2];

	static {
		melody[0] = new LinkedList();
		melody[1] = new LinkedList();


	}

	private LinkedList<MusicKey>[] remaining = new LinkedList[melody.length];

	public CrystalMusicTemple() {
		this.refreshMelody();
	}

	private void refreshMelody() {
		for (int i = 0; i < melody.length; i++)
			remaining[i] = new LinkedList(melody[i]);
	}

	public void setCore(TileEntityCrystalMusic te) {
		tileLocation = new Coordinate(te);
	}

	public void onMusicStart(World world) {
		if (isStructureComplete) {

		}
	}

	public void onNote(World world, Note n, int track) {
		if (isStructureComplete && track < melody.length && n.key == remaining[track].getFirst())
			remaining[track].remove();
	}

	public void onMusicEnd(World world) {
		if (isStructureComplete && this.validateTracks()) {
			this.onSongComplete(world);
		}
		this.refreshMelody();
	}

	private boolean validateTracks() {
		for (int i = 0; i < melody.length; i++) {
			if (!remaining[i].isEmpty())
				return false;
		}
		return true;
	}

	private void onSongComplete(World world) {

	}

	public void checkStructure(World world) {
		//isStructureComplete = this.checkForPillars(world, tileLocation.xCoord, tileLocation.yCoord, tileLocation.zCoord);
	}

	private boolean checkForMineralBlocks(World world, int x, int y, int z) {

	}

	private boolean isBlockValidMineral(World world, int x, int y, int z, Effects has) {
		Block b = world.getBlock(x, y, z);
		Effects e = Effects.getEffect(b);
		return e != null && (has == null || e == has);//b == Blocks.quartz_block || b.isBeaconBase(world, x, y, z, tileLocation.xCoord, tileLocation.yCoord, tileLocation.zCoord);
	}

	private boolean checkForPillars(World world, int x, int y, int z) {
		int r = 3;//2;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dy = y-1;
				int dz = z+k;
				if (world.getBlock(dx, dy, dz) != ChromaBlocks.PYLONSTRUCT.getBlockInstance())
					return false;
				StoneTypes s = StoneTypes.SMOOTH;
				if (Math.abs(i) == r || Math.abs(k) == r) {
					if (Math.abs(i) == Math.abs(k)) {
						s = StoneTypes.CORNER;
					}
					else {
						s = Math.abs(i) > Math.abs(k) ? StoneTypes.GROOVE1 : StoneTypes.GROOVE2;
					}
				}
			}
		}
		return true;
	}

	public boolean isComplete() {
		return isStructureComplete;
	}

	private static enum Effects {

		UNKNOWN1(Blocks.quartz_block),
		UNKNOWN2(Blocks.lapis_block),
		UNKNOWN3(Blocks.diamond_block),
		;

		private Effects(Block b) {

		}

		public void doEffect(World world, Coordinate center) {

		}

	}

}
