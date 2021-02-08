package Reika.ChromatiCraft.Magic;

import java.util.LinkedList;
import java.util.Map;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Structure.MusicTempleStructure;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityCrystalMusic;
import Reika.DragonAPI.Instantiable.MusicScore.Note;
import Reika.DragonAPI.Instantiable.MusicScore.NoteData;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalMusicTemple {

	private static final LinkedList<MusicKey>[] melody = new LinkedList[2];

	static {
		melody[0] = new LinkedList();
		melody[1] = new LinkedList();


	}

	private final LinkedList<MusicKey>[] tracks = new LinkedList[16];
	private final MusicTempleStructure structure = new MusicTempleStructure();

	private Coordinate tileLocation;
	private boolean isStructureComplete;
	private boolean isCorrectMelody;

	public CrystalMusicTemple() {

	}

	public void setCore(TileEntityCrystalMusic te) {
		tileLocation = new Coordinate(te);
		structure.setOrigin(te.worldObj, tileLocation);
	}

	public void onMusicStart(World world, Map<Integer, NoteData> track0) {
		if (isStructureComplete) {
			this.setMelody(track0);
		}
	}

	private void setMelody(Map<Integer, NoteData> track) {
		isCorrectMelody = false;
	}

	public void onNote(World world, Note n, int track) {

	}

	public void onMusicEnd(World world) {
		if (isStructureComplete && isCorrectMelody) {
			this.onSongComplete(world);
		}
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
