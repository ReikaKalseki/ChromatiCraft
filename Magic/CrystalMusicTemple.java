package Reika.ChromatiCraft.Magic;

import java.util.LinkedList;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Structure.MusicTempleStructure;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityCrystalMusic;
import Reika.DragonAPI.Instantiable.MusicScore.Note;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalMusicTemple {

	private Coordinate tileLocation;
	private boolean isStructureComplete;

	private static final LinkedList<MusicKey>[] melody = new LinkedList[2];

	static {
		melody[0] = new LinkedList();
		melody[1] = new LinkedList();


	}

	private final LinkedList<MusicKey>[] remaining = new LinkedList[melody.length];
	private final MusicTempleStructure structure = new MusicTempleStructure();

	public CrystalMusicTemple() {
		this.refreshMelody();
	}

	private void refreshMelody() {
		for (int i = 0; i < melody.length; i++)
			remaining[i] = new LinkedList(melody[i]);
	}

	public void setCore(TileEntityCrystalMusic te) {
		tileLocation = new Coordinate(te);
		structure.setOrigin(te.worldObj, tileLocation);
	}

	public void onMusicStart(World world) {
		if (isStructureComplete) {

		}
	}

	public void onNote(World world, Note n, int track) {
		if (isStructureComplete && track < melody.length && n.key == remaining[track].getFirst()) {
			remaining[track].remove();
		}
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
		isStructureComplete = tileLocation != null && structure.validate();
	}

	public boolean isComplete() {
		return isStructureComplete;
	}

	@SideOnly(Side.CLIENT)
	public void render(float ptick) {

	}

}
