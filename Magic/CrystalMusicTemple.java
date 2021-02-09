package Reika.ChromatiCraft.Magic;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Structure.MusicTempleStructure;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityCrystalMusic;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.MusicScore.Note;
import Reika.DragonAPI.Instantiable.MusicScore.ScoreTrack;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalMusicTemple {

	private static final ExpectedMelody melody = new ExpectedMelody();

	static {
		try(InputStream in = ChromatiCraft.class.getResourceAsStream("Resources/templesong.dat")) {
			melody.load(in);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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

	public void onMusicStart(World world, ScoreTrack track0) {
		if (track0 == null)
			return;
		/*
		int len = track0.getLengthInTicks()/8+1; //8 ticks/beat
		ExpectedNote[] data = new ExpectedNote[len];
		for (int i = 0; i < data.length; i++) {
			if (i == data.length-1) {
				data[i] = new ExpectedNote();
			}
			else {
				int tick = i*8;
				NoteData notes = track0.getNoteAt(tick);
				data[i] = new ExpectedNote(notes.keys());
			}
		}*//*
		int len = track0.getLengthInTicks();
		ExpectedNote[] data = new ExpectedNote[len/4+1]; //8 ticks/beat + 1 beat after to ensure end; resolutioin is 8th notes
		/*
		HashSet<MusicKey> active = null;
		int last = -1;
		for (int t = 0; t < len; t++) {
			int idx = t/8;
			if (idx != last) {
				if (active != null) {
					data[last] = new ExpectedNote(active);
					ReikaJavaLibrary.pConsole("Putting "+data[last]+" @ "+last);
				}
				active = new HashSet();
			}
			last = idx;
			NoteData nd = track0.getNoteAt(t);
			if (nd != null) {
				active.addAll(nd.keys());
			}
		}
		 *//*
		for (int i = 0; i < data.length; i++) {
			data[i] = new ExpectedNote(i*4);
		}
		for (NoteData nd : track0.getNotes()) {
			int dl = ReikaMathLibrary.roundToNearestX(4, nd.length());
			int time = ReikaMathLibrary.roundToNearestX(2, nd.tick);
			time = ReikaMathLibrary.roundDownToX(4, time);
			for (int t = time; t < time+dl; t++) {
				int idx = t/4;
				//ReikaJavaLibrary.pConsole("Adding "+nd.keys()+" to "+idx+" = "+data[idx]+" from "+time+"+"+dl+"="+(dl/4)+"x8ths");
				data[idx].add(nd.keys());
			}
		}
		  */
		if (isStructureComplete) {
			try {
				this.setMelody(track0);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (isCorrectMelody) {
				tileLocation.offset(2, -3, 0).setBlock(world, Blocks.stonebrick);
				tileLocation.offset(-2, -3, 0).setBlock(world, Blocks.stonebrick);
				tileLocation.offset(0, -3, 2).setBlock(world, Blocks.stonebrick);
				tileLocation.offset(0, -3, -2).setBlock(world, Blocks.stonebrick);
			}
		}
	}

	private void setMelody(ScoreTrack track) {
		track = track.alignToGrid(4);
		isCorrectMelody = true;
		MusicKey lowest = track.getLowest();
		if (lowest.getNote() != melody.lowest.rootNote) { //not even the right key
			isCorrectMelody = false;
			return;
		}
		int diff = lowest.octaveNumber-melody.lowest.octaveOffset;
		for (ExpectedNote e : melody.melody) {
			if (e != null) {
				e.setOctaveOffset(diff);
				if (!e.match(track)) {
					isCorrectMelody = false;
					return;
				}
			}
		}
	}

	public void onNote(World world, Note n, int track) {

	}

	public void onMusicEnd(World world) {
		if (isStructureComplete && isCorrectMelody) {
			this.onSongComplete(world);
		}
	}

	private void onSongComplete(World world) {
		tileLocation.offset(0, -3, 0).setBlock(world, Blocks.air);
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

	public void writeSyncTag(NBTTagCompound tag) {

	}

	@SideOnly(Side.CLIENT)
	public void readSyncTag(NBTTagCompound tag) {

	}

	private static class ExpectedMelody {

		private final LinkedList<ExpectedNote> melody = new LinkedList();

		private RelativeKey lowest;

		private void load(InputStream in) throws Exception {
			melody.clear();
			lowest = null;

			int tick = 0;
			ArrayList<String> li = ReikaFileReader.getFileAsLines(in, false, Charset.defaultCharset());
			for (String s : li) {
				ExpectedNote e = null;
				if (s.equalsIgnoreCase("null")) {

				}
				else {
					int idxc = s.indexOf(':');
					int idxl = s.indexOf('[');
					int idxr = s.indexOf(']');
					String flag = s.substring(0, idxc);
					String[] keys = s.substring(idxl+1, idxr).split(",");
					e = new ExpectedNote(tick);
					for (String s2 : keys) {
						RelativeKey rk = RelativeKey.parse(s2);
						e.permitted.add(rk);
						if (lowest == null || lowest.isAbove(rk))
							lowest = rk;
					}
					e.allowEmpty = Boolean.parseBoolean(flag);
				}
				melody.add(e);
				tick += 4;
			}

			//ReikaJavaLibrary.pConsole(melody);
		}

	}

	private static class ExpectedNote {

		public final int tick;
		private final HashSet<RelativeKey> permitted = new HashSet();

		private boolean allowEmpty = false;

		private ExpectedNote(int t, Collection<RelativeKey> c) {
			this(t, c != null ? c.toArray(new RelativeKey[c.size()]) : null);
		}

		private void setOctaveOffset(int off) {
			for (RelativeKey rk : permitted) {
				rk.scanOffset = off;
			}
		}

		private boolean match(ScoreTrack s) {
			for (int d = -12; d <= 12; d += 4) { //add a little timing flexibility
				Collection<Note> c = s.getActiveNotesAt(tick+d);
				if (this.match(c))
					return true;
			}
			return false;
		}

		private boolean match(Collection<Note> c) {
			if (this.requireEmpty())
				return c.isEmpty();
			if (c.isEmpty())
				return allowEmpty;
			for (Note n : c) {
				boolean flag = false;
				for (RelativeKey rk : permitted) {
					if (rk.match(n)) {
						flag = true;
						break;
					}
				}
				if (!flag)
					return false;
			}
			return true;
		}

		private ExpectedNote(int t, RelativeKey... keys) {
			tick = t;

			if (keys == null || keys.length == 0) {
				allowEmpty = true;
			}
			else {
				boolean flag = false;
				for (RelativeKey mk : keys) {
					if (mk == null)
						allowEmpty = true;
					else
						permitted.add(mk);
				}
			}
		}

		private void add(Collection<RelativeKey> keys) {
			allowEmpty = keys.isEmpty();
			for (RelativeKey key : keys) {
				this.add(key);
			}
		}

		private void add(RelativeKey key) {
			allowEmpty |= key == null;
			if (key != null)
				permitted.add(key);
		}

		private boolean requireEmpty() {
			return permitted.isEmpty();
		}

		@Override
		public String toString() {
			return tick+": "+allowEmpty+" & "+permitted;
		}

	}

	private static class RelativeKey {

		private final int octaveOffset;
		private final ReikaMusicHelper.Note rootNote;

		private int scanOffset = 0;

		private RelativeKey(ReikaMusicHelper.Note n, int o) {
			rootNote = n;
			octaveOffset = o;
		}

		public boolean match(Note n) {
			return n.key.getNote() == rootNote && n.key.octaveNumber == octaveOffset+scanOffset;
		}

		public boolean isAbove(RelativeKey rk) {
			return rk.octaveOffset < octaveOffset || (rk.octaveOffset == octaveOffset && rk.rootNote.ordinal() < rootNote.ordinal());
		}

		public static RelativeKey parse(String s) {
			int offset = Character.getNumericValue(s.charAt(s.length()-1));
			ReikaMusicHelper.Note n = ReikaMusicHelper.Note.getNoteByName(s.substring(0, s.length()-1));
			return new RelativeKey(n, offset);
		}

		@Override
		public int hashCode() {
			return rootNote.ordinal() | (octaveOffset << 4) | (scanOffset << 8);
		}

		@Override
		public String toString() {
			return rootNote+"@"+octaveOffset+"+"+scanOffset;
		}

	}

}
